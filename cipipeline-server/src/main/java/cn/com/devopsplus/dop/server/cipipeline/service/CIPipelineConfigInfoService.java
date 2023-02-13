package cn.com.devopsplus.dop.server.cipipeline.service;

import cn.com.devopsplus.dop.server.cipipeline.config.BusinessAssert;
import cn.com.devopsplus.dop.server.cipipeline.dao.configInfo.CIStageRepository;
import cn.com.devopsplus.dop.server.cipipeline.dao.configInfo.ConfigInfoRepository;
import cn.com.devopsplus.dop.server.cipipeline.dao.configInfo.TrainSetModeRepository;
import cn.com.devopsplus.dop.server.cipipeline.dao.configInfo.UpdateModeRepository;
import cn.com.devopsplus.dop.server.cipipeline.model.po.configInfo.CIStage;
import cn.com.devopsplus.dop.server.cipipeline.model.po.configInfo.ConfigInfo;
import cn.com.devopsplus.dop.server.cipipeline.model.po.configInfo.TrainSetMode;
import cn.com.devopsplus.dop.server.cipipeline.model.po.configInfo.UpdateMode;
import cn.com.devopsplus.dop.server.cipipeline.util.JenkinsFileUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 持续集成流水线管理配置信息管理模块业务实现
 *
 * @author yangyuyan
 * @since 2022-12-01
 */
@Service
public class CIPipelineConfigInfoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ConfigInfoRepository configInfoRepository;
    @Autowired
    private CIStageRepository ciStageRepository;
    @Autowired
    private TrainSetModeRepository trainSetModeRepository;
    @Autowired
    private UpdateModeRepository updateModeRepository;
    @Autowired
    private JenkinsFileUtil jenkinsFileUtil;
    // 用于读取applicaiton.yaml参数
    @Autowired
    private Environment environment;

    private static int MAX_STAGE_NUM=10;

    /**
     * 解析yaml文件获得配置信息
     *
     * @param configFile
     * @return
     */
    public void uploadConfigFile(long userId, String configFile) {
        logger.info("[uploadConfigFile] request comming userId={}, configFilePath={}", userId,configFile);
        Yaml yaml = new Yaml();
//        File configFile = new File(configFilePath);
        Map map = null;
        //解析yaml文本存储LinkedHashMap
        map = yaml.loadAs(configFile, Map.class);
//        //解析yaml文件存储LinkedHashMap
//        try {
//            map = yaml.loadAs(new FileInputStream(configFile), Map.class);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        String codeBaseUrl = map.get("codeBaseUrl").toString();
        BusinessAssert.notNull(codeBaseUrl, "代码仓库地址不能为空");
        String codeBaseAccessToken=map.get("codeBaseAccessToken").toString();
        BusinessAssert.notNull(codeBaseAccessToken, "代码仓库所有者access-token不能为空");
        String codeBaseBranch = map.get("codeBaseBranch").toString();
        BusinessAssert.notNull(codeBaseUrl, "代码仓库分支不能为空");
        ConfigInfo configInfo=this.configInfoRepository.findAllByCodeBaseUrlAndCodeBaseBranch(codeBaseUrl,codeBaseBranch);
        if (configInfo==null) {
            String ownerAndRepo=this.getOwnerAndRepoByCodeBaseUrl(codeBaseUrl);
//            BusinessAssert.state(this.configWebhook(ownerAndRepo,codeBaseAccessToken),"无法为该代码仓库创建pull-request的Webhook");
            configInfo=ConfigInfo.builder()
                    .codeBaseUrl(codeBaseUrl)
                    .ownerAndRepo(ownerAndRepo)
                    .codeBaseAccessToken(codeBaseAccessToken)
                    .codeBaseBranch(codeBaseBranch)
                    .userId(userId)
                    .createTime(LocalDateTime.now())
                    .ciResultPredict(false)
                    .staticCodeCheck(false)
                    .build();
        }
        else{
            BusinessAssert.notEqual(userId,configInfo.getUserId(),"配置信息已存在且非本人创建");
            configInfo.setCodeBaseAccessToken(codeBaseAccessToken);
            configInfo.setCiResultPredict(false);
            configInfo.setStaticCodeCheck(false);
        }
        configInfo=this.analyzeConfigFile(userId,map,configInfo);
        this.generateJenkinsFile(configInfo);
        this.configInfoRepository.saveAndFlush(configInfo);

        this.trainModel(configInfo);
    }

    /**
     * 解析持续集成流水线配置信息
     *
     * @param userId
     * @param map
     */
    public ConfigInfo analyzeConfigFile(long userId, Map map,ConfigInfo configInfo) {
        logger.info("[analyzeConfigFile] request comming userId={}, map={}", userId);
        String configName = map.getOrDefault("configName", null).toString();
        configInfo.setConfigName(configName);
        configInfo.setUpdateTime(LocalDateTime.now());
        int num=1;
        StringBuilder allStages=new StringBuilder("-");
        if (map.containsKey("ciStages")) {
            Map ciStages = (Map) map.get("ciStages");
            for(Object entry : ciStages.entrySet()){
                BusinessAssert.state(num<=MAX_STAGE_NUM,"流水线步骤超过最大允许数");
                CIStage ciStage=this.getCIStage(num,configInfo);
                if (ciStage==null){
                    ciStage=new CIStage();
                }
                ciStage=this.analyzeCIStageInfo((Map.Entry)entry,ciStage);
                this.ciStageRepository.saveAndFlush(ciStage);
                allStages.append(ciStage.getCiStageName()+"-");
                this.setCIStage(num,configInfo,ciStage);
                if(ciStage.getCiStageName().equals("ciResultPredict")){
                    configInfo.setCiResultPredict(true);
                }
                if(ciStage.getCiStageName().equals("staticCodeCheck")){
                    configInfo.setStaticCodeCheck(true);
                }
                num++;
            }
        }
        configInfo.setCiStageNum(num-1);
        configInfo.setAllStages(allStages.toString());
        return configInfo;
    }

    /**
     * 解析持续集成流水线每个步骤配置信息
     *
     * @param entry
     * @param ciStage
     * @return
     */
    public CIStage analyzeCIStageInfo(Map.Entry entry, CIStage ciStage){
        logger.info("[analyzeCIStageInfo] request comming entry={}, ciStage={}");
        ciStage.setCiStageName(entry.getKey().toString());
        {
            // 判断步骤是否合法
        }
        Map model= (Map) entry.getValue();
        if(model==null){
            ciStage.setHasModel(false);
            ciStage.setModelName(null);
            ciStage.setModelTrainSetMode(null);
            ciStage.setModelUpdateMode(null);
            return ciStage;
        }
        Map modelInfo=(Map)model.get("model");
        ciStage.setModelName(modelInfo.get("name").toString());
        {
            // 判断模型是否存在
        }
        ciStage.setHasModel(true);
        TrainSetMode modelTrainSetMode=ciStage.getModelTrainSetMode();
        if(modelTrainSetMode==null){
            modelTrainSetMode=new TrainSetMode();
        }
        modelTrainSetMode=this.analyzeModelTrainSetMode((Map)modelInfo.getOrDefault("trainSetMode",null), modelTrainSetMode);
        ciStage.setModelTrainSetMode(modelTrainSetMode);
        this.trainSetModeRepository.saveAndFlush(modelTrainSetMode);
        UpdateMode modelUpdateMode=ciStage.getModelUpdateMode();
        if(modelUpdateMode==null){
            modelUpdateMode=new UpdateMode();
        }
        modelUpdateMode=this.analyzeModelUpdateMode((Map)modelInfo.getOrDefault("updateMode",null), modelUpdateMode);
        ciStage.setModelUpdateMode(modelUpdateMode);
        this.updateModeRepository.saveAndFlush(modelUpdateMode);
        return ciStage;
    }

    /**
     * 解析优化模型训练集选择策略配置信息
     *
     * @param trainSetModeInfo
     * @param trainSetMode
     * @return
     */
    public TrainSetMode analyzeModelTrainSetMode(Map trainSetModeInfo,TrainSetMode trainSetMode){
        logger.info("[analyzeModelTrainSetMode] request comming trainSetModeInfo={}, trainSetMode={}");
        if(trainSetModeInfo==null){
            trainSetMode.setTrainSetModeType(TrainSetMode.TrainSetModeType.AllCommit);
            return trainSetMode;
        }
        String trainSetModeType = (String) trainSetModeInfo.keySet().iterator().next();
        BusinessAssert.notIn(trainSetModeType,new String[]{"lastCommit","periodOfTime","allCommit"},"训练集策略不存在");
        if(trainSetModeType.equals("lastCommit")){
            trainSetMode.setTrainSetModeType(TrainSetMode.TrainSetModeType.LastCommit);
            Map lastCommit=(Map)trainSetModeInfo.get("lastCommit");
            if(lastCommit==null){
                trainSetMode.setCommitNumber(100);
            }
            else{
                trainSetMode.setCommitNumber((Integer) (lastCommit.getOrDefault("commitNumber",100)));
            }
            trainSetMode.setStartTime(null);
            trainSetMode.setEndTime(null);
        }
        else if(trainSetModeType.equals("periodOfTime")){
            trainSetMode.setTrainSetModeType(TrainSetMode.TrainSetModeType.PeriodOfTime);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            Map periodOfTime=(Map)trainSetModeInfo.get("periodOfTime");
            if(periodOfTime==null){
                trainSetMode.setStartTime(LocalDate.parse("1970.01.01",formatter));
                trainSetMode.setEndTime(LocalDate.parse("2022.12.31",formatter));
            }
            else{
                LocalDate startTime=LocalDate.parse(periodOfTime.getOrDefault("startTime","1970.01.01").toString(), formatter);
                trainSetMode.setStartTime(startTime);
                LocalDate endTime=LocalDate.parse(periodOfTime.getOrDefault("endTime","2022.12.31").toString(), formatter);
                trainSetMode.setEndTime(endTime);
            }
            trainSetMode.setCommitNumber(null);
        }
        else{
            trainSetMode.setTrainSetModeType(TrainSetMode.TrainSetModeType.AllCommit);
            trainSetMode.setStartTime(null);
            trainSetMode.setEndTime(null);
            trainSetMode.setCommitNumber(null);
        }
        return trainSetMode;
    }

    /**
     * 解析优化模型更新策略配置信息
     *
     * @param updateModeInfo
     * @param updateMode
     * @return
     */
    public UpdateMode analyzeModelUpdateMode(Map updateModeInfo,UpdateMode updateMode){
        logger.info("[analyzeModelTrainSetMode] request comming updateModeInfo={}, updateMode={}");
        if(updateModeInfo==null){
            updateMode.setUpdateModeType(UpdateMode.UpdateModeType.NeverUpdate);
            return updateMode;
        }
        String updateModeType = (String) updateModeInfo.keySet().iterator().next();
        BusinessAssert.notIn(updateModeType,new String[]{"regularInterval","neverUpdate"},"模型更新策略不存在");
        if(updateModeType.equals("regularInterval")){
            updateMode.setUpdateModeType(UpdateMode.UpdateModeType.RegularInterval);
            Map regularInterval=(Map)updateModeInfo.get("regularInterval");
            if(regularInterval==null){
                updateMode.setIntervalTime(7);
            }
            else{
                updateMode.setIntervalTime((Integer) (regularInterval.getOrDefault("interval",7)));
            }
        }
        else{
            updateMode.setUpdateModeType(UpdateMode.UpdateModeType.NeverUpdate);
            updateMode.setIntervalTime(null);
        }
        return updateMode;
    }

    /**
     * 根据流水线步骤序号填充配置信息
     *
     * @param num
     * @param configInfo
     * @param ciStage
     */
    public void setCIStage(int num, ConfigInfo configInfo,CIStage ciStage){
        logger.info("[setCIStage] request comming num={}, configInfo={},ciStage={}",num);
        switch (num){
            case 1:
                configInfo.setCiStage1(ciStage);
                return;
            case 2:
                configInfo.setCiStage2(ciStage);
                return;
            case 3:
                configInfo.setCiStage3(ciStage);
                return;
            case 4:
                configInfo.setCiStage4(ciStage);
                return;
            case 5:
                configInfo.setCiStage5(ciStage);
                return;
            case 6:
                configInfo.setCiStage6(ciStage);
                return;
            case 7:
                configInfo.setCiStage7(ciStage);
                return;
            case 8:
                configInfo.setCiStage8(ciStage);
                return;
            case 9:
                configInfo.setCiStage9(ciStage);
                return;
            case 10:
                configInfo.setCiStage10(ciStage);
                return;
        }
    }

    /**
     * 根据流水线步骤序号获得已有配置信息
     *
     * @param num
     * @param configInfo
     * @return
     */
    public CIStage getCIStage(int num, ConfigInfo configInfo){
        logger.info("[getCIStage] request comming num={}, configInfo={}",num);
        switch (num){
            case 1:
                return configInfo.getCiStage1();
            case 2:
                return configInfo.getCiStage2();
            case 3:
                return configInfo.getCiStage3();
            case 4:
                return configInfo.getCiStage4();
            case 5:
                return configInfo.getCiStage5();
            case 6:
                return configInfo.getCiStage6();
            case 7:
                return configInfo.getCiStage7();
            case 8:
                return configInfo.getCiStage8();
            case 9:
                return configInfo.getCiStage9();
            case 10:
                return configInfo.getCiStage10();
        }
        return null;
    }

    /**
     * 根据代码仓库地址并使用access-token认证配置webhook，当pull-request创建时通知
     * @param ownerAndRepo
     * @param codeBaseAccessToken
     * @return
     */
    public boolean configWebhook(String ownerAndRepo,String codeBaseAccessToken){
        logger.info("[configWebhook] request coming codeBaseUrl={}, codeBaseAccessToken={}",ownerAndRepo,codeBaseAccessToken);
        String URL="https://api.github.com/repos/"+ownerAndRepo+"/hooks";
        //构建body参数
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("active",true);

        JSONArray events=new JSONArray();
        events.add("pull_request");
        jsonObject.put("events",events);

        JSONObject config=new JSONObject();
        config.put("url",environment.getProperty("hostUrl")+"/v1/cipipeline/coreScheduler/newCommitCome");
        config.put("content_type","json");
        config.put("insecure_ssl","0");
        jsonObject.put("config",config);

        // 添加请求头
        HttpHeaders headers = new HttpHeaders();
        headers.add("accept","application/vnd.github+json");
        headers.add("authorization","Bearer "+codeBaseAccessToken);

        // 组装请求头和参数
        HttpEntity<String> formEntity = new HttpEntity<String>(jsonObject.toJSONString(), headers);

        // 发起post请求
        RestTemplate restTemplate=new RestTemplate();
        ResponseEntity<String> stringResponseEntity = null;
        try {
            stringResponseEntity = restTemplate.postForEntity(URL, formEntity, String.class);
            logger.info("[configWebhook] get the response: stringResponseEntity={}",stringResponseEntity);
        } catch (RestClientException e) {
            e.printStackTrace();
        }

        // 获取http状态码
        int statusCodeValue = stringResponseEntity.getStatusCodeValue();
        logger.info("[configWebhook] get the statusCode statusCodeValue={}",statusCodeValue);

        // 获取返回体
        String body = stringResponseEntity.getBody();
        logger.info("[configWebhook] get the respinse body body={}",body);

        return statusCodeValue==201;
    }

    /**
     * 根据代码仓库url字符串获得对应的仓库所有者和仓库名称
     * @param codeBaseUrl
     * @return
     */
    public String getOwnerAndRepoByCodeBaseUrl(String codeBaseUrl){
        logger.info("[getOwnerAndRepoByCodeBaseUrl] request comming codeBaseUrl={}",codeBaseUrl);
        String[] strs=codeBaseUrl.split("\\/|\\.",-1);
        String ownerAndRepo=strs[4]+"/"+strs[5];
        logger.info("[getOwnerAndRepoByCodeBaseUrl] return ownerAndRepo={}",ownerAndRepo);
        return ownerAndRepo;
    }

    /**
     * 根据配置信息生成用于pr代码测试的Jenkinsfile
     * @param configInfo
     */
    public ConfigInfo generateJenkinsFile(ConfigInfo configInfo){
        logger.info("[generateJenkinsFile] generate JenkinsFile: configInfo");
        String jenkinsFile= this.jenkinsFileUtil.generate(configInfo);
        try {
            String jenkinsFilePath=this.environment.getProperty("projectPath")+"/JenkinsFiles/"+configInfo.getConfigInfoId();
            File file = new File(jenkinsFilePath);
            PrintStream ps = new PrintStream(new FileOutputStream(file));
            ps.println(jenkinsFile);
            configInfo.setJenkinsFilePath(jenkinsFilePath);
        } catch (IOException e) {
            logger.error("[generateJenkinsFile] error for write jenkinsFile in file");
            e.printStackTrace();
        }
        return configInfo;
    }

    /**
     * 根据configInfoId获得已生成的JenkinsFile文件内容
     * @param configInfoId
     * @return
     */
    public String getJenkinsFile(long configInfoId){
        String jenkinsFilePath=this.configInfoRepository.getJenkinsFilePathByConfigInfoId(configInfoId);
        File file = new File(jenkinsFilePath);
        StringBuilder builder=new StringBuilder();
        try {
            InputStream in = new FileInputStream(file);
            byte[] b = new byte[1024];
            int len = 0;
            while((len = in.read(b)) != -1){
                builder.append(new String(b, 0, len, "GBK"));
            }
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder.toString();
    }

    /**
     * 训练配置中使用的各种优化模型
     * @param configInfo
     */
    public void trainModel(ConfigInfo configInfo){
        logger.info("[trainModel] start train models of ci optimize technique: configInfo");
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,10,1, TimeUnit.SECONDS,new ArrayBlockingQueue<>(10));
        threadPoolExecutor.execute(new Thread(new Runnable(){
            @Override
            public void run() {
                trainCIResultPredictModel(configInfo);
            }
        }));
    }

    /**
     * 持续集成结果预测模型训练
     * @param configInfo
     */
    public void trainCIResultPredictModel(ConfigInfo configInfo){
        logger.info("[trainCIResultPredictModel] trein ciresult predict model: ConfigInfo");
        try {
            String[] args1 = new String[] { environment.getProperty("projectPath")+"/CIResultPredict/venv/bin/python", environment.getProperty("projectPath")+"/CIResultPredict/main.py", environment.getProperty("projectPath")+"/CIResultPredict",configInfo.getOwnerAndRepo(),configInfo.getCodeBaseAccessToken() };
            Process proc1 = Runtime.getRuntime().exec(args1);
            BufferedReader in = new BufferedReader(new InputStreamReader(proc1.getInputStream(), "UTF-8"));
            String line = in.readLine();
            logger.info("执行结果："+line);
            in.close();
            proc1.waitFor();
        }
        catch (IOException e) {
            logger.error("[trainModel] 持续集成结果预测模型训练失败: IOException");
            e.printStackTrace();
        }
        catch (InterruptedException e) {
            logger.error("[trainModel] 持续集成结果预测模型训练失败: InterruptedException");
            e.printStackTrace();
        }
        catch (Exception e){
            logger.error("[trainModel] 持续集成结果预测模型训练失败: Exception");
            e.printStackTrace();
        }
    }
}
