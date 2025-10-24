package com.example.numberbaseball.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import org.springframework.util.StringUtils;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Value("${app.cors.allowed-origins:*}")
    private String corsAllowedOrigins;

    @Value("${app.cors.allowed-methods:GET,POST,PUT,DELETE,PATCH,OPTIONS}")
    private String corsAllowedMethods;

    @Value("${app.cors.allowed-headers:*}")
    private String corsAllowedHeaders;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(parseToArray(corsAllowedOrigins))
                .allowedMethods(parseToArray(corsAllowedMethods))
                .allowedHeaders(parseToArray(corsAllowedHeaders))
                .allowCredentials(false);
    }

    private String[] parseToArray(String raw) {
        if (!StringUtils.hasText(raw)) {
            return new String[0];
        }
        return StringUtils.tokenizeToStringArray(raw, ",", true, true);
    }
}
