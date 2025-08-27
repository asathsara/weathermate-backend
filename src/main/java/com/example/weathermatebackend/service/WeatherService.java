package com.example.weathermatebackend.service;

import com.example.weathermatebackend.model.SearchHistory;
import com.example.weathermatebackend.model.User;
import com.example.weathermatebackend.repository.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class WeatherService {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    @Value("${openweathermap.api.url}")
    private String apiUrl;

    private final RestClient restClient;
    private final SearchHistoryRepository historyRepository;

    public WeatherService(RestClient restClient, SearchHistoryRepository historyRepository) {
        this.restClient = restClient;
        this.historyRepository = historyRepository;
    }


    public Map<String, Object> fetchWeather(String city, User user) {
        Map<String, Object>  response  = restClient.get()
                .uri("/weather?q={city}&appid={apiKey}", city, apiKey) // relative to base URL
                .retrieve()
                .body(Map.class);


        // Save history
        SearchHistory history = new SearchHistory();
        history.setCity(city);
        history.setUser(user);
        history.setSearchedAt(LocalDateTime.now());
        historyRepository.save(history);

        return response;
    }

    public List<SearchHistory> getHistory(User user) {
        return null;
    }
}
