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
    void regist(){
        agentService.registAgent(2);
    }
    @Test
    void create(){
        agentService.createMasterAgent("ggbo2");
    }

    @Test
    void getAgentBystate(){
        agentService.getAgentBystate(1,1);
    }
}