package com.example.weathermatebackend.controller;

import com.example.weathermatebackend.dto.AuthResponseDto;
import com.example.weathermatebackend.model.User;
import com.example.weathermatebackend.service.JwtService;
import com.example.weathermatebackend.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }



    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody User user, HttpServletResponse response) {
        AuthResponseDto authResponse = userService.verify(user, response);
        if(authResponse.getAccessToken() == null) {
            return ResponseEntity.status(401).body(authResponse);
        }
        return ResponseEntity.ok(authResponse);
    }


    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(@CookieValue(value = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null || !jwtService.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }

        String username = jwtService.extractUsername(refreshToken);
        String newAccessToken = jwtService.generateAccessToken(username);

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

}