package com.example.weathermatebackend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService {
    private final String secretKey;

    @Value("${jwt.access.expiration}")
    private long accessTokenExpirationMillis;

    @Value("${jwt.refresh.expiration}")
    private long refreshTokenExpirationMillis;

    public JwtService(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = secretKey;
    }

    public String generateAccessToken(String username) {
        return generateToken(username, accessTokenExpirationMillis);
    }

    public String generateRefreshToken(String username) {
        return generateToken(username, refreshTokenExpirationMillis);
    }

    // generate token
    public String generateToken(String username, long jwtExpirationInMillis) {

        Map<String , Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() +  jwtExpirationInMillis))
                .and()
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);

    }

    public String extractUsername(String token) {
        // extract the username from jwt token
        return extractClaim(token, Claims::getSubject);
    }


    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String userName = extractUsername(token); // parseClaimsJws happens here
            return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            // expired, malformed, or any invalid token, return false
            return false;
        }
    }


    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}

