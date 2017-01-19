package com.om1.stamp_rally.model.event;

public class LoginEvent {
    private boolean isSuccess;
    private String json;

    public LoginEvent(boolean isSuccess, String json) {
        this.isSuccess = isSuccess;
        this.json = json;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getJson() {
        return json;
    }
}
