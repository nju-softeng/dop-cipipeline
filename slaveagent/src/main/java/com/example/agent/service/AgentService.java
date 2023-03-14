package com.example.agent.service;

import com.example.agent.po.AgentattributePO;
import com.example.agent.po.AgentmasterPO;
import com.example.agent.pojo.ResultMsg;
import org.springframework.beans.factory.annotation.Autowired;
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

        String sql="select * from agentmaster where agent_master = ?";
        List<AgentmasterPO> agentmasterPOS=jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(AgentmasterPO.class),masterid);
        List<Integer> slaveids = new ArrayList<>();
        for(AgentmasterPO po:agentmasterPOS){
            slaveids.add(po.getAgent_id());
        }
        return slaveids;
    }

    public ResultMsg registAgent(int agentid){
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
        String sql="select * from agentattribute where agent_id = ?";
        AgentattributePO agentattributePO=jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<>(AgentattributePO.class),agentid);
        return agentattributePO;
    }

    public AgentattributePO getthisAgent(){
        String mac=serverDetailService.getLocalMac();
        String sql="select * from agentattribute where agent_mac = ?";
        AgentattributePO agentattributePO=jdbcTemplate.queryForObject(sql,new BeanPropertyRowMapper<>(AgentattributePO.class),mac);
        return agentattributePO;
    }


    public List<AgentattributePO> getslavesBymasterid(int masterid){
        return null;
    }

    public int createAgentBymaster(int masterid,String name){
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
        String insertsql="insert into agentattribute(agent_name,agent_os,agent_memory,agent_cpu,agent_state,agent_mac,agent_type,agent_ip,agent_port,agent_online_time) values (?,null,null,null,null,null,null,null,null,null)";
        int affectrow=jdbcTemplate.update(insertsql,name);
        return null;
    }

    public ResultMsg changeAgentState(int agentid,int state){
        String sql="update agentattribute set agent_state = ? where agent_id = ?";
        int res=jdbcTemplate.update(sql,state,agentid);
        return null;

    }
}
