package com.tcg.weatherinfo.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private String username;
    private String password;
    private boolean active;
}