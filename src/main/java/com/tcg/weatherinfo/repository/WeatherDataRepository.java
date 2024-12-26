package com.tcg.weatherinfo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tcg.weatherinfo.entity.WeatherData;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    // Find weather data by postal code
    List<WeatherData> findByPostalCodeOrderByRequestTimestampDesc(String postalCode);
    
    // Find weather data by user
    Page<WeatherData> findByUserIdOrderByRequestTimestampDesc(Long userId, Pageable pageable);
    
    // Find weather data by postal code and user
    List<WeatherData> findByPostalCodeAndUserIdOrderByRequestTimestampDesc(String postalCode, Long userId);
    
    // Find weather data within a date range
    @Query("SELECT w FROM WeatherData w WHERE w.requestTimestamp BETWEEN :startDate AND :endDate ORDER BY w.requestTimestamp DESC")
    List<WeatherData> findWeatherDataBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
    
    // Get latest weather data for a postal code
    @Query("SELECT w FROM WeatherData w WHERE w.postalCode = :postalCode ORDER BY w.requestTimestamp DESC LIMIT 1")
    Optional<WeatherData> findLatestByPostalCode(String postalCode);
    
    // Get weather statistics for a postal code
    @Query("SELECT AVG(w.temperature) as avgTemp, MAX(w.temperature) as maxTemp, " +
           "MIN(w.temperature) as minTemp, AVG(w.humidity) as avgHumidity " +
           "FROM WeatherData w WHERE w.postalCode = :postalCode AND " +
           "w.requestTimestamp >= :startDate")
    WeatherStatistics getWeatherStatistics(String postalCode, LocalDateTime startDate);
}