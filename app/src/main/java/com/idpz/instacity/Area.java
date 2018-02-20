package com.idpz.instacity;

/**
 * Created by h on 2017/08/29.
 */

public class Area {

    private int id;
    private String aename;
    private String afname;
    private float alat;
    private float alng;
    private float adiameter;
    private String server;
    private int zoom;

    public Area() {
    }

    public Area(int id, String aename, String afname, float alat, float alng, float adiameter, String server, int zoom) {
        this.id = id;
        this.aename = aename;
        this.afname = afname;
        this.alat = alat;
        this.alng = alng;
        this.adiameter = adiameter;
        this.server=server;
        this.zoom = zoom;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAename() {
        return aename;
    }

    public void setAename(String aename) {
        this.aename = aename;
    }

    public String getAfname() {
        return afname;
    }

    public void setAfname(String afname) {
        this.afname = afname;
    }

    public float getAlat() {
        return alat;
    }

    public void setAlat(float alat) {
        this.alat = alat;
    }

    public float getAlng() {
        return alng;
    }

    public void setAlng(float alng) {
        this.alng = alng;
    }

    public float getAdiameter() {
        return adiameter;
    }

    public void setAdiameter(float adiameter) {
        this.adiameter = adiameter;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }
}
