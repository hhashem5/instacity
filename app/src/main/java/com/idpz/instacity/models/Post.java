package com.idpz.instacity.models;

/**
 * Created by h on 3/8/2017.
 */

public class Post {
    private int id;
    private String userImg;
    private String userName;
    private String userPhone;
    private String postImageUrl;
    private String postLike;
    private String postComment;
    private String postAnswer;
    private String postDetail;
    private String postLK;

    public Post() {
    }

    public Post(int id, String userImg, String userName, String userPhone, String postImageUrl, String postLike, String postComment,String postAnswer, String postDetail, String postLK) {
        this.id = id;
        this.userImg = userImg;
        this.userName = userName;
        this.userPhone = userPhone;
        this.postImageUrl = postImageUrl;
        this.postLike = postLike;
        this.postComment = postComment;
        this.postAnswer = postAnswer;
        this.postDetail = postDetail;
        this.postLK = postLK;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserImg() {
        return userImg;
    }

    public void setUserImg(String userImg) {
        this.userImg = userImg;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPostImageUrl() {
        return postImageUrl;
    }

    public void setPostImageUrl(String postImageUrl) {
        this.postImageUrl = postImageUrl;
    }

    public String getPostLike() {
        return postLike;
    }

    public void setPostLike(String postLike) {
        this.postLike = postLike;
    }

    public String getPostComment() {
        return postComment;
    }

    public void setPostComment(String postComment) {
        this.postComment = postComment;
    }

    public String getPostAnswer() {
        return postAnswer;
    }

    public void setPostAnswer(String postAnswer) {
        this.postAnswer = postAnswer;
    }

    public String getPostDetail() {
        return postDetail;
    }

    public void setPostDetail(String postDetail) {
        this.postDetail = postDetail;
    }

    public String getPostLK() {
        return postLK;
    }

    public void setPostLK(String postLK) {
        this.postLK = postLK;
    }
}