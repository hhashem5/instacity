package com.idpz.instacity.models;

/**
 * Created by h on 2018/02/10.
 */

public class Video {
    private int id;
    private String title;
    private String pic;
    private String videoUrl;
    private String comment;
    private String detail;


    public Video() {
    }

    public Video(int id, String title,String pic, String videoUrl, String comment, String detail) {
        this.id = id;
        this.title = title;
        this.pic = pic;
        this.videoUrl = videoUrl;
        this.comment = comment;
        this.detail = detail;
    }

    public int getId() {
        return id;
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

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
