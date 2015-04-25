package com.example.lachlan.myfirstapp;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.media.MediaRecorder;
import android.media.MediaPlayer;
import java.io.IOException;


public class MyActivity extends ActionBarActivity {

    public final static String EXTRA_MESSAGE = "com.mycompany.myfirstapp.MESSAGE";

    // state variables
    private int picture = 1;
    private boolean recording = false;
    private boolean playing = false;
    private String[] itemNames = new String[5];

    // the media players/recorders
    private MediaRecorder mRecorder = null;
    private MediaPlayer   mPlayer = null;

    // form controls
    private Button stopbutton;
    private Button recordbutton;
    private Button playbutton;
    private EditText editText;
    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        stopbutton = (Button)findViewById(R.id.stopbutton);
        recordbutton = (Button)findViewById(R.id.recordbutton);
        playbutton = (Button)findViewById(R.id.playbutton);
        editText = (EditText)findViewById(R.id.edit_message);
        imageView = (ImageView) findViewById(R.id.myimageview);

        stopbutton.setEnabled(false);

        showPicture();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
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

    public void playButton(android.view.View view)
    {
        if (!playing && !recording) {
            recordbutton.setEnabled(false);
            playbutton.setEnabled(false);
            stopbutton.setEnabled(true);
            playing = true;
            startPlaying();
        }
    }

    public void recordButton(android.view.View view){

        if (!recording && !playing) {
            recordbutton.setEnabled(false);
            playbutton.setEnabled(false);
            stopbutton.setEnabled(true);
            recording = true;
            startRecording();
        }

        /*Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText)findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);*/

        //ImageView imageView = (ImageView) findViewById(R.id.myimageview);
        //imageView.setImageResource(R.drawable.me);
    }
    public void stopButton(android.view.View view) {
        handleStopButton();
    }

    private void handleStopButton() {

        if (recording || playing) {
            if (recording) {
                recording = false;
                stopRecording();
            }
            if (playing) {
                playing = false;
                stopPlaying();
            }
            recordbutton.setEnabled(true);
            playbutton.setEnabled(true);
            stopbutton.setEnabled(false);
        }
    }

    public void backButton(android.view.View view){

        itemNames[picture] = editText.getText().toString();

        picture--;
        if (picture < 1) picture = 4;
        showPicture();
    }
    public void nextButton(android.view.View view){

        itemNames[picture] = editText.getText().toString();

        picture++;
        if (picture > 4) picture = 1;
        showPicture();
    }

    private void showPicture() {

        int pictureToUse = 0;

        if (picture == 1) {
            pictureToUse = R.drawable.church;
        }
        if (picture == 2) {
            pictureToUse = R.drawable.goat;
        }
        if (picture == 3) {
            pictureToUse = R.drawable.rooster;
        }
        if (picture == 4) {
            pictureToUse = R.drawable.spider;
        }

        imageView.setImageResource(pictureToUse);

        String itemName = itemNames[picture];
        editText.setText(itemName);

    }

    private String getFilename() {
        String mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest" + picture + ".3gp";
        return mFileName;

    }

    private void startPlaying()
    {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                                        @Override
                                                        public void onCompletion(MediaPlayer arg0) {
                                                            handleStopButton();
                                                        }
                                                    });
            mPlayer.setDataSource(getFilename());
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
        }
    }

    private void stopPlaying()
    {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording()
    {

        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(getFilename());
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException e) {

        }

        mRecorder.start();
    }

    private void stopRecording()
    {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

}
