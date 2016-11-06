package com.github.nlread.quiteasy;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;


/**
 * Created by Benjamin on 11/5/2016.
 */

public class LogInActivity extends AppCompatActivity {

    public final String TAG = "LogIn";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }

    public void attemptLogIn(View v) throws MalformedURLException, IOException{
        EditText usernameText = (EditText) findViewById(R.id.logInUsername);
        String username = usernameText.getText().toString();
        EditText passwordText = (EditText)findViewById(R.id.logInPassword);
        String password = passwordText.getText().toString();

        URL url = new URL(getString(R.string.base_url));
        HttpURLConnection conn = (HttpURLConnection)  url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("Content-Type", "text/json");
        String body = "{\"function\":\"login\",\"username\":\""+username+"\",\"password\":\""+password+"\"}";
        LoginTask task = new LoginTask();
        task.execute(new Object[]{conn,body});
    }


    private class LoginTask extends AsyncTask<Object,Void,HashMap<String,Object>> {

        private HashMap<String,Object> logInParser(InputStream in) throws IOException{
            HashMap<String,Object> map = new HashMap<>();
            JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
            reader.beginObject();
            String successful = reader.nextName();//succesful
            Log.d(TAG,successful);
            map.put(successful,reader.nextBoolean());
            String id = reader.nextName();//id
            Log.d(TAG,id);
            map.put("userID", reader.nextInt());
            String token = reader.nextName();//token
            Log.d(TAG,token);
            map.put("token", reader.nextString());
            reader.close();

            return map;
        }

        @Override
        protected HashMap<String, Object> doInBackground(Object[] params) {
                    HttpURLConnection conn = (HttpURLConnection) params[0];
                    String body = (String)params[1];
                    try {
                        //TODO: getting this to work would be great. I have not been able to test it with the server, but it seems this part below is causing trouble
                        OutputStream out = conn.getOutputStream();
                        Log.d(TAG, "A");
                        BufferedWriter writer = new BufferedWriter(
                                new OutputStreamWriter(out, "UTF-8"));
                        writer.write(body);
                        writer.flush();
                        writer.close();
                        Log.d(TAG, "B");
                        InputStream in = conn.getInputStream();
                        Log.d(TAG, "C");
                        HashMap<String,Object> loginMap = logInParser(in);
                        return loginMap;
                    }catch(IOException e){
                        System.out.println("IOException");
                    }

            return null;
        }

        @Override
        protected void onPostExecute(HashMap<String,Object> map){
            processResults(map);
        }
    }

    public void processResults(HashMap<String,Object> responseMap){
        boolean success = (Boolean) responseMap.get("success");
        if (!success){
            Log.d("Login", "Error: Not successful");
            return;
        }
        Log.d(TAG, "Success?:" + success);
        String token = (String) responseMap.get("token");
        Log.d(TAG, "token?:" + token);
        int userID = (Integer) responseMap.get("userID");
        Log.d(TAG, "id?:" + userID);
        Intent homeIntent = new Intent(LogInActivity.this, HomeActivity.class);
        homeIntent.putExtra("token", token);
        homeIntent.putExtra("userID", userID);
        startActivity(homeIntent);
    }
}
