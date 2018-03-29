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
    public Ads() {
    }

    public Ads(int id,String owner, String title, String memo, String tel, String address,String pic) {
        this.id = id;
        this.title = title;
        this.memo = memo;
        this.tel = tel;
        this.address = address;
        this.pic = pic;
        this.owner = owner;
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
}
