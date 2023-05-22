package cn.com.devopsplus.dop.server.testmanage.controller;

import cn.com.devopsplus.dop.server.testmanage.service.TestManageService;
import cn.com.devopsplus.dop.server.testmanage.util.ShellUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/v1/testManager/testNode")
public class TestManageController {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    TestManageService testManageService;

    @PostMapping("/handleTestRequest")
    public String handleTestRequest(@RequestBody String testData){
        JSONObject testDataJsonObject=null;
        try{
            testDataJsonObject=JSONObject.parseObject(testData);
        }
        catch (Exception exception){
            exception.printStackTrace();
        }
        JSONObject testResultForAll=testManageService.handleTestRequest(testDataJsonObject);
        logger.info("[handleTestRequest] test result: {}",testResultForAll);
        return testResultForAll.toJSONString();
    }

    @PostMapping("/evaluation1")
    public String evaluation1(@RequestBody String testData){
        JSONObject testDataJsonObject=null;
        try{
            testDataJsonObject=JSONObject.parseObject(testData);
        }
        catch (Exception exception){
            exception.printStackTrace();
        }

        String name=testDataJsonObject.getString("name");
        System.out.println(name);
        String codeBasePath=testDataJsonObject.getString("codeBasePath");
        String sha=testDataJsonObject.getString("sha");
        String packageExcuteNum=testDataJsonObject.getString("packageExcuteNum");
        System.out.println(packageExcuteNum);
        String jenkinsFile=testDataJsonObject.getString("jenkinsFile");

        String[] shellOutput2= ShellUtil.runShell("git reset --hard "+sha,codeBasePath);
        List<String> packageExcutes = Arrays.asList(packageExcuteNum.split("-"));

        List<List<String>> mergeResult=this.testManageService.mergePrInCodeBase(codeBasePath,packageExcutes);

        JSONObject testResult=this.testManageService.startTest(name,jenkinsFile);

        return testResult.getString("pipelineBuildResult");
    }

    @PostMapping("/evaluation2")
    public String evaluation2(@RequestBody String testData){
        JSONObject testDataJsonObject=null;
        try{
            testDataJsonObject=JSONObject.parseObject(testData);
        }
        catch (Exception exception){
            exception.printStackTrace();
        }

        String name=testDataJsonObject.getString("name");
        System.out.println(name);
        String codeBasePath=testDataJsonObject.getString("codeBasePath");
        String prNum=testDataJsonObject.getString("prNum");
        String sha=testDataJsonObject.getString("sha");
        String jenkinsFile=testDataJsonObject.getString("jenkinsFile");

        String[] shellOutput2= ShellUtil.runShell("git reset --hard "+sha,codeBasePath);
        String[] shellOutput=ShellUtil.runShell("git pull origin refs/pull/"+prNum+"/head",codeBasePath);

        JSONObject testResult=this.testManageService.startTest(name,jenkinsFile);

        return testResult.getString("pipelineBuildResult");
    }
}
