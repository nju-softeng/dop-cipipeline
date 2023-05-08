package com.example.agent.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.agent.po.AgentattributePO;
import com.example.agent.pojo.ResultMsg;
import com.example.agent.pojo.SlaveMsg;
import com.example.agent.service.AgentService;
import com.example.agent.service.ToolsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
public class AgentController {
    @Autowired
    AgentService agentService;

    @Autowired
    AlivekeeperController alivekeeperController;

    @Autowired
    ToolsService toolsService;

    //注册从节点
    @PostMapping("/registerAgent")
    public ResultMsg registerAgent(@RequestBody String agentIdString){
        JSONObject agentIdObject=JSONObject.parseObject(agentIdString);
        int agentId= (int) agentIdObject.get("agentId");
//        //注册节点
        agentService.registAgent(agentId);
        //开启心跳
        alivekeeperController.startSchedule();
        //开启下载任务
//        toolsService.downloadNetResource("https://mirrors.jenkins-ci.org/war/latest/jenkins.war","jenkins","F:\\aa_agent\\slaveagent\\src\\main\\resources\\tools");
        //使用多线程下载任务提高下载速度
        toolsService.downloadNetResourceByMultiThread("https://mirrors.jenkins-ci.org/war/latest/jenkins.war","jenkins","/home/cipipeline/agent/tools");

        return null;
    }

//    @GetMapping("/registerAgent")
//    public ResultMsg registerAgent(int agentid){
////        AgentattributePO agentattributePO=agentService.getAgentattributeByid(agentid);
//
//        return null;
//    }

    @GetMapping(value = "/v1/agent")
    public AgentattributePO getAgentAttribute(String agentMac){
        return null;
    }

//    @PostMapping(value = "/v1/agent")
//    public ResultMsg setAgent(String agentMac, int gentId){
//        return null;
//    }
//

}
