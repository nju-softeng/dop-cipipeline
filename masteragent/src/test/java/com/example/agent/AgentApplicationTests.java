package com.example.agent;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.batch.BatchProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootTest
class AgentApplicationTests {
	@Autowired
	DataSource dataSource;
	@Autowired(required = false)
	JdbcTemplate jdbcTemplate;
	@Test
	void contextLoads() throws SQLException {
		System.out.println("默认数据源为"+dataSource.getClass());
		System.out.println("数据库连接实例"+dataSource.getConnection());
		Integer i=jdbcTemplate.queryForObject("select count(*) from agentmaster", Integer.class);
		System.out.println("agentmaster中共有"+i+"条数据");
	}

}
