package com.tcg.weatherinfo.exception;

public class InvalidPostalCodeException extends WeatherAppException {
    private static final long serialVersionUID = 1L;

	public InvalidPostalCodeException(String message) {
        super(message);
    }
}