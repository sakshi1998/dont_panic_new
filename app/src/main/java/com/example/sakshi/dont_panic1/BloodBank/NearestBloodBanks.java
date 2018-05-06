package com.example.sakshi.dont_panic1.BloodBank;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sakshi.dont_panic1.MainActivity;
import com.example.sakshi.dont_panic1.MapsActivity;
import com.example.sakshi.dont_panic1.R;
import com.example.sakshi.dont_panic1.Utils;
import com.example.sakshi.dont_panic1.adapter.Blood_udapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NearestBloodBanks extends AppCompatActivity {

    double latitude, longitude;
    public static java.lang.StringBuffer stringBuffer = new StringBuffer();


    Button scanButton, viewMapButton;
    ListView centersListView;

    GeometryBlood G1;
    LocationManager locationManager;
    Location location;
    public static double x,y;
    public static String s;
    BloodDetail N1;
    public int closest;
    private Activity mainActivity;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    public  NearestBloodBanks(Activity activity) {
        this.mainActivity = activity;

        try {

            locationManager = (LocationManager)activity. getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                throw new IllegalArgumentException("No GPS");
            } else if (!Utils.isGooglePlayServicesAvailable(activity)) {
                throw new IllegalArgumentException("No Google Play Services Available");
            } else getLocation(activity);




            GeometryBlood.loading = true;
            loadLocation();

            while (GeometryBlood.loading) {
                Log.d("Message=>>>>", "Waiting");
            }


            fillList(activity);

        } catch (IllegalArgumentException e) {
            Toast.makeText(NearestBloodBanks.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    void getLocation(Activity activity) {

        if(checkLocationPermission(activity)) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                Log.d("Achieved latitude=>", location.getLatitude() + ", longitide=> " + location.getLongitude());
            }

            if (location == null) {
                Log.d("GPS PRovider", "Enabled");
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }

            if (location == null)
                throw new IllegalArgumentException("Cann't trace location");

            latitude = location.getLatitude();
            longitude = location.getLongitude();

        }
        else
            return;


    }
    private void showNotification(Activity activity,String desc,double latitude,double longitude)
    {
        NotificationManager notificationManager = (NotificationManager)
                activity.getSystemService(NOTIFICATION_SERVICE);

        Intent mIntent = new Intent(activity,MapsActivity.class );

        mIntent.putExtra("lat", latitude);
        mIntent.putExtra("long",longitude);


        PendingIntent pIntent = PendingIntent.getActivity(activity, (int) System.currentTimeMillis(), mIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(activity);
        notificationBuilder.setContentTitle(" You should go to "+ desc+" hospital with least distance,Click to get the location" );

        notificationBuilder.setContentText(desc);
        notificationBuilder.setLights(Color.parseColor("#0086dd"), 2000, 2000);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setColor(ContextCompat.getColor(activity, R.color.colorAccent));
        notificationBuilder.setContentIntent(pIntent);
        notificationBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(desc));

        notificationBuilder.setDefaults(Notification.DEFAULT_VIBRATE);
        notificationBuilder.setDefaults(Notification.DEFAULT_SOUND);
        notificationBuilder.setAutoCancel(true);

        Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;


        notificationManager.notify(0, notification);
    }

    public boolean checkLocationPermission(Activity activity)
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = activity.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    protected void fillList(Activity activity) {

        ArrayList<String> placeName = new ArrayList<>();


        for (int i = 0; i < GeometryBlood.detailArrayList.size(); i++){
            placeName.add(GeometryBlood.detailArrayList.get(i).getHospitalName());
            double la= GeometryBlood.detailArrayList.get(i).getGeometry()[0];
            double lo=GeometryBlood.detailArrayList.get(i).getGeometry()[1];
            MainActivity.mMap.addMarker(new MarkerOptions().position(new LatLng(la, lo)).title(GeometryBlood.detailArrayList.get(i).getHospitalName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
            MainActivity.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(la,lo),16.0f));

        }

        ArrayList<String> ratingText = new ArrayList<>();
        for (int i = 0; i < GeometryBlood.detailArrayList.size(); i++){
            ratingText.add(GeometryBlood.detailArrayList.get(i).getRating());
        }

        ArrayList<String> openNow = new ArrayList<>();
        for (int i = 0; i < GeometryBlood.detailArrayList.size(); i++){
            openNow.add(GeometryBlood.detailArrayList.get(i).getOpeningHours());
        }

        Blood_udapter customPlacesAdapter = new Blood_udapter(activity, placeName, ratingText, openNow);
        MainActivity.centersListView.setAdapter(customPlacesAdapter);
      //  MainActivity.progressDialog.cancel();

         s=GeometryBlood.detailArrayList.get(0).getHospitalName();
         x=GeometryBlood.detailArrayList.get(0).getGeometry()[0];
         y= GeometryBlood.detailArrayList.get(0).getGeometry()[1];
        showNotification(activity,s,x,y);

    }


    void loadLocation() {
        try {
            new RetrieveFeedTask().execute();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (stringBuffer.length() == 0) {
                        //Log.d("Messege", "buffer reading");
                    }
                    GeometryBlood.manipulateData(stringBuffer);
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class RetrieveFeedTask extends AsyncTask<StringBuffer, StringBuffer, StringBuffer > {

        @Override
        protected StringBuffer doInBackground(StringBuffer... stringBuffers) {
            try {

                StringBuilder stringBuilder = new StringBuilder()
                        .append("https://maps.googleapis.com/maps/api/place/search/json?rankby=distance&keyword=bloodbank&location=")
                        .append(latitude)
                        .append(",")
                        .append(longitude)
                        .append("&key=AIzaSyBJ8O_MgT3BHO164RrKWRyQPAR6M2avEbg&sensor=false&libraries=places");


                URL url = new URL(stringBuilder.toString());
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuffer buffer = new StringBuffer();

                String n = "";

                while((n=bufferedReader.readLine())!=null){
                    buffer.append(n);

                    Log.v("sfvf",n);

                }

                Log.d("loaded ", "Size is " + buffer.length());

                stringBuffer = buffer;

                return buffer;

            } catch (Exception e) {
                return null;
            }
        }


    }


}




