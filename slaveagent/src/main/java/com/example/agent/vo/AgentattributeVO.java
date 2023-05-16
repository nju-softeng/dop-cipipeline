package com.example.agent.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentattributeVO {
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
