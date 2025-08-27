package com.example.weathermatebackend.dto;

import lombok.Data;

@Data
public class WeatherDto {

    private Main main;
    private Wind wind;

    // Nested class for main weather info
    @Data
    public static class Main {
        private double temp;
        private double feels_like;
        private int humidity;

    }

    // Nested class for wind info
    @Data
    public static class Wind {
        private double speed;

    }
}
