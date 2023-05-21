package com.example.agent.service;


import com.alibaba.fastjson.JSONObject;
import com.example.agent.po.AgentattributePO;
import com.example.agent.po.SlaveAgentPO;
import com.example.agent.util.alivekeeperRunner;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
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
    JdbcTemplate jdbcTemplate;
    @Autowired
    FileService fileService;
    @Autowired
    AgentService agentService;

    @Autowired
    ServerDetailService serverDetailService;

    private ScheduledFuture future;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void startTask(){
        logger.info("[startTask]");
        stop();

//        String cron = "0 */1 * * * ?";
        String cron1= "0/2 * * * * ?";

        future = aliveScheduler.schedule(new alivekeeperRunner(),new CronTrigger(cron1));


    }

    public void stop(){
        logger.info("[stop]");
        if(future!=null){
            future.cancel(true);
        }
    }

    public void refreshAgent(){
        logger.info("[refreshAgent]");
        int agentId= agentService.getThisAgentId();
        String slaveAgentSql="select * from slave_agent where agent_id = ?";
        SlaveAgentPO slaveAgentPO=jdbcTemplate.queryForObject(slaveAgentSql,new BeanPropertyRowMapper<>(SlaveAgentPO.class),agentId);
//        String url="http://172.31.59.131:8080/refreshagent";
        String url="http://"+slaveAgentPO.getMaster_ip()+":"+String.valueOf(slaveAgentPO.getMaster_port())+"/master-agent-server/refreshagent";
        System.out.println(url);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("agentid",agentId);
        jsonObject.put("agentmemory",serverDetailService.getMemory());
        jsonObject.put("agentip",serverDetailService.getIP());
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jsonObject.put("refreshtime",sdf.format(System.currentTimeMillis()));
        fileService.doPost(url,jsonObject);
    }
}
