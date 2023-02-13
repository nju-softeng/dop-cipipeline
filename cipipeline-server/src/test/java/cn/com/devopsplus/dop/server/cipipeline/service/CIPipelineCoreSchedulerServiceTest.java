package cn.com.devopsplus.dop.server.cipipeline.service;

import cn.com.devopsplus.dop.server.cipipeline.dao.pipeline.CIPipelineRepository;
import cn.com.devopsplus.dop.server.cipipeline.model.po.pipeline.CIPipeline;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CIPipelineCoreSchedulerServiceTest {

    @Autowired
    CIPipelineRepository ciPipelineRepository;
    @Test
    public void addCiPipeline(){
        for(long i=1;i<=4;i++){
            CIPipeline ciPipeline=CIPipeline.builder()
                    .configInfoId(1L)
                    .configName("test")
                    .userId(1L)
                    .sourceCodeBaseUrl("https://github.com/yangyyyyy/test.git")
                    .ownerAndRepo("yangyyyyy/test")
                    .codeBaseAccessToken("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")
                    .baseCodeBaseBranch("tobranch")
                    .prNumber(10+i)
                    .sourceCodeBaseUrl("https://github.com/yangyyyyy/test.git")
                    .sourceCodeBaseBranch("from"+i)
                    .ciResultPredict(true)
                    .ciResultPredictResult(true)
                    .staticCodeCheck(true)
                    .jenkinsFilePath("/Users/yangyuyan/Desktop/dop-cipipeline/cipipeline-server/JenkinsFiles/1")
                    .runningState(CIPipeline.RunningState.StartRunning)
                    .build();
            this.ciPipelineRepository.saveAndFlush(ciPipeline);
        }
    }
}
