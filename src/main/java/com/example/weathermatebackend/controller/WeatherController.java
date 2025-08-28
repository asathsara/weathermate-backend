package com.example.weathermatebackend.controller;


import com.example.weathermatebackend.dto.SearchHistoryDto;
import com.example.weathermatebackend.dto.WeatherDto;
import com.example.weathermatebackend.model.User;
import com.example.weathermatebackend.model.UserPrinciple;
import com.example.weathermatebackend.service.WeatherService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;

    }

    @GetMapping("/weather/{city}")
    public WeatherDto getWeather(@PathVariable String city, @AuthenticationPrincipal UserPrinciple userPrinciple) {
        User user =  userPrinciple.getUser();
        return weatherService.fetchWeather(city, user);

    }

    @GetMapping("/history")
    public List<SearchHistoryDto> getHistory(@AuthenticationPrincipal UserPrinciple userPrinciple) {
        User user =  userPrinciple.getUser();
        return weatherService.getHistory(user);
    }
}
