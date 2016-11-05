package com.github.nlread.quiteasy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Benjamin on 11/5/2016.
 */

public class PickTimeActivity extends AppCompatActivity {

    private int token;
    private String campaignType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_time);
        Bundle extras = getIntent().getExtras();
        this.token = (Integer) extras.get("token");
        this.campaignType = (String) extras.get("campaignType");
    }
}
