package com.example.weathermatebackend.dto;

import java.time.LocalDateTime;

/**
 * Projection interface for SearchHistory.
 *  Used for Spring Data JPA projections:
 *    - Spring automatically generates proxy objects implementing this interface.
 *    - Only selected columns are fetched from the database (efficient).
 *    - Provides a read-only view of the data.
 *    - No need for @Data or manual mapping.
 * Keep as interface for query projections; use a class only if you need full DTO behavior
 * (e.g., setters, constructors, or JSON serialization control).
 */
public interface SearchHistoryDto {
    long getId();
    String getCity();
    LocalDateTime getSearchedAt();
    double getTemperature();
}
