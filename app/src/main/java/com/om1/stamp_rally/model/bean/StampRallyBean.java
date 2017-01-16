package com.om1.stamp_rally.model.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class StampRallyBean {
    long id;
    private Integer stampRallyId;
    private String stampRallyTitle;
    private String creatorUserName;
    private Integer creatorUserId;
    private byte[] pictPath;

    public StampRallyBean(){}

    public StampRallyBean(byte[] pictPath, String creatorUserName, String stampRallyId) {
        this.pictPath = pictPath;
        creatorUserName = creatorUserName;
        this.stampRallyTitle = stampRallyId;
    }

    public Integer getStampRallyId() { return stampRallyId; }

    public void setStampRallyId(Integer stampRallyId) { this.stampRallyId = stampRallyId; }

    public String getStampRallyTitle() {
        return stampRallyTitle;
    }

    public void setStampRallyTitle(String stampRallyTitle) { this.stampRallyTitle = stampRallyTitle; }

    public String getCreatorUserName() {
        return creatorUserName;
    }

    public void setCreatorUserName(String creatorUserName) {
        this.creatorUserName = creatorUserName;
    }

    public Integer getCreatorUserId() { return creatorUserId; }

    public void setCreatorUserId(Integer creatorUserId) { this.creatorUserId = creatorUserId; }

    public byte[] getPictPath() {
        return pictPath;
    }

    public void setPictPath(byte[] pictPath) {
        this.pictPath = pictPath;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Bitmap getPictureBitmap() {
        return BitmapFactory.decodeByteArray(pictPath, 0, pictPath.length);
    }
}
