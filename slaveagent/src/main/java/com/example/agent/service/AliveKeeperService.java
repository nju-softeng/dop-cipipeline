package com.example.agent.service;


import com.alibaba.fastjson.JSONObject;
import com.example.agent.po.AgentattributePO;
import com.example.agent.util.alivekeeperRunner;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ScheduledFuture;

@Service
public class AliveKeeperService {
    @Resource(name="myThreadPoolTaskScheduler")
    private ThreadPoolTaskScheduler aliveScheduler;

    @Autowired
    FileService fileService;
    @Autowired
    AgentService agentService;

    @Autowired
    ServerDetailService serverDetailService;

    private ScheduledFuture future;

    public void startTask(){
        stop();

//        String cron = "0 */1 * * * ?";
        String cron1= "0/2 * * * * ?";

        future = aliveScheduler.schedule(new alivekeeperRunner(),new CronTrigger(cron1));


    }

    public void stop(){
        if(future!=null){
            future.cancel(true);
        }
    }

    public void refreshAgent(){
        AgentattributePO  agentattributePO=agentService.getthisAgent();
        String url="http://127.0.0.1:8080/refreshagent";
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("agentid",agentattributePO.getAgent_id());
        jsonObject.put("agentmemory",serverDetailService.getMemory());
        jsonObject.put("agentip",serverDetailService.getIP());
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonObject.put("refreshtime",sdf.format(System.currentTimeMillis()));
        fileService.doPost(url,jsonObject);
    }
}
