package com.tcg.weatherinfo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherRequestDTO {
	@NotBlank(message = "Postal code is required")
	private String postalCode;

	private String username;
}
