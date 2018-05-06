package com.example.sakshi.dont_panic1.Hospital;

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
import android.widget.Toast;

import com.example.sakshi.dont_panic1.MainActivity;
import com.example.sakshi.dont_panic1.MapsActivity;
import com.example.sakshi.dont_panic1.R;
import com.example.sakshi.dont_panic1.Utils;
import com.example.sakshi.dont_panic1.adapter.CustomPlacesAdapter;
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
import java.util.HashMap;

/**
 * Created by Sakshi on 26-Feb-18.
 */

public class NearestHospital extends AppCompatActivity {

    double latitude, longitude;
    StringBuffer stringBuffer =new StringBuffer();
    Button scanButton, viewMapButton;
  // ListView centersListView;
    LocationManager locationManager;
    Button button;
    Location location;
    NearbyHospitalsDetail N1;
    public int closest;
    private Activity mainActivity;
    public static double x,y;
    public static String s;
    public static HashMap<String, Integer> result = new HashMap<>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    public NearestHospital(Activity activity){
        this.mainActivity =  activity;


        try {


            locationManager = (LocationManager)activity. getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                throw new IllegalArgumentException("No GPS");
            } else if (!Utils.isGooglePlayServicesAvailable(activity)) {
                throw new IllegalArgumentException("No Google Play Services Available");
            } else getLocation(activity);


            GeometryController.loading = true;
            loadLocation();

            while (GeometryController.loading) {
                //Log.d("Message=>>>>", "Waiting");
            }


            fillList(activity);

        } catch (IllegalArgumentException e) {
            Toast.makeText(activity, e.getMessage(), Toast.LENGTH_LONG).show();
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


    public boolean checkLocationPermission(Activity activity)
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = activity.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
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

    protected void fillList(Activity activity) {


        ArrayList<String> placeName = new ArrayList<>();
        double lat,lon;
        double  minDist=Double.MAX_VALUE;

        for (int i = 0; i < GeometryController.detailArrayList.size(); i++){
            placeName.add(GeometryController.detailArrayList.get(i).getHospitalName());
            double la=GeometryController.detailArrayList.get(i).getGeometry()[0];
            double lo=GeometryController.detailArrayList.get(i).getGeometry()[1];
           MainActivity.mMap.addMarker(new MarkerOptions().position(new LatLng(la, lo)).title(GeometryController.detailArrayList.get(i).getHospitalName()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
           MainActivity.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(la,lo),16.0f));


        }

        ArrayList<String> ratingText = new ArrayList<>();
        for (int i = 0; i < GeometryController.detailArrayList.size(); i++){
            ratingText.add(GeometryController.detailArrayList.get(i).getRating());
        }

        ArrayList<String> openNow = new ArrayList<>();
        for (int i = 0; i < GeometryController.detailArrayList.size(); i++){
            openNow.add(GeometryController.detailArrayList.get(i).getOpeningHours());
        }


/*Default Best*/
         x = GeometryController.detailArrayList.get(0).getGeometry()[0];
         y = GeometryController.detailArrayList.get(0).getGeometry()[1];
         s = GeometryController.detailArrayList.get(0).getHospitalName();


        CustomPlacesAdapter customPlacesAdapter = new CustomPlacesAdapter(activity, placeName, ratingText, openNow);
        MainActivity.centersListView.setAdapter(customPlacesAdapter);

        /*for(int i=0;i<GeometryController.detailArrayList.size();i++) {
            String s = GeometryController.detailArrayList.get(i).getHospitalName();
           //boolean r= check(s);
            //double x = GeometryController.detailArrayList.get(closest).getGeometry()[0];
            //double y = GeometryController.detailArrayList.get(closest).getGeometry()[1];

        }*/
        showNotification(activity, s, x, y);

       // MainActivity.progressDialog.cancel();
    }

   /* public boolean check(String s){
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference().child("Availability");
        ValueEventListener responseListener = new ValueEventListener() {
            @Override
            void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(s)) {
                    // run some code
                }
            }
        };
        return false;
    }*/

    void loadLocation() {
        try {


            RetrieveFeedTask ret=new RetrieveFeedTask();
            ret.execute();

            new Thread(new Runnable() {
                @Override
                public void run() {
                   while (stringBuffer.length() == 0) Log.d("Messege", "buffer reading");
                    GeometryController.manipulateData(stringBuffer);
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


                Log.v("gdh",String.valueOf(latitude));
                StringBuilder stringBuilder = new StringBuilder()
                        .append("https://maps.googleapis.com/maps/api/place/search/json?rankby=distance&keyword=hospital&location=")
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




