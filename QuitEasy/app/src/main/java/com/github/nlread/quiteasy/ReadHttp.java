package com.github.nlread.quiteasy;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Connor Valenti on 11/5/2016.
 */

public class ReadHttp extends IntentService {
    private static final String TAG = "ReadHttp";

    public ReadHttp(){
        super("ReadHttp");
    }

    public void onHandleIntent(Intent intent){
        String urlStr = intent.getStringExtra("url");
        try{
            URL url = new URL(urlStr);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            List<Message> messages = readJsonStream(urlConnection.getInputStream());
            for(Message msg : messages){
                Log.d(TAG, msg.toString());
            }
        }
        catch(Exception e){
            Log.e("Error occurred", e.getMessage(), e);
        }
    }

    public List<Message> readJsonStream(InputStream in) throws IOException{
        JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try{
            return readResponseObject(reader);
        }finally{
            reader.close();
        }
    }

    public List<Message> readResponseObject(JsonReader reader) throws IOException{
        List<Message> messages = new ArrayList<Message>();
        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if(name.equals("results")){
                reader.beginArray();
                while(reader.hasNext()){
                    messages.add(readLocation(reader));
                }
                reader.endArray();
            }
            else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return messages;
    }

    public Message readLocation(JsonReader reader) throws IOException{
        boolean hasAlcohol = false;
        boolean isOpen = false;
        String id = "";

        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            Log.d(TAG, name);
            if(name.equals("types") && reader.peek() != null){
                hasAlcohol = checkForAlcohol(reader);
            }
            else if(name.equals("opening_hours")){
                isOpen = checkOpen(reader);
            }
            else if(name.equals("place_id")){
                id = reader.nextString();
            }
            else{
                reader.skipValue();
            }
        }
        reader.endObject();
        Bundle bundle = new Bundle();
        bundle.putBoolean("alcohol", hasAlcohol);
        bundle.putBoolean("open", isOpen);
        bundle.putString("id", id);
        Message message = new Message();
        message.setData(bundle);
        return message;
    }

    public boolean checkForAlcohol(JsonReader reader) throws IOException{
        List<String> types = new ArrayList<String>();
        reader.beginArray();
        while(reader.hasNext()){
            types.add(reader.nextString());
        }
        reader.endArray();
        for(String type : types){
            if(type.equals("bar") || type.equals("liquor_store") || type.equals("night_club")){
                return true;
            }
        }
        return false;
    }

    public boolean checkOpen(JsonReader reader) throws IOException{
        reader.beginObject();
        while(reader.hasNext()){
            String text = reader.nextName();
            if(text.equals("open_now")){
                boolean openClose = reader.nextBoolean();
                if(openClose){
                    return true;
                }
            }
            else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return false;
    }
}
