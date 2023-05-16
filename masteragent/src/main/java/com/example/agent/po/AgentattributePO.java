package com.example.agent.po;

import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentattributePO implements Serializable {
    Integer agent_id;

    String agent_name;

    String agent_os;

    Integer agent_memory;

    String agent_cpu;

    Integer agent_state;

    String agent_mac;

    Integer agent_type;

    String agent_ip;

    Integer agent_port;

    String agent_online_time;

    public static AgentattributePO fromJson(JSONObject json) {
        Integer agent_id = json.getInteger("agent_id");
        String agent_name = json.getString("agent_name");
        String agent_os = json.getString("agent_os");
        Integer agent_memory = json.getInteger("agent_memory");
        String agent_cpu = json.getString("agent_cpu");
        Integer agent_state = json.getInteger("agent_state");
        String agent_mac = json.getString("agent_mac");
        Integer agent_type = json.getInteger("agent_type");
        String agent_ip = json.getString("agent_ip");
        Integer agent_port = json.getInteger("agent_port");
        String agent_online_time = json.getString("agent_online_time");

        return new AgentattributePO(agent_id, agent_name, agent_os, agent_memory, agent_cpu, agent_state, agent_mac,
                agent_type, agent_ip, agent_port, agent_online_time);
    }

}
