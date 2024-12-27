package com.tcg.weatherinfo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tcg.weatherinfo.entity.WeatherData;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    // Find weather data by postal code
    List<WeatherData> findByPostalCodeOrderByRequestTimestampDesc(String postalCode);
    
    // Find weather data by postal code and user
    List<WeatherData> findByPostalCodeAndUserIdOrderByRequestTimestampDesc(String postalCode, Long userId);
    
}