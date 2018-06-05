package com.idpz.instacity.models;

/**
 * Created by h on 2018/04/18.
 */

public class MyWeather {
    private String day;
    private String icon;
    private String summary;
    private String maxTemp;
    private String Mintemp;
    private String wind;
    private String humidity;
    private String pressure;

    public MyWeather() {
    }

    public MyWeather(String day, String icon, String summary, String maxTemp, String mintemp, String wind, String humidity, String pressure) {
        this.day = day;
        this.icon = icon;
        this.summary = summary;
        this.maxTemp = maxTemp;
        Mintemp = mintemp;
        this.wind = wind;
        this.humidity = humidity;
        this.pressure = pressure;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(String maxTemp) {
        this.maxTemp = maxTemp;
    }

    public String getMintemp() {
        return Mintemp;
    }

    public void setMintemp(String mintemp) {
        Mintemp = mintemp;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPressure() {
        return pressure;
    }

    public void setPressure(String pressure) {
        this.pressure = pressure;
    }
}
