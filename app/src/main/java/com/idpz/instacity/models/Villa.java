package com.idpz.instacity.models;

/**
 * Created by h on 2018/03/16.
 */

public class Villa {
    private int id;
    private String name;
    private String area;
    private String price;
    private String room;
    private String facility;
    private String tel;
    private String address;
    private String memo;
    private String lat;
    private String lng;
    private String pic;
    private String owner;
    private String alat;
    private String alng;
    private String aename;
    private String afname;
    private String state;

    public Villa() {
    }

    public Villa(int id, String name, String owner, String area, String price, String room, String facility, String tel, String address,
                 String memo, String lat, String lng, String pic, String alat, String alng, String aename, String afname, String state) {
        this.id = id;
        this.name = name;
        this.area = area;
        this.price = price;
        this.room = room;
        this.facility = facility;
        this.tel = tel;
        this.address = address;
        this.memo = memo;
        this.lat = lat;
        this.lng = lng;
        this.pic = pic;
        this.owner = owner;
        this.alat = alat;
        this.alng = alng;
        this.aename = aename;
        this.afname = afname;
        this.state = state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getFacility() {
        return facility;
    }

    public void setFacility(String facility) {
        this.facility = facility;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getAlat() {
        return alat;
    }

    public void setAlat(String alat) {
        this.alat = alat;
    }

    public String getAlng() {
        return alng;
    }

    public void setAlng(String alng) {
        this.alng = alng;
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
}
