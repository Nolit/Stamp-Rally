package com.om1.stamp_rally.utility.dbadapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by b3176 on 2016/12/06.
 */

public class StampRallyDbAdapter extends BaseDbAdapter {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String IS_CHALLENGE = "isChallenge";

    public StampRallyDbAdapter(Context context) {
        super(context);
        tableName = "stamp_rally";
        dll = "CREATE TABLE " + tableName + " ("
                + ID + " INTEGER PRIMARY KEY,"
                + NAME + " TEXT NOT NULL,"
                + IS_CHALLENGE + " INTEGER DEFAULT 0);";
    }

    public void createStampRally(Integer id, String name){
        open();
        ContentValues values = new ContentValues();
        values.put(ID, id);
        values.put(NAME, name);
        db.insertOrThrow(tableName, null, values);
        close();
    }

    public Map<String, Object> getTryingStampRally(){
        open();
        Cursor c = db.query(tableName, null, IS_CHALLENGE + " = 1", null, null, null, null, null);
        Map<String, Object> stampRally = convert(c);
        c.close();
        close();

        return stampRally;
    }

    public Map<String, Object> getById(int id){
        open();
        Cursor c = db.query(tableName, null, ID + " = " + id, null, null, null, null, null);
        Map<String, Object> stampRally = convert(c);
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
                Log.d("id: ", c.getString(c.getColumnIndex(ID)));
                Log.d("size: ", ""+c.getString(c.getColumnIndex(NAME)));
                Log.d("isChallenge: ", ""+c.getInt(c.getColumnIndex(IS_CHALLENGE)));
            }while(c.moveToNext());
        }
        close();
    }

    private Map<String, Object> convert(Cursor c){
        if(!c.moveToFirst()){
            return null;
        }

        Map<String, Object> stampRally = new HashMap<>();
        stampRally.put(ID, c.getInt(c.getColumnIndex(ID)));
        stampRally.put(NAME, c.getString(c.getColumnIndex(NAME)));
        stampRally.put(IS_CHALLENGE, c.getInt(c.getColumnIndex(IS_CHALLENGE)));

        return stampRally;
    }
}