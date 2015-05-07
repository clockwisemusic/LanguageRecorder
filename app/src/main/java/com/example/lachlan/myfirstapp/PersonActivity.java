package com.example.lachlan.myfirstapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.lachlan.myfirstapp.code.DatabaseHelper;
import com.example.lachlan.myfirstapp.code.Person;

public class PersonActivity extends ActionBarActivity {

    private EditText nameEditText;
    private EditText ageEditText;
    private EditText locationEditText;
    private EditText locationYearsEditText;
    private AutoCompleteTextView firstLanguageAutocomplete;
    private AutoCompleteTextView secondLanguageAutocomplete;
    private AutoCompleteTextView thirdLanguageAutocomplete;
    private AutoCompleteTextView otherLanguagesAutocomplete;
    private Spinner genderSpinner;
    private Spinner educatedToSpinner;

    private int personId;
    private Boolean editMode = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);

        nameEditText = (EditText)findViewById(R.id.nameEditText);
        ageEditText = (EditText)findViewById(R.id.ageEditText);
        locationEditText = (EditText)findViewById(R.id.locationEditText);
        locationYearsEditText = (EditText)findViewById(R.id.locationYearsEditText);
        firstLanguageAutocomplete = (AutoCompleteTextView)findViewById(R.id.autocomplete_first_language);
        secondLanguageAutocomplete = (AutoCompleteTextView)findViewById(R.id.autocomplete_second_language);
        thirdLanguageAutocomplete = (AutoCompleteTextView)findViewById(R.id.autocomplete_third_language);
        otherLanguagesAutocomplete = (AutoCompleteTextView)findViewById(R.id.autocomplete_other_languages);
        genderSpinner = (Spinner)findViewById(R.id.gender_spinner);
        educatedToSpinner = (Spinner)findViewById(R.id.educated_to_spinner);

        populateReferenceData();

        populatePersonDetails();

        getWindow().setSoftInputMode(android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    private void populateReferenceData() {
        String[] genders = { "", "Male", "Female" };
        String[] educatedTo = { "", "Primary", "Secondary", "University" };
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
        secondLanguageAutocomplete.setAdapter(adapter);
        thirdLanguageAutocomplete.setAdapter(adapter);
        otherLanguagesAutocomplete.setAdapter(adapter);

    }

    private void populatePersonDetails() {
        Intent intent = getIntent();
        personId = intent.getIntExtra(HomeActivity.INTENT_PERSONID, -1);

        if (personId != -1) {
            editMode = true;
            DatabaseHelper db = new DatabaseHelper(getApplicationContext());
            Person person = db.getPerson(personId);
            if (person != null) {
                nameEditText.setText(person.name);
                ageEditText.setText(String.valueOf(person.age));

                if (person.gender.equalsIgnoreCase("Male")) {
                    genderSpinner.setSelection(1);
                }
                if (person.gender.equalsIgnoreCase("Female")) {
                    genderSpinner.setSelection(2);
                }
                locationEditText.setText(person.livesin);
                locationYearsEditText.setText(String.valueOf(person.livesinyears));
                firstLanguageAutocomplete.setText(person.firstlanguage);
                secondLanguageAutocomplete.setText(person.secondlanguage);
                thirdLanguageAutocomplete.setText(person.thirdlanguage);
                otherLanguagesAutocomplete.setText(person.otherlanguages);
                if (person.education.equalsIgnoreCase("Primary")) {
                    educatedToSpinner.setSelection(1);
                }
                if (person.education.equalsIgnoreCase("Secondary")) {
                    educatedToSpinner.setSelection(2);
                }
                if (person.education.equalsIgnoreCase("University")) {
                    educatedToSpinner.setSelection(3);
                }
            }
            getWindow().setTitle("Edit person");
        }

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

        Person person = new Person();

        person.personid = personId;
        person.name = nameEditText.getText().toString();
        person.age = Integer.parseInt( ageEditText.getText().toString());
        person.gender = genderSpinner.getSelectedItem().toString();
        person.livesin = locationEditText.getText().toString();
        person.livesinyears = Integer.parseInt(locationYearsEditText.getText().toString());
        person.firstlanguage = firstLanguageAutocomplete.getText().toString();
        person.secondlanguage = secondLanguageAutocomplete.getText().toString();
        person.thirdlanguage = thirdLanguageAutocomplete.getText().toString();
        person.otherlanguages = otherLanguagesAutocomplete.getText().toString();
        person.education = educatedToSpinner.getSelectedItem().toString();

        if ( person.name.trim().length() > 0) {

            DatabaseHelper db = new DatabaseHelper(getApplicationContext());

            if (editMode) {
                db.updatePerson(person);
            } else {
                db.insertPerson(person);
            }
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
    }
}
