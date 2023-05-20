package com.example.agent.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.agent.po.AgentattributePO;
import com.example.agent.pojo.ResultMsg;
import com.example.agent.pojo.SlaveMsg;
import com.example.agent.service.AgentService;
import com.example.agent.service.ToolsService;
import com.example.agent.vo.AgentattributeVO;
//import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //注册从节点
    @PostMapping("/registerAgent")
    public AgentattributeVO registerAgent(@RequestBody String agentIdString, HttpServletRequest request){
        logger.info("[registerAgent]");
        JSONObject agentIdObject=JSONObject.parseObject(agentIdString);
        int agentId= (int) agentIdObject.get("agentId");
        String agentName= (String) agentIdObject.get("agentName");
        String slaveIp =(String) agentIdObject.get("slaveIp");
        String masterIp= (String) agentIdObject.get("agentUrl");
        int masterPort=(int) agentIdObject.get("agentport");
//        //注册节点
        AgentattributeVO agentattributeVO= agentService.registAgent(agentId,agentName,masterIp,masterPort,slaveIp);
        //开启心跳
//        alivekeeperController.startSchedule();
        //开启下载任务
//        toolsService.downloadNetResource("https://mirrors.jenkins-ci.org/war/latest/jenkins.war","jenkins","F:\\aa_agent\\slaveagent\\src\\main\\resources\\tools");
        //使用多线程下载任务提高下载速度
//        toolsService.downloadNetResourceByMultiThread("https://mirrors.jenkins-ci.org/war/latest/jenkins.war","jenkins","/home/cipipeline/agent/tools");
        if(agentattributeVO!=null) return agentattributeVO;
        return null;
    }


//    @GetMapping("/registerAgent")
//    public ResultMsg registerAgent(int agentid){
////        AgentattributePO agentattributePO=agentService.getAgentattributeByid(agentid);
//
//        return null;
//    }

    @GetMapping(value = "/v1/getAgentAttribute")
    public AgentattributePO getAgentAttribute(String agentMac){
        logger.info("[getAgentAttribute]");
        return null;
    }

//    @PostMapping(value = "/v1/agent")
//    public ResultMsg setAgent(String agentMac, int gentId){
//        return null;
//    }
//

}
