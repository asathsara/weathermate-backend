package com.example.weathermatebackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {

    @NotBlank
    @Size(min = 3)
    private String username;

    @NotBlank
    @Size(min = 8)
    private String password;

}
