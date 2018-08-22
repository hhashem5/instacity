package com.idpz.instacity.models;

/**
 * Created by h on 2018/03/12.
 */

public class Ads {
    private int id;
    private String title;
    private String memo;
    private String tel;
    private String address;
    private String pic;
    private String owner;
    private String alat;
    private String alng;
    private String aename;
    private String afname;
    private String state;

    public Ads() {
    }

    public Ads(int id, String owner, String title, String memo, String tel, String address, String pic, String alat, String alng, String aename, String afname, String state) {
        this.id = id;
        this.title = title;
        this.memo = memo;
        this.tel = tel;
        this.address = address;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
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
