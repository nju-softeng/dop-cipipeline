package com.example.agent.util;

import com.example.agent.service.AgentService;
import com.example.agent.service.AliveKeeperService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class alivekeeperRunner implements  Runnable{



    AliveKeeperService aliveKeeperService;


    @Override
    public void run() {
//        System.out.println(1);
        aliveKeeperService=SpringContextUtils.getApplicationContext().getBean(AliveKeeperService.class);
        aliveKeeperService.refreshAgent();
    }
}
