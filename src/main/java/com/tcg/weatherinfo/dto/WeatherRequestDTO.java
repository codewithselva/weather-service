package com.tcg.weatherinfo.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherRequestDTO {
	@Pattern(regexp = "^\\d{5}(?:[-\\s]\\d{4})?$", message = "Invalid US postal code format")
	private String postalCode;
	
	private String username;
}
