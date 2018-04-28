package com.example.sakshi.dont_panic1.Pharmacy;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sakshi.dont_panic1.Home;
import com.example.sakshi.dont_panic1.Hospital.GeometryController;
import com.example.sakshi.dont_panic1.Hospital.NearestHospital;
import com.example.sakshi.dont_panic1.MapsActivity;
import com.example.sakshi.dont_panic1.Pharmacy.GeometryPharmacy;
import com.example.sakshi.dont_panic1.Pharmacy.PharmacyDetail;
import com.example.sakshi.dont_panic1.R;
import com.example.sakshi.dont_panic1.UpdateInfo;
import com.example.sakshi.dont_panic1.Utils;
import com.example.sakshi.dont_panic1.adapter.CustomPlacesAdapter;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

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

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Pd = new PharmacyDetail();

        centersListView = findViewById(R.id.hosplist);
        viewMapButton = findViewById(R.id.viewMapButton);
        scanButton = findViewById(R.id.scanButton);

        latitude=getIntent().getDoubleExtra("latitude", 0);
        longitude=getIntent().getDoubleExtra("longitude", 0);


        centersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("Selected=> ", i + "");
                listSelection(i);
            }
        });


        viewMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                viewMapButton();
            }
        });
        scanButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                try {
                    updateLoc();
                    GeometryPharmacy.loading = true;
                    loadLocation();

                    while (GeometryPharmacy.loading) {
                        Log.d("Message=>>>>", "Waiting");
                    }


                    fillList();

                } catch (IllegalArgumentException e) {
                    Toast.makeText(NearestPharmacy.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    void listSelection(int i) {

        Intent intent = new Intent(NearestPharmacy.this, UpdateInfo.class);
        intent.putExtra("id", GeometryPharmacy.detailArrayList.get(i).getPharmacyName());
        intent.putExtra("id2", GeometryPharmacy.detailArrayList.get(i).getAddress());
        startActivity(intent);
    }

    void viewMapButton() {
        Intent intent = new Intent(NearestPharmacy.this,MapsActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivity(intent);
    }
    public void updateLoc() {


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            throw new IllegalArgumentException("No GPS");
        } else if (!Utils.isGooglePlayServicesAvailable(this)) {
            throw new IllegalArgumentException("No Google Play Services Available");
        } else getLocation();

    }

    void getLocation() {

        if(checkLocationPermission()) {
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


    public boolean checkLocationPermission()
    {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    protected void fillList() {

        ArrayList<String> placeName = new ArrayList();
        double lat,lon;
        double  minDist=Double.MAX_VALUE;

        for (int i = 0; i < GeometryPharmacy.detailArrayList.size(); i++){
            placeName.add(GeometryPharmacy.detailArrayList.get(i).getPharmacyName());

        }

        ArrayList<String> ratingText = new ArrayList();
        for (int i = 0; i < GeometryPharmacy.detailArrayList.size(); i++){
            ratingText.add(GeometryPharmacy.detailArrayList.get(i).getRating());
        }

        ArrayList<String> openNow = new ArrayList<>();
        for (int i = 0; i < GeometryPharmacy.detailArrayList.size(); i++){
            openNow.add(GeometryPharmacy.detailArrayList.get(i).getOpeningHours());
        }





        CustomPlacesAdapter customPlacesAdapter = new CustomPlacesAdapter(this, placeName, ratingText, openNow);
        centersListView.setAdapter(customPlacesAdapter);
        //Home.progressDialog.cancel();

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