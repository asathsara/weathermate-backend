package com.example.weathermatebackend.service;

import com.example.weathermatebackend.dto.SearchHistoryDto;
import com.example.weathermatebackend.dto.WeatherDto;
import com.example.weathermatebackend.model.SearchHistory;
import com.example.weathermatebackend.model.User;
import com.example.weathermatebackend.repository.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class WeatherService {

    @Value("${openweathermap.api.key}")
    private String apiKey;

    private final RestClient restClient;
    private final SearchHistoryRepository historyRepository;

    public WeatherService(RestClient restClient, SearchHistoryRepository historyRepository) {
        this.restClient = restClient;
        this.historyRepository = historyRepository;
    }

    protected WeatherDto callWeatherApi(String city) {
        return restClient.get()
                .uri("/weather?q={city}&appid={apiKey}", city, apiKey)
                .retrieve()
                .body(WeatherDto.class);
    }

    public WeatherDto fetchWeather(String city, User user) {
        // Call external API
        WeatherDto response = callWeatherApi(city);

        // Save history
        if (response != null) {
            SearchHistory history = new SearchHistory();
            history.setCity(city);
            history.setUser(user);
            history.setSearchedAt(LocalDateTime.now());
            history.setTemperature(response.getMain().getTemp());
            historyRepository.save(history);
        }


        return response;
    }

    public List<SearchHistoryDto> getHistory(User user) {
        return historyRepository.findByUserOrderBySearchedAtDesc(user);
    }
}
