package com.example.agent.util;

import com.example.agent.bean.AgentRedis;
import com.example.agent.po.AgentattributePO;
import com.example.agent.service.AgentService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class alivekeeperRunner implements  Runnable{


    AgentRedis agentRedis;
    RedisUtil redisUtil;
    AgentService agentService;
    @Override
    public void run() {
//        System.out.println("1");
        agentService=SpringContextUtils.getApplicationContext().getBean(AgentService.class);
        redisUtil=SpringContextUtils.getApplicationContext().getBean(RedisUtil.class);
        agentRedis=SpringContextUtils.getApplicationContext().getBean(AgentRedis.class);
//        List<Integer> slaves=agentService.getslaveidsBymasterId(1);
        List<Integer> slaves=agentRedis.getAgentIds();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date=sdf.format(System.currentTimeMillis());
        System.out.println("当前时间为"+date);
        for(Integer slave:slaves){
            AgentattributePO agentattributePO=agentRedis.getAgentById(slave);
            System.out.println(agentattributePO.getAgent_id()+"号节点的上次刷新时间为"+agentattributePO.getAgent_online_time());
            if(TimeUtil.isTimeDifferenceGreaterThan30Seconds(date,agentattributePO.getAgent_online_time())){
                agentattributePO.setAgent_state(0);
                System.out.println(String.valueOf(agentattributePO.getAgent_id())+"号机器超时");
            }
            agentRedis.setAgentById(slave,agentattributePO);
        }
    }
}
