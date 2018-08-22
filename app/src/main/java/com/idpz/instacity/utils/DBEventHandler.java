package com.idpz.instacity.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.CalendarContract;

import com.idpz.instacity.models.Evnt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by h on 3/8/2017.
 */

public class DBEventHandler extends SQLiteOpenHelper {


    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "evnt.db";
    // Contacts table name
    private static final String MY_TABLE = "events";
    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_OWNER = "owner";
    private static final String KEY_CONTACT = "contact";
    private static final String KEY_PLACE = "place";
    private static final String KEY_EDATE = "edate";
    private static final String KEY_ETIME = "etime";
    private static final String KEY_INFO = "info";
    private static final String KEY_MEMO = "memo";
    private static final String KEY_ALL = "*";


    public DBEventHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MY_TABLE = "CREATE TABLE " + MY_TABLE + " ("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_OWNER + " TEXT,"+ KEY_CONTACT + " TEXT,"+ KEY_PLACE + " TEXT,"
                + KEY_EDATE + " TEXT,"+ KEY_ETIME + " TEXT,"
                + KEY_INFO + " TEXT,"+ KEY_MEMO + " TEXT)";
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
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MY_TABLE, new String[]{KEY_ALL }, null,
                null , null, null, null, null);
        if (cursor != null)
            return cursor.getCount();

        return 0;
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
    public void addEvent(Evnt events) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, events.getName()); // Shop Name
        values.put(KEY_OWNER, events.getOwner()); // Shop Phone Number
        values.put(KEY_CONTACT, events.getContact()); // Shop Phone Number
        values.put(KEY_PLACE, events.getPlace());
        values.put(KEY_EDATE, events.getEdate());
        values.put(KEY_ETIME, events.getEtime());
        values.put(KEY_INFO, events.getInfo());
        values.put(KEY_MEMO, events.getMemo());
// Inserting Row
        db.insert(MY_TABLE, null, values);
        db.close(); // Closing database connection
    }

    // Getting one shop
    public Evnt getEvent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MY_TABLE, new String[] { KEY_ID,
                        KEY_NAME, KEY_OWNER, KEY_CONTACT,KEY_PLACE,KEY_EDATE,KEY_ETIME,KEY_INFO,KEY_MEMO }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Evnt contact = new Evnt(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),cursor.getString(3),
                cursor.getString(4),cursor.getString(5),cursor.getString(6),
                cursor.getString(7),cursor.getString(8));
// return shop
        return contact;
    }

    // Getting All Shops
    public List<Evnt> getAllevents() {
        List<Evnt> evetsList = new ArrayList<Evnt>();
// Select All Query
        String selectQuery = "SELECT * FROM " + MY_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Evnt events_Service = new Evnt();
                events_Service.setId(Integer.parseInt(cursor.getString(0)));
                events_Service.setName(cursor.getString(1));
                events_Service.setOwner(cursor.getString(2));
                events_Service.setContact(cursor.getString(3));
                events_Service.setPlace(cursor.getString(4));
                events_Service.setEdate(cursor.getString(5));
                events_Service.setEtime(cursor.getString(6));
                events_Service.setInfo(cursor.getString(7));
                events_Service.setMemo(cursor.getString(8));

// Adding contact to list
                evetsList.add(events_Service);
            } while (cursor.moveToNext());
        }
        return evetsList;
    }
}