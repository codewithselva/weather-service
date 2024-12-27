package com.tcg.weatherinfo.integration.mapper;

import org.springframework.stereotype.Component;

import com.tcg.weatherinfo.dto.WeatherResponseDTO;
import com.tcg.weatherinfo.entity.WeatherData;
import com.tcg.weatherinfo.integration.dto.WeatherApiResponse;

@Component
public class WeatherMapper {
    
    public WeatherData toWeatherData(WeatherApiResponse response, String postalCode) {
        return WeatherData.builder()
            .postalCode(postalCode)
            .temperature(response.getMain().getTemp())
            .humidity(response.getMain().getHumidity())
            .weatherCondition(response.getWeather().get(0).getMain())
            .build();
    }

    public WeatherResponseDTO toResponseDTO(WeatherData weatherData) {
        return WeatherResponseDTO.builder()
            .postalCode(weatherData.getPostalCode())
            .temperature(weatherData.getTemperature())
            .humidity(weatherData.getHumidity())
            .weatherCondition(weatherData.getWeatherCondition())
            .timestamp(weatherData.getRequestTimestamp())
            .username(weatherData.getUser().getUsername())
            .build();
    }
}