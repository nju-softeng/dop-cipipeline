package com.example.agent.controller;

import com.example.agent.po.AgentattributePO;
import com.example.agent.pojo.ResultMsg;
import com.example.agent.pojo.SlaveMsg;
import com.example.agent.service.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
public class AgentController {
    @Autowired
    AgentService agentService;

    @GetMapping("/getslaveAttributes")
    public List<AgentattributePO> getAllSlaves(int agentId){
        List<Integer> slaveIds=agentService.getslaveidsBymasterId(agentId);
        List<AgentattributePO> slaves=new ArrayList<>();
        for(Integer slaveid:slaveIds){
            slaves.add(agentService.getAgentattributeByid(slaveid));
        }
        return slaves;
    }


    @GetMapping("/registerAgent")
    public ResultMsg registerAgent(int agentid){
//        AgentattributePO agentattributePO=agentService.getAgentattributeByid(agentid);

        return null;
    }

    @GetMapping(value = "/v1/agent")
    public SlaveMsg getAgentAttribute(String agentMac){
        return null;
    }



}
