package com.example.agent.controller;

import com.example.agent.pojo.ResultMsg;
import com.example.agent.service.AgentService;
import com.example.agent.service.AliveKeeperService;
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

    @GetMapping(value = "/v1/agent")
    public int getAgent(int agentId){
        return 0;
    }

    @GetMapping("setOnline")
    public ResultMsg setAgentOnline(int agentid){
        agentService.changeAgentState(agentid,1);
        return null;
    }

    @GetMapping("setOffline")
    public ResultMsg setAgentOffline(int agentid){
        agentService.changeAgentState(agentid,0);
        return null;
    }



    @PostMapping(value = "/v1/agent")
    public ResultMsg addAgentship(int masterid,int slaveid){
        return null;
    }

    @GetMapping("keepAlive")
    public void keepAlive(){
        aliveKeeperService.startTask();
    }
}
