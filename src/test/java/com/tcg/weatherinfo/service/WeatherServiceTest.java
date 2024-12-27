package com.tcg.weatherinfo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
        
        weatherResponseDTO= new WeatherResponseDTO();
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
        assertThrows(InvalidPostalCodeException.class, () -> {
            weatherService.getWeatherData("invalid", "testuser");
        });
    }

    @Test
    void testGetWeatherData_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(java.util.Optional.empty());
        assertThrows(UserNotFoundException.class, () -> {
            weatherService.getWeatherData("94041", "unknown");
        });
    }

    @Test
    void testGetWeatherHistory_Valid() throws Exception {
        when(userRepository.findByUsername(anyString())).thenReturn(java.util.Optional.of(user));
        when(weatherDataRepository.findByPostalCodeAndUserIdOrderByRequestTimestampDesc(anyString(),anyLong()))
                .thenReturn(List.of(weatherData));

        CompletableFuture<List<WeatherResponseDTO>> response = weatherService.getWeatherHistory("94041", "testuser");
        assertEquals(1, response.get().size());
        verify(weatherDataRepository, times(1)).findByPostalCodeAndUserIdOrderByRequestTimestampDesc("94041", user.getId());
    }

    @Test
    void testGetWeatherHistory_InvalidPostalCode() {
        assertThrows(IllegalArgumentException.class, () -> {
            weatherService.getWeatherHistory("invalid", "testuser");
        });
    }

    @Test
    void testGetWeatherHistory_UserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(java.util.Optional.empty());
        assertThrows(UserNotFoundException.class, () -> {
            weatherService.getWeatherHistory("94041", "unknown");
        });
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
