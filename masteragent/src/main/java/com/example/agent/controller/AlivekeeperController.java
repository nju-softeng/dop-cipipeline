package com.example.agent.controller;


import com.alibaba.fastjson.JSONObject;
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
        double memory= (double) jsonObject.get("agentmemory");
        String ip= (String) jsonObject.get("agentip");
        redisUtil.set(agentId+"_refreshtime",refreshtime);
        redisUtil.set(agentId+"_memory",memory);
        redisUtil.set(agentId+"_ip",ip);
        System.out.println(jsonObject);
    }



}
