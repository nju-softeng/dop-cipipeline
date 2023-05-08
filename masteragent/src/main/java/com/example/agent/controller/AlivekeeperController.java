package com.example.agent.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.agent.bean.AgentRedis;
import com.example.agent.po.AgentattributePO;
import com.example.agent.pojo.ResultMsg;
import com.example.agent.service.AliveKeeperService;
import com.example.agent.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
public class AlivekeeperController {
    @Autowired
    AgentRedis agentRedis;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    AliveKeeperService alivekeeperService;

    @GetMapping("/starttask")
    public void startSchedule(){
        System.out.println("start");
        alivekeeperService.startTask();
    }


    @PostMapping("/refreshagent")
    public void refreshAgent(@RequestBody JSONObject jsonObject) {
        int agentId= (int) jsonObject.get("agentid");
        String refreshtime= (String) jsonObject.get("refreshtime");
        int memory= (int) jsonObject.get("agentmemory");
        String ip= (String) jsonObject.get("agentip");
        AgentattributePO agentattributePO=agentRedis.getAgentById(agentId);
        agentattributePO.setAgent_online_time(refreshtime);
        agentattributePO.setAgent_memory(memory);
        agentattributePO.setAgent_ip(ip);
        agentRedis.setAgentById(agentId,agentattributePO);

    }



}
