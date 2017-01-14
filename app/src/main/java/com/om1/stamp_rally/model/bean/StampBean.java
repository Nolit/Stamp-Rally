package com.om1.stamp_rally.model.bean;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.LatLng;

public class StampBean {
    long id;
    private String markerId;
    private int stampId;
    private String stampPadId;
    private String userId;
    private String stampTitle;
    private String stampComment;
    private String stampDate;
    private LatLng stampLatLng;

    private byte[] pictPath;

    public StampBean(){}

    public StampBean(String markerId, int stampId, String stampPadId, String userId, byte[] pictPath, String stampTitle, String stampComment, String stampDate) {
        this.markerId = markerId;
        this.stampId = stampId;
        this.stampPadId = stampPadId;
        this.userId = userId;
        this.pictPath = pictPath;
        this.stampTitle = stampTitle;
        this.stampComment = stampComment;
        this.stampDate = stampDate;
    }

    public long getId(){
        return id;
    }

    public String getMarkerId() {return markerId; }

    public void setMarkerId(String markerId) { this.markerId = markerId; }

    public int getStampId() {
        return stampId;
    }

    public void setStampId(int stampId) {
        this.stampId = stampId;
    }

    public String getStampPadId() {
        return stampPadId;
    }

    public void setStampPadId(String stampPadId) {
        this.stampPadId = stampPadId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public byte[] getPictPath() {
        return pictPath;
    }

    public void setPictPath(byte[] pictPath) {
        this.pictPath = pictPath;
    }

    public String getStampTitle() {
        return stampTitle;
    }

    public void setStampTitle(String stampTitle) {
        this.stampTitle = stampTitle;
    }

    public String getStampComment() {
        return stampComment;
    }

    public void setStampComment(String stampComment) {
        this.stampComment = stampComment;
    }

    public String getStampDate() {
        return stampDate;
    }

    public void setStampDate(String stampDate) {
        this.stampDate = stampDate;
    }

    public LatLng getStampLatLng() { return stampLatLng; }

    public void setLatLng(LatLng latLng) { this.stampLatLng = latLng; }

    public Bitmap getPicture(){
        return BitmapFactory.decodeByteArray(pictPath, 0, pictPath.length);
    }
    
}
