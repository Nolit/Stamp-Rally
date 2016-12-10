package com.om1.stamp_rally.utility.dbadapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by b3176 on 2016/12/06.
 */

public class StampRallyDbAdapter extends BaseDbAdapter {
    public static final String ID = "id";
    public static final String SIZE = "size";
    public static final String IS_CHALLENGE = "isChallenge";

    public StampRallyDbAdapter(Context context) {
        super(context);
        tableName = "stamp_rally";
        dll = "CREATE TABLE " + tableName + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SIZE + " INTEGER NOT NULL,"
                + IS_CHALLENGE + " INTEGER DEFAULT 0);";
    }

    public void createStampRally(int size){
        open();
        ContentValues values = new ContentValues();
        values.put(SIZE, size);
        db.insertOrThrow(tableName, null, values);
        close();
    }

    public Map<String, Object> getTryingStampRally(){
        open();
        Cursor c = db.query(tableName, null, IS_CHALLENGE + " = 1", null, null, null, null, null);
        if(!c.moveToFirst()){
            return null;
        }

        Map<String, Object> stampRally = new HashMap<>();
        stampRally.put(ID, c.getInt(c.getColumnIndex(ID)));
        stampRally.put(SIZE, c.getInt(c.getColumnIndex(SIZE)));
        stampRally.put(IS_CHALLENGE, c.getInt(c.getColumnIndex(IS_CHALLENGE)));
        c.close();
        close();

        return stampRally;
    }

    public void tryStampRally(int id){
        open();
        cancelTryingStampRally();

        ContentValues values = new ContentValues();
        values.put(IS_CHALLENGE, 1);
        db.update(tableName, values, ID + " = " + id, null);
        close();
    }

    private void cancelTryingStampRally(){
        ContentValues values = new ContentValues();
        values.put(IS_CHALLENGE, 0);
        db.update(tableName, values, IS_CHALLENGE + " = 1", null);
    }

    public void log(){
        open();
        Cursor c = getAll();
        if(c.moveToFirst()){
            do {
                Log.d("id: ", c.getString(c.getColumnIndex("id")));
                Log.d("size: ", ""+c.getInt(c.getColumnIndex("size")));
                Log.d("isChallenge: ", ""+c.getInt(c.getColumnIndex("isChallenge")));
            }while(c.moveToNext());
        }
        close();
    }
}
