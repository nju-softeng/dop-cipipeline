package com.example.agent.service;


import com.example.agent.util.alivekeeperRunner;
import jakarta.annotation.Resource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledFuture;

@Service
public class AliveKeeperService {
    @Resource(name="myThreadPoolTaskScheduler")
    private ThreadPoolTaskScheduler aliveScheduler;

    private ScheduledFuture future;

    public void startTask(){
        stop();

//        String cron = "0 */1 * * * ?";
        String cron1= "0/10 * * * * ?";

        future = aliveScheduler.schedule(new alivekeeperRunner(),new CronTrigger(cron1));


    }

    public void stop(){
        if(future!=null){
            future.cancel(true);
        }
    }
}
