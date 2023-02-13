package cn.com.devopsplus.dop.server.cipipeline.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
public class CIPipelineConfigInfoServiceTest {
    @Autowired
    CIPipelineConfigInfoService ciPipelineConfigInfoService;

    @Test
    public void configWebhookTest_Success(){
        boolean result=this.ciPipelineConfigInfoService.configWebhook("https://github.com/yangyyyyy/test.git","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");
        Assert.assertTrue(result);
    }
}