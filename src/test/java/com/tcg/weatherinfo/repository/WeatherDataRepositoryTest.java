package com.tcg.weatherinfo.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @BeforeEach
    @Transactional
    void setUp() {
    	// Save users first
        user1 = userRepository.save(User.builder().id(1L).username("activeuser").password("password")
                .active(true).createdAt(LocalDateTime.now()).version(1).build());

        user2 = userRepository.save(User.builder().id(2L).username("inactiveuser").password("password")
                .active(false).createdAt(LocalDateTime.now()).version(1).build());
    	WeatherData weatherData1 = WeatherData.builder()
                .postalCode("94040")
                .temperature(25.5)
                .humidity(60.0)
                .weatherCondition("Sunny")
                .user(user1)
                .build();

        WeatherData weatherData2 = WeatherData.builder()
                .postalCode("94040")
                .temperature(28.0)
                .humidity(65.0)
                .weatherCondition("Cloudy")
                .user(user1)
                .build();

        WeatherData weatherData3 = WeatherData.builder()
                .postalCode("94040")
                .temperature(22.0)
                .humidity(55.0)
                .weatherCondition("Rainy")
                .user(user2)
                .build();

        weatherDataRepository.save(weatherData1);
        weatherDataRepository.save(weatherData2);
        weatherDataRepository.save(weatherData3);
    }

    @Test
    void testFindByPostalCodeOrderByRequestTimestampDesc() {
        List<WeatherData> results = weatherDataRepository.findByPostalCodeOrderByRequestTimestampDesc("94040");
        assertThat(results).hasSize(3);
        assertThat(results.get(0).getTemperature()).isEqualTo(25.5); // Latest entry
    }

    @Test
    void testFindByUserIdOrderByRequestTimestampDesc() {
        var pageable = org.springframework.data.domain.PageRequest.of(0, 10);
        var results = weatherDataRepository.findByUserIdOrderByRequestTimestampDesc(user1.getId() , pageable);
        assertThat(results.getTotalElements()).isEqualTo(2);
        assertThat(results.getContent().get(0).getPostalCode()).isEqualTo("94040");
    }

    @Test
    void testFindByPostalCodeAndUserIdOrderByRequestTimestampDesc() {
        List<WeatherData> results = weatherDataRepository.findByPostalCodeAndUserIdOrderByRequestTimestampDesc("94040", 1L);
        assertThat(results).hasSize(2);
    }

    @Test
    void testFindWeatherDataBetweenDates() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(3);
        LocalDateTime endDate = LocalDateTime.now();
        List<WeatherData> results = weatherDataRepository.findWeatherDataBetweenDates(startDate, endDate);
        assertThat(results).hasSize(3);
    }

    @Test
    void testFindLatestByPostalCode() {
        Optional<WeatherData> result = weatherDataRepository.findLatestByPostalCode("94040");
        assertThat(result).isPresent();
        assertThat(result.get().getTemperature()).isEqualTo(26.0);
    }

    @Test
    void testGetWeatherStatistics() {
        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        var statistics = weatherDataRepository.getWeatherStatistics("94040", startDate);
        assertThat(statistics).isNotNull();
        assertThat(statistics.getAvgTemp()).isEqualTo(25.75);
        assertThat(statistics.getMaxTemp()).isEqualTo(26.0);
        assertThat(statistics.getMinTemp()).isEqualTo(25.5);
        assertThat(statistics.getAvgHumidity()).isEqualTo(62.5);
    }
}
