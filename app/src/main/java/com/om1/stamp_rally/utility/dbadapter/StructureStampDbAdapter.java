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
 * Created by yaboo on 2017/01/17.
 */

public class StructureStampDbAdapter  extends BaseDbAdapter {
    static final String ID = "id";
    static final String STAMP_ID = "stampId";
    static final String STAMP_RALLY_ID = "stampRallyId";

    public StructureStampDbAdapter(Context context){
        super(context);
        tableName = "structure_stamp";
        dll = "CREATE TABLE " + tableName + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + STAMP_ID + " INTEGER,"
                + STAMP_RALLY_ID + " INTEGER);";
        nonExistsIfCreate();
    }

    public void createRelation(Integer stampId, Integer stampRallyId){
        open();
        ContentValues values = new ContentValues();
        values.put(STAMP_ID, stampId);
        values.put(STAMP_RALLY_ID, stampRallyId);
        db.insertOrThrow(tableName, null, values);
        close();
    }

    public List<Integer> getByStampRallyIdAsList(int stampRallyId){
        open();
        Cursor c = db.query(tableName, null, STAMP_RALLY_ID + " = " + stampRallyId, null, null, null, null, null);
        List<Integer> stampIdList = getExtractedCursorAsList(c);
        close();

        return stampIdList;
    }

    private List<Integer> getExtractedCursorAsList(Cursor c){
        List<Integer> stampIdList = new ArrayList<>();

        if(c.moveToFirst()){
            do {
                stampIdList.add(c.getInt(c.getColumnIndex(STAMP_ID)));
            }while(c.moveToNext());
        }
        c.close();

        return stampIdList;
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

    public void truncate(){
        open();
        db.execSQL("DELETE FROM " + tableName);
        db.execSQL("VACUUM");
        close();
    }

    public void log(){
        open();
        Cursor c = getAll();
        if(c.moveToFirst()){
            do {
                Log.d("スタンプラリー", "id : " + c.getInt(c.getColumnIndex(ID)));
                Log.d("スタンプラリー", "stamp_id : " + c.getInt(c.getColumnIndex(STAMP_ID)));
                Log.d("stamp_rally", "stamp_rally_id : " + c.getInt(c.getColumnIndex(STAMP_RALLY_ID)));
            }while(c.moveToNext());
        }
        c.close();
        close();
    }
}
