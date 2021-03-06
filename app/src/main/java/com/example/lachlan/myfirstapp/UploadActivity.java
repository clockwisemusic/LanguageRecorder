package com.example.lachlan.myfirstapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.example.lachlan.myfirstapp.code.DatabaseHelper;
import com.example.lachlan.myfirstapp.code.DiskSpace;
import com.example.lachlan.myfirstapp.code.Person;
import com.example.lachlan.myfirstapp.code.PersonWord;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class UploadActivity extends ActionBarActivity {

    private String baseUrl = "http://lachlanbarclay.net";
    private TextView uploadProgressTextView;
    private Button uploadFileButton;
    private String progressText;
    private TextView dataInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadFileButton = (Button)findViewById(R.id.upload_file_button);
        uploadProgressTextView = (TextView)findViewById(R.id.upload_progress);
        dataInfo = (TextView)findViewById(R.id.data_info);

        DatabaseHelper db = new DatabaseHelper(getApplicationContext());
        Person[] people = db.getPeople();

        String numberOfPeopleText = getResources().getString(R.string.upload_number_of_people_label);
        dataInfo.setText(numberOfPeopleText + ": " + people.length);

        progressText = "";
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload, menu);
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





    public void uploadToServer(android.view.View view) {

//        addMessage( getResources().getString(R.string.upload_starting_upload) + "...");

        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                addNotification("Language App", "Upload Complete!");
            }
        }, 5000);*/

  //      return;
        new Thread(new Runnable() {
            public void run() {

                addMessage( getResources().getString(R.string.upload_starting_upload) + "...");

                uploadDataToServer();


            }
        }).start();
    }

    private void addNotification(String title, String message) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.livru_timor_logo)
                        .setContentTitle(title)
                        .setContentText(message);

        mBuilder.setAutoCancel(true);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, UploadActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(UploadActivity.class);

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
            stackBuilder.getPendingIntent(
                    0,
                    PendingIntent.FLAG_UPDATE_CURRENT
            );

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        int notifyId = 1;

        // notifyId allows you to update the notification later on.
        mNotificationManager.notify(notifyId, mBuilder.build());
    }

    private void addMessage(String message) {
        Date now = new Date();
        String thetime = DateFormat.getTimeInstance().format(now);
        progressText += thetime + " " + message + "\n";
        uploadProgressTextView.post(new Runnable() {
            public void run() {
                uploadProgressTextView.setText(progressText);
            }
        });

    }

    private void uploadDataToServer() {

        try {

            uploadDbData();
            uploadAudioData();

            String success = getResources().getString(R.string.upload_upload_successful);
            addMessage(success);
            addNotification("Language App", success);

        }
        catch (Exception e) {
            String failed = getResources().getString(R.string.upload_upload_failed);
            addMessage(failed + ": " + e.getMessage());
            addNotification("Language App", failed);
        }

    }


    private void uploadDbData() throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(baseUrl + "/api/languagedata");

        try {

            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            String json = dbHelper.getAllData();

            Log.i("LanguageApp", json);

            StringEntity stringEntity = new StringEntity(json);

            httppost.setEntity(stringEntity);
            httppost.setHeader("Accept", "application/json");
            httppost.setHeader("Content-type", "application/json");

            HttpResponse response = httpclient.execute(httppost);

            //TODO do I need to read the response?
            //return uploadAudioData();

        } catch (ClientProtocolException e) {
            //addNotification("Language App", "Data upload failed");
            Log.i("languageapp", e.toString());
            throw new Exception(e.getMessage());
        } catch (IOException e) {
            //addNotification("Language App", "Data upload failed");
            Log.i("languageapp", e.toString());
            throw new Exception(e.getMessage());
        }
    }

    private void uploadAudioData() throws Exception {

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        PersonWord[] words = dbHelper.getAllWords();

        if (words == null) {
            return ; //true;
        }

        for (int i=0;i<words.length;i++) {
            String audiofilename = words[i].audiofilename;

            if (audiofilename != null)
            {
                if (audiofilename.length() > 0)
                {
                    String basePath = DiskSpace.getAudioFileBasePath();
                    File f = new File(basePath + audiofilename);

                    if (f.exists()) {

                        String msg = getResources().getString(R.string.upload_uploading_audio);
                        if (words[i].word != null && words.length > 0) {
                            addMessage(msg + ": " + audiofilename);
                        }

                        doFileUpload(basePath + audiofilename, audiofilename);
                    }
                }
            }
        }
    }


    private void doFileUpload(String audioFilename, String shortName) throws Exception {
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        DataInputStream inStream = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1*1024*1024;
        String responseFromServer = "";
        String urlString = baseUrl + "/api/audiodata";

        String failureMessage = null;

        try
        {

            //------------------ CLIENT REQUEST
            FileInputStream fileInputStream = new FileInputStream(new File(audioFilename) );
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
            conn.setRequestProperty("uploaded_file", shortName);
            dos = new DataOutputStream( conn.getOutputStream() );
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"" + shortName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);
            // create a buffer of maximum size
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];
            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0)
            {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            // send multipart form data necesssary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
            // close streams
            Log.i("LanguageApp", "File is written");
            fileInputStream.close();
            dos.flush();
            dos.close();
        }
        catch (MalformedURLException ex)
        {
            Log.e("LanguageApp", "error: " + ex.getMessage(), ex);
            failureMessage = ex.getMessage(); //throw new Exception(ex.getMessage());
        }
        catch (IOException ioe)
        {
            Log.e("LanguageApp", "error: " + ioe.getMessage(), ioe);
            failureMessage = ioe.getMessage(); //throw new Exception(ioe.getMessage());
        }


        //------------------ read the SERVER RESPONSE
        try {
            inStream = new DataInputStream ( conn.getInputStream() );
            String str;

            while (( str = inStream.readLine()) != null)
            {
                Log.i("LanguageApp","Server Response: "+str);
            }
            inStream.close();

        }
        catch (IOException ioex){
            Log.e("LanguageApp", "error: " + ioex.getMessage(), ioex);

            if (failureMessage != null) {
                failureMessage = failureMessage.concat(" " + ioex.getMessage());
            } else  {
                failureMessage = ioex.getMessage();
            }
            throw new Exception(failureMessage);
        }
    }


}
