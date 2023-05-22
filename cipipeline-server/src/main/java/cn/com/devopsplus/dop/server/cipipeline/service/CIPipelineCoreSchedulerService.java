package cn.com.devopsplus.dop.server.cipipeline.service;

import cn.com.devopsplus.dop.server.cipipeline.dao.configInfo.ConfigInfoRepository;
import cn.com.devopsplus.dop.server.cipipeline.dao.pipeline.CIPipelineRepository;
import cn.com.devopsplus.dop.server.cipipeline.dao.pipeline.CITestResultRepository;
import cn.com.devopsplus.dop.server.cipipeline.model.po.configInfo.ConfigInfo;
import cn.com.devopsplus.dop.server.cipipeline.model.po.pipeline.CIPipeline;
import cn.com.devopsplus.dop.server.cipipeline.model.po.pipeline.CITestResult;
import cn.com.devopsplus.dop.server.cipipeline.model.vo.CITestResultVo;
import cn.com.devopsplus.dop.server.cipipeline.model.vo.CIpipelineVo;
import cn.com.devopsplus.dop.server.cipipeline.model.vo.ConfigInfoVo;
import cn.com.devopsplus.dop.server.cipipeline.mq.MessageSender;
import cn.com.devopsplus.dop.server.cipipeline.mq.RocketMQMessageSender;
import com.alibaba.fastjson.JSONArray;
import org.springframework.data.redis.core.RedisTemplate;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 持续集成流水线核心调度模块业务实现
 *
 * @author yangyuyan
 * @since 2022-12-10
 */
@Service
public class CIPipelineCoreSchedulerService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ConfigInfoRepository configInfoRepository;
    @Autowired
    private CIPipelineRepository ciPipelineRepository;
    @Autowired
    private CITestResultRepository ciTestResultRepository;
    @Autowired
    private Environment environment;
    @Autowired
    private MessageSender messageSender;
    @Autowired
    private RedisTemplate redisTemplate;
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void handleNewCommit(String webhookEvent, JSONObject webHookJsonObject) {
        logger.info("[handleNewCommit] request comming webhookEvent={}, webHookJsonObject",webhookEvent);
        if(!webhookEvent.equals("pull_request")||!webHookJsonObject.getOrDefault("action","").equals("opened")){
            logger.info("该webhook不是新的pr创建");
            return;
        }
        JSONObject pullRequestInfo=webHookJsonObject.getJSONObject("pull_request");
        String baseCodeBaseUrl=pullRequestInfo.getJSONObject("base").getJSONObject("repo").getString("html_url")+".git";
        String baseCodeBaseBranch=pullRequestInfo.getJSONObject("base").getString("ref");
        ConfigInfo configInfo=configInfoRepository.findAllByCodeBaseUrlAndCodeBaseBranch(baseCodeBaseUrl,baseCodeBaseBranch);
        if(configInfo==null){
            logger.info("该webhook通知的pr不是来自所监听的分支");
            return;
        }
        CIPipeline ciPipeline=CIPipeline.builder()
                .configInfoId(configInfo.getConfigInfoId())
                .configName(configInfo.getConfigName())
                .userId(configInfo.getUserId())
                .baseCodeBaseUrl(baseCodeBaseUrl)
                .ownerAndRepo(configInfo.getOwnerAndRepo())
                .codeBaseAccessToken(configInfo.getCodeBaseAccessToken())
                .baseCodeBaseBranch(baseCodeBaseBranch)
                .prNumber(pullRequestInfo.getLong("number"))
                .sourceCodeBaseUrl(pullRequestInfo.getJSONObject("head").getJSONObject("repo").getString("html_url")+".git")
                .sourceCodeBaseBranch(pullRequestInfo.getJSONObject("head").getString("ref"))
                .ciResultPredict(configInfo.isCiResultPredict())
                .ciResultPredictResult(false)
                .staticCodeCheck(configInfo.isStaticCodeCheck())
                .jenkinsFilePath(configInfo.getJenkinsFilePath())
                .runningState(CIPipeline.RunningState.StartRunning)
                .build();
        this.ciPipelineRepository.saveAndFlush(ciPipeline);

        startRunCIPipeline(ciPipeline);
    }

    /**
     * 开始执行持续集成流水线
     * @param ciPipeline
     */
    public void startRunCIPipeline(CIPipeline ciPipeline){
        logger.info("[startRunCIPipeline] start run ci pipeline");
        if(ciPipeline.getCiResultPredict()){
            boolean ciResultPredictResult=this.ciResultPredict(ciPipeline);
            logger.info("[startRunCIPipeline] the result of ci result predict is: "+ciResultPredictResult);
            ciPipeline.setCiResultPredictResult(ciResultPredictResult);
        }
        this.putInQueueForExecute(ciPipeline);
        ciPipeline.setRunningState(CIPipeline.RunningState.RunningForTest);
        this.ciPipelineRepository.saveAndFlush(ciPipeline);
    }

    /**
     * 调用持续集成结果预测执行
     * @param ciPipeline
     * @return
     */
    public boolean ciResultPredict(CIPipeline ciPipeline){
        logger.info("[ciResultPredict] start predict ciResult: ciPipeline");
        String line=null;
        try {
            // 持续集成结果预测模型
            String[] args = new String[] { environment.getProperty("projectPath")+"/CIResultPredict/venv/bin/python", environment.getProperty("projectPath")+"/CIResultPredict/verify.py", environment.getProperty("projectPath")+"/CIResultPredict",ciPipeline.getOwnerAndRepo(), String.valueOf(ciPipeline.getPrNumber()),ciPipeline.getCodeBaseAccessToken() };
            Process proc = Runtime.getRuntime().exec(args);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream(), "UTF-8"));
//            while((line=in.readLine())!=null){
//                System.out.println(line);
//            }
            line = in.readLine();
            logger.info("执行结果："+line);
            in.close();
            System.out.println(proc.waitFor());
        }
        catch (IOException e) {
            logger.error("[ciResultPredict] 持续集成结果预测执行失败: IOException");
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            logger.error("[ciResultPredict] 持续集成结果预测执行失败: InterruptedException");
            e.printStackTrace();
        }
        catch (Exception e){
            logger.error("[ciResultPredict] 持续集成结果预测执行失败: Exception");
            e.printStackTrace();
        }
        return line!=null&&line.equals("1");
    }

    /**
     * 根据持续集成结果预测结果，将流水线信息加入mq等待执行
     * @param ciPipeline
     */
    public void putInQueueForExecute(CIPipeline ciPipeline){
        logger.info("[putInQueueForExecute] put in redis or rocketmq wait for execute: ciPipeline");
        String topic=String.valueOf(ciPipeline.getConfigInfoId());
        String ciPipelineId=String.valueOf(ciPipeline.getCiPipelineId());
        String prNumber=String.valueOf(ciPipeline.getPrNumber());
        if(ciPipeline.getCiResultPredictResult()){
            logger.info("[putInQueueForExecute] CiResultPredictResult is Success, put in redis");
            JSONObject waitInRedisInfo = new JSONObject();
            waitInRedisInfo.put("prNumber",prNumber);
            waitInRedisInfo.put("joinTime", LocalDateTime.now().format(this.df));
            redisTemplate.opsForList().rightPush(topic,waitInRedisInfo.toString());
            logger.info("[putInQueueForExecute] put task info in resid: info:{}", waitInRedisInfo);
        }
        else{
            logger.info("[putInQueueForExecute] CiResultPredictResult is Fail, put in rocketmq for execute directly");
            String packageExcuteIds=prNumber;
            while(redisTemplate.opsForList().size(topic)!=0){
                JSONObject waitInRedisInfo=JSONObject.parseObject((String) redisTemplate.opsForList().rightPop(topic));
                String waitPrNumber=(String) waitInRedisInfo.get("prNumber");
                packageExcuteIds=waitPrNumber+"-"+packageExcuteIds;
            }
            this.sendMessageToMq(topic+"_"+prNumber,ciPipeline.getConfigInfoId(),packageExcuteIds);
        }
    }

    /**
     * 将redis中各项目等待超过30分钟的任务打包加入执行队列（10分钟轮询一次）
     */
    @Scheduled(fixedRate = 60000)
    public void putOverTimeTaskIntoMq(){
        logger.info("[putOverTimeTaskIntoMq] scan overtime task in redis to execute mq");
        long maxWaitTime=5L;
        List<Long> configInfos=this.configInfoRepository.getConfigInfoIds();
        for(long configInfo:configInfos){
            String topic=String.valueOf(configInfo);
            String packageExcuteIds="";
            String lastTaskId=null;
            LocalDateTime currentTime=LocalDateTime.now();
            while(redisTemplate.opsForList().size(topic)!=0){
                JSONObject waitInRedisInfo=JSONObject.parseObject((String) redisTemplate.opsForList().index(topic,0));
                LocalDateTime joinTime= LocalDateTime.parse((String)waitInRedisInfo.get("joinTime"),this.df);
                long between= Duration.between(joinTime, currentTime).toMinutes();
                if(between<maxWaitTime){
                    break;
                }
                String prNumber= (String) waitInRedisInfo.get("prNumber");
                lastTaskId=prNumber;
                packageExcuteIds+=prNumber+"-";
                redisTemplate.opsForList().leftPop(topic);
            }
            if(packageExcuteIds.length()!=0){
                this.sendMessageToMq(topic+"_"+lastTaskId,configInfo,packageExcuteIds.substring(0,packageExcuteIds.length()-1));
            }
        }
    }

    /**
     * 将测试任务填充到消息队列中等待执行
     * @param lastTaskId
     * @param configInfoId
     * @param packageExcuteIds
     */
    public void sendMessageToMq(String lastTaskId,long configInfoId,String packageExcuteIds){
        JSONObject executeInfo = new JSONObject();
        executeInfo.put("projectId",configInfoId);
        executeInfo.put("packageExcuteNum",packageExcuteIds);
        ConfigInfo configInfo=configInfoRepository.findAllByConfigInfoId(configInfoId);
        executeInfo.put("codeBaseUrl",configInfo.getCodeBaseUrl());
        executeInfo.put("codeBaseBranch",configInfo.getCodeBaseBranch());
        executeInfo.put("staticCodeCheck",configInfo.isStaticCodeCheck());
        executeInfo.put("ownerAndRepo",configInfo.getOwnerAndRepo());
        try {
            this.messageSender.sendToExecuteQueue(lastTaskId,executeInfo.toString());
        }
        catch (Exception e){
            logger.error("[putInQueueForExecute] error for put execute tasks to rocketmq");
        }
    }

    /**
     * 保存测试结束的结果数据
     * @param resultStr
     */
    public void saveTestResult(String resultStr){
        logger.info("[resultStr] request comming: resultStr={}",resultStr);
        JSONObject resultJsonObject=null;
        try{
            resultJsonObject=JSONObject.parseObject(resultStr);
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
        logger.info("[saveTestResult] request coming: resultJsonObject={}",resultJsonObject);
        long configInfoId=resultJsonObject.getLong("projectId");
        JSONObject staticCodeCheckResult=resultJsonObject.getJSONObject("staticCodeCheckResult");
        JSONArray resultArray=resultJsonObject.getJSONArray("resultArray");
        for(int i=0;i<resultArray.size();i++){
            JSONObject singleResultJsonObject=resultArray.getJSONObject(i);
            long prNumber=Long.parseLong(singleResultJsonObject.getString("prNumber"));
            CIPipeline ciPipeline=this.ciPipelineRepository.findAllByConfigInfoIdAndPrNumber(configInfoId,prNumber);
            if(singleResultJsonObject.getString("result").equals("MergeFail")){
                CITestResult ciTestResult=CITestResult.builder()
                        .ciPipelineId(ciPipeline.getCiPipelineId())
                        .prMergeResult("MergeFail")
                        .prMergeLog(singleResultJsonObject.getString("mergeLog"))
                        .build();
                this.ciTestResultRepository.saveAndFlush(ciTestResult);
                ciPipeline.setRunningState(CIPipeline.RunningState.Done);
                ciPipelineRepository.saveAndFlush(ciPipeline);
                continue;
            }
            CITestResult ciTestResult=CITestResult.builder()
                    .ciPipelineId(ciPipeline.getCiPipelineId())
                    .prMergeResult("Success")
                    .testResult(singleResultJsonObject.getString("result"))
                    .testLog(singleResultJsonObject.getString("testLog"))
                    .staticCodeCheckResult(staticCodeCheckResult.getString("staticCodeCheckResult"))
                    .staticCodeCheckLog(staticCodeCheckResult.getString("log"))
                    .build();
            ciTestResultRepository.saveAndFlush(ciTestResult);
            ciPipeline.setRunningState(CIPipeline.RunningState.Done);
            ciPipelineRepository.saveAndFlush(ciPipeline);
        }
    }

    /**
     * 获取某流水线配置下的所有运行流水线
     * @param configInfoId
     * @return
     */
    public List<CIpipelineVo> getCIPipelinesForTable(long configInfoId){
        logger.info("[getCIPipelinesForTable] Request coming: configInfoId={}",configInfoId);
        List<CIPipeline> ciPipelines = this.ciPipelineRepository.findAllByConfigInfoId(configInfoId);
        List<CIpipelineVo> cipipelineVos = new ArrayList<>();
        for (int i = 0; i < ciPipelines.size(); i++) {
            CIPipeline ciPipeline=ciPipelines.get(i);
            CIpipelineVo cipipelineVo = CIpipelineVo.builder()
                    .ciPipelineId(ciPipeline.getCiPipelineId())
                    .prNumber(ciPipeline.getPrNumber())
                    .sourceCodeBaseUrl(ciPipeline.getSourceCodeBaseUrl())
                    .sourceCodeBaseBranch(ciPipeline.getSourceCodeBaseBranch())
                    .build();
            if(ciPipeline.getRunningState()== CIPipeline.RunningState.RunningForTest){
                cipipelineVo.setRunningState("RunningForTest");
            }
            else if(ciPipeline.getRunningState()== CIPipeline.RunningState.StartRunning){
                cipipelineVo.setRunningState("StartRunning");
            }
            else if(ciPipeline.getRunningState()== CIPipeline.RunningState.Done){
                cipipelineVo.setRunningState("Done");
            }
            cipipelineVos.add(cipipelineVo);
        }
        return cipipelineVos;
    }

    /**
     * 获得某条流水线的运行结果
     * @param ciPipelineId
     * @return
     */
    public CITestResultVo getTestResult(long ciPipelineId){
        CITestResult ciTestResult=ciTestResultRepository.findAllByCiPipelineId(ciPipelineId);
        CITestResultVo ciTestResultVo=CITestResultVo.builder()
                .prMergeResult(ciTestResult.getPrMergeResult())
                .prMergeLog(ciTestResult.getPrMergeLog())
                .staticCodeCheckResult(ciTestResult.getStaticCodeCheckResult())
                .staticCodeCheckLog(ciTestResult.getStaticCodeCheckLog())
                .testResult(ciTestResult.getTestResult())
                .testLog(ciTestResult.getTestLog())
                .build();
        return ciTestResultVo;
    }
}
