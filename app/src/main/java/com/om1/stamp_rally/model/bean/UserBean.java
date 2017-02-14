package com.om1.stamp_rally.model.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class UserBean {
    long id;
    private Integer userId;
    private byte[] pictPath;
    private String userName;
    private String searchId;

    public UserBean() { }

    public UserBean(Integer userId, byte[] pictPath, String userName) {
        this.userId = userId;
        this.pictPath = pictPath;
        this.userName = userName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public byte[] getPictPath() {
        return pictPath;
    }

    public void setPictPath(byte[] pictPath) {
        this.pictPath = pictPath;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public Bitmap getPictureBitmap() {
        return BitmapFactory.decodeByteArray(pictPath, 0, pictPath.length);
    }

}
