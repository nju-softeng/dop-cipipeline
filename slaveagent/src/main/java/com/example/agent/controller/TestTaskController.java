package com.example.agent.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.agent.feign.TestManageFeign;
import com.example.agent.vo.AgentattributeVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestTaskController {

    @Autowired
    TestManageFeign testManageFeign;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //注册从节点
    @PostMapping("/testTask")
    public String testTask(@RequestBody String transmitData){
        logger.info("[testTask] get test data!");
        String testResult=this.testManageFeign.handleTestRequest(transmitData);
        return testResult;
    }
}
