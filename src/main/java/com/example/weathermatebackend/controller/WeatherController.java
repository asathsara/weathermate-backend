package com.example.weathermatebackend.controller;


import com.example.weathermatebackend.model.SearchHistory;
import com.example.weathermatebackend.model.User;
import com.example.weathermatebackend.model.UserPrinciple;
import com.example.weathermatebackend.service.WeatherService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;

    }

    @GetMapping("/weather/{city}")
    public Map<String, Object> getWeather(@PathVariable String city, @AuthenticationPrincipal UserPrinciple userPrinciple) {
        User user =  userPrinciple.getUser();
        return weatherService.fetchWeather(city, user);

    }

    @GetMapping("/history")
    public List<SearchHistory> getHistory(@AuthenticationPrincipal UserPrinciple userPrinciple) {
        User user =  userPrinciple.getUser();
        return weatherService.getHistory(user);
    }
}
