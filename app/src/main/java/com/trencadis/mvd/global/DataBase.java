package com.trencadis.mvd.global;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class DataBase extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE = "mbd_table";

    // Database Name
    private static final String DATABASE_NAME = "mvd_db";

    private static final String ID = "_id";
    private static final String LBB_ID = "lbb_id";
    private static final String SENSOR_ID = "sensor_id";
    private static final String TYPE = "type";
    private static final String VALUE_TO = "value_to";
    private static final String VALUE_FROM = "value_from";
    private static final String LAST_MESSAGE = "last_message";
    private static final String TIMESTAMP = "timestamp";
    private static final String TIMESTAMP_SENT = "timestamp_sent";

    private static DataBase dataBase = null;

    public static DataBase getInstance(Context context){
        if(dataBase == null){
            dataBase = new DataBase(context);
        }
        return dataBase;
    }

    private DataBase(Context context) {
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
                + LAST_MESSAGE + " TEXT,"
                + TIMESTAMP + " TEXT,"
                + TIMESTAMP_SENT + " TEXT" + ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addEntry(Entry entry){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(LBB_ID, entry.getLbbId());
        values.put(SENSOR_ID, entry.getSensorId());
        values.put(TYPE, entry.getType());
        values.put(VALUE_TO, entry.getValueTo());
        values.put(VALUE_FROM, entry.getValueFrom());
        values.put(LAST_MESSAGE, entry.getLastMessage());
        values.put(TIMESTAMP, entry.getTimestamp());
        values.put(TIMESTAMP_SENT, "");

        // Inserting Row
        db.insert(TABLE, null, values);
        db.close(); // Closing database connection
    }

    public void setEntrySent(Entry entry){
        String selectQuery = "SELECT  * FROM " + TABLE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        int id;

        cursor.moveToFirst();

        while (!cursor.isAfterLast()){
            String lbbId = entry.getLbbId();
            String sensorId = entry.getSensorId();
            if (lbbId.equalsIgnoreCase(cursor.getString(1))
                    && sensorId.equalsIgnoreCase(cursor.getString(2))
                    && entry.getTimestamp().equalsIgnoreCase(cursor.getString(7))
                    && entry.getType().equalsIgnoreCase(cursor.getString(3))
                    && entry.getValueTo().equalsIgnoreCase(cursor.getString(4))
                    && entry.getValueFrom().equalsIgnoreCase(cursor.getString(5))
                    && entry.getLastMessage().equalsIgnoreCase(cursor.getString(6))){
                id = cursor.getInt(0);

                String strFilter = ID + "=" + id;
                ContentValues args = new ContentValues();
                args.put(TIMESTAMP_SENT, "" + (System.currentTimeMillis() / 1000));
                db.update(TABLE, args, strFilter, null);

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
