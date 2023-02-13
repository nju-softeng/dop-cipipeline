package cn.com.devopsplus.dop.server.testmanage.controller;

import cn.com.devopsplus.dop.server.testmanage.service.TestManageService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/v1/testManager/testNode")
public class TestManageController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    TestManageService testManageService;

    @PostMapping("/handleTestRequest")
    public void handleTestRequest(@RequestBody String testData){
        JSONObject testDataJsonObject=null;
        try{
            testDataJsonObject=JSONObject.parseObject(testData);
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
        testManageService.handleTestRequest(testDataJsonObject);
    }
}
