package com.android.arijit.firebase.walker.models;

import com.google.firebase.Timestamp;

public class PictureData extends SuperPictureData {
    public String imgUrl;
    public Timestamp date;

    public PictureData(){}

    public PictureData(String imgUrl) {
        this.imgUrl = imgUrl;
        this.date = Timestamp.now();
    }

    public PictureData(String imgUrl, Timestamp date) {
        this.imgUrl = imgUrl;
        this.date = date;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

}

