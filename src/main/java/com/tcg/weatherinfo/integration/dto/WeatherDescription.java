package com.tcg.weatherinfo.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeatherDescription {
    private int id;
    private String main;
    private String description;
    private String icon;
}