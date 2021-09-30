package org.techtown.sit2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataAdapter {
    protected static final String TAG = "DataAdapter";

    // TODO : TABLE 이름을 명시해야함
    protected static final String TABLE_NAME = "sit_table";

    private final Context mContext;
    private SQLiteDatabase mDb;
    private DatabaseHelper mDbHelper;

    public DataAdapter(Context context) {
        this.mContext = context;
        mDbHelper = new DatabaseHelper(mContext);
    }

    public DataAdapter createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
        } catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    public DataAdapter open() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    public Cursor getTableData() {
        try
        {
            String sql ="SELECT * FROM " + TABLE_NAME;

            Cursor mCur = mDb.rawQuery(sql, null);
            if (mCur!=null)
            {
                mCur.moveToNext();
            }
            return mCur;
        } catch (SQLException mSQLException) {
            Log.e(TAG, "getTestData >>" + mSQLException.toString());
            throw mSQLException;
        }
    }

    public void reset(){
       // String sql ="SELECT * FROM " + TABLE_NAME;
        //String sql = "UPDATE sit_table SET s1 = 0, s2 = 0, s3 = 0, s4 = 0, s5 = 0";
        ContentValues updateRowValue1 = new ContentValues();
        updateRowValue1.put("s1", 0);
        mDb.update(TABLE_NAME, updateRowValue1, null,null);
        updateRowValue1.put("s2", 0);
        mDb.update(TABLE_NAME, updateRowValue1, null,null);
        updateRowValue1.put("s3", 0);
        mDb.update(TABLE_NAME, updateRowValue1, null,null);
        updateRowValue1.put("s4", 0);
        mDb.update(TABLE_NAME, updateRowValue1, null,null);
        updateRowValue1.put("s5", 0);
        mDb.update(TABLE_NAME, updateRowValue1, null,null);

    }

    public void situp(int ss1, int ssd){
        String s0 = "s";
        String s1 = "";

        ContentValues updateRowValue2 = new ContentValues();

        if(ss1 == 1) {
            //s1 = "s1";//Integer.toString(ss1);
            updateRowValue2.put("s1", ssd);
        }
        else if(ss1 == 2){
            updateRowValue2.put("s2", ssd);
        }
        else if(ss1 == 3){
            updateRowValue2.put("s3", ssd);
        }
        else if(ss1 == 4){
            updateRowValue2.put("s4", ssd);
        }
        else if(ss1 == 5){
            updateRowValue2.put("s5", ssd);
        }

        int a = ssd;
        //String result = s0.concat(s1);

        mDb.update(TABLE_NAME, updateRowValue2, null,null);

    }
}
