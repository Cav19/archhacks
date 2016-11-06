package com.github.nlread.quiteasy;

import android.Manifest;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionApi;
import com.google.android.gms.location.places.PlaceFilter;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Benjamin on 11/5/2016.
 */

public class TrackingService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {


    protected static final String TAG = "TrackingService";

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;



    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }


    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    private static String token;
    private static int userId;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        token = intent.getStringExtra("token");
        userId = intent.getIntExtra("userId", -1);

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        buildGoogleApiClient();
        mGoogleApiClient.connect();

        return START_STICKY;
    }

    public void resumeService() {

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    public void pauseService() {
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    public void stopService() {
        mGoogleApiClient.disconnect();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.d(TAG, "Initial Pos: " + mCurrentLocation.getLatitude() + ", " + mCurrentLocation.getLongitude());
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            searchForNearbyPlaces();
            mRequestingLocationUpdates = true;
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            Log.d(TAG, "Starting Location Updates");
            startLocationUpdates();
        }
    }

    private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private static final String API_KEY = "AIzaSyDl87saL3wB0z4Fvt0_5z_Aku3PuCF8VcI";
    private static final String RADIUS = "1000";
    private List<Message> dangerZones = new ArrayList<Message>();

    public void searchForNearbyPlaces(){
        String url = (PLACES_SEARCH_URL + "key=" + API_KEY + "&location=" + mCurrentLocation.getLatitude() + "," + mCurrentLocation.getLongitude() + "&radius=" + RADIUS);
        Object[] params = new Object[]{url, this};
        ReadHttp reader = new ReadHttp();
        reader.execute(params);
    }

    /**
     * Calculates the distance in meters between two lat and long points.
     * @param lat1
     * @param lat2
     * @param lon1
     * @param lon2
     * @return
     */
    public static double distance(double lat1, double lat2, double lon1, double lon2) {

        final int R = 6371; // Radius of the earth

        Double latDistance = Math.toRadians(lat2 - lat1);
        Double lonDistance = Math.toRadians(lon2 - lon1);
        Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

    public void onReceive(List<Message> messages){
        dangerZones = messages;
        checkProximityToDanger();
    }

    private List<String> flaggedLocations = new ArrayList<String>();

    public void checkProximityToDanger(){
        for(Message place : dangerZones){
            Log.d(TAG, place.getData().getString("name"));
            double dist = distance(mCurrentLocation.getLatitude(), place.getData().getDouble("latitude"), mCurrentLocation.getLongitude(), place.getData().getDouble("longitude"));
            if(dist <= 1000){
                Log.d(TAG, "You are close to a point: " + dist);
                try {
                    //Thread.sleep(5000);
                }
                catch(Exception e){
                    // Ignore
                }
                double newDist = distance(mCurrentLocation.getLatitude(), place.getData().getDouble("latitude"), mCurrentLocation.getLongitude(), place.getData().getDouble("longitude"));
                if(newDist <= 1000){
                    if(!flaggedLocations.contains(place.getData().getString("name"))) {
                        flaggedLocations.add(place.getData().getString("name"));
                        makeLocationNotification(place.getData().getString("name"));
                        //makeMessageNotification();
                        break; //Used for demo to ensure only one notificaiton pops at a time
                    }
                }
            }
        }
    }

    public void makeMessageNotification(){
        try {
            getMessageFromServer();
        }
        catch(Exception e){
            Log.e("Error occurred", e.getMessage(), e);
        }
    }

    public void getMessageFromServer() throws IOException{
        URL url = new URL(getString(R.string.base_url));
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("Content-Type", "text/json");
        String body = "{\"function\":\"getMessage\",\"userId\":\""+userId+"\",\"token\":\""+token+"\"}";
        MessageTask task = new MessageTask();
        task.execute(new Object[]{conn,body});
    }

    private class MessageTask extends AsyncTask<Object, Void, HashMap<String,Object>> {
        @Override
        protected HashMap<String, Object> doInBackground(Object[] params){
            HttpURLConnection conn = (HttpURLConnection) params[0];
            String body = (String)params[1];
            try {
                //TODO: getting this to work would be great. I have not been able to test it with the server, but it seems this part below is causing trouble
                OutputStream out = conn.getOutputStream();
                Log.d(TAG, "A");
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(out, "UTF-8"));
                writer.write(body);
                writer.flush();
                writer.close();
                Log.d(TAG, "B");
                InputStream in = conn.getInputStream();
                Log.d(TAG, "C");
                HashMap<String,Object> loginMap = logInParser(in);
                return loginMap;
            }catch(IOException e){
                System.out.println("IOException");
            }

            return null;
        }

        private HashMap<String,Object> logInParser(InputStream in) throws IOException{
            HashMap<String,Object> map = new HashMap<>();
            JsonReader reader = new JsonReader(new InputStreamReader(in, "UTF-8"));
            reader.beginObject();
            String successful = reader.nextName();//succesful
            map.put(successful,reader.nextBoolean());
            String firstName = reader.nextName();//id
            map.put("firstName", reader.nextString());
            Log.d(TAG, map.get("firstName").toString());
            String lastName = reader.nextName();//token
            map.put("lastName", reader.nextString());
            String message = reader.nextName();
            map.put("message", reader.nextString());
            reader.close();

            return map;
        }

        @Override
        protected void onPostExecute(HashMap<String, Object> map){
            processResults(map);
        }

    }

    public void processResults(HashMap<String,Object> responseMap){
        boolean success = (Boolean) responseMap.get("success");
        if (!success){
            Log.d("Login", "Error: Not successful");
            return;
        }
        Log.d(TAG, "Success?:" + success);
        String firstName = (String) responseMap.get("firstName");
        String lastName = (String) responseMap.get("lastName");
        String message = (String) responseMap.get("message");
        NotificationCompat.Builder messageBuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_img)
                .setContentTitle("Message from" + firstName + " " + lastName)
                .setContentText(message);
        int notificationId = 002;
        NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyMgr.notify(notificationId, messageBuilder.build());
    }

    public void makeLocationNotification(String name){
        NotificationCompat.Builder mbuilder =
                new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_img)
                .setContentTitle("Danger Zone!")
                .setContentText("You're near a place with alcohol: " + name + ". Are you considering drinking?");
        int notificationId = 001;
        NotificationManager notifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notifyMgr.notify(notificationId, mbuilder.build());
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        Log.d(TAG, "latitude: " + mCurrentLocation.getLatitude() + ", longitude: " + mCurrentLocation.getLongitude());
        searchForNearbyPlaces();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
