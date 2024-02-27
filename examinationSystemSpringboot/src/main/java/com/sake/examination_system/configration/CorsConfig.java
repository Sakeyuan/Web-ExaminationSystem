package com.sake.examination_system.configration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry){
        registry.addMapping("/**")
                .allowedOriginPatterns("*") // 或者具体的域名，例如："https://example.com"
                .allowedMethods("*")
                .maxAge(168000)
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}

