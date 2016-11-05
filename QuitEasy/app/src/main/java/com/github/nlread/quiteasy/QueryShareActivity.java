package com.github.nlread.quiteasy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by Benjamin on 11/5/2016.
 */
public class QueryShareActivity extends AppCompatActivity{

    private int token;
    private String campaignType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_share);
        this.token = (Integer) getIntent().getExtras().get("token");
    }

    public void yesClicked(View v){
        //TODO: communicate with backend and update the share for this campaign
        goToHome();
    }

    public void noClicked(View v){
        //TODO: communicate with backend and update the share for this campaign
        goToHome();
    }

    private void goToHome() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.putExtra("token", token);
        startActivity(homeIntent);
    }
}

