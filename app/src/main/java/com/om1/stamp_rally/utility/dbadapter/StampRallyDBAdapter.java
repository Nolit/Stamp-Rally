package com.om1.stamp_rally.utility.dbadapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;


/**
 * Created by b3176 on 2016/12/06.
 */

public class StampRallyDbAdapter extends BaseDbAdapter {
    static final String ID = "id";
    static final String SIZE = "size";
    static final String IS_CHALLENGE = "isChallenge";

    public StampRallyDbAdapter(Context context) {
        super(context);
        tableName = "stamp_rally";
        dll = "CREATE TABLE " + tableName + " ("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + SIZE + " INTEGER NOT NULL,"
                + IS_CHALLENGE + " INTEGER DEFAULT 0);";
    }

    public void createStampRally(int size){
        ContentValues values = new ContentValues();
        values.put(SIZE, size);
        db.insertOrThrow(tableName, null, values);
    }

    public Cursor getTryingStampRally(){
        return db.query(tableName, null, IS_CHALLENGE + " = 1", null, null, null, null, null);
    }

    public void tryStampRally(int id){
        cancelTryingStampRally();

        ContentValues values = new ContentValues();
        values.put(IS_CHALLENGE, 1);
        db.update(tableName, values, ID + " = " + id, null);
    }

    private void cancelTryingStampRally(){
        ContentValues values = new ContentValues();
        values.put(IS_CHALLENGE, 0);
        db.update(tableName, values, IS_CHALLENGE + " = 1", null);
    }
}
