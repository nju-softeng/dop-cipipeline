DROP TABLE IF EXISTS agentattribute;

CREATE TABLE agentattribute
(
    agent_id BIGINT(20) AUTO_INCREMENT COMMENT '主键ID',
    agent_name VARCHAR(30) NULL DEFAULT NULL COMMENT '测试节点名称',
    agent_os VARCHAR(30) NULL DEFAULT NULL COMMENT '测试节点操作系统类型',
    agent_memory INT(11) NULL DEFAULT NULL COMMENT '测试机器的内存大小',
    agent_cpu VARCHAR(30) NULL DEFAULT NULL COMMENT '测试节点机器的cpu类型',
    agent_state INT(11) NULL DEFAULT NULL COMMENT '测试节点状态，0表示不可用，1表示空闲等待运行，2表示正在运行）',
    agent_mac VARCHAR(255) NULL DEFAULT NULL COMMENT '测试节点的mac地址',
    agent_type INT(11) NULL DEFAULT NULL COMMENT '测试节点是主节点还是从节点（0表示主，1表示从)',
    agent_ip  VARCHAR(255) NULL DEFAULT NULL COMMENT '测试节点的ip地址',
    agent_port  INT(11) NULL DEFAULT NULL COMMENT '测试节点的监听端口',
    agent_online_time VARCHAR(255) NUll DEFAULT NULL COMMENT '节点上次从更新的时刻',
    PRIMARY KEY (agent_id)
);

