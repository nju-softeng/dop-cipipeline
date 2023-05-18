package com.example.agent.controller;

import com.example.agent.bean.AgentRedis;
import com.example.agent.po.AgentattributePO;
import com.example.agent.pojo.ResultMsg;
import com.example.agent.pojo.SlaveMsg;
import com.example.agent.service.AgentService;
import com.example.agent.service.ToolService;
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
    AgentRedis agentRedis;

    @Autowired
    AgentService agentService;

    @Autowired
    AlivekeeperController alivekeeperController;

    @Autowired
    ToolService toolService;

    @GetMapping("/getslaveAttributes")
    public List<AgentattributePO> getAllSlaves(int agentId){
        System.out.println("AgentController [getAllSlaves]");
        List<Integer> slaveIds=agentRedis.getAgentIds();
        List<AgentattributePO> slaves=new ArrayList<>();
        for(Integer slaveid:slaveIds){
            slaves.add(agentRedis.getAgentById(slaveid));
        }
        return slaves;
    }


    @GetMapping("/registerAgent")
    public ResultMsg registerAgent(int agentId){
        System.out.println("AgentController [registerAgent]");
        //注册节点
        agentService.registAgent(agentId);
        //开启心跳
        agentRedis.init();
        //开启下载任务
        toolService.downloadNetResource("https://dlcdn.apache.org/maven/maven-3/3.9.0/binaries/apache-maven-3.9.0-bin.zip","maven","F:\\aa_agent\\slaveagent\\src\\main\\resources\\tools");
        return null;
    }

    @GetMapping("/createAgent")
    public ResultMsg createAgent(String name,String urlAndport){
        System.out.println("AgentController [createAgent]");
        agentService.createAgent(name,urlAndport.split(":")[0],Integer.parseInt(urlAndport.split(":")[1]));

        agentRedis.init();
        return null;
    }



    @GetMapping(value = "/v1/agent")
    public SlaveMsg getAgentAttribute(String agentMac){
        return null;
    }



}
