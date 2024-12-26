package com.tcg.weatherinfo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tcg.weatherinfo.dto.WeatherResponseDTO;
import com.tcg.weatherinfo.entity.User;
import com.tcg.weatherinfo.entity.WeatherData;
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

	@Transactional
	public WeatherResponseDTO getWeatherData(String postalCode, String username) {
		log.info("Fetching weather data for postal code: {}", postalCode);

		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User not found: " + username));

		if (!user.isActive()) {
			throw new WeatherServiceException("User account is not active");
		}

		try {// Get weather data from API
			WeatherApiResponse apiResponse = weatherClient.getWeatherByPostalCode(postalCode);

			// Convert to domain model
			WeatherData weatherData = weatherMapper.toWeatherData(apiResponse, postalCode);
			weatherData.setUser(user);
			weatherData.setRequestTimestamp(LocalDateTime.now());

			// Save to database
			weatherData = weatherDataRepository.save(weatherData);

			// Convert to response DTO
			return weatherMapper.toResponseDTO(weatherData);
		} catch (Exception e) {
			log.error("Error fetching weather data: ", e);
			throw new WeatherServiceException("Error fetching weather data: " + e.getMessage());
		}
	}

	public List<WeatherResponseDTO> getWeatherHistory(String postalCode, String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UserNotFoundException("User not found: " + username));

		return weatherDataRepository.findByPostalCodeAndUserIdOrderByRequestTimestampDesc(postalCode, user.getId())
				.stream().map(this::mapToWeatherResponseDTO).toList();
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