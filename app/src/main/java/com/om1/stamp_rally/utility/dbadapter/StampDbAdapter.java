package com.om1.stamp_rally.utility.dbadapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        open();
        ContentValues values = new ContentValues();
        values.put(STAMP_RALLY_ID, stampRallyId);
        values.put(TITLE, title);
        values.put(MEMO, memo);
        values.put(PICTURE, picture);
        values.put(CREATE_TIME, System.currentTimeMillis());
        db.insertOrThrow(tableName, null, values);
        close();
    }

    public List<Map<String, Object>> getListByStampRallyId(int stampRallyId){
        List<Map<String, Object>> stampList = new ArrayList<>();
        open();
        Cursor c = db.query(tableName, null, STAMP_RALLY_ID + " = " + stampRallyId, null, null, null, null, null);
        if(c.moveToFirst()){
            do {
                Map<String, Object> stamp = new HashMap<>();
                stamp.put(ID, c.getInt(c.getColumnIndex(ID)));
                stamp.put(STAMP_RALLY_ID, c.getInt(c.getColumnIndex(STAMP_RALLY_ID)));
                stamp.put(TITLE, c.getString(c.getColumnIndex(TITLE)));
                stamp.put(MEMO, c.getString(c.getColumnIndex(MEMO)));
                stamp.put(PICTURE, c.getBlob(c.getColumnIndex(PICTURE)));
                stamp.put(CREATE_TIME, c.getLong(c.getColumnIndex(CREATE_TIME)));
                stampList.add(stamp);
            }while(c.moveToNext());
        }
        c.close();
        close();

        return stampList;
    }

    public void dropTable(){
        open();
        db.execSQL("drop table stamp;");
        close();
    }

    public void log(){
        open();
        Cursor c = getAll();
        if(c.moveToFirst()){
            do {
                Log.d("stamp_rally", ""+c.getInt(c.getColumnIndex("id")));
                Log.d("stamp_rally", ""+c.getInt(c.getColumnIndex("stampRallyId")));
                Log.d("stamp_rally", ""+c.getString(c.getColumnIndex("title")));
                Log.d("stamp_rally", ""+c.getString(c.getColumnIndex("memo")));
                Log.d("stamp_rally", ""+c.getBlob(c.getColumnIndex("picture")));
                Log.d("stamp_rally", ""+new SimpleDateFormat("yyyy/MM/dd HH:mm").format(c.getLong(c.getColumnIndex("create_time"))));
            }while(c.moveToNext());
        }
        c.close();
        close();
    }
}
