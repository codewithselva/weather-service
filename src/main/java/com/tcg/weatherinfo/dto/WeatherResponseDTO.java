package com.tcg.weatherinfo.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class WeatherResponseDTO {
    private String postalCode;
    private double temperature;
    private double humidity;
    private String weatherCondition;
    private LocalDateTime timestamp;
    private String username;
	public WeatherResponseDTO(String postalCode, double temperature) {
		this.postalCode = postalCode;
		this.temperature = temperature;
	}
    
    
}