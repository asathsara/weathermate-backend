package com.example.weathermatebackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRegistrationDto {

    @NotBlank
    @Size(min = 3)
    private String username;

    @NotBlank
    @Size(min = 8)
    private String password;

}
