package com.example.demo.config;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    public Jackson2ObjectMapperBuilderCustomizer dateCustomizer() {
        return builder -> builder
                .simpleDateFormat("yyyy-MM-dd")
                .failOnUnknownProperties(false);
    }
}
