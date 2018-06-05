package com.idpz.instacity.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.idpz.instacity.Area;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by h on 3/9/2017.
 */

public class DBAreaHandler extends SQLiteOpenHelper {


    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "area.db";
    // Contacts table name
    private static final String MY_TABLE = "areas";
    // Shops Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_AENAME = "aename";
    private static final String KEY_AFNAME = "afname";
    private static final String KEY_ALAT = "alat";
    private static final String KEY_ALNG = "alng";
    private static final String KEY_DIAMETER = "adiameter";
    private static final String KEY_SERVER = "server";
    private static final String KEY_ZOOM = "zoom";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_PIC = "pic";
    private static final String KEY_ALL = "*";


    public DBAreaHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MY_TABLE = "CREATE TABLE " + MY_TABLE + " ("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_AENAME + " TEXT,"
                + KEY_AFNAME + " TEXT,"+ KEY_ALAT + " TEXT,"+ KEY_ALNG + " TEXT,"
                + KEY_DIAMETER + " TEXT,"+ KEY_SERVER + " TEXT,"
                + KEY_ZOOM + " TEXT,"+ KEY_DESCRIPTION + " TEXT,"+ KEY_PIC + " TEXT)";
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
    public void addJob(Area jobs) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_AENAME, jobs.getAename()); // area english name
        values.put(KEY_AFNAME, jobs.getAfname()); // area farsi name
        values.put(KEY_ALAT, String.valueOf(jobs.getAlat())); // area latitude
        values.put(KEY_ALNG, String.valueOf(jobs.getAlng()));
        values.put(KEY_DIAMETER, String.valueOf(jobs.getAdiameter()));
        values.put(KEY_SERVER, jobs.getServer());
        values.put(KEY_ZOOM, String.valueOf(jobs.getZoom()));
        values.put(KEY_DESCRIPTION, jobs.getDescription());
        values.put(KEY_PIC, jobs.getPic());

// Inserting Row
        db.insert(MY_TABLE, null, values);
        db.close(); // Closing database connection
    }

    // Getting one shop
    public Area getJob(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(MY_TABLE, new String[] { KEY_ID,
                        KEY_AENAME, KEY_AFNAME, KEY_ALAT,KEY_ALNG,KEY_DIAMETER,KEY_SERVER,KEY_ZOOM,KEY_DESCRIPTION,KEY_PIC }, KEY_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Area contact = new Area(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2),Float.valueOf(cursor.getString(3)),
                Float.valueOf(cursor.getString(4)),Float.valueOf(cursor.getString(5)),cursor.getString(6),
                Integer.valueOf(cursor.getString(7)),0,cursor.getString(8),cursor.getString(9));
// return shop
        return contact;
    }

    // Getting All Shops
    public List<Area> getAllAreas() {
        List<Area> areaList = new ArrayList<Area>();
// Select All Query
        String selectQuery = "SELECT * FROM " + MY_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Area events_Service = new Area();
                events_Service.setId(Integer.parseInt(cursor.getString(0)));
                events_Service.setAename(cursor.getString(1));
                events_Service.setAfname(cursor.getString(2));
                events_Service.setAlat(Float.valueOf(cursor.getString(3)));
                events_Service.setAlng(Float.valueOf(cursor.getString(4)));
                events_Service.setAdiameter(Float.valueOf(cursor.getString(5)));
                events_Service.setServer(cursor.getString(6));
                events_Service.setZoom(Integer.valueOf(cursor.getString(7)));
                events_Service.setDescription(cursor.getString(8));
                events_Service.setPic(cursor.getString(9));

// Adding contact to list
                areaList.add(events_Service);
            } while (cursor.moveToNext());
        }
        return areaList;
    }

    public Cursor searchByInputText(String inputText) throws SQLException {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * from " + MY_TABLE +
                " WHERE " + KEY_AENAME + " LIKE '" + inputText + "';";

        Cursor mCursor = db.rawQuery(query,null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;

    }
}