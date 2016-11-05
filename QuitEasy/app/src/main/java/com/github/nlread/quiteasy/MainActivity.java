package com.github.nlread.quiteasy;

import android.content.ComponentName;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void logInClicked(View v) {
        Log.d("Test", "click worked");
        Intent logInIntent = new Intent(MainActivity.this, LogInActivity.class);
        startActivity(logInIntent);
    }

    public void signUpClicked(View v) {
        //Go to sign up page
        Intent register = new Intent(MainActivity.this, NewAccountActivity.class);
        startActivity(register);
    }
}
