package com.om1.stamp_rally.model.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CreateStampRallyBean {
    long id;
    private String stampRallyTitle;
    private byte[] pictPath;

    public CreateStampRallyBean(String stampRallyTitle, byte[] pictPath) {
        this.stampRallyTitle = stampRallyTitle;
        this.pictPath = pictPath;
    }

    public String getStampRallyTitle() {
        return stampRallyTitle;
    }

    public void setStampRallyTitle(String stampRallyTitle) {
        this.stampRallyTitle = stampRallyTitle;
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
