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
            "name varchar, " +
            "age integer, " +
            "gender varchar, "  +
            "livesin varchar, "  +
            "livesinyears integer, "  +
            "firstlanguage varchar, "  +
            "secondlanguage varchar, "  +
            "thirdlanguage varchar, "  +
            "otherlanguages varchar, "  +
            "education varchar);";

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

    public void insertPerson(Person person) {

        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL("drop table person;");
//       db.execSQL(createPersonTable);

        ContentValues values = new ContentValues();
        values.put("name", person.name  );
        values.put("age", person.age);
        values.put("gender", person.gender);
        values.put("livesin", person.livesin);
        values.put("livesinyears", person.livesinyears);
        values.put("firstlanguage", person.firstlanguage);
        values.put("secondlanguage", person.secondlanguage);
        values.put("thirdlanguage", person.thirdlanguage);
        values.put("otherlanguages", person.otherlanguages);
        values.put("education", person.education);

        db.insert(PERSON_TABLE_NAME, null, values);

    }

    public void updatePerson(Person person) {

        ContentValues values = new ContentValues();
        values.put("name", person.name  );
        values.put("age", person.age);
        values.put("gender", person.gender);
        values.put("livesin", person.livesin);
        values.put("livesinyears", person.livesinyears);
        values.put("firstlanguage", person.firstlanguage);
        values.put("secondlanguage", person.secondlanguage);
        values.put("thirdlanguage", person.thirdlanguage);
        values.put("otherlanguages", person.otherlanguages);
        values.put("education", person.education);

        SQLiteDatabase db = this.getWritableDatabase();
        db.update(PERSON_TABLE_NAME, values, "personid = " + String.valueOf(person.personid), null);

    }

    public Person[] getPeople() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("select personid, name, age, gender, livesin, livesinyears,  " +
                        "firstlanguage, secondlanguage, thirdlanguage, otherlanguages, education " +
                        "from " + PERSON_TABLE_NAME, null);

        List<Person> items = new ArrayList<Person>();

        while (c.moveToNext()) {
            Person p = new Person(
                    c.getInt(0),
                    c.getString(1),
                    c.getInt(2),
                    c.getString(3),
                    c.getString(4),
                    c.getInt(5),
                    c.getString(6),
                    c.getString(7),
                    c.getString(8),
                    c.getString(9),
                    c.getString(10)
                    );
            items.add(p);
        }

        return (Person[]) items.toArray(new Person[items.size()]);
    }

    public Person getPerson(int personId) {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("select personid, name, age, gender, livesin, livesinyears,  " +
                "firstlanguage, secondlanguage, thirdlanguage, otherlanguages, education from " + PERSON_TABLE_NAME + "" +
                " where personid = " + personId, null);

        Person p = null;
        if (c.moveToNext()) {
            p = new Person(
                    c.getInt(0),
                    c.getString(1),
                    c.getInt(2),
                    c.getString(3),
                    c.getString(4),
                    c.getInt(5),
                    c.getString(6),
                    c.getString(7),
                    c.getString(8),
                    c.getString(9),
                    c.getString(10));
        }

        return p;
    }

}