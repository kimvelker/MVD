package com.trencadis.mvd.global;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Kimv on 4/8/2015.
 */
public class TempDataBase extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE = "temp_table";

    // Database Name
    private static final String DATABASE_NAME = "temp_db";

    private static final String ID = "id";
    private static final String LBB_ID = "lbb_id";
    private static final String SENSOR_ID = "sensor_id";
    private static final String TYPE = "type";
    private static final String VALUE_TO = "value_to";
    private static final String VALUE_FROM = "value_from";
    private static final String LAST_MESSAGE = "last_message";
    private static final String TIMESTAMP = "timestamp";


    public TempDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE + "("
                + ID + " INTEGER PRIMARY KEY,"
                + LBB_ID + " TEXT,"
                + SENSOR_ID + " TEXT,"
                + TYPE + " TEXT,"
                + VALUE_TO + " TEXT,"
                + VALUE_FROM + " TEXT,"
                + LAST_MESSAGE + " TEXT"
                + TIMESTAMP + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addEntry(Entry entry){

        deleteEntryIfItExists(entry);

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(LBB_ID, entry.getLbbId());
        values.put(SENSOR_ID, entry.getSensorId());
        values.put(TYPE, entry.getType());
        values.put(VALUE_TO, entry.getValueTo());
        values.put(VALUE_FROM, entry.getValueFrom());
        values.put(LAST_MESSAGE, entry.getLastMessage());
        values.put(TIMESTAMP, entry.getTimestamp());

        // Inserting Row
        db.insert(TABLE, null, values);
        db.close(); // Closing database connection
    }

    private void deleteEntryIfItExists(Entry entry) {
        String selectQuery = "SELECT  * FROM " + TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        int id = 0;

        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            String lbbId = entry.getLbbId();
            String sensorId = entry.getSensorId();
            if (lbbId.equalsIgnoreCase(cursor.getString(1))
                    && sensorId.equalsIgnoreCase(cursor.getString(2))){
                id = cursor.getInt(0);
                String whereClause = "id"+"=?";

                String[]whereArgs = new String[] {"" + id};

                db.delete(TABLE, whereClause , whereArgs);
                cursor.close();
                db.close();
                return;
            }
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

    }

    public ArrayList<Entry> getEntries(){
        ArrayList<Entry> entries = new ArrayList<>();

        String lbbId, sensorId, type, valueTo, valueFrom, lastMessage, timestamp;


        String selectQuery = "SELECT  * FROM " + TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row

        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            lbbId = cursor.getString(1);
            sensorId = cursor.getString(2);
            type = cursor.getString(3);
            valueTo = cursor.getString(4);
            valueFrom = cursor.getString(5);
            lastMessage = cursor.getString(6);
            timestamp = cursor.getString(7);

            entries.add(new Entry(lbbId, sensorId, type, valueTo, valueFrom, lastMessage, timestamp));

            cursor.moveToNext();

        }

        cursor.close();
        db.close();

        return entries;
    }

    public void resetTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE, null, null);
        db.close();
    }
}
