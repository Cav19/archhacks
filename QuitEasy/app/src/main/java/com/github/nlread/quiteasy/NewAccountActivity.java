package com.github.nlread.quiteasy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Benjamin on 11/5/2016.
 */

public class NewAccountActivity extends AppCompatActivity {
    static final String BASE_URL = "https://somesite.com";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);
    }

    //Attempts to create account
    public void attemptRegistration(View v) throws MalformedURLException, IOException {
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
        EditText usernameField = (EditText)findViewById(R.id.registerUsername);
        EditText passwordField = (EditText)findViewById(R.id.registerPassword);
        EditText phoneNumber = (EditText)findViewById(R.id.registerPhoneNumber);
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        String phoneNumberStr = phoneNumber.getText().toString();

        URL url = new URL(BASE_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        String body = "username="+username+"&password="+password+"&phone_number="+phoneNumberStr;
        conn.setFixedLengthStreamingMode(body.length());
        OutputStream out = new BufferedOutputStream(conn.getOutputStream());
        out.write(body.getBytes());


        //TODO: submit all fields to backend to create account
        //TODO: Once registered receive a token. stand in token below
        int token = 12345;
        Intent campaignPage = new Intent(NewAccountActivity.this, RegisterCampaignActivity.class);
        campaignPage.putExtra("token", token);
        startActivity(campaignPage);
    }
}
