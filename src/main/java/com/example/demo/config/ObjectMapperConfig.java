package com.example.demo.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.blackbird.BlackbirdModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper
                .registerModule(hibernateModule())
                .registerModule(new JavaTimeModule())
                .registerModule(new BlackbirdModule())
//                .registerModule(new JSR310Module())
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    public Hibernate6Module hibernateModule() {
        Hibernate6Module module = new Hibernate6Module();
        module.disable(Hibernate6Module.Feature.USE_TRANSIENT_ANNOTATION);
        module.enable(Hibernate6Module.Feature.FORCE_LAZY_LOADING);
        module.enable(Hibernate6Module.Feature.REPLACE_PERSISTENT_COLLECTIONS);
        return module;
    }
}
