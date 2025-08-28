package com.example.weathermatebackend.service;

import com.example.weathermatebackend.dto.AuthResponseDto;
import com.example.weathermatebackend.model.User;
import com.example.weathermatebackend.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;
    final AuthenticationManager authManager;
    final JwtService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository, AuthenticationManager authManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.authManager = authManager;
        this.jwtService = jwtService;
    }

    public User register(User user){

        // Bcrypt the password
        user.setPassword(encoder.encode(user.getPassword()));

        userRepository.save(user);
        return user;
    }

    public AuthResponseDto verify(User user, HttpServletResponse response) {
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        if(authentication.isAuthenticated()) {
            String accessToken = jwtService.generateAccessToken(user.getUsername());
            String refreshToken = jwtService.generateRefreshToken(user.getUsername());

            // THIS is where the cookie is created and sent to the browser
            Cookie cookie = new Cookie("refreshToken", refreshToken);
            cookie.setHttpOnly(true);       // JS cannot access it
            cookie.setSecure(true);         // HTTPS only
            cookie.setPath("/");            // available for all endpoints
            cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days
            response.addCookie(cookie);     // <-- browser saves it automatically

            return new AuthResponseDto(accessToken, "Login successful");
        }

        return new AuthResponseDto(null, "Authentication failed");
    }

}
