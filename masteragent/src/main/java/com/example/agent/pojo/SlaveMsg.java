package com.example.agent.pojo;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SlaveMsg {
    int agent_id;

    String agent_name;

    int agent_os;

    int agent_memory;

    String agent_cpu;

    int agent_state;

    String agent_mac;

    int agent_type;
}
