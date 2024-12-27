package com.tcg.weatherinfo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import com.tcg.weatherinfo.entity.User;
import com.tcg.weatherinfo.entity.WeatherData;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
class WeatherDataRepositoryTest {

	@Autowired
	private WeatherDataRepository weatherDataRepository;

	@Autowired
	private UserRepository userRepository;

	private User user1;
	private User user2;
	private List<WeatherData> mockresults;

	@BeforeEach
	@Transactional
	void setUp() {
		weatherDataRepository.deleteAll();
		userRepository.deleteAll();
		user1 = userRepository.save(User.builder().username("activeuser").password("password").active(true)
				.createdAt(LocalDateTime.now()).version(1).build());

		user2 = userRepository.save(User.builder().username("inactiveuser").password("password").active(false)
				.createdAt(LocalDateTime.now()).version(1).build());

		WeatherData weatherData1 = WeatherData.builder().postalCode("94040").temperature(25.5).humidity(60.0)
				.weatherCondition("Sunny").user(user1).build();

		WeatherData weatherData2 = WeatherData.builder().postalCode("94040").temperature(28.0).humidity(65.0)
				.weatherCondition("Cloudy").user(user1).build();

		WeatherData weatherData3 = WeatherData.builder().postalCode("94040").temperature(22.0).humidity(55.0)
				.weatherCondition("Rainy").user(user2).build();

		mockresults = new ArrayList<>();
		mockresults.add(weatherData3);
		mockresults.add(weatherData2);
		mockresults.add(weatherData1);

		weatherDataRepository.save(weatherData1);
		weatherDataRepository.save(weatherData2);
		weatherDataRepository.save(weatherData3);
	}

	@Test
	void testFindByPostalCodeOrderByRequestTimestampDesc() {
		List<WeatherData> results = weatherDataRepository.findByPostalCodeOrderByRequestTimestampDesc("94040");
		results.forEach(result -> System.out.println(result.toString()));
		assertThat(results).hasSize(3);
		assertThat(results.get(0).getTemperature()).isEqualTo(22.0); // Latest entry
	}

	@Test
	void testFindByPostalCodeAndUserIdOrderByRequestTimestampDesc() {
		List<WeatherData> results = weatherDataRepository.findByPostalCodeAndUserIdOrderByRequestTimestampDesc("94040",
				user1.getId());
		assertThat(results).hasSize(2);
	}
}
