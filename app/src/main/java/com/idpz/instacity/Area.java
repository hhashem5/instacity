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
    private int distance;
    private String description;
    private String pic;
    private String state;

    public Area() {
    }

    public Area(int id, String aename, String state, float alat, float alng, float adiameter,
                String server, int zoom,int distance,String description,String pic, String afname) {
        this.id = id;
        this.aename = aename;
        this.afname = afname;
        this.alat = alat;
        this.alng = alng;
        this.adiameter = adiameter;
        this.server=server;
        this.zoom = zoom;
        this.distance = distance;
        this.description=description;
        this.pic=pic;
        this.state = state;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
