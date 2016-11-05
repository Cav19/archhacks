package com.github.nlread.quiteasy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;

/**
 * Created by Benjamin on 11/5/2016.
 */

public class RegisterCampaignActivity extends AppCompatActivity{

    private int token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_campaign);
        this.token = (Integer) getIntent().getExtras().get("token");
    }

    public void notNowClicked(View v){
        Intent homeIntent = new Intent(this,HomeActivity.class);
        homeIntent.putExtra("token", token);
        startActivity(homeIntent);
    }

    public void continueClicked(View v) {
        Spinner spinner = (Spinner)findViewById(R.id.possibleCampaignSpinner);
        String campaign = (String) spinner.getSelectedItem();
        //TODO: register this campaign as a new campaign
        Intent timeIntent = new Intent(this, QueryShareActivity.class);
        timeIntent.putExtra("token", token);
        timeIntent.putExtra("campaignType", campaign);
        startActivity(timeIntent);
    }

}
