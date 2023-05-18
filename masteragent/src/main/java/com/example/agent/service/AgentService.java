package com.example.agent.service;

import com.alibaba.fastjson.JSONObject;
import com.example.agent.po.AgentattributePO;
import com.example.agent.po.AgentmasterPO;
import com.example.agent.pojo.ResultMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Service
public class AgentService {

    @Autowired
    DataSource dataSource;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ServerDetailService serverDetailService;

    @Autowired
    FileService fileService;

    @Value("${server.port}")
    Integer masterPort;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public List<Integer> getslaveidsBymasterId(int masterid){
        logger.info("[getslaveidsBymasterId]");
        String sql="select * from agentmaster where agent_master = ?";
//        String sql="select * from agentmaster";
        List<AgentmasterPO> agentmasterPOS=jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(AgentmasterPO.class),masterid);
//        List<AgentmasterPO> agentmasterPOS=jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(AgentmasterPO.class));
        List<Integer> slaveids = new ArrayList<>();
        for(AgentmasterPO po:agentmasterPOS){
            slaveids.add(po.getAgent_id());
        }
        return slaveids;
    }

    public ResultMsg registAgent(int agentid){
        logger.info("[registAgent]");
        String server_os=serverDetailService.getOS();
        double server_memory=serverDetailService.getMemory();
        String server_mac=serverDetailService.getLocalMac();
        String server_cpu=serverDetailService.getCPU();
        String server_ip=serverDetailService.getIP();
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date=sdf.format(System.currentTimeMillis());
        String sql="update agentattribute set agent_os=?,agent_memory=?,agent_cpu=?,agent_mac=?,agent_ip=?,agent_online_time=? where agent_id=?";
        int res=jdbcTemplate.update(sql,server_os,server_memory,server_cpu,server_mac,server_ip,date,agentid);
        return null;
    }

    public AgentattributePO getAgentattributeByid(int agentid){
        logger.info("[getAgentattributeByid]");
        String sql="select * from agentattribute where agent_id = ?";
        AgentattributePO agentattributePO=jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<>(AgentattributePO.class),agentid);
        return agentattributePO;
    }

    public List<AgentattributePO> getAllAgentAttributes(){
        logger.info("[getAllAgentAttributes]");
        String sql="select * from agentattribute";
        List<AgentattributePO> agentattributePOS=jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(AgentattributePO.class));
        return agentattributePOS;
    }





    public void createAgent(String name,String urlStr,int port){
//        String insertsql="insert into agentattribute(agent_name,agent_os,agent_memory,agent_cpu,agent_state,agent_mac,agent_type,agent_port) values (?,null,0,null,0,null,0,0)";
//        int affectRow=jdbcTemplate.update(insertsql,name);
//        if(affectRow>0){
        String agentIdSql="select max(agent_id) from agentattribute";
        int agentId=1;
        Integer lastAgentId=jdbcTemplate.queryForObject(agentIdSql,Integer.class);
        if(lastAgentId!=null){
            agentId=lastAgentId+1;
        }
        if(agentId>0) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("agentId", agentId);
            jsonObject.put("slaveIp", urlStr);
            jsonObject.put("agentName", name);
            jsonObject.put("agentport", masterPort);
            String urlAndIpStr = "http://" + urlStr + ":" + String.valueOf(port) + "/registerAgent";
//            System.out.println(urlAndIpStr);
            //调用从节点的连接接口
            String agentattributeVOStr = fileService.doPost(urlAndIpStr, jsonObject);
            JSONObject agentattributeObject = JSONObject.parseObject(agentattributeVOStr);
            AgentattributePO agentattributePO = AgentattributePO.fromJson(agentattributeObject);
            insertSlaveAgentPO(agentattributePO);
        }


        }

    public ResultMsg changeAgentState(int agentid,int state){
        String sql="update agentattribute set agent_state = ? where agent_id = ?";
        int res=jdbcTemplate.update(sql,state,agentid);
        return null;

    }

    public List<AgentattributePO> getAgentBystate(int masterid,int state){
        String sql="select * from agentattribute where agent_state = ?";
        List<AgentattributePO> agentattributePOS=jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(AgentattributePO.class),state);
        return agentattributePOS;
    }

    public void insertSlaveAgentPO(AgentattributePO agentPO){
        // 定义SQL语句和参数
        String sql = "INSERT INTO agentattribute(agent_id, agent_name, agent_os, agent_memory, agent_cpu, agent_state, agent_mac, agent_type, agent_ip, agent_port, agent_online_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        Object[] params = {agentPO.getAgent_id(), agentPO.getAgent_name(), agentPO.getAgent_os(), agentPO.getAgent_memory(), agentPO.getAgent_cpu(), agentPO.getAgent_state(), agentPO.getAgent_mac(), agentPO.getAgent_type(), agentPO.getAgent_ip(), agentPO.getAgent_port(), agentPO.getAgent_online_time()};

// 执行插入操作
        int rows = jdbcTemplate.update(sql, params);

// 判断是否插入成功
        if (rows > 0) {
            System.out.println("Insert successful");
        } else {
            System.out.println("Insert failed");
        }
    }




}
