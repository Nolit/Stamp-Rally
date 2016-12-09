package com.om1.stamp_rally.utility.dbadapter;

import android.content.ContentValues;
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
    static final String CREATE_TIME = "create_time";

    public StampDbAdapter(Context context){
        super(context);
        tableName = "stamp";
        dll = "CREATE TABLE " + tableName + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + STAMP_RALLY_ID + " INTEGER NOT NULL,"
                + TITLE + " TEXT NOT NULL,"
                + MEMO + " TEXT,"
                + PICTURE + " BLOB NOT NULL,"
                + CREATE_TIME + " INTEGER NOT NULL);";
    }

    public void createStamp(Integer stampRallyId, String title, String memo, byte[] picture){
        ContentValues values = new ContentValues();
        values.put(STAMP_RALLY_ID, stampRallyId);
        values.put(TITLE, title);
        values.put(MEMO, memo);
        values.put(PICTURE, picture);
        values.put(CREATE_TIME, System.currentTimeMillis());
        db.insertOrThrow(tableName, null, values);
    }

    public void dropTable(){
        db.execSQL("drop table stamp;");
    }
}
