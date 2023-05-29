package com.artdev.config;

import com.artdev.analyzers.errorprone.ErrorProneAnalyzer;
import com.artdev.analyzers.flake8.Flake8Analyzer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public ErrorProneAnalyzer errorProneAnalyzer(){return new ErrorProneAnalyzer();}

    @Bean
    public Flake8Analyzer flake8Analyzer(){return new Flake8Analyzer();}
}
