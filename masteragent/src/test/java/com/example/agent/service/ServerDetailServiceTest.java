package com.example.agent.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class ServerDetailServiceTest {
    @Autowired
    ServerDetailService serverDetailService;


    @Test
    void getOS(){
        System.out.println(serverDetailService.getOS());
    }
    @Test
    void getMemory(){
        System.out.println(serverDetailService.getMemory());
    }

    @Test
    void getIP() {
        System.out.println(serverDetailService.getIP());
    }

    @Test
    void getLocalMac() {
        System.out.println(serverDetailService.getLocalMac());
    }
}