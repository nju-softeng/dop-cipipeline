package cn.com.devopsplus.dop.server.cipipeline.controller;

import cn.com.devopsplus.dop.server.cipipeline.model.po.pipeline.CIPipeline;
import cn.com.devopsplus.dop.server.cipipeline.model.vo.CITestResultVo;
import cn.com.devopsplus.dop.server.cipipeline.model.vo.CIpipelineVo;
import cn.com.devopsplus.dop.server.cipipeline.model.vo.ConfigInfoVo;
import cn.com.devopsplus.dop.server.cipipeline.service.CIPipelineCoreSchedulerService;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 持续集成流水线管理核心调度模块接口实现
 *
 * @author yangyuyan
 * @since 2022-12-01
 */

@RestController
@RequestMapping("/v1/cipipeline/coreScheduler")
public class CIPipelineCoreSchedulerController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private CIPipelineCoreSchedulerService ciPipelineCoreSchedulerService;

    @PostMapping(value = "/newCommitCome")
    public void newCommitCome(@RequestHeader("X-GitHub-Event") String webhookEvent,
                              @RequestBody String webhookBody){
        logger.info("[newCommitCome] request comming webhookEvent={}, webhookBody={}",webhookEvent,webhookBody);
        JSONObject webHookJsonObject=null;
        try{
            webHookJsonObject=JSONObject.parseObject(webhookBody);
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
        this.ciPipelineCoreSchedulerService.handleNewCommit(webhookEvent,webHookJsonObject);
    }

    @PostMapping(value = "/test")
    public void test(@RequestHeader Long configInfoId,@RequestHeader Long prNumber,@RequestHeader boolean ciResultPredictResult){
        CIPipeline ciPipeline=CIPipeline.builder()
                .configInfoId(configInfoId)
                .configName("configName")
                .userId(10L)
                .baseCodeBaseUrl("baseCodeBaseUrl")
                .ownerAndRepo("ownerAndRepo")
                .codeBaseAccessToken("codeBaseAccessToken")
                .baseCodeBaseBranch("baseCodeBaseBranch")
                .prNumber(prNumber)
                .sourceCodeBaseUrl("sourceCodeBaseUrl")
                .sourceCodeBaseBranch("sourceCodeBaseBranch")
                .ciResultPredict(true)
                .ciResultPredictResult(ciResultPredictResult)
                .staticCodeCheck(false)
                .jenkinsFilePath("jenkinsFilePath")
                .runningState(CIPipeline.RunningState.StartRunning)
                .build();
        this.ciPipelineCoreSchedulerService.putInQueueForExecute(ciPipeline);
    }

    @PostMapping(value = "/saveTestResult")
    public void saveTestResult(@RequestBody String resultStr){
        this.ciPipelineCoreSchedulerService.saveTestResult(resultStr);
    }

    @GetMapping("/getCIPipelines/{configInfoId}")
    public List<CIpipelineVo> getCIPipelines(@PathVariable(value = "configInfoId")Long configInfoId){
        logger.info("[getCIPipelines] request comming configInfoId={}",configInfoId);
        return ciPipelineCoreSchedulerService.getCIPipelinesForTable(configInfoId);
    }

    @GetMapping("/getTestResult/{ciPipelineId}")
    public CITestResultVo getTestResult(@PathVariable(value = "ciPipelineId")Long ciPipelineId){
        logger.info("[getTestResult] request comming ciPipelineId={}",ciPipelineId);
        return ciPipelineCoreSchedulerService.getTestResult(ciPipelineId);
    }
}
