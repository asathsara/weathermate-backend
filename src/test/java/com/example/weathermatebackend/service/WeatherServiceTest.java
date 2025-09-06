package com.example.weathermatebackend.service;


import com.example.weathermatebackend.dto.SearchHistoryDto;
import com.example.weathermatebackend.dto.WeatherDto;
import com.example.weathermatebackend.model.SearchHistory;
import com.example.weathermatebackend.model.User;
import com.example.weathermatebackend.repository.SearchHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link WeatherService}.
 * Focus: API call simulation and history saving.
 * Approach: Mock external API response and repository.
 */
@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private SearchHistoryRepository historyRepository;

    @InjectMocks
    private WeatherService weatherService;

    private User user;

    @BeforeEach
    void setUp() {
        // Sample user for tests
        user = new User();
        user.setId(1);
        user.setUsername("john");
    }

    /**
     * Scenario: Fetch weather for a city.
     * Expectation: WeatherDto is returned and history is saved.
     */
    @Test
    void fetchWeather_ShouldReturnWeatherAndSaveHistory() {
        // Arrange → mock WeatherDto response
        WeatherDto.Main main = mock(WeatherDto.Main.class);
        when(main.getTemp()).thenReturn(25.0);

        WeatherDto weatherDto = mock(WeatherDto.class);
        when(weatherDto.getMain()).thenReturn(main);

        // Use spy or override fetchWeather API call to return mocked response
        WeatherService spyService = spy(weatherService);

        // Only mock the API call
        doReturn(weatherDto).when(spyService).callWeatherApi(anyString());

        // Act → call the method
        WeatherDto result = spyService.fetchWeather("Colombo", user);

        // Assert → response returned correctly
        assertNotNull(result);
        assertEquals(25.0, result.getMain().getTemp());

        // Verify that history save is triggered
        verify(historyRepository, atLeastOnce()).save(any(SearchHistory.class));
    }

    /**
     * Scenario: Get search history for a user.
     * Expectation: Repository method is called and history list returned.
     */
    @Test
    void getHistory_ShouldReturnHistoryForUser() {
        // Arrange → mock projection interface
        SearchHistoryDto historyDto = mock(SearchHistoryDto.class);
        lenient().when(historyDto.getCity()).thenReturn("Colombo");
        lenient().when(historyDto.getTemperature()).thenReturn(25.0);
        lenient().when(historyDto.getSearchedAt()).thenReturn(LocalDateTime.now());
        lenient().when(historyDto.getId()).thenReturn(1L);

        when(historyRepository.findByUserOrderBySearchedAtDesc(user))
                .thenReturn(List.of(historyDto));

        // Act → call service
        List<SearchHistoryDto> history = weatherService.getHistory(user);

        // Assert → repository returns the expected list
        assertNotNull(history);
        assertEquals(1, history.size());
        assertEquals("Colombo", history.getFirst().getCity());
        assertEquals(25.0, history.getFirst().getTemperature());

        // Verify repository call
        verify(historyRepository).findByUserOrderBySearchedAtDesc(user);
    }
}