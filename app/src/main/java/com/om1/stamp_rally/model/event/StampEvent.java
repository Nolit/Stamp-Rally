package com.om1.stamp_rally.model.event;

/**
 * Created by yaboo on 2016/12/06.
 */

public class StampEvent {
    private Integer stampId, stampRallyId;
    private Double latitude, longitude;

    public StampEvent(Integer stampId, Integer stampRallyId, Double latitude, Double longitude) {
        this.stampId = stampId;
        this.stampRallyId = stampRallyId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getStampId() {
        return stampId;
    }

    public Integer getStampRallyId() {
        return stampRallyId;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
