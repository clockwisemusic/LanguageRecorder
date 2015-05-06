package com.example.lachlan.myfirstapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.lachlan.myfirstapp.code.DatabaseHelper;

public class PersonActivity extends ActionBarActivity {

    private EditText nameEditText;
    private EditText ageEditText;
    private EditText locationEditText;
    private AutoCompleteTextView firstLanguageAutocomplete;
    private AutoCompleteTextView otherLanguageAutocomplete;
    private Spinner genderSpinner;
    private Spinner educatedToSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        nameEditText = (EditText)findViewById(R.id.nameEditText);
        ageEditText = (EditText)findViewById(R.id.ageEditText);
        locationEditText = (EditText)findViewById(R.id.locationEditText);
        firstLanguageAutocomplete = (AutoCompleteTextView)findViewById(R.id.autocomplete_first_language);
        otherLanguageAutocomplete = (AutoCompleteTextView)findViewById(R.id.autocomplete_other_language);
        genderSpinner = (Spinner)findViewById(R.id.gender_spinner);
        educatedToSpinner = (Spinner)findViewById(R.id.educated_to_spinner);

        String[] genders = { "Male", "Female" };
        String[] educatedTo = { "Primary", "Secondary", "University" };
        String[] languages = getResources().getStringArray(R.array.languages_array);

        ArrayAdapter<String> genderSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, genders);

        ArrayAdapter<String> educatedToSpinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, educatedTo);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, languages);

        genderSpinner.setAdapter(genderSpinnerAdapter);
        educatedToSpinner.setAdapter(educatedToSpinnerAdapter);

        firstLanguageAutocomplete.setAdapter(adapter);
        otherLanguageAutocomplete.setAdapter(adapter);

        getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_person, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void okButton(android.view.View view) {

        String name = nameEditText.getText().toString();
        String age = ageEditText.getText().toString();
        String location = locationEditText.getText().toString();


        if (    age.trim().length() > 0 &&
                name.trim().length() > 0 &&
                location.trim().length() > 0) {

            DatabaseHelper db = new DatabaseHelper(getApplicationContext());


            int personAge = Integer.parseInt(age);

            db.insertPerson(name, personAge, location);

            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }
}
