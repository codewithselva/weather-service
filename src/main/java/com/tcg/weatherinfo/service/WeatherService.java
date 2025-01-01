package com.tcg.weatherinfo.service;

import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;

import com.tcg.weatherinfo.dto.WeatherResponseDTO;
import com.tcg.weatherinfo.entity.User;
import com.tcg.weatherinfo.entity.WeatherData;
import com.tcg.weatherinfo.exception.InvalidPostalCodeException;
import com.tcg.weatherinfo.exception.UserNotFoundException;
import com.tcg.weatherinfo.exception.WeatherServiceException;
import com.tcg.weatherinfo.integration.OpenWeatherMapClient;
import com.tcg.weatherinfo.integration.dto.WeatherApiResponse;
import com.tcg.weatherinfo.integration.mapper.WeatherMapper;
import com.tcg.weatherinfo.repository.UserRepository;
import com.tcg.weatherinfo.repository.WeatherDataRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class WeatherService {
	private final WeatherDataRepository weatherDataRepository;
	private final UserRepository userRepository;
	private final OpenWeatherMapClient weatherClient;
	private final WeatherMapper weatherMapper;

	@Value("${weather.api.key}")
	private String apiKey;

	@Value("${weather.api.baseUrl}")
	private String baseUrl;

	String zipCodePattern = "^\\d{5}(?:[-\\s]\\d{4})?$";
	Pattern pattern = Pattern.compile(zipCodePattern);

	@Transactional
	@Async
	public CompletableFuture<WeatherResponseDTO> getWeatherData(String postalCode, String username) {
		log.info("Fetching weather data for postal code: {}", postalCode);
		System.out.println("postal code: {}" + postalCode);
		final String finalPostalCode = postalCode.replaceAll("^\"|\"$", ""); // Remove leading and trailing quotes

		return CompletableFuture.supplyAsync(() -> {
			if (!pattern.matcher(finalPostalCode).matches()) {
				throw new InvalidPostalCodeException("Invalid US postal code format: " + finalPostalCode);
			}

			User user = userRepository.findByUsername(username)
					.orElseThrow(() -> new UserNotFoundException("User not found: " + username));

			if (!user.isActive()) {
				throw new WeatherServiceException("User account is not active");
			}

			try {
				// Get weather data from API
				WeatherApiResponse apiResponse = weatherClient.getWeatherByPostalCode(finalPostalCode);

				// Convert to domain model
				WeatherData weatherData = weatherMapper.toWeatherData(apiResponse, finalPostalCode);
				weatherData.setUser(user);
				weatherData.setRequestTimestamp(LocalDateTime.now());

				// Save to database
				weatherData = weatherDataRepository.save(weatherData);

				// Convert to response DTO
				return weatherMapper.toResponseDTO(weatherData);
			} catch (HttpClientErrorException.Unauthorized ex) {
				log.error("Invalid API Key: ", ex);
				throw new WeatherServiceException("Invalid API key", ex);
			} catch (Exception e) {
				log.error("Error fetching weather data: ", e);
				throw new WeatherServiceException("Error fetching weather data: " + e.getMessage(), e);
			}
		}).exceptionally(ex -> {
			Throwable cause = ex.getCause();
			if (cause instanceof InvalidPostalCodeException || cause instanceof UserNotFoundException
					|| cause instanceof WeatherServiceException) {
				throw new CompletionException(cause);
			} else {
				log.error("Unexpected error occurred: ", ex);
				throw new WeatherServiceException("Unexpected error fetching weather data: " + ex.getMessage(), ex);
			}
		});
	}

	public CompletableFuture<List<WeatherResponseDTO>> getWeatherHistory(String postalCode, String username) {
		final String finalPostalCode = postalCode.replaceAll("^\"|\"$", "");
		return CompletableFuture.supplyAsync(() -> {
			if (!StringUtils.hasLength(finalPostalCode) || !(pattern.matcher(finalPostalCode).matches())) {
				throw new IllegalArgumentException("Invalid US postal code format: " + finalPostalCode);
			}
			User user = null;
			if (StringUtils.hasLength(username)) {
				user = userRepository.findByUsername(username)
						.orElseThrow(() -> new UserNotFoundException("User not found: " + username));
			}
			List<WeatherResponseDTO> history;
			if (ObjectUtils.isEmpty(user)) {
				history = weatherDataRepository.findByPostalCodeOrderByRequestTimestampDesc(
						finalPostalCode).stream()
						.map(this::mapToWeatherResponseDTO).toList();
			} else {
				history = weatherDataRepository
						.findByPostalCodeAndUserIdOrderByRequestTimestampDesc(
								finalPostalCode, user.getId())
						.stream()
						.map(this::mapToWeatherResponseDTO).toList();
			}
			return history;
		}).exceptionally(ex -> {
			// Handle exception and return an empty list or a custom error message
			// Log the exception for debugging purposes
			log.error("Error occurred: " + ex.getMessage());
			// Alternatively, you can throw a custom exception or return a default value
			throw new WeatherServiceException("Failed to fetch weather history: " + ex.getMessage(), ex);
		});
	}

	private WeatherResponseDTO mapToWeatherResponseDTO(WeatherData data) {
		WeatherResponseDTO dto = new WeatherResponseDTO();
		dto.setPostalCode(data.getPostalCode());
		dto.setTemperature(data.getTemperature());
		dto.setHumidity(data.getHumidity());
		dto.setWeatherCondition(data.getWeatherCondition());
		dto.setTimestamp(data.getRequestTimestamp());
		dto.setUsername(data.getUser().getUsername());
		return dto;
	}
}