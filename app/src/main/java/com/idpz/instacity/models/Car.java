package com.idpz.instacity.models;

/**
 * Created by h on 2018/03/17.
 */

public class Car {
    private String code;
    private String fname;
    private String pelak;
    private String driver;

    public Car() {
    }

    public Car(String code, String fname, String pelak, String driver) {
        this.code = code;
        this.fname = fname;
        this.pelak = pelak;
        this.driver = driver;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getPelak() {
        return pelak;
    }

    public void setPelak(String pelak) {
        this.pelak = pelak;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }
}
