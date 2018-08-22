package com.idpz.instacity.models;

/**
 * Created by h on 2018/03/12.
 */

public class Arts {

    private int id;
    private String owner;
    private String code;
    private String name;
    private String type;
    private String weight;
    private String material;
    private String color;
    private String price;
    private String memo;
    private String pic;
    private String stamp;
    private String alat;
    private String alng;
    private String aename;
    private String afname;
    private String state;

    public Arts() {
    }

    public Arts(int id, String owner, String code, String name, String type, String weight, String material,
                String color, String price, String memo, String pic, String stamp,String alat,String alng,String aename,String afname,String state) {
        this.id = id;
        this.owner = owner;
        this.code = code;
        this.name = name;
        this.type = type;
        this.weight = weight;
        this.material = material;
        this.color = color;
        this.price = price;
        this.memo = memo;
        this.pic = pic;
        this.stamp = stamp;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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

    public String getStamp() {
        return stamp;
    }

    public void setStamp(String stamp) {
        this.stamp = stamp;
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
