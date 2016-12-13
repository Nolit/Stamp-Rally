package com.om1.stamp_rally.model.bean;

import com.google.android.gms.maps.model.LatLng;

public class StampBean {
    long id;
    private int stampId;
    private String stampPadId;
    private String userId;
    private String pictPath;
    private String stampTitle;
    private String stampComment;
    private String stampDate;
    private LatLng stampLatLng;

    public StampBean(){}

    public StampBean(int stampId, String stampPadId, String userId, String pictPath, String stampTitle, String stampComment, String stampDate) {
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

    public String getPictPath() {
        return pictPath;
    }

    public void setPictPath(String pictPath) {
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
    
}
