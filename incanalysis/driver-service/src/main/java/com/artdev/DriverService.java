package com.artdev;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class DriverService {
    public static void main(String[] args) {
        SpringApplication.run(DriverService.class,args);
    }
}
