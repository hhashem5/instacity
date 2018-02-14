package com.idpz.instacity.models;

/**
 * Created by h on 2018/01/22.
 */

public class Comment {
    private int id;
    private String soid;
    private String usrName;
    private String mycomment;
    private String cmtime;


    public Comment() {
    }

    public Comment(int id, String soid, String usrName, String mycomment, String cmtime) {
        this.id = id;
        this.soid = soid;
        this.usrName = usrName;
        this.mycomment = mycomment;
        this.cmtime = cmtime;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSoid() {
        return soid;
    }

    public void setSoid(String soid) {
        this.soid = soid;
    }

    public String getUsrName() {
        return usrName;
    }

    public void setUsrName(String usrName) {
        this.usrName = usrName;
    }

    public String getMycomment() {
        return mycomment;
    }

    public void setMycomment(String mycomment) {
        this.mycomment = mycomment;
    }

    public String getCmtime() {
        return cmtime;
    }

    public void setCmtime(String cmtime) {
        this.cmtime = cmtime;
    }
}
