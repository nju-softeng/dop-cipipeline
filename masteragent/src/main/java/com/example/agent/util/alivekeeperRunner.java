package com.example.agent.util;

import com.example.agent.po.AgentattributePO;
import com.example.agent.service.AgentService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class alivekeeperRunner implements  Runnable{


    RedisUtil redisUtil;
    AgentService agentService;
    @Override
    public void run() {
//        System.out.println("1");
        agentService=SpringContextUtils.getApplicationContext().getBean(AgentService.class);
        redisUtil=SpringContextUtils.getApplicationContext().getBean(RedisUtil.class);
//        List<Integer> slaves=agentService.getslaveidsBymasterId(1);
        List<Integer> slaves=new ArrayList<>();
        slaves.add(1);
        slaves.add(2);
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date=sdf.format(System.currentTimeMillis());
        System.out.println("当前时间为"+date);
        for(Integer slave:slaves){
            AgentattributePO agentattributePO=agentService.getAgentattributeByid(slave);
            System.out.println(agentattributePO.getAgent_id()+"号节点的上次刷新时间为"+agentattributePO.getAgent_online_time());
        }
    }
}
