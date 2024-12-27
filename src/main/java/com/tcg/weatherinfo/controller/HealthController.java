package com.tcg.weatherinfo.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health Check", description = "Endpoint for application health monitoring")
@Slf4j
public class HealthController {

    @Operation(summary = "Check application health",
            description = "Returns the current status of the application")
    @GetMapping
    public ResponseEntity<Map<String, String>> healthCheck() {
    	log.info("Health Check ==> START!");
        Map<String, String> status = Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now().toString()
        );
        log.info("Health Check ==> END!");
        return ResponseEntity.ok(status);
    }
}
