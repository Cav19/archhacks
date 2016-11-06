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
import java.util.Collection;
import java.util.List;

/**
 * Created by Connor Valenti on 11/5/2016.
 */

public class ReadHttp extends AsyncTask<Object, Void, List<Message>> {
    private static final String TAG = "ReadHttp";
    private TrackingService service;

    @Override
    protected List<Message> doInBackground(Object... params){
        List<Message> dangerousPlaces = new ArrayList<Message>();
        service = (TrackingService)params[1];
            try {
                URL url = new URL(params[0].toString());
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                List<Message> messages = readJsonStream(urlConnection.getInputStream());
                dangerousPlaces = getDangerousPlaces(messages);
            } catch (Exception e) {
                Log.e("Error occurred", e.getMessage(), e);
            }
        return dangerousPlaces;
    }

    @Override
    protected void onPostExecute(List<Message> messages){
        service.onReceive(messages);
    }

    private List<Message> getDangerousPlaces(List<Message> messages){
        List<Message> dangerousPlaces = new ArrayList<Message>();
        for(Message message : messages){
            if(message.getData().getBoolean("alcohol")){
                dangerousPlaces.add(message);
            }
        }
        return dangerousPlaces;
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
        String locationName = "";
        String latitude = "";
        String longitude = "";

        reader.beginObject();
        while(reader.hasNext()){
            String name = reader.nextName();
            if(name.equals("types")){
                hasAlcohol = checkForAlcohol(reader);
            }
            else if(name.equals("opening_hours")){
                isOpen = checkOpen(reader);
            }
            else if(name.equals("name")){
                locationName = reader.nextString();
            }
            else if(name.equals("geometry")){
                String[] location = getLatLong(reader);
                latitude = location[0];
                longitude = location[1];
            }
            else{
                reader.skipValue();
            }
        }
        reader.endObject();
        Bundle bundle = new Bundle();
        bundle.putBoolean("alcohol", hasAlcohol);
        bundle.putBoolean("open", isOpen);
        bundle.putString("name", locationName);
        bundle.putDouble("latitude", Double.valueOf(latitude));
        bundle.putDouble("longitude", Double.valueOf(longitude));
        Message message = new Message();
        message.setData(bundle);
        return message;
    }

    public String[] getLatLong(JsonReader reader) throws IOException{
        String[] location = new String[2];
        reader.beginObject();
        while(reader.hasNext()){
            String text = reader.nextName();
            if(text.equals("location")){
                reader.beginObject();
                while(reader.hasNext()){
                    String data = reader.nextName();
                    if(data.equals("lat")){
                        location[0] = reader.nextString();
                    }
                    else if(data.equals("lng")){
                        location[1] = reader.nextString();
                    }
                    else{
                        reader.skipValue();
                    }
                }
                reader.endObject();
            }
            else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return location;
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
        boolean openClose = false;
        reader.beginObject();
        while(reader.hasNext()){
            String text = reader.nextName();
            if(text.equals("open_now")){
                openClose = reader.nextBoolean();
            }
            else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return openClose;
    }
}
