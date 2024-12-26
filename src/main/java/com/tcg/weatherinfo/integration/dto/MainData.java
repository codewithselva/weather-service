package com.tcg.weatherinfo.integration.dto;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MainData {
    private double temp;
    private double feelsLike;
    private double tempMin;
    private double tempMax;
    private double pressure;
    private double humidity;
    private double seaLevel;
    private double grndLevel;

    @JsonProperty("feels_like")
    public double getFeelsLike() {
        return feelsLike;
    }
    
    @JsonProperty("temp_min")
    public double getTempMin() {
        return tempMin;
    }
    
    @JsonProperty("temp_max")
    public double getTempMax() {
        return tempMax;
    }
    
    @JsonProperty("sea_level")
    public double getSeaLevel() {
        return seaLevel;
    }
    
    @JsonProperty("grnd_level")
    public double getGrndLevel() {
        return grndLevel;
    }
}