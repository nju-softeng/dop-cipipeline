package com.example.agent.service;

import com.example.agent.po.AgentattributePO;
import com.example.agent.po.AgentmasterPO;
import com.example.agent.pojo.ResultMsg;
import com.example.agent.vo.AgentattributeVO;
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
import java.util.Date;
import java.util.List;

@Service
public class AgentService {

    @Autowired
    DataSource dataSource;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ServerDetailService serverDetailService;

    @Value("${server.port}")
    Integer agent_port;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

//    @Autowired
//    public AgentService() {
//
//        ComboPooledDataSource dataSource=new ComboPooledDataSource();
//        try {
//            dataSource.setDriverClass("com.mysql.jdbc.Driver");
//        } catch (PropertyVetoException e) {
//            throw new RuntimeException(e);
//        }
//        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/agent");
//        dataSource.setUser("root");
//        dataSource.setPassword("root");
//        JdbcTemplate jdbcTemplate=new JdbcTemplate();
//        //设置数据源对象，知道数据库在哪
//        jdbcTemplate.setDataSource(dataSource);
//    }

    public List<Integer> getslaveidsBymasterId(int masterid){
        logger.info("[getslaveidsBymasterId] masterid={}",masterid);
        String sql="select * from agentmaster where agent_master = ?";
        List<AgentmasterPO> agentmasterPOS=jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(AgentmasterPO.class),masterid);
        List<Integer> slaveids = new ArrayList<>();
        for(AgentmasterPO po:agentmasterPOS){
            slaveids.add(po.getAgent_id());
        }
        return slaveids;
    }

    public AgentattributeVO registAgent(int agentid, String agentName, String masterIp, int masterPort, String slaveIp){
        logger.info("[registAgent] agentid={} agentName={} masterIp={} masterPort={} slaveIp={}",agentid,agentName,masterIp,masterPort,slaveIp);
        String insertSql = "INSERT INTO slave_agent (agent_id,slave_ip, master_ip, master_port) VALUES (?,?, ?, ?)";
        int affectrow=jdbcTemplate.update(insertSql,agentid,slaveIp,masterIp,masterPort);
        if(affectrow>0){
            String server_os=serverDetailService.getOS();
            int server_memory=serverDetailService.getMemory();
            String server_mac=serverDetailService.getLocalMac();
            String server_cpu=serverDetailService.getCPU();
            String server_ip=serverDetailService.getIP();
            SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date=sdf.format(System.currentTimeMillis());
            AgentattributeVO agentattributeVO=new AgentattributeVO(agentid,agentName,server_os,server_memory,server_cpu,0,server_mac,1,slaveIp,agent_port,date);
            return agentattributeVO;
        }
        return null;
    }

    public AgentattributePO getAgentattributeByid(int agentid){
        logger.info("[getAgentattributeByid] agentid={}",agentid);
        String sql="select * from agentattribute where agent_id = ?";
        AgentattributePO agentattributePO=jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<>(AgentattributePO.class),agentid);
        return agentattributePO;
    }

    public AgentattributePO getthisAgent(){
        logger.info("[getthisAgent]");
        String mac=serverDetailService.getLocalMac();
        String sql="select * from agentattribute where agent_mac = ?";
        AgentattributePO agentattributePO=jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<>(AgentattributePO.class),mac);
        return agentattributePO;
    }

    public int getThisAgentId(){
        logger.info("[getThisAgentId]");
        String latestIdSql="select max(agent_id) from agentattribute";
        int latestId=jdbcTemplate.queryForObject(latestIdSql,Integer.class);
        return latestId;
    }


    public List<AgentattributePO> getslavesBymasterid(int masterid){
        return null;
    }

    public int createAgentBymaster(int masterid,String name){
        logger.info("[createAgentBymaster] masterid={} name={}",masterid,name);
        String insertsql="insert into agentattribute(agent_name,agent_os,agent_memory,agent_cpu,agent_state,agent_mac,agent_type) values (?,null,null,null,null,null,null)";
        int affectrow=jdbcTemplate.update(insertsql,name);
        String findPriKey="select agent_id from agentattribute where agent_name=?";
        int agent_id=jdbcTemplate.queryForObject(findPriKey,Integer.class,name);
//        System.out.println("新agent主键是"+agent_id);
        String createShipSql="insert into agentmaster(agent_id,agent_master) values(?,?)";
        int res=jdbcTemplate.update(createShipSql,agent_id,masterid);


        return res;
    }

    public ResultMsg createMasterAgent(String name){
        logger.info("[createMasterAgent] name={}",name);
        String insertsql="insert into agentattribute(agent_name,agent_os,agent_memory,agent_cpu,agent_state,agent_mac,agent_type,agent_ip,agent_port,agent_online_time) values (?,null,null,null,null,null,null,null,null,null)";
        int affectrow=jdbcTemplate.update(insertsql,name);
        return null;
    }

    public ResultMsg changeAgentState(int agentid,int state){
        logger.info("[changeAgentState] agentid={} state={}",agentid,state);
        String sql="update agentattribute set agent_state = ? where agent_id = ?";
        int res=jdbcTemplate.update(sql,state,agentid);
        return null;
    }
}
