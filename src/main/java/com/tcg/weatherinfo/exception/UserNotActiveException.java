package com.tcg.weatherinfo.exception;

public class UserNotActiveException extends WeatherAppException {
    private static final long serialVersionUID = 1L;

	public UserNotActiveException(String message) {
        super(message);
    }
}