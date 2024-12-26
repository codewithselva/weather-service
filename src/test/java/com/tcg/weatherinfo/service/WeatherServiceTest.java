package com.tcg.weatherinfo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tcg.weatherinfo.dto.WeatherResponseDTO;
import com.tcg.weatherinfo.entity.User;
import com.tcg.weatherinfo.entity.WeatherData;
import com.tcg.weatherinfo.exception.UserNotFoundException;
import com.tcg.weatherinfo.exception.WeatherServiceException;
import com.tcg.weatherinfo.integration.OpenWeatherMapClient;
import com.tcg.weatherinfo.integration.dto.Clouds;
import com.tcg.weatherinfo.integration.dto.MainData;
import com.tcg.weatherinfo.integration.dto.WeatherApiResponse;
import com.tcg.weatherinfo.integration.dto.WeatherDescription;
import com.tcg.weatherinfo.integration.dto.Wind;
import com.tcg.weatherinfo.integration.mapper.WeatherMapper;
import com.tcg.weatherinfo.repository.UserRepository;
import com.tcg.weatherinfo.repository.WeatherDataRepository;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

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

	private User activeUser;
	private User inactiveUser;

	@BeforeEach
	void setUp() {
		activeUser = User.builder().id(1L).username("testuser").password("password") // Set a default password if needed
				.active(true).createdAt(LocalDateTime.now()) // You can either set this manually or rely on the
																// @PrePersist
				.build();

		inactiveUser = User.builder().id(2L).username("inactiveuser").password("password") // Set a default password if needed
				.active(false).createdAt(LocalDateTime.now()) // You can either set this manually or rely on the @PrePersist
				.build();
	}

	@Test
	void testGetWeatherData_Success() {
		// Given
		String postalCode = "94040";
		String username = "testuser";

		WeatherApiResponse apiResponse = WeatherApiResponse.builder()
				.main(MainData.builder().temp(72.5).humidity(65.0).build())
				.weather(List.of(WeatherDescription.builder().main("Sunny").description("clear sky").build()))
				.wind(Wind.builder().speed(5.0).build()).clouds(Clouds.builder().all(10).build()).name("Sample City")
				.statusCode(200).build();
		WeatherData weatherData = new WeatherData();
		weatherData.setPostalCode(postalCode);
		weatherData.setTemperature(72.5);
		weatherData.setHumidity(65);
		weatherData.setWeatherCondition("Sunny");
		weatherData.setRequestTimestamp(LocalDateTime.now());
		weatherData.setUser(activeUser);

		WeatherResponseDTO expectedResponseDTO = new WeatherResponseDTO("94040", 72.5, 65, "Sunny", LocalDateTime.now(),
				"testuser");

		when(userRepository.findByUsername(username)).thenReturn(Optional.of(activeUser));
		when(weatherClient.getWeatherByPostalCode(postalCode)).thenReturn(apiResponse);
		when(weatherMapper.toWeatherData(apiResponse, postalCode)).thenReturn(weatherData);
		when(weatherDataRepository.save(weatherData)).thenReturn(weatherData);
		when(weatherMapper.toResponseDTO(weatherData)).thenReturn(expectedResponseDTO);

		// When
		WeatherResponseDTO responseDTO = weatherService.getWeatherData(postalCode, username);

		// Then
		assertEquals(expectedResponseDTO.getPostalCode(), responseDTO.getPostalCode());
		assertEquals(expectedResponseDTO.getTemperature(), responseDTO.getTemperature());
		assertEquals(expectedResponseDTO.getHumidity(), responseDTO.getHumidity());
		assertEquals(expectedResponseDTO.getWeatherCondition(), responseDTO.getWeatherCondition());
		assertEquals(expectedResponseDTO.getUsername(), responseDTO.getUsername());

		verify(userRepository, times(1)).findByUsername(username);
		verify(weatherClient, times(1)).getWeatherByPostalCode(postalCode);
		verify(weatherDataRepository, times(1)).save(weatherData);
	}

	@Test
	void testGetWeatherData_UserNotFound() {
		// Given
		String postalCode = "94040";
		String username = "nonexistentuser";

		when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(UserNotFoundException.class, () -> weatherService.getWeatherData(postalCode, username));

		verify(userRepository, times(1)).findByUsername(username);
	}

	@Test
	void testGetWeatherData_UserInactive() {
		// Given
		String postalCode = "94040";
		String username = "inactiveuser";

		when(userRepository.findByUsername(username)).thenReturn(Optional.of(inactiveUser));

		// When & Then
		assertThrows(WeatherServiceException.class, () -> weatherService.getWeatherData(postalCode, username));

		verify(userRepository, times(1)).findByUsername(username);
	}

	@Test
	void testGetWeatherData_ApiFailure() {
		// Given
		String postalCode = "94040";
		String username = "testuser";

		when(userRepository.findByUsername(username)).thenReturn(Optional.of(activeUser));
		when(weatherClient.getWeatherByPostalCode(postalCode)).thenThrow(new RuntimeException("API Error"));

		// When & Then
		assertThrows(WeatherServiceException.class, () -> weatherService.getWeatherData(postalCode, username));

		verify(userRepository, times(1)).findByUsername(username);
		verify(weatherClient, times(1)).getWeatherByPostalCode(postalCode);
	}

	@Test
	void testGetWeatherHistory_Success() {
		// Given
		String postalCode = "94040";
		String username = "testuser";

		WeatherData weatherData1 = new WeatherData();
		weatherData1.setPostalCode(postalCode);
		weatherData1.setTemperature(72.5);
		weatherData1.setHumidity(65);
		weatherData1.setWeatherCondition("Sunny");
		weatherData1.setRequestTimestamp(LocalDateTime.now().minusDays(1));
		weatherData1.setUser(activeUser);

		WeatherData weatherData2 = new WeatherData();
		weatherData2.setPostalCode(postalCode);
		weatherData2.setTemperature(75.0);
		weatherData2.setHumidity(60);
		weatherData2.setWeatherCondition("Cloudy");
		weatherData2.setRequestTimestamp(LocalDateTime.now());
		weatherData2.setUser(activeUser);

		List<WeatherData> weatherDataList = List.of(weatherData1, weatherData2);

		when(userRepository.findByUsername(username)).thenReturn(Optional.of(activeUser));
		when(weatherDataRepository.findByPostalCodeAndUserIdOrderByRequestTimestampDesc(postalCode, activeUser.getId()))
				.thenReturn(weatherDataList);

		// When
		List<WeatherResponseDTO> responseDTOList = weatherService.getWeatherHistory(postalCode, username);

		// Then
		assertEquals(2, responseDTOList.size());
		assertEquals("Sunny", responseDTOList.get(0).getWeatherCondition());
		assertEquals("Cloudy", responseDTOList.get(1).getWeatherCondition());

		verify(userRepository, times(1)).findByUsername(username);
		verify(weatherDataRepository, times(1)).findByPostalCodeAndUserIdOrderByRequestTimestampDesc(postalCode,
				activeUser.getId());
	}

	@Test
	void testGetWeatherHistory_UserNotFound() {
		// Given
		String postalCode = "94040";
		String username = "nonexistentuser";

		when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

		// When & Then
		assertThrows(UserNotFoundException.class, () -> weatherService.getWeatherHistory(postalCode, username));

		verify(userRepository, times(1)).findByUsername(username);
	}
}