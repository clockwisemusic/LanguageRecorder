package com.example.lachlan.myfirstapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.lachlan.myfirstapp.code.DatabaseHelper;
import com.example.lachlan.myfirstapp.code.Person;


public class HomeActivity extends ActionBarActivity {

    public final static String INTENT_PERSONID = "com.example.lachlan.myfirstapp.personid";

    Spinner selectPersonSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        selectPersonSpinner = (Spinner) findViewById(R.id.person_spinner);

        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        Person[] people = db.getPeople();

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, people);
        selectPersonSpinner.setAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void startButton(android.view.View view) {
        Intent intent = new Intent(this, CaptureActivity.class);
        startActivity(intent);
    }

    public void newPersonButton(android.view.View view) {
        Intent intent = new Intent(this, PersonActivity.class);
        startActivity(intent);
    }

    public void editPersonButton(android.view.View view) {
        Person p = (Person)selectPersonSpinner.getSelectedItem();
        if (p != null) {
            Intent intent = new Intent(this, PersonActivity.class);
            intent.putExtra(INTENT_PERSONID, p.personid);
            startActivity(intent);
        }
    }
}