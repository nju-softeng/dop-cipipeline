package com.example.agent.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ToolsServiceTest {

    @Autowired
    ToolsService toolsService;
    @Test
    void downloadTools() {
        toolsService.downloadNetResource("https://mirrors.jenkins-ci.org/war/latest/jenkins.war","jenkins3","F:\\aa_agent\\slaveagent\\src\\main\\resources\\tools");
//        toolsService.downloadNetResource("http://www.jenkins.io/download/thank-you-downloading-windows-installer","jenkins1","F:\\aa_agent\\slaveagent\\src\\main\\resources\\tools");

    }

    @Test
    void fixTools(){
        toolsService.fixWar("F:\\aa_agent\\slaveagent\\src\\main\\resources\\tools","jenkins.war");
    }
}