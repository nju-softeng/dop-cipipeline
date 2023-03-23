package com.example.agent.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
@Component
@Configuration
@EnableAutoConfiguration
public class SocketConfig {
    //普通文件socket传输端口
    @Value("${filesystem.file.port}")
    private int FILEPORT;

    @Value("${filesystem.default.directory}")
    private String DEFAULTDIR;
    //压缩文件socket传输端口
    @Value("${filesystem.zipfile.port}")
    private int ZIPFILEPORT;

    @Bean(name = "serverFileSocket")
    public ServerSocket serverFileSocket() throws IOException {
        return new ServerSocket(FILEPORT);
    }

    @Bean(name = "serverZipSocket")
    public ServerSocket serverZipSocket() throws IOException {
//        System.out.println(ZIPFILEPORT);
        return new ServerSocket(ZIPFILEPORT);
    }
}