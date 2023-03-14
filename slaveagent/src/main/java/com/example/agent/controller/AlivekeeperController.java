package com.example.agent.controller;

import com.example.agent.service.AliveKeeperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AlivekeeperController {

    @Autowired
    AliveKeeperService alivekeeperService;

    @GetMapping("/starttask")
    public void startSchedule(){
        System.out.println("start");
        alivekeeperService.startTask();
    }

}
