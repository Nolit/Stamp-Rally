package com.om1.stamp_rally.model.event;

public class StampRallyDetailEvent {
    private boolean success;
    private String json;

    public StampRallyDetailEvent(boolean success, String json) {
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
