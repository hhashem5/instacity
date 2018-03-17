package com.idpz.instacity.models;

/**
 * Created by h on 2018/03/15.
 */

public class VisitPlace {
    private int id;
    private String  name;
    private String year;
    private String ticket;
    private String days;
    private String hours;
    private String lat;
    private String lng;
    private String tel;
    private String address;
    private String memo;
    private String pic;

    public VisitPlace() {
    }

    public VisitPlace(int id, String name, String year, String ticket, String days, String hours, String lat, String lng, String tel, String address, String memo, String pic) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.ticket = ticket;
        this.days = days;
        this.hours = hours;
        this.lat = lat;
        this.lng = lng;
        this.memo = memo;
        this.pic = pic;
        this.tel = tel;
        this.address = address;
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getHours() {
        return hours;
    }

    public void setHours(String hours) {
        this.hours = hours;
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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
