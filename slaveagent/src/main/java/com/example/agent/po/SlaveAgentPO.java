package com.example.agent.po;

import lombok.Data;

@Data
public class SlaveAgentPO {
    int agent_id;

    String slave_ip;

    String master_ip;

    int master_port;
}
