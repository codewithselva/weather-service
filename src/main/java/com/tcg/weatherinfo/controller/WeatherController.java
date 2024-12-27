package com.tcg.weatherinfo.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.weatherinfo.dto.WeatherRequestDTO;
import com.tcg.weatherinfo.dto.WeatherResponseDTO;
import com.tcg.weatherinfo.service.WeatherService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
@Tag(name = "Weather API", description = "Endpoints for weather data operations")
@Slf4j
public class WeatherController {
	private final WeatherService weatherService;

	@Operation(summary = "Get current weather by postal code", description = "Retrieves current weather data for a given US postal code")
	@ApiResponse(responseCode = "200", description = "Weather data retrieved successfully")
	@ApiResponse(responseCode = "400", description = "Invalid postal code format")
	@ApiResponse(responseCode = "403", description = "User not active or unauthorized")
	@PostMapping("/current")
	public ResponseEntity<WeatherResponseDTO> getCurrentWeather( @RequestBody WeatherRequestDTO request) {
		log.info("Request Body: "+request.toString());
		WeatherResponseDTO response = weatherService.getWeatherData(request.getPostalCode(), request.getUsername());
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "Get weather history", description = "Retrieves weather history for a given postal code")
	@GetMapping("/history/{postalCode}/{username}")
	public ResponseEntity<List<WeatherResponseDTO>> getWeatherHistory(@PathVariable String postalCode,
			@PathVariable String username) {
		List<WeatherResponseDTO> history = weatherService.getWeatherHistory(postalCode, username);
		return ResponseEntity.ok(history);
	}

	@Operation(summary = "Get weather history", description = "Retrieves weather history for a given postal code")
	@GetMapping("/history/{postalCode}")
	public ResponseEntity<List<WeatherResponseDTO>> getWeatherHistoryNew(@PathVariable String postalCode,
			Principal principal) {
		String username = principal.getName();
		List<WeatherResponseDTO> history = weatherService.getWeatherHistory(postalCode, username);
		return ResponseEntity.ok(history);
	}
}
