package com.example.lachlan.myfirstapp;

import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AppKeyPair;
import com.example.lachlan.myfirstapp.code.DatabaseHelper;
import com.example.lachlan.myfirstapp.code.DiskSpace;
import com.example.lachlan.myfirstapp.code.DropBoxSettings;
import com.example.lachlan.myfirstapp.code.Person;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
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


public class SettingsActivity extends ActionBarActivity {

    final static public String PREFS_NAME = "MyPrefsFile";
    private String baseUrl = "http://10.0.0.31";


    // In the class declaration section:
    private DropboxAPI<AndroidAuthSession> mDBApi;
    private boolean isLoggedIn;

    //private Button playbutton;
    private TextView editTextTotalDiskSpace;
    private TextView editTextTotalDiskSpaceFree;
    private TextView dropboxAccessToken;
    private TextView uploadProgressTextView;
    private Button loginButton;
    private Button uploadFileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        editTextTotalDiskSpace = (TextView)findViewById(R.id.total_disk_space_label);
        editTextTotalDiskSpaceFree = (TextView)findViewById(R.id.total_disk_space_free_label);
        dropboxAccessToken = (TextView)findViewById(R.id.dropbox_access_token);
        loginButton = (Button)findViewById(R.id.connect_dropbox_button);
        uploadFileButton = (Button)findViewById(R.id.upload_file_button);
        uploadProgressTextView = (TextView)findViewById(R.id.upload_progres);

        String diskSpace = DiskSpace.totalDiskSpace();
        String freeSpace = DiskSpace.totalAvailableDiskSpace();

        editTextTotalDiskSpace.setText("Total disk size: " + diskSpace);
        editTextTotalDiskSpaceFree.setText("Total available space: " + freeSpace);

        loggedIn(false);


        AppKeyPair pair = new AppKeyPair(DropBoxSettings.APP_KEY, DropBoxSettings.APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(pair);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);

        progressText = "";
    }

    public void loggedIn(boolean isLogged) {
        isLoggedIn = isLogged;
        uploadFileButton.setEnabled(isLogged);
        loginButton.setText(isLogged ? "Log out of dropbox" : "Log in to dropbox");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_disk_space, menu);
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

    public void connectToDropBox(android.view.View view) {
        if (isLoggedIn) {
            mDBApi.getSession().unlink();
            loggedIn(false);
        } else {

            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            String accessToken = settings.getString("dropboxAccessToken", null);

            if (accessToken == null) {
                mDBApi.getSession().startOAuth2Authentication(SettingsActivity.this);
            } else {
                mDBApi.getSession().setOAuth2AccessToken(accessToken);
                loggedIn(true);
            }

        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (mDBApi == null) {
            Log.e("myfirstapp", "mDBApi is null in resume, what do I do now??");
        } else {
            AndroidAuthSession session = mDBApi.getSession();
            if (session.authenticationSuccessful()) {
                try {
                    session.finishAuthentication();
                    String accessToken = mDBApi.getSession().getOAuth2AccessToken();

                    SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("dropboxAccessToken", accessToken);
                    editor.commit();

                    loggedIn(true);
                } catch (IllegalStateException e) {
                    Log.e("myfirstapp", e.toString());
                }
            }
        }
    }



    public void uploadToServer(android.view.View view) {

        new Thread(new Runnable() {
            public void run() {

                addMessage("starting to upload...");
                addMessage("uploading data");
                uploadData();

                String audioFilename = DiskSpace.getFilename(1);

                doFileUpload(audioFilename);
            }
        }).start();
    }

    private String progressText;

    private void addMessage(String message) {
        Date now = new Date();
        String thetime = DateFormat.getTimeInstance().format(now);
        progressText = thetime + " " + message + "\n" + progressText;
        uploadProgressTextView.post(new Runnable() {
            public void run() {
                uploadProgressTextView.setText(progressText);
            }
        });

    }

    private void uploadData() {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(baseUrl + "/uploaddata.php");

        try {

            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            String json = dbHelper.getAllData();

            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("json", json));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            Log.i("languageapp", e.toString());
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.i("languageapp", e.toString());
        }

    }




    private void doFileUpload(String audioFilename){
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
        String urlString = baseUrl + "/uploadaudio.php";

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
            conn.setRequestProperty("uploaded_file", "test.3gp");
            dos = new DataOutputStream( conn.getOutputStream() );
            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\"test.3gp\"" + lineEnd);
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
        }
        catch (IOException ioe)
        {
            Log.e("LanguageApp", "error: " + ioe.getMessage(), ioe);
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
        }
    }


    public void uploadFile(android.view.View view)
    {
        dropboxAccessToken.setText("Uploading...");

        uploadFileButton.setEnabled(false);

        new Thread(new Runnable() {
            public void run() {

                try {

                    for (int i=1;i<=5;i++) {

                        String filename = DiskSpace.getFilename(i);
                        File file = new File(filename);

                        if (file.exists()) {
                            FileInputStream inputStream = new FileInputStream(file);

                            DropboxAPI.Entry response = mDBApi.putFileOverwrite(
                                    "/audio-" + i + ".3gp", inputStream, file.length(), null);

                            //Log.i("DbExampleLog", "The uploaded file's rev is: " + response.rev);
                        }
                    }

                    dropboxAccessToken.post(new Runnable() {
                        public void run() {
                            dropboxAccessToken.setText("File uploaded!");
                            uploadFileButton.setEnabled(true);
                        }
                    });
                }
                catch (Exception e2) {
                    Log.e("DbExampleLog", e2.toString());

                    dropboxAccessToken.post(new Runnable() {
                        public void run() {
                            dropboxAccessToken.setText("An error occured...");
                            uploadFileButton.setEnabled(true);
                        }
                    });
                }
            }
        }).start();
    }
}

