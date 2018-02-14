package com.idpz.instacity.models;

/**
 * Created by h on 3/15/2017.
 */

public class Shop {

    private int id;
    private String name;
    private String owner;
    private String tel;
    private String mobile;
    private String pic;
    private String address;
    private String jlat;
    private String jlng;
    private String tag;
    private String jkey;
    private String memo;
    private String pub;

    public Shop() {
    }

    public Shop(int id, String name, String owner, String tel, String mobile, String pic, String address, String jlat, String jlng, String tag, String jkey, String memo, String pub) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.tel = tel;
        this.mobile = mobile;
        this.pic = pic;
        this.address = address;
        this.jlat = jlat;
        this.jlng = jlng;
        this.tag = tag;
        this.jkey = jkey;
        this.memo = memo;
        this.pub = pub;
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

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJlat() {
        return jlat;
    }

    public void setJlat(String jlat) {
        this.jlat = jlat;
    }

    public String getJlng() {
        return jlng;
    }

    public void setJlng(String jlng) {
        this.jlng = jlng;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getJkey() {
        return jkey;
    }

    public void setJkey(String jkey) {
        this.jkey = jkey;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getPub() {
        return pub;
    }

    public void setPub(String pub) {
        this.pub = pub;
    }
}
