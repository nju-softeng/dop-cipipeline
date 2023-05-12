package cn.com.devopsplus.dop.server.testenvmanage.service;

import cn.com.devopsplus.dop.server.testenvmanage.feign.CIPipelineFeign;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 节点间数据传输业务实现
 *
 * @author yangyuyan
 * @since 2023-01-16
 */
@Service
public class DataTransmitService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CIPipelineFeign ciPipelineFeign;

    @Autowired
    private RedisTemplate redisTemplate;

    public DataTransmitService(){
        this.redisTemplate.opsForValue().set("FREE_NODE_NUMBER",0);
    }

    public JSONObject generateTransmitData(String keys,String messageBody){
        logger.info("[generateTransmitData] request coming keys={}, messageBody={}",keys,messageBody);
        JSONObject messageBodyJsonObject=JSONObject.parseObject(messageBody);
        long configInfoId=messageBodyJsonObject.getLong("projectId");
        JSONObject transmitDataJsonObject = new JSONObject();
        transmitDataJsonObject.put("configInfo",messageBodyJsonObject);
        transmitDataJsonObject.put("jenkinsFile",this.ciPipelineFeign.getJenkinsFile(configInfoId));
        return transmitDataJsonObject;
    }

    public int getFreeNodeNumber(){
        return (int) this.redisTemplate.opsForValue().get("FREE_NODE_NUMBER");
    }

    public void addFreeNodeNumber(){
        int freeNodeNumber=this.getFreeNodeNumber();
        this.redisTemplate.opsForValue().set("FREE_NODE_NUMBER",freeNodeNumber+1);
    }

    public void subFreeNodeNumber(){
        int freeNodeNumber=this.getFreeNodeNumber();
        this.redisTemplate.opsForValue().set("FREE_NODE_NUMBER",freeNodeNumber-1);
    }
}
