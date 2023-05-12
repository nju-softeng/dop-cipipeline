package cn.com.devopsplus.dop.server.testenvmanage.controller;

import cn.com.devopsplus.dop.server.testenvmanage.feign.CIPipelineFeign;
import cn.com.devopsplus.dop.server.testenvmanage.service.DataTransmitService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/v1/testEnvManage")
public class TestenvmanageMasternodeController {

    @Autowired
    private CIPipelineFeign ciPipelineFeign;

    @Autowired
    private DataTransmitService dataTransmitService;

    @PostMapping("/testReturnTestResult")
    public void testReturnTestResult(@RequestBody String testData){
        ciPipelineFeign.saveTestResult(testData);
    }

    @PostMapping("/testFreeNode")
    public void testFreeNode(){
       dataTransmitService.addFreeNodeNumber();
    }
}
