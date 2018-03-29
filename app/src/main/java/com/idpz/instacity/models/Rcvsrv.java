package com.idpz.instacity.models;

/**
 * Created by hashem on 07/02/2017.
 */

public class Rcvsrv {

    private int id;
    private String code;
    private String lat;
    private String lng;

    public String getCode() {
        return code;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }



    public Rcvsrv() {
    }

    public Rcvsrv(int id, String code, String lat, String lng) {
        this.id = id;
        this.code = code;
        this.lat = lat;
        this.lng = lng;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setCode(String code) {
        this.code = code;
    }
}