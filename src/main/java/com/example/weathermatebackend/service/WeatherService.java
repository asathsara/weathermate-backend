package com.example.weathermatebackend.service;

import com.example.weathermatebackend.model.SearchHistory;
import com.example.weathermatebackend.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class WeatherService {
    public Map<String, Object> fetchWeather(String city, User user) {
        return null;
    }

    public List<SearchHistory> getHistory(User user) {
        return null;
    }
}
