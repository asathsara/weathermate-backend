package com.example.weathermatebackend.repository;

import com.example.weathermatebackend.model.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
}
