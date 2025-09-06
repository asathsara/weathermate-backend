package com.example.weathermatebackend.service;

import com.example.weathermatebackend.dto.AuthResponseDto;
import com.example.weathermatebackend.dto.UserRegistrationDto;
import com.example.weathermatebackend.exception.UsernameAlreadyExistsException;
import com.example.weathermatebackend.model.User;
import com.example.weathermatebackend.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link UserService}.
 * Focus: testing business logic only (repository, jwt, and auth manager are mocked).
 * Approach: "Arrange, Act, Assert" structure inside each test.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Create a fresh service instance for each test
        userService = new UserService(userRepository, authManager, jwtService);

        // Inject fake config values for @Value fields
        ReflectionTestUtils.setField(userService, "refreshTokenCookieName", "refreshToken");
        ReflectionTestUtils.setField(userService, "refreshTokenExpirationSeconds", 3600L);
    }

    /**
     * Scenario: User tries to register with a username that already exists.
     * Expectation: A UsernameAlreadyExistsException should be thrown
     * and the user should NOT be saved into the repository.
     */
    @Test
    void register_ShouldThrowException_WhenUsernameExists() {
        UserRegistrationDto dto = new UserRegistrationDto("john", "password");

        // Arrange → mock repository to return true for existing username
        when(userRepository.existsByUsername("john")).thenReturn(true);

        // Act & Assert → service should throw exception
        assertThrows(UsernameAlreadyExistsException.class,
                () -> userService.register(dto));

        // Verify → no new user is saved
        verify(userRepository, never()).save(any());
    }

    /**
     * Scenario: User registers with a new username.
     * Expectation: The user should be saved with an encoded password,
     * and the returned object should contain the username.
     */
    @Test
    void register_ShouldSaveUser_WhenUsernameIsNew() {
        UserRegistrationDto dto = new UserRegistrationDto("alex", "password");

        // Arrange → username does not exist
        when(userRepository.existsByUsername("alex")).thenReturn(false);
        when(userRepository.save(any(User.class)))
                .thenAnswer(inv -> inv.getArgument(0)); // return same user object passed in

        // Act
        User savedUser = userService.register(dto);

        // Assert
        assertEquals("alex", savedUser.getUsername());
        assertNotEquals("password", savedUser.getPassword()); // password should be encrypted
        verify(userRepository).save(any(User.class));
    }

    /**
     * Scenario: User logs in with correct credentials.
     * Expectation: Authentication should succeed, tokens should be generated,
     * a cookie should be added to response, and a success message returned.
     */
    @Test
    void verify_ShouldReturnSuccess_WhenAuthenticationSucceeds() {
        User user = new User(1,"john", "password");

        // Arrange → mock authentication success
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        // Tokens returned from jwtService
        when(jwtService.generateAccessToken("john")).thenReturn("access-token");
        when(jwtService.generateRefreshToken("john")).thenReturn("refresh-token");

        // Act
        AuthResponseDto responseDto = userService.verify(user, response);

        // Assert
        assertEquals("Login successful", responseDto.getMessage());
        assertEquals("access-token", responseDto.getAccessToken());
        verify(response).addCookie(any(Cookie.class)); // refresh token cookie is set
    }

    /**
     * Scenario: User logs in with incorrect credentials.
     * Expectation: Authentication should fail, no tokens should be created,
     * and a failure message should be returned.
     */
    @Test
    void verify_ShouldFail_WhenAuthenticationFails() {
        User user = new User(1,"john", "wrong-password");

        // Arrange → mock failed authentication
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);
        when(authManager.authenticate(any())).thenReturn(auth);

        // Act
        AuthResponseDto responseDto = userService.verify(user, response);

        // Assert
        assertEquals("Authentication failed", responseDto.getMessage());
        assertNull(responseDto.getAccessToken()); // no token should be generated
    }
}
