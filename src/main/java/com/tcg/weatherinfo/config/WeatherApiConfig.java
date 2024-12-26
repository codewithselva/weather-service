package com.tcg.weatherinfo.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WeatherApiConfig {
    
    @Bean
    public RestTemplate weatherApiRestTemplate(WeatherApiProperties properties) {
        return new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofMillis(properties.getConnectTimeout()))
            .setReadTimeout(Duration.ofMillis(properties.getReadTimeout()))
            .build();
    }
}
