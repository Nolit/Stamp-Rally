package com.om1.stamp_rally.utility.dbadapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by b3176 on 2016/12/06.
 */

public class StampRallyDbAdapter extends BaseDbAdapter {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String SUMMARY = "summary";

    public StampRallyDbAdapter(Context context) {
        super(context);
        tableName = "stamp_rally";
        dll = "CREATE TABLE " + tableName + " ("
                + ID + " INTEGER PRIMARY KEY,"
                + NAME + " TEXT NOT NULL,"
                + SUMMARY + " TEXT);";
        nonExistsIfCreate();
    }

    public List<Map<String, Object>> getAllAsList(){
        open();
        List<Map<String, Object>> stampRallyList = getExtractedCursorAsList(super.getAll());
        close();

        return stampRallyList;
    }

    private List<Map<String, Object>> getExtractedCursorAsList(Cursor c){
        List<Map<String, Object>> stampRallyList = new ArrayList<>();

        if(c.moveToFirst()){
            do {
                Map<String, Object> stampRally = new HashMap<>();
                stampRally.put(ID, c.getInt(c.getColumnIndex(ID)));
                stampRally.put(NAME, c.getString(c.getColumnIndex(NAME)));
                stampRally.put(SUMMARY, c.getString(c.getColumnIndex(SUMMARY)));
                stampRallyList.add(stampRally);
            }while(c.moveToNext());
        }
        c.close();

        return stampRallyList;
    }

    public void createStampRally(String name, String summary){
        open();
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(SUMMARY, summary);
        db.insertOrThrow(tableName, null, values);
        close();
    }

    public Map<String, Object> getById(int id){
        open();
        Cursor c = db.query(tableName, null, ID + " = " + id, null, null, null, null, null);
        Map<String, Object> stampRally = convert(c);
        c.close();
        close();

        return stampRally;
    }

    public void log(){
        open();
        Cursor c = getAll();
        if(c.moveToFirst()){
            do {
                Log.d("id: ", ""+c.getInt(c.getColumnIndex(ID)));
                Log.d("name: ", ""+c.getString(c.getColumnIndex(NAME)));
                Log.d("summary: ", ""+c.getString(c.getColumnIndex(SUMMARY)));
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
        stampRally.put(SUMMARY, c.getString(c.getColumnIndex(SUMMARY)));

        return stampRally;
    }

    public void deleteById(int id){
        open();
        db.delete(tableName, ID + "=" + id, null);
        close();
    }

    public void update(Map<String, Object> data){
        open();

        ContentValues cv = new ContentValues();
        cv.put(NAME, (String) data.get(NAME));
        cv.put(SUMMARY, (String) data.get(SUMMARY));
        Integer id = (Integer) data.get(ID);
        db.update(tableName, cv, "id = " + id, null);

        close();
    }

    public void nonExistsIfCreate(){
        open();
        String query = "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='" + tableName + "';";
        Cursor c = db.rawQuery(query, null);
        c.moveToFirst();
        int count = Integer.valueOf(c.getString(0));
        if(count == 0){
            db.execSQL(dll);
        }
        close();
    }
}
