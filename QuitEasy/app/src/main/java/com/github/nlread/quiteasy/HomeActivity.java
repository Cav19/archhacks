package com.github.nlread.quiteasy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Benjamin on 11/5/2016.
 */

public class HomeActivity extends AppCompatActivity {

    private static final String BASE_URL = "http://www.something.com";
    ArrayList<Friend> friends;
    private String token;
    private int userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        Bundle extras = getIntent().getExtras();
        this.token = (String) extras.get("token");
        this.userID = (Integer) extras.get("userID");

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},1);
        Intent trackingIntent = new Intent(HomeActivity.this, TrackingService.class);
        //this.startService(trackingIntent);

        friends = new ArrayList<>();
        //Create test data
        for(int i = 0; i < 10; i ++){
            Campaign campaign1 = new Campaign("alcohol", 12345);
            Campaign campaign2 = new Campaign("tobacco", 23456);
            Friend newFriend = new Friend("username" + i, new Campaign[] {campaign1, campaign2}, new Date(1478374200000l));
            friends.add(newFriend);
        }
        //real data could be below
        String urlParams = "request=friendships&token=" + token + "&userID=" + userID;
        try {
            URL url = new URL(BASE_URL + "?" + urlParams);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(conn.getInputStream());
            String body = readInput(in);
        }catch(Exception e){
            System.out.println("Exception encountered: " + e.getMessage());
        }
        initTable();
    }

    private String readInput(InputStream in) throws IOException{
        String body = "";
        while(in.available() > 0){
            body += (char) in.read(); //convert byte to character
        }
        return body;
    }

    public void friendClicked(View v){
        ListView friendList = (ListView)findViewById(R.id.friends_list);
        int pos = friendList.getPositionForView(v);
        Friend friendClicked = friends.get(pos);
        Intent openFriend = new Intent(HomeActivity.this, FriendViewActivity.class);
        openFriend.putExtra("friend", friendClicked);
        openFriend.putExtra("token", token);
        openFriend.putExtra("userID", userID);
        startActivity(openFriend);
    }

    private void initTable() {
        ListView friendList = (ListView)findViewById(R.id.friends_list);
        FriendsAdapter adapter = new FriendsAdapter(this,R.id.friends_list,friends);
        friendList.setAdapter(adapter);
    }

    private class FriendsAdapter extends ArrayAdapter{
        public FriendsAdapter(Context context, int resourceID, List<Friend> friends){
            super(context, resourceID, friends);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            Friend friend = (Friend) getItem(position);
            Button b = new Button(this.getContext());
            b.setText(friend.username);
            b.setTextSize(18f);
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    friendClicked(v);
                }
            };

            b.setOnClickListener(listener);
            return b;
        }
    }

}
