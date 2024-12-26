package com.tcg.weatherinfo.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.tcg.weatherinfo.dto.WeatherResponseDTO;
import com.tcg.weatherinfo.service.WeatherService;

@ExtendWith(MockitoExtension.class)
class WeatherControllerTest {

	@Mock
	private WeatherService weatherService;

	@InjectMocks
	private WeatherController weatherController;

	private MockMvc mockMvc;
	// private UserDetails userDetails;

	@BeforeEach
	void setUpSecurityContext() {
		UserDetails userDetails = org.springframework.security.core.userdetails.User.builder().username("testuser")
				.password("password").roles("USER").build();

		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());

		SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
		securityContext.setAuthentication(authentication);
		SecurityContextHolder.setContext(securityContext);

		mockMvc = MockMvcBuilders.standaloneSetup(weatherController).build();
	}

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void testGetCurrentWeather() throws Exception {
		// Given
		WeatherResponseDTO responseDTO = new WeatherResponseDTO("Sunny", 75.0);

		when(weatherService.getWeatherData("94040", "testuser")).thenReturn(responseDTO);

		// When & Then
		mockMvc.perform(post("/api/v1/weather/current").contentType(MediaType.APPLICATION_JSON)
				.content("{\"postalCode\":\"94040\", \"username\":\"testuser\"}")).andExpect(status().isOk())
				.andExpect(jsonPath("$.weatherCondition").value("Sunny")).andExpect(jsonPath("$.temperature").value(75.0));

		// Verify the service method was called
		verify(weatherService, times(1)).getWeatherData("94040", "testuser");
	}

	@Test
	void testGetWeatherHistory() throws Exception {
		// Given
		WeatherResponseDTO responseDTO1 = new WeatherResponseDTO("Sunny", 75.0);
		WeatherResponseDTO responseDTO2 = new WeatherResponseDTO("Cloudy", 70.0);
		List<WeatherResponseDTO> history = List.of(responseDTO1, responseDTO2);

		when(weatherService.getWeatherHistory("94040", "testuser")).thenReturn(history);

		// When & Then
		mockMvc.perform(get("/api/v1/weather/history/94040/testuser")).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].weatherCondition").value("Sunny")).andExpect(jsonPath("$[1].weatherCondition").value("Cloudy"))
				.andExpect(jsonPath("$[0].temperature").value(75.0))
				.andExpect(jsonPath("$[1].temperature").value(70.0));

		// Verify the service method was called
		verify(weatherService, times(1)).getWeatherHistory("94040", "testuser");
	}

	@Test
	void testGetCurrentWeather_InvalidPostalCode() throws Exception {
		// Given
		// WeatherRequestDTO requestDTO = new WeatherRequestDTO("InvalidCode");

		// When & Then
		mockMvc.perform(post("/api/v1/weather/current").contentType(MediaType.APPLICATION_JSON)
				.content("{\"postalCode\":\"InvalidCode\"}").principal(() -> "testuser"))
				.andExpect(status().isBadRequest());

		// Verify that the service method was not called
		verify(weatherService, times(0)).getWeatherData("InvalidCode", "testuser");
	}

}
