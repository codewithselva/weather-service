package com.tcg.weatherinfo.integration.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WeatherApiResponse {
    private Coord coord;
    private List<WeatherDescription> weather;
    private String base;
    private MainData main;
    private int visibility;
    private Wind wind;
    private Clouds clouds;
    private long dt;
    private Sys sys;
    private int timezone;
    private int id;
    private String name;
    @JsonProperty("cod")
    private int statusCode;
    
    // Constructor with all arguments
    public WeatherApiResponse(Coord coord, List<WeatherDescription> weather, String base, MainData main, int visibility, Wind wind, Clouds clouds, long dt, Sys sys, int timezone, int id, String name, int statusCode) {
        this.coord = coord;
        this.weather = weather;
        this.base = base;
        this.main = main;
        this.visibility = visibility;
        this.wind = wind;
        this.clouds = clouds;
        this.dt = dt;
        this.sys = sys;
        this.timezone = timezone;
        this.id = id;
        this.name = name;
        this.statusCode = statusCode;
    }
}
