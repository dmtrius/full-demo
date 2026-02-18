package com.example.demo.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class AppConfig {
    @Bean
    public JsonMapperBuilderCustomizer dateCustomizer() {
        return builder -> builder
                .defaultDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    }
}
