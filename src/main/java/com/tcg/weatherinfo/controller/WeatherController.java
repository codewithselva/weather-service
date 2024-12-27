package com.tcg.weatherinfo.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.weatherinfo.dto.WeatherRequestDTO;
import com.tcg.weatherinfo.dto.WeatherResponseDTO;
import com.tcg.weatherinfo.service.WeatherService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
@Tag(name = "Weather API", description = "Endpoints for weather data operations")
public class WeatherController {
	private final WeatherService weatherService;

	@Operation(summary = "Get current weather by postal code", description = "Retrieves current weather data for a given US postal code")
	@ApiResponse(responseCode = "200", description = "Weather data retrieved successfully")
	@ApiResponse(responseCode = "400", description = "Invalid postal code format")
	@ApiResponse(responseCode = "403", description = "User not active or unauthorized")
	@PostMapping("/current")
	public CompletableFuture<ResponseEntity<WeatherResponseDTO>> getCurrentWeather(
			@Valid @RequestBody WeatherRequestDTO request) {
		return weatherService.getWeatherData(request.getPostalCode(), request.getUsername())
				.thenApply(ResponseEntity::ok);
	}

	@Operation(summary = "Get weather history", description = "Retrieves weather history for a given postal code")
	@GetMapping("/history")
	public CompletableFuture<ResponseEntity<List<WeatherResponseDTO>>> getWeatherHistory(
			@RequestParam String postalCode, @RequestParam String username) {
		return weatherService.getWeatherHistory(postalCode, username).thenApply(ResponseEntity::ok);
	}
}
