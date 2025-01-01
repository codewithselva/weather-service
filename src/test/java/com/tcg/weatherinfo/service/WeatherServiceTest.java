package com.tcg.weatherinfo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

import lombok.extern.slf4j.Slf4j;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class WeatherServiceTest {

    @Mock
    private WeatherDataRepository weatherDataRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OpenWeatherMapClient weatherClient;

    @Mock
    private WeatherMapper weatherMapper;

    @InjectMocks
    private WeatherService weatherService;

    private User user;
    private WeatherData weatherData;
    private WeatherResponseDTO weatherResponseDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");
        user.setActive(true);
        user.setId(1L);

        weatherData = new WeatherData();
        weatherData.setPostalCode("94041");
        weatherData.setTemperature(75.0);
        weatherData.setHumidity(50);
        weatherData.setWeatherCondition("Sunny");
        weatherData.setRequestTimestamp(LocalDateTime.now());
        weatherData.setUser(user);

        weatherResponseDTO = new WeatherResponseDTO();
        weatherResponseDTO.setPostalCode("94041");
        weatherResponseDTO.setTemperature(75.0);
    }

    @Test
    void testGetWeatherData_Valid() throws Exception {
        log.info("Executing Test Case : testGetWeatherData_Valid");
        WeatherApiResponse apiResponse = new WeatherApiResponse();
        when(userRepository.findByUsername(anyString())).thenReturn(java.util.Optional.of(user));
        when(weatherClient.getWeatherByPostalCode(anyString())).thenReturn(apiResponse);
        when(weatherMapper.toWeatherData(apiResponse, "94041")).thenReturn(weatherData);
        when(weatherDataRepository.save(weatherData)).thenReturn(weatherData);
        when(weatherMapper.toResponseDTO(weatherData)).thenReturn(weatherResponseDTO);

        CompletableFuture<WeatherResponseDTO> response = weatherService.getWeatherData("94041", "testuser");
        assertEquals("94041", response.get().getPostalCode());
        verify(weatherDataRepository, times(1)).save(weatherData);
        log.info("Execution completed : testGetWeatherData_Valid");
    }

    @Test
    void testGetWeatherData_InvalidPostalCode() {
        CompletableFuture<WeatherResponseDTO> future = weatherService.getWeatherData("invalid", "testuser");
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertTrue(exception.getCause() instanceof InvalidPostalCodeException);
    }

    @Test
    void testGetWeatherData_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(java.util.Optional.empty());
        CompletableFuture<WeatherResponseDTO> future = weatherService.getWeatherData("94041", "unknown");
        ExecutionException exception = assertThrows(ExecutionException.class, future::get);
        assertTrue(exception.getCause() instanceof UserNotFoundException);
    }

    @Test
    void testGetWeatherHistory_Valid() throws Exception {
        when(userRepository.findByUsername(anyString())).thenReturn(java.util.Optional.of(user));
        when(weatherDataRepository.findByPostalCodeAndUserIdOrderByRequestTimestampDesc(anyString(), anyLong()))
                .thenReturn(List.of(weatherData));

        CompletableFuture<List<WeatherResponseDTO>> response = weatherService.getWeatherHistory("94041", "testuser");
        assertEquals(1, response.get().size());
        verify(weatherDataRepository, times(1)).findByPostalCodeAndUserIdOrderByRequestTimestampDesc("94041",
                user.getId());
    }

    @Test
    void testGetWeatherHistory_InvalidPostalCode() {
        CompletionException exception = assertThrows(CompletionException.class, () -> {
            weatherService.getWeatherHistory("invalid", "testuser").join();
        });

        // Traverse through the nested exceptions
        Throwable cause = exception.getCause();
        while (cause != null && !(cause instanceof IllegalArgumentException)) {
            cause = cause.getCause();
        }

        // Assert that we found an IllegalArgumentException
        assertTrue(cause instanceof IllegalArgumentException, "Expected cause to be IllegalArgumentException");
    }

    @Test
    void testGetWeatherHistory_UserNotFound() {
        CompletionException exception = assertThrows(CompletionException.class, () -> {
            weatherService.getWeatherHistory("94041", "unknown").join();
        });

        // Traverse through the nested exceptions
        Throwable cause = exception.getCause();
        while (cause != null && !(cause instanceof UserNotFoundException)) {
            cause = cause.getCause();
        }

        // Assert that we found an IllegalArgumentException
        assertTrue(cause instanceof UserNotFoundException, "Expected cause to be UserNotFoundException");
    }

    @Test
    void testGetWeatherHistory_NoUser() throws Exception {
        when(weatherDataRepository.findByPostalCodeOrderByRequestTimestampDesc(anyString()))
                .thenReturn(List.of(weatherData));

        CompletableFuture<List<WeatherResponseDTO>> response = weatherService.getWeatherHistory("94041", null);
        assertEquals(1, response.get().size());
        verify(weatherDataRepository, times(1)).findByPostalCodeOrderByRequestTimestampDesc("94041");
    }
}
