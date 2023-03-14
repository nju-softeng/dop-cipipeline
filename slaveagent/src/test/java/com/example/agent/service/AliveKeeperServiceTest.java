package com.example.agent.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class AliveKeeperServiceTest {

    @Autowired
    AliveKeeperService aliveKeeperService;
    @Test
    void startTask() {
        aliveKeeperService.startTask();
    }

    @Test
    void refresh(){
        aliveKeeperService.refreshAgent();
    }
}