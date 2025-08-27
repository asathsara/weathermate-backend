package com.example.weathermatebackend.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${openweathermap.api.url}")
    private String apiUrl;

    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder.baseUrl(apiUrl).build();
    }
}
