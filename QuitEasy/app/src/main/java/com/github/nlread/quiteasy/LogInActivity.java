package com.github.nlread.quiteasy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Benjamin on 11/5/2016.
 */

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
    }

    public void attemptLogIn(View v){
        EditText usernameText = (EditText) findViewById(R.id.logInUsername);
        String username = usernameText.getText().toString();
        EditText passwordText = (EditText)findViewById(R.id.logInPassword);
        String password = passwordText.getText().toString();
        //TODO: query backend to verify user. if cannot, alter UI. backend returns token
        int token = 12345; //placeholder
        Intent homeIntent = new Intent(LogInActivity.this, HomeActivity.class);
        homeIntent.putExtra("token", token);
        startActivity(homeIntent);
    }
}
