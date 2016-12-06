package com.om1.stamp_rally.utility.dbadapter;

import android.content.Context;

/**
 * Created by b3176 on 2016/12/06.
 */

public class StampDbAdapter extends BaseDbAdapter {
    static final String ID = "id";
    static final String STAMP_RALLY_ID = "stampRallyId";
    static final String TITLE = "title";
    static final String MEMO = "memo";
    static final String PICTURE = "picture";

    public StampDbAdapter(Context context){
        super(context);
        tableName = "stamp";
        dll = "CREATE TABLE " + tableName + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + STAMP_RALLY_ID + " INTEGER NOT NULL,"
                + TITLE + " TEXT NOT NULL,"
                + MEMO + " TEXT,"
                + PICTURE + " BLOB NOT NULL,"
                + "FOREIGN KEY stamp_rally_id"
                + "REFERENCES stamp_rally(id));";
    }

    public void createStamp(int stampId, int stampRallyId, String title, String memo, Byte picture){

    }
}
