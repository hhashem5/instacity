package com.idpz.instacity.models;

/**
 * Created by h on 2018/02/05.
 */

public class GiftPlace {
    private int id;
    private String name;
    private String pic;
    private String discount;

    public GiftPlace() {
    }

    public GiftPlace(int id, String name, String pic, String discount) {
        this.id = id;
        this.name = name;
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
}

