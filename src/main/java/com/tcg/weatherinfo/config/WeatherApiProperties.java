package com.tcg.weatherinfo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties(prefix = "weather.api")
@Validated
@Data
public class WeatherApiProperties {
    @NotBlank
    private String key;
    
    @NotBlank
    private String baseUrl;
    
    private int connectTimeout = 5000;
    private int readTimeout = 5000;
    private int retryAttempts = 3;
}

