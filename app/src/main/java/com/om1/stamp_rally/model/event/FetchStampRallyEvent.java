package com.om1.stamp_rally.model.event;

/**
 * Created by yaboo on 2017/01/09.
 */

public class FetchStampRallyEvent extends FetchJsonEvent {
    public FetchStampRallyEvent(boolean success, String json) {
        super(success, json);
    }
}
