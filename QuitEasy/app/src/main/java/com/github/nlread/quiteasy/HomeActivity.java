package com.github.nlread.quiteasy;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Benjamin on 11/5/2016.
 */

public class HomeActivity extends AppCompatActivity {

    final String TAG = "HomeActivity";
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
        this.startService(trackingIntent);
        friends = new ArrayList<>();
        //Create test data
        String body = "{\"function\":\"getFriends\",\"token\":\"" + token + "\",\"id\":\"" + userID + "\"}";
        try {
            URL url = new URL(getString(R.string.base_url));
            HttpURLConnection conn = (HttpURLConnection)  url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "text/json");
            GetFriendsTask task = new GetFriendsTask();
            task.execute(new Object[] {conn,body});
        }catch(Exception e){
            System.out.println("Exception encountered: " + e.getMessage());
        }
//        initTable();
    }

    //beginning of private class
    private class GetFriendsTask extends AsyncTask<Object,Void,ArrayList<Friend>> {

        private ArrayList<Friend> friendParser(InputStream in) throws IOException{
            ArrayList<Friend> friends = new ArrayList<>();
            JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
            reader.beginObject(); //first json object
            reader.nextName(); // "success"
            boolean success = reader.nextBoolean();
            if (!success){
                Log.d("Tag", "Error: no friends! How sad");
                return null;
            }

            reader.nextName(); // "friends: "
            reader.beginArray(); // begin the array of all friends
            //This is the loop for all friends
            while (reader.peek() != JsonToken.END_ARRAY) {
                reader.beginObject();
                reader.nextName();// "id"
                int id = reader.nextInt();
                reader.nextName();// "firstName"
                String firstName = reader.nextString();
                reader.nextName();// "lastName"
                String lastName = reader.nextString();
                reader.nextName();// "username"
                String username = reader.nextString();
                reader.nextName(); // "campaigns

                ArrayList<Campaign> campaigns = new ArrayList<>();
                reader.beginArray();
                //a new, smaller loop for campaigns
                while (reader.peek() != JsonToken.END_ARRAY) {
                    reader.beginObject();
                    reader.nextName(); // "campaignID"
                    int campaignID = reader.nextInt();
                    reader.nextName(); // "ownerID"
                    int ownerID = reader.nextInt();
                    reader.nextName(); // "campaign type"
                    String campaignType = reader.nextString();
                    reader.endObject();
                    Campaign c = new Campaign(campaignType, campaignID);
                    campaigns.add(c);
                }
                reader.endArray();
                //end of campaign loop
                reader.endObject();
                //end of one friend
                Friend f = new Friend(id, firstName, lastName, username, campaigns, new Date(1478374200000l));
                friends.add(f);
            }
            reader.endArray();
            //end of friend loop
            reader.endObject();
            reader.close();

            return friends;
        }

        @Override
        protected ArrayList<Friend> doInBackground(Object[] params) {
            HttpURLConnection conn = (HttpURLConnection) params[0];
            String body = (String)params[1];
            try {
                OutputStream out = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(out, "UTF-8"));
                writer.write(body);
                writer.flush();
                writer.close();
                InputStream in = conn.getInputStream();
                ArrayList<Friend> friends = friendParser(in);
                return friends;
            }catch(IOException e){
                System.out.println("IOException");
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Friend> friends){
            setFriends(friends);
            initTable();
        }
    }

    //End of private class

    public void setFriends(ArrayList<Friend> friends){
        this.friends = friends;
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
            b.setText(friend.firstName + " " + friend.lastName);
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
