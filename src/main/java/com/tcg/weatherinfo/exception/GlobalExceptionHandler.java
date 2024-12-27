package com.tcg.weatherinfo.exception;

import java.time.LocalDateTime;
import java.util.concurrent.CompletionException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(WeatherServiceException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleWeatherServiceException(WeatherServiceException ex) {
		log.error("Weather service error: ", ex);
		return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Weather Service Error", ex.getMessage(),
				LocalDateTime.now());
	}

	@ExceptionHandler(UserNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ErrorResponse handleUserNotFoundException(UserNotFoundException ex) {
		return new ErrorResponse(HttpStatus.NOT_FOUND.value(), "User Not Found", ex.getMessage(), LocalDateTime.now());
	}


	@ExceptionHandler(InvalidPostalCodeException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleInvalidPostalCodeException(InvalidPostalCodeException ex) {
		return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Invalid Postal Code", ex.getMessage(),
				LocalDateTime.now());
	}

	@ExceptionHandler(UserNotActiveException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorResponse handleUserNotActiveException(UserNotActiveException ex) {
		return new ErrorResponse(HttpStatus.FORBIDDEN.value(), "User Not Active", ex.getMessage(), LocalDateTime.now());
	}

	@ExceptionHandler(BindException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleBindException(BindException ex) {
		return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Validation Error",
				ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(), LocalDateTime.now());
	}

	@ExceptionHandler(RestClientException.class)
	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	public ErrorResponse handleRestClientException(RestClientException ex) {
		log.error("External API error: ", ex);
		return new ErrorResponse(HttpStatus.SERVICE_UNAVAILABLE.value(), "External Service Error",
				"Unable to fetch weather data from external service", LocalDateTime.now());
	}

	@ExceptionHandler(CompletionException.class)
	public ResponseEntity<String> handleCompletionException(CompletionException ex) {
		if (ex.getCause() instanceof InvalidPostalCodeException) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getCause().getMessage());
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ErrorResponse handleGenericException(Exception ex) {
		log.error("Unexpected error: ", ex);
		return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
				"An unexpected error occurred", LocalDateTime.now());
	}
}