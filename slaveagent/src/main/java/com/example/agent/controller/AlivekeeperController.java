package com.example.agent.controller;

import com.example.agent.service.AliveKeeperService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AlivekeeperController {

    @Autowired
    AliveKeeperService alivekeeperService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //开启传输心跳包
    @GetMapping("/starttask")
    public void startSchedule(){
        logger.info("[starttask]");
//        System.out.println("start");
        alivekeeperService.startTask();
    }

}
