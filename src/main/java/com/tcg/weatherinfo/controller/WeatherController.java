package com.tcg.weatherinfo.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tcg.weatherinfo.dto.WeatherRequestDTO;
import com.tcg.weatherinfo.dto.WeatherResponseDTO;
import com.tcg.weatherinfo.exception.ErrorResponse;
import com.tcg.weatherinfo.exception.InvalidPostalCodeException;
import com.tcg.weatherinfo.exception.UserNotFoundException;
import com.tcg.weatherinfo.exception.WeatherServiceException;
import com.tcg.weatherinfo.service.WeatherService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
	@ApiResponse(responseCode = "401", description = "Unauthorized access - Invalid API Key")
	@ApiResponse(responseCode = "403", description = "User not active or unauthorized")
	@PostMapping("/current")
	public CompletableFuture<ResponseEntity<?>> getWeatherData(@RequestBody WeatherRequestDTO requestDTO) {
		return weatherService.getWeatherData(
				requestDTO.getPostalCode(), requestDTO.getUsername())
				.<ResponseEntity<?>>thenApply(ResponseEntity::ok)
				.exceptionally(ex -> {
					Throwable cause = ex.getCause();
					if (cause instanceof InvalidPostalCodeException) {
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
								new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid Postal Code",
										cause.getMessage(), LocalDateTime.now()));
					} else if (cause instanceof UserNotFoundException) {
						return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
								new ErrorResponse(HttpStatus.NOT_FOUND.value(), "User Not Found", cause.getMessage(),
										LocalDateTime.now()));
					} else if (cause instanceof WeatherServiceException
							&& "User account is not active".equals(cause.getMessage())) {
						return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
								new ErrorResponse(HttpStatus.FORBIDDEN.value(), "User Not Active", cause.getMessage(),
										LocalDateTime.now()));
					} else if (cause instanceof WeatherServiceException
							&& "Invalid API key".equals(cause.getMessage())) {
						return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
								.body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
										"Unauthorized Access - Invalid API Key", cause.getMessage(),
										LocalDateTime.now()));
					} else {
						return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
								new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
										"An unexpected error occurred", LocalDateTime.now()));
					}
				});
	}

	@Operation(summary = "Get weather history", description = "Retrieves weather history for a given postal code")
	@GetMapping("/history")
	public CompletableFuture<ResponseEntity<List<WeatherResponseDTO>>> getWeatherHistory(
			@RequestParam String postalCode, @RequestParam String username) {
		return weatherService.getWeatherHistory(postalCode, username).thenApply(ResponseEntity::ok);
	}
}
