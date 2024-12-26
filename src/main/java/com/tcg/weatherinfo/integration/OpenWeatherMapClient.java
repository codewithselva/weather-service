package com.tcg.weatherinfo.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.tcg.weatherinfo.exception.WeatherServiceException;
import com.tcg.weatherinfo.integration.dto.WeatherApiResponse;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
@EnableCaching
@EnableRetry
public class OpenWeatherMapClient {

    private final RestTemplate restTemplate;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.baseUrl}")
    private String baseUrl;

    @PostConstruct
    public void validateConfiguration() {
        if (apiKey == null || apiKey.isEmpty() || baseUrl == null || baseUrl.isEmpty()) {
            throw new WeatherServiceException("API key or base URL is missing in configuration");
        }
    }

    @Retryable(maxAttempts = 3, value = { HttpServerErrorException.class, HttpClientErrorException.class })
    @Cacheable(value = "weatherDataCache", key = "#postalCode")
    public WeatherApiResponse getWeatherByPostalCode(String postalCode) {
        validatePostalCode(postalCode);
        try {

            String url = String.format("%s?zip=%s,US&appid=%s&units=metric", baseUrl, postalCode, apiKey);
            log.debug("Calling OpenWeatherMap API with URL: {}", url);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            // Directly map the response to WeatherApiResponse
            ResponseEntity<WeatherApiResponse> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, WeatherApiResponse.class);

            if (response == null || response.getBody() == null) {
                log.error("Received no response body for postal code: {}", postalCode);
                throw new WeatherServiceException("No response received from weather API");
            }

            log.info("Successfully retrieved weather data for postal code: {}", postalCode);
            return response.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Error from weather API for postal code {}: {} - {}", postalCode, e.getStatusCode(),
                    e.getResponseBodyAsString());
            throw new WeatherServiceException("Weather API error: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error fetching weather data for postal code {}: {}", postalCode, e.getMessage());
            throw new WeatherServiceException("Failed to fetch weather data: " + e.getMessage(), e);
        }
    }

    private void validatePostalCode(String postalCode) {
        if (postalCode == null || !postalCode.matches("\\d{5}")) {
            throw new WeatherServiceException("Invalid postal code format: " + postalCode);
        }
    }
}
