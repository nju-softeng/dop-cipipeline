package com.example.agent.controller;

import com.example.agent.pojo.ResultMsg;
import com.example.agent.service.AgentService;
import com.example.agent.service.AliveKeeperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

//@Controller
public class ManageController {
    @Autowired
    AliveKeeperService aliveKeeperService;

    @Autowired
    AgentService agentService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//    @GetMapping(value = "/v1/agent")
//    public int getAgent(int agentId){
//        return 0;
//    }

    @GetMapping("setOnline")
    public ResultMsg setAgentOnline(int agentid){
        logger.info("[setOnline]");
        agentService.changeAgentState(agentid,1);
        return null;
    }

    @GetMapping("setOffline")
    public ResultMsg setAgentOffline(int agentid){
        logger.info("[setOffline]");
        agentService.changeAgentState(agentid,0);
        return null;
    }



    @GetMapping("addagentship")
    public ResultMsg addAgentship(int masterid,int slaveid){
        return null;
    }

//    @GetMapping("keepAlive")
//    public void keepAlive(){
//        aliveKeeperService.startTask();
//    }

    @GetMapping("/manageagent")
    public void getFreeagent(int masterid){

    }
}
