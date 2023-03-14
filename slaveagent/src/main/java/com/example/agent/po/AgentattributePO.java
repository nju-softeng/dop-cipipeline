package com.example.agent.po;

import lombok.Data;
import org.springframework.context.annotation.Bean;

import java.util.Date;

@Data
public class AgentattributePO {
    int agent_id;

    String agent_name;

    String agent_os;

    int agent_memory;

    String agent_cpu;

    int agent_state;

    String agent_mac;

    int agent_type;

    String agent_ip;

    int agent_port;

    String agent_online_time;


}
