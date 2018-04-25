package com.example.sakshi.dont_panic1;

import android.*;
import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.VoiceInteractor;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by sakshi on 22/3/18.
 */

public class Emergency extends FragmentActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    /** variables for longitude and latitude */
    double longitude, latitude;
    DatabaseReference databaseReference;


    GeoFire geoFire;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        latitude = getIntent().getDoubleExtra("latitude", 0);
        longitude = getIntent().getDoubleExtra("longitude", 0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.googleMap1);
        mapFragment.getMapAsync(this);

        databaseReference= FirebaseDatabase.getInstance().getReference();
        geoFire = new GeoFire(databaseReference);


    }




    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(latitude,longitude))
                .radius(500)
                .strokeColor(Color.BLUE)
                .fillColor(0x220000FF)
                .strokeWidth(5.0f)
        );
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longitude)).title("user"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(latitude, longitude)));

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latitude,longitude), 0.6f);

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            public void onKeyEntered(String key, GeoLocation location) {
                Log.v("df",key);
           //     showNotification(String.format("Key %s entered the search area at [%f,%f]", key,location.latitude, location.longitude));
                databaseReference.child("Devices").child(key).child("present").setValue("1");
            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
                databaseReference.child("Devices").child(key).child("present").setValue("0");
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });
        mMap.setMyLocationEnabled(true);
       new send_notifications().execute("send");
    }


}
