package com.personal.financialvault.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.type.LogicalType;

@Configuration
public class JacksonConfig {

//    @Bean
//    public ObjectMapper objectMapper() {
//        ObjectMapper mapper = new ObjectMapper();
//
//        // Reject String → Integer
//        // mapper.version(LogicalType.Integer).
//        mapper.coercionConfigFor(LogicalType.Integer)
//                .setCoercion(CoercionInputShape.String, CoercionAction.Fail);
//
//        // Reject String → Double
//        mapper.coercionConfigFor(LogicalType.Float)
//                .setCoercion(CoercionInputShape.String, CoercionAction.Fail);
//
//        return mapper;
//    }
}