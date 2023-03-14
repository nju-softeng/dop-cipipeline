package com.example.agent.service;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
class AgentServiceTest {

    @Autowired
    AgentService agentService;
    @Test
    void createAgentBymaster() {
        agentService.createAgentBymaster(1,"ggbo3");
    }
    @Test
    void regist(){
        agentService.registAgent(3);
    }
    @Test
    void create(){
        agentService.createMasterAgent("ggbo3");
    }
    @Test
    void getthisAgent(){
        agentService.getthisAgent();
    }
}