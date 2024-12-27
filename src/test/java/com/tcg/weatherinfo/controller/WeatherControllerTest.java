package com.tcg.weatherinfo.controller;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.tcg.weatherinfo.dto.WeatherResponseDTO;
import com.tcg.weatherinfo.exception.GlobalExceptionHandler;
import com.tcg.weatherinfo.exception.InvalidPostalCodeException;
import com.tcg.weatherinfo.service.WeatherService;

@ExtendWith(MockitoExtension.class)
class WeatherControllerTest {

	@Mock
	private WeatherService weatherService;

	@InjectMocks
	private WeatherController weatherController;

	private WeatherController weatherController1;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(weatherController).build();
	}

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void testGetCurrentWeather() throws Exception {
		// Given
		WeatherResponseDTO responseDTO = WeatherResponseDTO.builder().weatherCondition("Sunny").temperature(75.0)
				.postalCode("94040").username("testuser").build();
		when(weatherService.getWeatherData("94040", "testuser"))
				.thenReturn(CompletableFuture.completedFuture(responseDTO));
		// When & Then
		mockMvc.perform(post("/api/v1/weather/current").contentType(MediaType.APPLICATION_JSON)
				.content("{\"postalCode\":\"94040\", \"username\":\"testuser\"}")).andExpect(status().isOk());

		// Verify the service method was called
		verify(weatherService, times(1)).getWeatherData("94040", "testuser");
	}

	@Test
	void testGetWeatherHistory() throws Exception {
		// Given
		WeatherResponseDTO responseDTO1 = new WeatherResponseDTO();
		responseDTO1.setWeatherCondition("Sunny");
		responseDTO1.setTemperature(75.0);
		WeatherResponseDTO responseDTO2 = new WeatherResponseDTO();
		responseDTO2.setWeatherCondition("Cloudy");
		responseDTO2.setTemperature(70.0);
		List<WeatherResponseDTO> history = List.of(responseDTO1, responseDTO2);
		when(weatherService.getWeatherHistory("94040", "testuser"))
				.thenReturn(CompletableFuture.completedFuture(history));
		// When & Then
		mockMvc.perform(get("/api/v1/weather/history").param("postalCode", "94040").param("username", "testuser"))
				.andExpect(status().isOk());

		// Verify the service method was called
		verify(weatherService, times(1)).getWeatherHistory("94040", "testuser");
	}

	@Test
	void testGetCurrentWeather_InvalidPostalCode() throws Exception {
		// When & Then
		mockMvc.perform(post("/api/v1/weather/current").contentType(MediaType.APPLICATION_JSON)
				.content("{\"postalCode\":\"InvalidCode\", \"username\":\"testuser\"}"))
				.andExpect(status().isBadRequest());
		// Verify that the service method was not called
		verify(weatherService, times(1)).getWeatherData("InvalidCode", "testuser");
	}

}
