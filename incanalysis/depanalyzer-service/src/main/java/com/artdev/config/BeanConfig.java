package com.artdev.config;

import com.artdev.analyzers.jdeps.JdepsAnalyzer;
import com.artdev.analyzers.pipreqs.PipreqsAnalyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public JdepsAnalyzer pylintAnalyzer(){
        return new JdepsAnalyzer();
    }

    @Bean
    public PipreqsAnalyzer checkStyleAnalyzer(){
        return new PipreqsAnalyzer();
    }
}
