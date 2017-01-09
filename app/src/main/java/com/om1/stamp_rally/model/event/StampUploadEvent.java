package com.om1.stamp_rally.model.event;

/**
 * Created by yaboo on 2016/12/06.
 */

public class StampUploadEvent {
    private boolean isSuccess;
    private boolean isClear;

    public StampUploadEvent(boolean isSuccess, boolean isClear) {
        this.isSuccess = isSuccess;
        this.isClear = isClear;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public boolean isClear() {
        return isClear;
    }
}
