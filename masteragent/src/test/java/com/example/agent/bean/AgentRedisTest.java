package com.example.agent.bean;

import com.example.agent.po.AgentattributePO;
import com.example.agent.util.RedisUtil;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
class AgentRedisTest {

    @Autowired
    AgentRedis agentRedis;

    @Autowired
    RedisTemplate<String,Object> redisTemplate;

    @Autowired
    RedisUtil redisUtil;

    @Test
    void getAgentById() {

        System.out.println(agentRedis.getAgentById(1));
//        System.out.println(agentattributePO1.getAgent_os());


    }

    @Test
    void getagentIds(){
//        redisUtil.appendInteger("agentId",1);
//        redisUtil.appendInteger("agentId",2);

//        redisUtil.appendInteger("agentId");
//        System.out.println(redisUtil.getList("ints"));
//        List<Object> list=redisUtil.getList("agentId");
//        System.out.println(list);
//        System.out.println(agentRedis.getAgentIds());
//        List<Object> list=agentRedis.getAgentIds();
//        for(Object integer:list){
//            System.out.println((Integer)integer);
//        }
    }
}