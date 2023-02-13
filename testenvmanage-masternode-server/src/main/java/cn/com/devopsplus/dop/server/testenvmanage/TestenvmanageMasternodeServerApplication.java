package cn.com.devopsplus.dop.server.testenvmanage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableFeignClients
public class TestenvmanageMasternodeServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestenvmanageMasternodeServerApplication.class, args);
    }

}
