package com.example.weathermatebackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link JwtService}.
 * Focus: testing JWT generation, claim extraction, and validation.
 * Approach: Arrange → Act → Assert structure for clarity.
 */
class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        // Use a fixed Base64-encoded secret key for testing purposes
        jwtService = new JwtService("ZmFrZXNlY3JldGtleXRlc3Rmb3J0ZXN0aW5nMTIzNDU2Nzg5MGFiY2RlZj0=");

        // Inject expiration times for access and refresh tokens
        ReflectionTestUtils.setField(jwtService, "accessTokenExpirationMillis", 3600000L);  // 1 hour
        ReflectionTestUtils.setField(jwtService, "refreshTokenExpirationMillis", 604800000L); // 7 days
    }

    /**
     * Scenario: Generate an access token for a user.
     * Expectation: The token should contain the correct username in its claims.
     */
    @Test
    void generateAccessToken_ShouldContainUsername() {
        String token = jwtService.generateAccessToken("john");
        String username = jwtService.extractUsername(token);
        assertEquals("john", username);
    }

    /**
     * Scenario: Validate a properly generated token against the correct user.
     * Expectation: Validation should succeed.
     */
    @Test
    void validateToken_ShouldReturnTrue_ForValidToken() {
        String token = jwtService.generateAccessToken("john");
        UserDetails user = new org.springframework.security.core.userdetails.User(
                "john", "password", List.of());

        assertTrue(jwtService.validateToken(token, user));
    }

    /**
     * Scenario: Validate an expired token.
     * Expectation: Validation should fail and return false without throwing an exception.
     */
    @Test
    void validateToken_ShouldReturnFalse_ForExpiredToken() throws InterruptedException {
        // Arrange → set expiration to 1 ms so token expires immediately
        ReflectionTestUtils.setField(jwtService, "accessTokenExpirationMillis", 1L);
        String token = jwtService.generateAccessToken("john");
        UserDetails user = new org.springframework.security.core.userdetails.User(
                "john", "password", List.of());

        // Wait a tiny bit to ensure token is expired
        Thread.sleep(10);

        // Act & Assert → validateToken safely returns false
        assertFalse(jwtService.validateToken(token, user));
    }

    /**
     * Scenario: Validate a token against a different username.
     * Expectation: Validation should fail because token does not belong to the user.
     */
    @Test
    void validateToken_ShouldReturnFalse_ForWrongUsername() {
        String token = jwtService.generateAccessToken("john");
        UserDetails user = new org.springframework.security.core.userdetails.User(
                "alice", "password", List.of());

        assertFalse(jwtService.validateToken(token, user));
    }

    /**
     * Scenario: Validate a malformed token.
     * Expectation: Validation should fail without throwing an exception.
     */
    @Test
    void validateToken_ShouldReturnFalse_ForMalformedToken() {
        String badToken = "this.is.not.a.valid.token";
        UserDetails user = new org.springframework.security.core.userdetails.User(
                "john", "password", List.of());

        // validateToken handles the exception internally and returns false
        assertFalse(jwtService.validateToken(badToken, user));
    }
}
