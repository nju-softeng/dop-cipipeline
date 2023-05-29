package com.artdev.config;

import com.artdev.analyzers.checkstyle.CheckStyleAnalyzer;
import com.artdev.analyzers.pylint.PylintAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public PylintAnalyzer pylintAnalyzer(){
        return new PylintAnalyzer();
    }

    @Bean
    public CheckStyleAnalyzer checkStyleAnalyzer(){
        return new CheckStyleAnalyzer();
    }
}
