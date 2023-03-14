package com.example.agent;

import com.example.agent.util.BeanUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AgentApplication {

	public static void main(String[] args) {

		SpringApplication.run(AgentApplication.class, args);
	}

	@Bean
	public BeanUtil beanUtil() {
		return new BeanUtil();
	}

}
