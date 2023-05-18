package com.example.agent.bean;

import com.example.agent.po.AgentattributePO;
import com.example.agent.service.AgentService;
import com.example.agent.util.RedisUtil;
import com.example.agent.util.SpringContextUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Component
public class AgentRedis {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private AgentService agentService;

//    @Resource
//    private RedisTemplate<String,AgentattributePO> redisTemplate;

    @PostConstruct
    public void  init(){
        System.out.println("AgentRedis [init]");
        List<AgentattributePO> agentattributePOList=agentService.getAllAgentAttributes();
        List<Integer> agentIds=new ArrayList<>();
        redisTemplate.delete("agentIds");
        for(AgentattributePO agentattributePO:agentattributePOList){
            agentIds.add(agentattributePO.getAgent_id());
            redisTemplate.opsForValue().set(String.valueOf(agentattributePO.getAgent_id()),agentattributePO);
            redisUtil.appendInteger("agentIds",agentattributePO.getAgent_id());
        }
    }

    public void setAgentById(int agentId,AgentattributePO agentattributePO){
        System.out.println("AgentRedis [setAgentById]");
        redisUtil.set(String.valueOf(agentId),agentattributePO);
    }

    public AgentattributePO getAgentById(int agentId){
        System.out.println("AgentRedis [getAgentById]");
    //        AgentattributePO agentattributePO=(AgentattributePO) redisUtil.get(String.valueOf(agentId));
       return (AgentattributePO) redisTemplate.opsForValue().get(String.valueOf(agentId));
    }

    public List<Integer> getAgentIds(){
        System.out.println("AgentRedis [getAgentIds]");
        List<Integer> ans=new ArrayList<>();
        if(redisUtil.getList("agentIds")==null){
            return ans;
        }
        for(Object o:redisUtil.getList("agentIds")){
            ans.add((Integer) o);
        }
        return ans;

    }
}
