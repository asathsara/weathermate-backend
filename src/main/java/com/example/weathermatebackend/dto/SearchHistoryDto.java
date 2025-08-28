package com.example.weathermatebackend.dto;

import java.time.LocalDateTime;

public interface SearchHistoryDto {
    long getId();
    String getCity();
    LocalDateTime getSearchedAt();
    double getTemperature();
}
