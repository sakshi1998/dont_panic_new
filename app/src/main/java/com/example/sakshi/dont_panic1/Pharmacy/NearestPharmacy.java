package com.example.sakshi.dont_panic1.Pharmacy;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sakshi.dont_panic1.Hospital.NearestHospital;
import com.example.sakshi.dont_panic1.MainActivity;
import com.example.sakshi.dont_panic1.Utils;
import com.example.sakshi.dont_panic1.adapter.pharmacy_udapter;
import com.firebase.geofire.GeoFire;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class NearestPharmacy extends AppCompatActivity {

    double latitude, longitude;
    public static java.lang.StringBuffer stringBuffer = new StringBuffer();



    Button scanButton, viewMapButton;
    ListView centersListView;
    LocationManager locationManager;
    GeometryPharmacy G1;

    Location location;
    NearestHospital Hosp;
    PharmacyDetail Pd;
    public int closest;
    DatabaseReference databaseReference;
    GeoFire geoFire;
    private Activity mainActivity;
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }
    public NearestPharmacy(Activity activity) {
        this.mainActivity = activity;

        try {

            locationManager = (LocationManager)activity. getSystemService(LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                throw new IllegalArgumentException("No GPS");
            } else if (!Utils.isGooglePlayServicesAvailable(activity)) {
                throw new IllegalArgumentException("No Google Play Services Available");
            } else getLocation(activity);




            GeometryPharmacy.loading = true;
        loadLocation();

        while (GeometryPharmacy.loading) {
        Log.d("Message=>>>>", "Waiting");
        }


        fillList(activity);

        } catch (IllegalArgumentException e) {
        Toast.makeText(NearestPharmacy.this, e.getMessage(), Toast.LENGTH_LONG).show();
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

    protected void fillList(Activity activity) {

        ArrayList<String> placeName = new ArrayList();
        double lat,lon;
        double  minDist=Double.MAX_VALUE;

        for (int i = 0; i < GeometryPharmacy.detailArrayList.size(); i++){
            placeName.add(GeometryPharmacy.detailArrayList.get(i).getPharmacyName());
            double la= GeometryPharmacy.detailArrayList.get(i).getGeometry()[0];
            double lo=GeometryPharmacy.detailArrayList.get(i).getGeometry()[1];
            MainActivity.mMap.addMarker(new MarkerOptions().position(new LatLng(la, lo)).title(GeometryPharmacy.detailArrayList.get(i).getPharmacyName()));
            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_parking)))
            MainActivity.mMap.moveCamera(CameraUpdateFactory.newLatLng( new LatLng(la,lo)));

        }

        ArrayList<String> ratingText = new ArrayList();
        for (int i = 0; i < GeometryPharmacy.detailArrayList.size(); i++){
            ratingText.add(GeometryPharmacy.detailArrayList.get(i).getRating());
        }

        ArrayList<String> openNow = new ArrayList<>();
        for (int i = 0; i < GeometryPharmacy.detailArrayList.size(); i++){
            openNow.add(GeometryPharmacy.detailArrayList.get(i).getOpeningHours());
        }





        pharmacy_udapter adapter = new pharmacy_udapter(activity, placeName, ratingText, openNow);
        MainActivity.centersListView.setAdapter(adapter);
        MainActivity.progressDialog.cancel();

       /* String s=GeometryPharmacy.detailArrayList.get(closest).getPharmacyName();
        double x=GeometryPharmacy.detailArrayList.get(closest).getGeometry()[0];
        double y=GeometryPharmacy.detailArrayList.get(closest).getGeometry()[1];
        //Hosp.showNotification(s,x,y);
        */
    }

    void loadLocation() {
        try {
            new NearestPharmacy.RetrieveFeedTask().execute();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (stringBuffer.length() == 0)  Log.d("Messege", "buffer reading");
                    GeometryPharmacy.manipulateData(stringBuffer);

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
                        .append("https://maps.googleapis.com/maps/api/place/search/json?rankby=distance&keyword=pharmacy&location=")
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
                    Log.v("huh",n);
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