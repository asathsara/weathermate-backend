package com.example.weathermatebackend.repository;

import com.example.weathermatebackend.dto.SearchHistoryDto;
import com.example.weathermatebackend.model.SearchHistory;
import com.example.weathermatebackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    List<SearchHistoryDto> findByUserOrderBySearchedAtDesc(User user);
}
