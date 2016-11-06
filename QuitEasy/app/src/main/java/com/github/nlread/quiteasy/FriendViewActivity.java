package com.github.nlread.quiteasy;

import android.content.ClipData;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Benjamin on 11/5/2016.
 */

public class FriendViewActivity extends AppCompatActivity {

    Friend currFriend;
    private String token;
    private int userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_view);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        Bundle extras = getIntent().getExtras();
        this.token = (String) extras.get("token");
        this.userID = (Integer) extras.get("userID");
        Friend friend = (Friend)getIntent().getSerializableExtra("friend");
        this.currFriend = friend;
        //update friend name text view
        TextView friendName = (TextView)findViewById(R.id.friendName);
        friendName.setText(friend.firstName + " " + friend.lastName);
        //create campaign str
        TextView campaignView = (TextView)findViewById(R.id.campaignString);
        ArrayList<Campaign> campaigns = friend.campaigns;

        if (campaigns.size() > 0) {
            String campaignStr = campaigns.get(0).campaignType;
            for (int i = 1; i < campaigns.size(); i++) {
                campaignStr += ", " + campaigns.get(i).campaignType;
            }
            campaignView.append(campaignStr);
        }
        else{
            campaignView.setVisibility(View.INVISIBLE);
        }
        //update encouragement string
        TextView encouragementText = (TextView)findViewById(R.id.encouragementDayCount);
        String text = encouragementText.getText().toString();
        text = text.replace("x", String.valueOf(friend.lastTimeEncouraged));
        encouragementText.setText(text);
        //update spinner
        Spinner campaignSpinner = (Spinner)findViewById(R.id.campaignSpinner);
        campaignSpinner.setAdapter(new CampaignsAdapter(this,R.id.campaignSpinner,campaigns));
    }

    public void sendMessage(View v){
        EditText messageBox = (EditText)findViewById(R.id.encouragementMessage);
        String message = messageBox.getText().toString();
        if (message.equals("") || message.equals(" ")){
            return; //reject empty message
        }
        Spinner campaignSpinner = (Spinner)findViewById(R.id.campaignSpinner);
        int pos = campaignSpinner.getSelectedItemPosition();
        Campaign chosenCampaign = currFriend.campaigns.get(pos);
        Toast.makeText(this,"Message Sent!",Toast.LENGTH_SHORT).show();
        messageBox.setText("");
        //TODO: create web call here to pass in message to backend. We have the message, the campaign id, the token, the friend, and the user.
//        String body = "{\"function\":\"getFriends\",\"token\":\"" + token + "\",\"id\":\"" + userID + "\"}";
//        try {
//            URL url = new URL(getString(R.string.base_url));
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("POST");
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//            conn.setRequestProperty("Content-Type", "text/json");
//        }catch (Exception e){
//            Log.d("Tag", "Exception: " + e.getMessage());
//        }
 }



    //Adapter for spinner. Private class
    private class CampaignsAdapter extends ArrayAdapter implements SpinnerAdapter{

        public CampaignsAdapter(Context context, int resourceID, ArrayList<Campaign> campaigns){
            super(context, resourceID, campaigns);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            Campaign campaign = (Campaign) getItem(position);
            TextView b = new TextView(this.getContext());
            b.setText(campaign.campaignType);
            b.setTextSize(20);
            return b;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent){
            Campaign campaign = (Campaign) getItem(position);
            TextView b = new TextView(this.getContext());
            b.setText(campaign.campaignType);
            b.setTextSize(20);
            return b;
        }
    }
}
