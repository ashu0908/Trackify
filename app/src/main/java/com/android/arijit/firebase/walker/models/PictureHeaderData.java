package com.android.arijit.firebase.walker.models;

public class PictureHeaderData extends SuperPictureData{
    public String date;

    public PictureHeaderData(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
