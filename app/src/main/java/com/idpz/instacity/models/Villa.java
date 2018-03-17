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

    public Villa() {
    }

    public Villa(int id, String name, String area, String price, String room, String facility, String tel, String address, String memo, String lat, String lng, String pic) {
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
}
