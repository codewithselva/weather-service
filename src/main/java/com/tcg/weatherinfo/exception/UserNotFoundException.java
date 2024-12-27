package com.tcg.weatherinfo.exception;

public class UserNotFoundException extends WeatherAppException {
    private static final long serialVersionUID = 1L;

	public UserNotFoundException(String message) {
        super(message);
    }
}
