package com.idpz.instacity.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.idpz.instacity.models.Jobs;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by h on 3/9/2017.
 */

public class DBJobHandler extends SQLiteOpenHelper {


    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "job.db";
    // Contacts table name
    private static final String MY_TABLE = "jobs";
    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_NUMBER = "owner";
    private static final String KEY_TITLE = "contact";
    private static final String KEY_JOB = "job";
    private static final String KEY_PEYMENT = "peyment";
    private static final String KEY_INFO = "info";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_TEL = "tel";
    private static final String KEY_JDATE = "jdate";
    private static final String KEY_ALL = "*";


    public DBJobHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MY_TABLE = "CREATE TABLE " + MY_TABLE + " ("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_NUMBER + " TEXT,"+ KEY_TITLE + " TEXT,"+ KEY_JOB + " TEXT,"
                + KEY_PEYMENT + " TEXT,"+ KEY_INFO + " TEXT,"
                + KEY_ADDRESS + " TEXT,"+ KEY_TEL + " TEXT,"+ KEY_JDATE + " TEXT)";
        db.execSQL(CREATE_MY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + MY_TABLE);
// Creating tables again
        onCreate(db);
    }

    public void removeAll()
    {
        // db.delete(String tableName, String whereClause, String[] whereArgs);
        // If whereClause is null, it will delete all rows.
        SQLiteDatabase db = getWritableDatabase(); // helper is object extends SQLiteOpenHelper
        db.delete(MY_TABLE, null, null);

    }

    public int getnumofrow(){
        String countQuery = "SELECT  * FROM " + MY_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int cnt = cursor.getCount();
        cursor.close();
        return cnt;
    }

    public int getnumofcol(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MY_TABLE, new String[]{KEY_ALL }, null,
                null , null, null, null, null);
        if (cursor != null)
            return cursor.getColumnCount();

        return 0;
    }



    // Adding new shop
    public void addJob(Jobs jobs) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, jobs.getName()); // Shop Name
        values.put(KEY_NUMBER, jobs.getName()); // Shop Phone Number
        values.put(KEY_TITLE, jobs.getTitle()); // Shop Phone Number
        values.put(KEY_JOB, jobs.getJob());
        values.put(KEY_PEYMENT, jobs.getPayment());
        values.put(KEY_INFO, jobs.getInfo());
        values.put(KEY_ADDRESS, jobs.getAddress());
        values.put(KEY_TEL, jobs.getTel());
        values.put(KEY_JDATE, jobs.getJdate());

// Inserting Row
        db.insert(MY_TABLE, null, values);
        db.close(); // Closing database connection
    }

    // Getting one shop
    public Jobs getJob(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MY_TABLE, new String[] { KEY_ID,
                        KEY_NAME, KEY_NUMBER, KEY_TITLE,KEY_JOB,KEY_PEYMENT,KEY_INFO,KEY_ADDRESS,KEY_TEL,KEY_JDATE }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Jobs contact = new Jobs(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),cursor.getString(3),
                cursor.getString(4),cursor.getString(5),cursor.getString(6),
                cursor.getString(7),cursor.getString(8),cursor.getString(9));
// return shop
        return contact;
    }

    // Getting All Shops
    public List<Jobs> getAllJobs() {
        List<Jobs> jobList = new ArrayList<Jobs>();
// Select All Query
        String selectQuery = "SELECT * FROM " + MY_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Jobs events_Service = new Jobs();
                events_Service.setId(Integer.parseInt(cursor.getString(0)));
                events_Service.setName(cursor.getString(1));
                events_Service.setNumber(cursor.getString(2));
                events_Service.setTitle(cursor.getString(3));
                events_Service.setJob(cursor.getString(4));
                events_Service.setPayment(cursor.getString(5));
                events_Service.setInfo(cursor.getString(6));
                events_Service.setAddress(cursor.getString(7));
                events_Service.setTel(cursor.getString(8));
                events_Service.setJdate(cursor.getString(9));

// Adding contact to list
                jobList.add(events_Service);
            } while (cursor.moveToNext());
        }
        return jobList;
    }

    public Cursor searchByInputText(String inputText) throws SQLException {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * from " + MY_TABLE +
                " WHERE " + KEY_NAME + " LIKE '" + inputText + "';";

        Cursor mCursor = db.rawQuery(query,null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
}