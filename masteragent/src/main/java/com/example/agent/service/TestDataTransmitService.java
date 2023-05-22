package com.example.agent.service;

import com.alibaba.fastjson.JSONObject;
import com.example.agent.bean.AgentRedis;
import com.example.agent.feign.CIPipelineFeign;
import com.example.agent.po.AgentattributePO;
import com.example.agent.util.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 节点间数据传输业务实现
 *
 * @author yangyuyan
 * @since 2023-01-16
 */
@Service
public class TestDataTransmitService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CIPipelineFeign ciPipelineFeign;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private AgentRedis agentRedis;

    @Autowired
    private FileService fileService;

    @Autowired
    private AgentService agentService;

    public void generateTransmitData(String keys, String messageBody,int agentId){
        logger.info("[generateTransmitData] request coming keys={}, messageBody={}",keys,messageBody);

        JSONObject messageBodyJsonObject=JSONObject.parseObject(messageBody);
        long configInfoId=messageBodyJsonObject.getLong("projectId");
        JSONObject transmitDataJsonObject = new JSONObject();
        transmitDataJsonObject.put("configInfo",messageBodyJsonObject);
        transmitDataJsonObject.put("jenkinsFile",this.ciPipelineFeign.getJenkinsFile(configInfoId));

        // 异步传递数据到测试节点开始执行测试任务
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,10,1, TimeUnit.SECONDS,new ArrayBlockingQueue<>(10));
        threadPoolExecutor.execute(new Thread(new Runnable(){
            @Override
            public void run() {
                AgentattributePO agentattributePO=agentRedis.getAgentById(agentId);
                String url="http://" + agentattributePO.getAgent_ip() + ":" + agentattributePO.getAgent_port() + "/testTask";
                String testResultStr=fileService.doPost(url,transmitDataJsonObject);
                ciPipelineFeign.saveTestResult(testResultStr);
                addFreeAgent(agentId);
            }
        }));
    }

    public long getFreeAgentsNum(){
        logger.info("[getFreeAgentsNum]");
        if(redisUtil.getList("freeAgents")==null){
            return 0l;
        }
        return this.redisTemplate.opsForList().size("freeAgents");
    }

    public int useFirstFreeAgent(){
        logger.info("[useFirstFreeAgent]");
        return Integer.parseInt(this.redisTemplate.opsForList().leftPop("freeAgents"));
    }

    public void addFreeAgent(int agentId){
        logger.info("[addFreeAgent]");
        this.agentService.changeAgentState(agentId,1);
    }
}
