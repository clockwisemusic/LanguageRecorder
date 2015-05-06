package com.example.lachlan.myfirstapp.code;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

/**
 * Created by lachlan on 4/05/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "lachtest";
    private static final String PERSON_TABLE_NAME = "person";

    String createPersonTable = "create table " + PERSON_TABLE_NAME + " " +
            "(personid integer primary key autoincrement," +
            "name varchar," +
            "age integer," +
            "location varchar);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createPersonTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int fromVersion, int toVersion) {

    }

    public void insertPerson(String name, int age, String location) {

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("age", age);
        values.put("location", location);

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(PERSON_TABLE_NAME, null, values);

    }

    public String[] getPeople() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("select name from " + PERSON_TABLE_NAME, null);

        List<String> items = new ArrayList<String>();

        while (c.moveToNext()) {
            items.add(c.getString(0));
        }

        return (String[]) items.toArray(new String[items.size()]);
    }

    public Cursor getPeopleCursor() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery("select name from " + PERSON_TABLE_NAME, null);
    }

}