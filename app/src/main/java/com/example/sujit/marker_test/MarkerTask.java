package com.example.sujit.marker_test;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by sujit on 7/23/16.
 */

//Progress Dialog baki cha
public class MarkerTask extends AsyncTask<Void, Void, String>{
    private GoogleMap mMap;

    private static final String SERVICE_URL = "http://192.168.0.107/retrieve.php";
    private static final String LOG_TAG = "MarkersApp";
    Context context;

    @Override
    protected void onPostExecute(String s) {

        try {
            // De-serialize the JSON string into an array of city objects
            JSONArray jsonArray = new JSONArray("markers");
            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObj = jsonArray.getJSONObject(i);

                double lat = jsonObj.getDouble("lat");
                double lng =jsonObj.getDouble("lang");      //YO lang nai cha json response ma pani

                LatLng latLng1 = new LatLng(lat,lng);

                //LatLng latLng1 = new LatLng(jsonObj.getJSONArray("latlng").getDouble(0),
                      //jsonObj.getJSONArray("latlng").getDouble(1));

                //move CameraPosition on first result
                if (i == 0) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(latLng1).zoom(13).build();

                    mMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));
                }

                // Create a marker for each city in the JSON data.
                mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        .title(jsonObj.getString("name"))
                        .snippet(Integer.toString(jsonObj.getInt("id")))
                        .position(latLng1));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error processing JSON", e);
        }


    }
      @Override
    protected String doInBackground(Void... voids) {
          HttpURLConnection conn = null;
        final StringBuilder json = new StringBuilder();
        try {
            // Connect to the web service
            URL url = new URL(SERVICE_URL);
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Read the JSON data into the StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                json.append(buff, 0, read);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error connecting to service", e);
            //throw new IOException("Error connecting to service", e); //uncaught
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return json.toString();
    }
}

