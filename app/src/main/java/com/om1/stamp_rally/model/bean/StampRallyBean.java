package com.om1.stamp_rally.model.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class StampRallyBean {
    long id;
    private String stampRallyTitle;
    private String CreatorName;
    private byte[] pictPath;

    public StampRallyBean(){}

    public StampRallyBean(byte[] pictPath, String creatorName, String stampRallyId) {
        this.pictPath = pictPath;
        CreatorName = creatorName;
        this.stampRallyTitle = stampRallyId;
    }

    public String getStampRallyTitle() {
        return stampRallyTitle;
    }

    public void setStampRallyTitle(String stampRallyTitle) {
        this.stampRallyTitle = stampRallyTitle;
    }

    public String getCreatorName() {
        return CreatorName;
    }

    public void setCreatorName(String creatorName) {
        CreatorName = creatorName;
    }

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
