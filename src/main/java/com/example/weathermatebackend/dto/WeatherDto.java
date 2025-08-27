package com.example.weathermatebackend.dto;


public class WeatherDto {

    private Main main;
    private Wind wind;

    // Getters and Setters
    public Main getMain() { return main; }
    public void setMain(Main main) { this.main = main; }

    public Wind getWind() { return wind; }
    public void setWind(Wind wind) { this.wind = wind; }

    // Nested class for main weather info
    public static class Main {
        private double temp;
        private double feels_like;
        private int humidity;

        public double getTemp() { return temp; }
        public void setTemp(double temp) { this.temp = temp; }

        public double getFeels_like() { return feels_like; }
        public void setFeels_like(double feels_like) { this.feels_like = feels_like; }

        public int getHumidity() { return humidity; }
        public void setHumidity(int humidity) { this.humidity = humidity; }
    }

    // Nested class for wind info
    public static class Wind {
        private double speed;

        public double getSpeed() { return speed; }
        public void setSpeed(double speed) { this.speed = speed; }
    }
}
