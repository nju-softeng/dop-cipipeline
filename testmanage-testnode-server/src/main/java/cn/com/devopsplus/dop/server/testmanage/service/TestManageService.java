package cn.com.devopsplus.dop.server.testmanage.service;

import cn.com.devopsplus.dop.server.testmanage.config.JobConfig;
import cn.com.devopsplus.dop.server.testmanage.util.JenkinsUtils;
import cn.com.devopsplus.dop.server.testmanage.util.ShellUtil;
import com.alibaba.fastjson.JSONArray;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import org.eclipse.jgit.api.Git;
import com.offbytwo.jenkins.JenkinsServer;
import com.offbytwo.jenkins.model.Build;
import com.offbytwo.jenkins.model.Job;
import com.offbytwo.jenkins.model.JobWithDetails;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

/**
 * 测试管理业务实现类
 *
 * @author yangyuyan
 * @since 2023-01-27
 */
@Service
public class TestManageService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${localCodeBasePath}")
    private String localCodeBasePath;

    @Autowired
    private JenkinsServer jenkins;

    @Autowired
    private RestTemplate restTemplate;

    Git git;

    @Bean
    public static JenkinsServer getJenkinsServer() throws URISyntaxException {
        return new JenkinsServer(new URI(JenkinsUtils.uri), JenkinsUtils.username, JenkinsUtils.password);
    }

    /**
     * 处理测试请求，解析代码仓库和流水线数据等
     *
     * @param testDataJsonObject
     */
    public JSONObject handleTestRequest(JSONObject testDataJsonObject) {
        logger.info("[handleTestRequest] request coming: testDataJsonObject={}", testDataJsonObject);
        JSONObject configInfoObject = testDataJsonObject.getJSONObject("configInfo");
        String codeBaseUrl = configInfoObject.getString("codeBaseUrl");
        String codeBaseBranch = configInfoObject.getString("codeBaseBranch");
        String ownerAndRepo = configInfoObject.getString("ownerAndRepo");
        String packageExcuteNum=configInfoObject.getString("packageExcuteNum");
        List<String> packageExcutes = Arrays.asList(packageExcuteNum.split("-"));
        long projectId=configInfoObject.getLong("projectId");
        boolean staticCodeCheck = configInfoObject.getBoolean("staticCodeCheck");
        String jenkinsFile=testDataJsonObject.getString("jenkinsFile");

        List<List<String>> getCodeResult=this.getCodeForTest(codeBaseUrl, codeBaseBranch, ownerAndRepo, packageExcutes);
        JSONObject testResultForAll=new JSONObject();
        testResultForAll.put("projectId",projectId);
        JSONArray resultArray=new JSONArray();
        for(String failMergePr:getCodeResult.get(1)){
            JSONObject failMergePrResult=new JSONObject();
            failMergePrResult.put("prNumber",failMergePr);
            failMergePrResult.put("result","MergeFail");
            failMergePrResult.put("mergeLog","can not merge to codebase(conflicted or not at the same base)");
            resultArray.add(failMergePrResult);
        }
        if(getCodeResult.get(0).size()==0){
            testResultForAll.put("resultArray",resultArray);
            return testResultForAll;
        }

        List<String> successMerge=getCodeResult.get(0);
        JSONObject paralleResult=excuteStaticCodeCheckAndTestParallelly(staticCodeCheck, ownerAndRepo, projectId, jenkinsFile);
        JSONObject staticCodeCheckResult=paralleResult.getJSONObject("staticCodeCheckResult");
        testResultForAll.put("staticCodeCheckResult",staticCodeCheckResult);
        JSONObject testLog=paralleResult.getJSONObject("testResult");
        if(testLog.getString("pipelineBuildResult").equals("Success")){
            for(String prNumber:successMerge){
                JSONObject result=new JSONObject();
                result.put("prNumber",prNumber);
                result.put("testResult","Success");
                result.put("testLog",testLog);
                resultArray.add(result);
            }
            testResultForAll.put("resultArray",resultArray);
            return testResultForAll;
        }

        JSONObject lastResult=new JSONObject();
        String lastPrNum=successMerge.get(successMerge.size()-1);
        successMerge.remove(successMerge.size()-1);
        System.out.println(successMerge.size());
        while(successMerge.size()!=0){
            lastResult=new JSONObject();
            lastResult.put("prNumber",lastPrNum);
            lastResult.put("testLog",testLog);
            JSONObject currentTestLog=backtrackExecuteTest(String.valueOf(projectId),ownerAndRepo);
            if(currentTestLog.getString("pipelineBuildResult").equals("Success")){
                lastResult.put("result","TestFail");
                resultArray.add(lastResult);
                for(String prNumber:successMerge){
                    JSONObject result=new JSONObject();
                    result.put("prNumber",prNumber);
                    result.put("result","TestSuccess");
                    result.put("testLog",currentTestLog);
                    resultArray.add(result);
                }
                testResultForAll.put("resultArray",resultArray);
                return testResultForAll;
            }
            if(testLog.getString("testLog").equals(currentTestLog.getString("testLog"))){
                lastResult.put("result","TestSuccess");
            }
            else{
                lastResult.put("result","TestFail");
            }
            resultArray.add(lastResult);

            testLog=currentTestLog;
            lastPrNum=successMerge.get(successMerge.size()-1);
            successMerge.remove(successMerge.size()-1);
        }
        lastResult=new JSONObject();
        lastResult.put("prNumber",lastPrNum);
        lastResult.put("result","TestFail");
        lastResult.put("testLog",testLog);
        resultArray.add(lastResult);
        testResultForAll.put("resultArray",resultArray);
        logger.info("!!!!!!!!!!!{}",testResultForAll);
        return testResultForAll;
    }

    /**
     * 并行执行静态代码检查和代码验证测试
     * @param staticCodeCheck
     * @param ownerAndRepo
     * @param projectId
     * @param jenkinsFile
     * @return
     */
    public JSONObject excuteStaticCodeCheckAndTestParallelly(boolean staticCodeCheck,String ownerAndRepo,long projectId,String jenkinsFile){
        Future<JSONObject> future=null;
        if(staticCodeCheck){
            future=this.staticCodeCheck(ownerAndRepo,projectId);
        }
        JSONObject testResult=this.startTest(String.valueOf(projectId),jenkinsFile);
        JSONObject staticCodeCheckResult=null;
        try {
            staticCodeCheckResult=future.get();
        }
        catch (ExecutionException| InterruptedException e){
            logger.error("[staticCodeCheck] staticCodeCheck result get error: {}",e);
        }
        JSONObject paralleResult=new JSONObject();
        paralleResult.put("testResult",testResult);
        paralleResult.put("staticCodeCheckResult",staticCodeCheckResult);
        return paralleResult;
    }

    /**
     * 上次执行测试流水线失败，执行回溯操作
     * @param projectId
     * @return
     */
    public JSONObject backtrackExecuteTest(String projectId,String ownerAndRepo){
        ShellUtil.runShell("git reset --hard HEAD^",localCodeBasePath + ownerAndRepo);
        return runTestPipeline(projectId);
    }

    /**
     * 静态代码检查
     * @param ownerAndRepo
     */
    public Future<JSONObject> staticCodeCheck(String ownerAndRepo, long projectId) {
        logger.info("[staticCodeCheck] start running staticCodeCheck: ownerAndRepo={}, projectId={}",ownerAndRepo,projectId);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,10,1, TimeUnit.SECONDS,new ArrayBlockingQueue<>(10));
        Future<JSONObject> future=threadPoolExecutor.submit(new StaticCodeCheck(localCodeBasePath+ownerAndRepo,projectId));
        return future;
    }

    /**
     * 获取验证所需代码
     *
     * @param codeBaseUrl
     * @param codeBaseBranch
     * @param packageExcuteNum
     * @return
     */
    public List<List<String>> getCodeForTest(String codeBaseUrl, String codeBaseBranch, String ownerAndRepo, List<String> packageExcuteNum) {
        logger.info("[getCodeForTest] request coming: codeBaseUrl={}, codeBaseBranch={}, packageExcuteNum={}", codeBaseUrl, codeBaseBranch, packageExcuteNum);
        File codeBaseDir = new File(localCodeBasePath + ownerAndRepo);
        try {
            if (!codeBaseDir.exists() || codeBaseDir.length() == 0) {
                git = Git.cloneRepository()
                        .setURI(codeBaseUrl)
                        .setDirectory(codeBaseDir)
                        .setCloneAllBranches(true)
                        .setBranchesToClone(Collections.singleton(codeBaseBranch))
                        .setBranch(codeBaseBranch)
                        .setRemote("origin")
                        .call();
            } else {
                git = new Git(new FileRepositoryBuilder()
                        .setGitDir(new File(localCodeBasePath + ownerAndRepo+"/.git"))
                        .build());
                if(this.isBranchExistLocal(git,codeBaseBranch)){
                    git.checkout().setCreateBranch(false).setName(codeBaseBranch).setStartPoint("origin/" + codeBaseBranch).call();
                }
                else{
                    git.checkout().setCreateBranch(true).setName(codeBaseBranch).setStartPoint("origin/" + codeBaseBranch).call();
                }
                git.pull().call();
            }
        } catch (GitAPIException | IOException e) {
            logger.error("[getCodeForTest] git error: {}", e.getMessage());
        }
        List<List<String>> mergeResult=this.mergePrInCodeBase(localCodeBasePath + ownerAndRepo,packageExcuteNum);
        logger.info("[getCodeForTest] PRs that have merged: {}",mergeResult.get(0));
        logger.info("[getCodeForTest] PRs that is conflicted: {}",mergeResult.get(1));
        return mergeResult;
    }

    /**
     * 将pr请求代码批量合并到代码仓库指定分支
     *
     * @param codeBasePath
     * @param packageExcuteNum
     * @return
     */
    public List<List<String>> mergePrInCodeBase(String codeBasePath,List<String> packageExcuteNum){
        logger.info("[mergePrInCodeBase] request coming: codeBasePath={},packageExcuteNum={}", codeBasePath, packageExcuteNum);
        ShellUtil.runShell("git config pull.rebase false",codeBasePath);
        List<String> mergedPRs=new ArrayList<>();
        List<String> conflictedPRs=new ArrayList<>();
        for(String excuteNum :packageExcuteNum){
            String[] shellOutput=ShellUtil.runShell("git pull origin refs/pull/"+excuteNum+"/head",codeBasePath);
            if(!shellOutput[0].equals("0")){
                logger.info("[getCodeForTest] shell output1: "+shellOutput[1]);
                logger.info("[getCodeForTest] shell output2: "+shellOutput[2]);
                conflictedPRs.add(excuteNum);
                ShellUtil.runShell("git reset --hard HEAD",codeBasePath);
                continue;
            }
            mergedPRs.add(excuteNum);
        }
        List<List<String>> result=new ArrayList<>();
        result.add(mergedPRs);
        result.add(conflictedPRs);
        return result;
    }

    /**
     * 开始运行测试流水线
     * @return
     */
    public JSONObject startTest(String name,String jenkinsFile){
        logger.info("[startTest] start run test pipeline...");
        try {
            if(String.valueOf(jenkins.getJob(name))==null){
                jenkins.createJob(name, new JobConfig(jenkinsFile).getXml(),true);
            }
            else{
                jenkins.updateJob(name, new JobConfig(jenkinsFile).getXml(),true);
            }
            logger.info("[startTest] create jenkins job success!");
        }
        catch (Exception e) {
            logger.error("[startTest] 无法创建流水线！Exception",e);
        }

        return runTestPipeline(name);
    }

    /**
     * 运行测试流水线
     * @param name
     * @return
     */
    public JSONObject runTestPipeline(String name){
        int nextBuildNum = 0;
        try {
            nextBuildNum=jenkins.getJob(name).details().getNextBuildNumber();
            jenkins.getJob(name).build(true);
        }catch (Exception e) {
            logger.error("[startTest] 无法构建流水线！Exception",e);
        }
        logger.info("nextBuildNum ={}",nextBuildNum);
        waitForFinish(name,nextBuildNum);
        JSONObject testResult=new JSONObject();
        testResult.put("pipelineBuildResult",getBuildResult(name));
        testResult.put("testLog",getBuildOutputText(name));
        return testResult;
    }

    public void waitForFinish(String name,int buildNum){
        try{
            while(true){
                if(jenkins.getJob(name).getBuildByNumber(buildNum)==null){
                    continue;
                }
                if(!jenkins.getJob(name).getBuildByNumber(buildNum).details().isBuilding()){
                    break;
                }
            }
            logger.info("[waitForFinish] build is finished");
        }
        catch (Exception e) {
            logger.error("[isPipelineRunning] 无法获得流水线运行状态！Exception",e);
        }
    }

    /**
     * 根据流水线的名字的到运行日志
     * @param name
     * @return
     */
    public String getBuildOutputText(String name) {
        logger.info("[getBuildOutputText] Request coming: name={}",name);
        try {
            Map<String, Job> jobs = jenkins.getJobs();
            JobWithDetails job = jenkins.getJob(name).details();
            Build builds = job.getLastBuild();
            return builds.details().getConsoleOutputText();
        } catch (Exception e) {
            logger.error("[getBuildOutputText] 没有该流水线！Exception",e);
            return "没有该流水线";
        }
    }

    /**
     * 根据流水线的名字得到运行结果
     * @param name
     * @return
     */
    public String getBuildResult(String name) {
        logger.info("[getBuildResult] Request coming: name={}",name);
        try {
            Map<String, Job> jobs = jenkins.getJobs();
            JobWithDetails job = jobs.get(name).details();
            Build builds = job.getLastBuild();
            return builds.details().getResult().toString();
        } catch (Exception e) {
            logger.error("[getBuildResult] 无法获得流水线运行结果！Exception",e);
            return e.toString();
        }
    }

    /**
     * 判断本地仓库指定分支是否存在
     * @param git
     * @param codeBaseBranch
     * @return
     */
    public boolean isBranchExistLocal(Git git, String codeBaseBranch) {
        logger.info("[isBranchExistLocal] request coming: codeBaseBranch={}", codeBaseBranch);
        try {
            List<Ref> refs = git.branchList().call();
            for (Ref ref : refs) {
                if (ref.getName().contains(codeBaseBranch)) {
                    return true;
                }
            }
        } catch (GitAPIException e) {
            logger.error("[isBranchExistLocal] git error: {}", e.getMessage());
        }
        return false;
    }
}

class StaticCodeCheck implements Callable<JSONObject> {
    private String codeBasePath;
    private long projectId;

    public StaticCodeCheck(String codeBasePath,long projectId){
        super();
        this.codeBasePath=codeBasePath;
        this.projectId=projectId;
    }

    @Override
    public JSONObject call() throws Exception {
        // 静态代码检查调用
        System.out.println("静态代码检查调用");
        Thread.currentThread().sleep(60000);
        System.out.println("静态代码检查调用完成");
        JSONObject staticCodeCheckResult=new JSONObject();
        staticCodeCheckResult.put("staticCodeCheckResult","Success");
        staticCodeCheckResult.put("log","...");
        return staticCodeCheckResult;
    }
}
