package com.idpz.instacity.models;

/**
 * Created by h on 3/8/2017.
 */

public class Jobs {
    private int id;
    private String name;
    private String number;
    private String title;
    private String job;
    private String payment;
    private String info;
    private String address;
    private String tel;
    private String jdate;

    public Jobs() {
    }

    public Jobs(int id, String name, String number, String title, String job, String payment, String info, String address, String tel, String jdate) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.title = title;
        this.job = job;
        this.payment = payment;
        this.info = info;
        this.address = address;
        this.tel = tel;
        this.jdate = jdate;
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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getJdate() {
        return jdate;
    }

    public void setJdate(String jdate) {
        this.jdate = jdate;
    }
}
