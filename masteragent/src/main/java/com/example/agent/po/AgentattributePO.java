package com.example.agent.po;

import lombok.Data;
import org.springframework.context.annotation.Bean;

import java.io.Serializable;

@Data
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


}
