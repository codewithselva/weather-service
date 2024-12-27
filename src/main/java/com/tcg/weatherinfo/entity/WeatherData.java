package com.tcg.weatherinfo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
@Entity
@Table(name = "weather_data")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String postalCode;

    @Column(nullable = false)
    private double temperature;

    private double humidity;

    private String weatherCondition;

    @Column(name = "request_timestamp")
    private LocalDateTime requestTimestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist
    protected void onCreate() {
        requestTimestamp = LocalDateTime.now();
    }
}