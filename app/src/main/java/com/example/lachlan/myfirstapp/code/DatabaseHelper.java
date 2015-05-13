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
    private static final String PERSONCAPTURE_TABLE_NAME = "personcapture";

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

    String createPersonCaptureTable = "create table " + PERSONCAPTURE_TABLE_NAME + " " +
            "(personcaptureid integer primary key autoincrement," +
            "personid integer, " +
            "itemid integer, " +
            "word varchar);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createPersonTable);
        db.execSQL(createPersonCaptureTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int fromVersion, int toVersion) {

    }

    private ContentValues getDbValues(Person person) {
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
        return values;
    }

    public void insertPerson(Person person) {

        SQLiteDatabase db = this.getWritableDatabase();
//        db.execSQL(createPersonCaptureTable);
//        db.execSQL("drop table person;");
//       db.execSQL(createPersonTable);

        ContentValues values = getDbValues(person);

        long personid = db.insert(PERSON_TABLE_NAME, null, values);

        person.personid = (int)personid;
    }

    public void updatePerson(Person person) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = getDbValues(person);

        db.update(PERSON_TABLE_NAME, values, "personid = " + String.valueOf(person.personid), null);

    }

    public void saveWord(int personid, int pictureid, String word) {

        SQLiteDatabase db = this.getWritableDatabase();

/*        db.execSQL("drop table " + PERSONCAPTURE_TABLE_NAME + ";");
        db.execSQL("drop table " + PERSON_TABLE_NAME + ";");
        db.execSQL(createPersonTable);
        db.execSQL(createPersonCaptureTable);*/

        String sql = "select personcaptureid from " + PERSONCAPTURE_TABLE_NAME +
                " where personid = " + String.valueOf(personid) + " and " +
                " itemid = " + String.valueOf(pictureid);

        Cursor c = db.rawQuery(sql, null);

        if (c.moveToNext()) {
            int personcaptureid = c.getInt(0);

            ContentValues values = new ContentValues();
            values.put("word", word );

            db.update(PERSONCAPTURE_TABLE_NAME, values,  "personcaptureid = " + String.valueOf(personcaptureid), null);
        } else {
            if (word != null) {
                ContentValues values = new ContentValues();
                values.put("word", word);
                values.put("personid", personid);
                values.put("itemid", pictureid);
                db.insert(PERSONCAPTURE_TABLE_NAME, null, values);
            }
        }
    }

    public String getWord(int personid, int pictureid) {

        SQLiteDatabase db = this.getReadableDatabase();

        String sql = "select word from " + PERSONCAPTURE_TABLE_NAME +
                " where personid = " + String.valueOf(personid) + " and " +
                " itemid = " + String.valueOf(pictureid);

        Cursor c = db.rawQuery(sql, null);

        if (c.moveToNext()) {
            return c.getString(0);
        }
        return null;
    }

    public Person[] getPeople() {

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("select personid, name, age, gender, livesin, livesinyears,  " +
                        "firstlanguage, secondlanguage, thirdlanguage, otherlanguages, education " +
                        "from " + PERSON_TABLE_NAME, null);

        List<Person> items = new ArrayList<Person>();

        while (c.moveToNext()) {

            Integer age = null;
            Integer livesinyears = null;

            if (!c.isNull(2))
            {
                age = c.getInt(2);
            }
            if (!c.isNull(5)) {
                livesinyears = c.getInt(5);
            }

            Person p = new Person(
                    c.getInt(0),
                    c.getString(1),
                    age,
                    c.getString(3),
                    c.getString(4),
                    livesinyears,
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

            Integer age = null;
            Integer livesinyears = null;

            if (!c.isNull(2)) {
                age = c.getInt(2);
            }
            if (!c.isNull(5)) {
                livesinyears = c.getInt(5);
            }

            p = new Person(
                    c.getInt(0),
                    c.getString(1),
                    age,
                    c.getString(3),
                    c.getString(4),
                    livesinyears,
                    c.getString(6),
                    c.getString(7),
                    c.getString(8),
                    c.getString(9),
                    c.getString(10));
        }

        return p;
    }

}