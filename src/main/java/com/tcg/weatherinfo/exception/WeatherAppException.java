package com.tcg.weatherinfo.exception;

public class WeatherAppException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public WeatherAppException(String message) {
        super(message);
    }

    public WeatherAppException(String message, Throwable cause) {
        super(message, cause);
    }
}