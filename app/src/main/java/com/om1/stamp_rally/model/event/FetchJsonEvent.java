package com.om1.stamp_rally.model.event;

/**
 * Created by karin757 on 2016/11/18.
 */

public class FetchJsonEvent {
    private boolean success;
    private String json;

    public FetchJsonEvent(boolean success, String json) {
        this.success = success;
        this.json = json;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getJson() {
        return json;
    }
}
