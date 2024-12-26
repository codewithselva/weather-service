package com.tcg.weatherinfo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
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

	@Min(100)
	private int connectTimeout;

	@Min(100)
	private int readTimeout;

	@Min(1)
	private int retryAttempts;
}
