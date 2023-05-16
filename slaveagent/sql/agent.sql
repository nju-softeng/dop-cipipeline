DROP TABLE IF EXISTS slave_agent;

create table slave_agent
(
    agent_id BIGINT(20) AUTO_INCREMENT COMMENT '从节点id',
    slave_ip VARCHAR(20) not null,
    master_ip VARCHAR(20) not null,
    master_port INT not null,
    PRIMARY KEY (agent_id)

);