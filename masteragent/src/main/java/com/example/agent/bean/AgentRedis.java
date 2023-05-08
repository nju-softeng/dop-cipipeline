package com.example.agent.bean;

import ch.qos.logback.core.model.INamedModel;
import com.example.agent.po.AgentattributePO;
import com.example.agent.service.AgentService;
import com.example.agent.util.RedisUtil;
import com.example.agent.util.SpringContextUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Component
public class AgentRedis {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private RedisUtil redisUtil;
    @Resource
    private AgentService agentService;

//    @Resource
//    private RedisTemplate<String,AgentattributePO> redisTemplate;

    @PostConstruct
    public void  init(){
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
        redisUtil.set(String.valueOf(agentId),agentattributePO);
    }

    public AgentattributePO getAgentById(int agentId){
    //        AgentattributePO agentattributePO=(AgentattributePO) redisUtil.get(String.valueOf(agentId));
       return (AgentattributePO) redisTemplate.opsForValue().get(String.valueOf(agentId));
    }

    public List<Integer> getAgentIds(){
        List<Integer> ans=new ArrayList<>();
        for(Object o:redisUtil.getList("agentIds")){
            ans.add((Integer) o);
        }
        return ans;

    }
}
