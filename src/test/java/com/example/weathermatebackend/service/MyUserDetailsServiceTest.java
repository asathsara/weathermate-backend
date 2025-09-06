package com.example.weathermatebackend.service;


import com.example.weathermatebackend.model.User;
import com.example.weathermatebackend.model.UserPrinciple;
import com.example.weathermatebackend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link MyUserDetailsService}.
 * Focus: loading user by username and handling not found case.
 */
@ExtendWith(MockitoExtension.class)
class MyUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private MyUserDetailsService userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        // Sample user entity for tests
        user = new User();
        user.setId(1);
        user.setUsername("john");
        user.setPassword("password123");
    }

    /**
     * Scenario: User exists in repository.
     * Expectation: loadUserByUsername returns a UserPrinciple with correct username.
     */
    @Test
    void loadUserByUsername_ShouldReturnUserPrinciple_WhenUserExists() {
        // Arrange → mock repository to return user
        when(userRepository.findByUsername("john")).thenReturn(user);

        // Act → call service
        UserDetails userDetails = userDetailsService.loadUserByUsername("john");

        // Assert → returned object is correct and username matches
        assertNotNull(userDetails);
        assertInstanceOf(UserPrinciple.class, userDetails);
        assertEquals("john", userDetails.getUsername());

        // Verify repository method was called
        verify(userRepository).findByUsername("john");
    }

    /**
     * Scenario: User does not exist in repository.
     * Expectation: loadUserByUsername throws UsernameNotFoundException.
     */
    @Test
    void loadUserByUsername_ShouldThrowException_WhenUserNotFound() {
        // Arrange → mock repository to return null
        when(userRepository.findByUsername("alice")).thenReturn(null);

        // Act & Assert → exception thrown
        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("alice")
        );

        assertEquals("user not found", exception.getMessage());

        // Verify repository method was called
        verify(userRepository).findByUsername("alice");
    }
}
