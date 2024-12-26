package com.tcg.weatherinfo.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.tcg.weatherinfo.exception.WeatherServiceException;
import com.tcg.weatherinfo.integration.dto.Clouds;
import com.tcg.weatherinfo.integration.dto.MainData;
import com.tcg.weatherinfo.integration.dto.WeatherApiResponse;
import com.tcg.weatherinfo.integration.dto.WeatherDescription;
import com.tcg.weatherinfo.integration.dto.Wind;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class OpenWeatherMapClientTest {

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private OpenWeatherMapClient openWeatherMapClient;

	@Value("${weather.api.key}")
	private String apiKey;

	@Value("${weather.api.baseUrl}")
	private String baseUrl;

	private String postalCode;
	private String validUrl;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		postalCode = "94040";
		validUrl = String.format("%s?zip=%s,US&appid=%s&units=metric", baseUrl, postalCode, apiKey);
	}

	@Test
	void testGetWeatherByPostalCode_Success() {
		// Arrange
		WeatherApiResponse mockResponse = WeatherApiResponse.builder()
				.main(MainData.builder().temp(20.0).humidity(50.0).build())
				.weather(List.of(WeatherDescription.builder().main("Clear").description("clear sky").build()))
				.wind(Wind.builder().speed(5.0).build()).clouds(Clouds.builder().all(10).build()).name("Sample City")
				.statusCode(200).build();
		System.out.println("VALID URL ==> " + validUrl);

		// Mock the restTemplate behavior to return the mock response
		when(restTemplate.getForEntity(validUrl, WeatherApiResponse.class))
				.thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

		// Act
		WeatherApiResponse response = openWeatherMapClient.getWeatherByPostalCode(postalCode);

		System.out.println("response ==> " + response);

		// Assert
		assertNotNull(response);
		assertEquals(20.0, response.getMain().getTemp());
		assertEquals(50.0, response.getMain().getHumidity());
		verify(restTemplate, times(1)).getForEntity(validUrl, WeatherApiResponse.class);
	}

	@Test
	void testGetWeatherByPostalCode_NoResponseBody() {

		// Act & Assert
		WeatherServiceException exception = assertThrows(WeatherServiceException.class,
				() -> openWeatherMapClient.getWeatherByPostalCode(postalCode));

		assertEquals("Failed to fetch weather data: No response received from weather API", exception.getMessage());

	}

	@Test
	void testGetWeatherByPostalCode_ApiError() {

		// Act & Assert
		WeatherServiceException exception = assertThrows(WeatherServiceException.class,
				() -> openWeatherMapClient.getWeatherByPostalCode(postalCode));

		System.out.println(" exception.getMessage() ==> " + exception.getMessage());

		assertTrue(exception.getMessage().contains("Failed to fetch weather data"));
		assertTrue(exception.getMessage().contains("No response received from weather API"));
		// verify(restTemplate, times(1)).getForEntity(validUrl,
		// WeatherApiResponse.class);
	}
}
