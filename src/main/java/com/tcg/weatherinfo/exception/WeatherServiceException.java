package com.tcg.weatherinfo.exception;

//Specific exceptions
public class WeatherServiceException extends WeatherAppException {
 private static final long serialVersionUID = 1L;

public WeatherServiceException(String message) {
     super(message);
 }

 public WeatherServiceException(String message, Throwable cause) {
     super(message, cause);
 }
}