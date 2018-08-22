package com.idpz.instacity.models;

/**
 * Created by h on 2018/02/05.
 */

public class GiftPlace {
    private int id;
    private String name;
    private String tel;
    private String address;
    private String score;
    private String pic;
    private String discount;

    public GiftPlace() {
    }

    public GiftPlace(int id, String name,String tel, String address,String score, String pic, String discount) {
        this.id = id;
        this.name = name;
        this.tel = tel;
        this.address = address;
        this.score = score;
        this.pic = pic;
        this.discount = discount;
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

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
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

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}

