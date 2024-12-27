package com.tcg.weatherinfo.repository;

public interface WeatherStatistics {
    Double getAvgTemp();
    Double getMaxTemp();
    Double getMinTemp();
    Double getAvgHumidity();
}