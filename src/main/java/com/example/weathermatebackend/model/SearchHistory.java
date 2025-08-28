package com.example.weathermatebackend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String city;

    private LocalDateTime searchedAt;

    private double temperature;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


}
