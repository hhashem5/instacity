package com.idpz.instacity.models;

/**
 * Created by h on 3/8/2017.
 */

public class Evnt {
    private int id;
    private String name;
    private String owner;
    private String contact;
    private String place;
    private String edate;
    private String etime;
    private String info;
    private String memo;

    public Evnt() {
    }

    public Evnt(int id, String name, String owner, String contact, String place, String edate, String etime, String info, String memo) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.contact = contact;
        this.place = place;
        this.edate = edate;
        this.etime = etime;
        this.info = info;
        this.memo = memo;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getEdate() {
        return edate;
    }

    public void setEdate(String edate) {
        this.edate = edate;
    }

    public String getEtime() {
        return etime;
    }

    public void setEtime(String etime) {
        this.etime = etime;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }
}
