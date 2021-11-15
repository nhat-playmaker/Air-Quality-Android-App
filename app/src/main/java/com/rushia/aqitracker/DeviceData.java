package com.rushia.aqitracker;

import java.io.Serializable;

public class DeviceData implements Serializable {
    private int id, co2, hcho, tvoc, pm25, pm100, aqi;
    private double temperature, humidity;
    private String date, time;

    public DeviceData(int id, int co2, int hcho, int tvoc, int pm25, int pm100, double temperature, double humidity, String date, String time) {
        this.id = id;
        this.co2 = co2;
        this.hcho = hcho;
        this.tvoc = tvoc;
        this.pm25 = pm25;
        this.pm100 = pm100;
        this.temperature = temperature;
        this.humidity = humidity;
        this.date = date;
        this.time = time;
    }

    public int getAqi() {
        return aqi;
    }

    public void setAqi(int aqi) {
        this.aqi = aqi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCo2() {
        return co2;
    }

    public void setCo2(int co2) {
        this.co2 = co2;
    }

    public int getHcho() {
        return hcho;
    }

    public void setHcho(int hcho) {
        this.hcho = hcho;
    }

    public int getTvoc() {
        return tvoc;
    }

    public void setTvoc(int tvoc) {
        this.tvoc = tvoc;
    }

    public int getPm25() {
        return pm25;
    }

    public void setPm25(int pm25) {
        this.pm25 = pm25;
    }

    public int getPm100() {
        return pm100;
    }

    public void setPm100(int pm100) {
        this.pm100 = pm100;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

