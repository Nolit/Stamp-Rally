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
    static final String STAMP_ID = "stampId";
    static final String STAMP_RALLY_ID = "stampRallyId";
    static final String STAMP_RALLY_NAME = "stampRallyName";
    static final String TITLE = "title";
    static final String MEMO = "memo";
    static final String PICTURE = "picture";
    static final String LATITUDE = "latitude";
    static final String LONGITUDE = "longitude";
    static final String CREATE_TIME = "create_time";

    public StampDbAdapter(Context context){
        super(context);
        tableName = "stamp";
        dll = "CREATE TABLE " + tableName + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + STAMP_ID + " INTEGER,"
                + STAMP_RALLY_ID + " INTEGER,"
                + STAMP_RALLY_NAME + " TEXT,"
                + TITLE + " TEXT NOT NULL,"
                + MEMO + " TEXT,"
                + PICTURE + " BLOB NOT NULL,"
                + LATITUDE + " REAL,"
                + LONGITUDE + " REAL,"
                + CREATE_TIME + " INTEGER NOT NULL);";
    }

    public void createStamp(Integer stampId, Integer stampRallyId, String stampRallyName, String title, String memo, byte[] picture, Double latitude, Double longitude){
        open();
        ContentValues values = new ContentValues();
        values.put(STAMP_ID, stampId);
        values.put(STAMP_RALLY_ID, stampRallyId);
        values.put(STAMP_RALLY_NAME, stampRallyName);
        values.put(TITLE, title);
        values.put(MEMO, memo);
        values.put(PICTURE, picture);
        values.put(LATITUDE, latitude);
        values.put(LONGITUDE, longitude);
        values.put(CREATE_TIME, System.currentTimeMillis());
        db.insertOrThrow(tableName, null, values);
        close();
        log();
    }

    public List<Map<String, Object>> getAllAsList(){
        open();
        List<Map<String, Object>> stampList = getExtractedCursorAsList(super.getAll());
        close();

        return stampList;
    }

    public List<Map<String, Object>> getByStampRallyIdAsList(int stampRallyId){
        open();
        Cursor c = db.query(tableName, null, STAMP_RALLY_ID + " = " + stampRallyId, null, null, null, null, null);
        List<Map<String, Object>> stampList = getExtractedCursorAsList(c);
        close();

        return stampList;
    }

    private List<Map<String, Object>> getExtractedCursorAsList(Cursor c){
        List<Map<String, Object>> stampList = new ArrayList<>();

        if(c.moveToFirst()){
            do {
                Map<String, Object> stamp = new HashMap<>();
                stamp.put(ID, c.getInt(c.getColumnIndex(ID)));
                stamp.put(STAMP_ID, c.getInt(c.getColumnIndex(STAMP_ID)));
                stamp.put(STAMP_RALLY_ID, c.getInt(c.getColumnIndex(STAMP_RALLY_ID)));
                stamp.put(STAMP_RALLY_NAME, c.getString(c.getColumnIndex(STAMP_RALLY_NAME)));
                stamp.put(TITLE, c.getString(c.getColumnIndex(TITLE)));
                stamp.put(MEMO, c.getString(c.getColumnIndex(MEMO)));
                stamp.put(PICTURE, c.getBlob(c.getColumnIndex(PICTURE)));
                stamp.put(CREATE_TIME, c.getLong(c.getColumnIndex(CREATE_TIME)));
                stamp.put(LATITUDE, c.getDouble(c.getColumnIndex(LATITUDE)));
                stamp.put(LONGITUDE, c.getDouble(c.getColumnIndex(LONGITUDE)));
                stampList.add(stamp);
            }while(c.moveToNext());
        }
        c.close();

        return stampList;
    }

    public void deleteById(int id){
        open();
        db.delete(tableName, ID + "=" + id, null);
        close();
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
                Log.d("stamp_rally", ""+c.getInt(c.getColumnIndex(ID)));
                Log.d("stamp_rally", ""+c.getInt(c.getColumnIndex(STAMP_ID)));
                Log.d("stamp_rally", ""+c.getInt(c.getColumnIndex(STAMP_RALLY_ID)));
                Log.d("stamp_rally", ""+c.getString(c.getColumnIndex(TITLE)));
                Log.d("stamp_rally", ""+c.getString(c.getColumnIndex(MEMO)));
                Log.d("stamp_rally", ""+c.getBlob(c.getColumnIndex(PICTURE)));
                Log.d("stamp_rally", ""+new SimpleDateFormat("yyyy/MM/dd HH:mm").format(c.getLong(c.getColumnIndex(CREATE_TIME))));
                Log.d("stamp_rally", ""+c.getDouble(c.getColumnIndex(LATITUDE)));
                Log.d("stamp_rally", ""+c.getDouble(c.getColumnIndex(LONGITUDE)));
            }while(c.moveToNext());
        }
        c.close();
        close();
    }
}
