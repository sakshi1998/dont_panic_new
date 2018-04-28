package com.example.sakshi.dont_panic1.BloodBank;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
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
import com.example.sakshi.dont_panic1.MapsActivity;
import com.example.sakshi.dont_panic1.R;
import com.example.sakshi.dont_panic1.UpdateInfo;
import com.example.sakshi.dont_panic1.adapter.CustomPlacesAdapter;


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


    BloodDetail N1;
    public int closest;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);


        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        N1=new BloodDetail();
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


                   GeometryBlood.loading = true;
                    loadLocation();

                    while (GeometryBlood.loading) {
                        //Log.d("Message=>>>>", "Waiting");
                    }


                    fillList();

                } catch (IllegalArgumentException e) {
                    Toast.makeText(NearestBloodBanks.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });


    }


    void listSelection(int i) {

        Intent intent=new Intent(NearestBloodBanks.this,UpdateInfo.class);
        //intent.putExtra("id",GeometryBlood.detailArrayList.get(i).getHospitalName());
        //intent.putExtra("id2",GeometryBlood.detailArrayList.get(i).getAddress());
        startActivity(intent);
    }


    void viewMapButton() {
        Intent intent = new Intent(NearestBloodBanks.this,MapsActivity.class);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        startActivity(intent);
    }

    public void showNotification(String desc,double latitude,double longitude)
    {
        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(NOTIFICATION_SERVICE);

        Intent mIntent = new Intent(this,MapsActivity.class );

        mIntent.putExtra("lat", latitude);
        mIntent.putExtra("long",longitude);


        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), mIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setContentTitle(" You should go to "+ desc+R.string.notification );

        notificationBuilder.setContentText(desc);
        notificationBuilder.setLights(Color.parseColor("#0086dd"), 2000, 2000);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setColor(ContextCompat.getColor(this, R.color.colorAccent));
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

    protected void fillList() {

        ArrayList<String> placeName = new ArrayList();


        for (int i = 0; i < GeometryController.detailArrayList.size(); i++){
            placeName.add(GeometryController.detailArrayList.get(i).getHospitalName());

        }

        ArrayList<String> ratingText = new ArrayList();
        for (int i = 0; i < GeometryController.detailArrayList.size(); i++){
            ratingText.add(GeometryController.detailArrayList.get(i).getRating());
        }

        ArrayList<String> openNow = new ArrayList<>();
        for (int i = 0; i < GeometryController.detailArrayList.size(); i++){
            openNow.add(GeometryController.detailArrayList.get(i).getOpeningHours());
        }






        CustomPlacesAdapter customPlacesAdapter = new CustomPlacesAdapter(this, placeName, ratingText, openNow);
        centersListView.setAdapter(customPlacesAdapter);
        Home.progressDialog.cancel();

        String s=GeometryController.detailArrayList.get(closest).getHospitalName();
        double x=GeometryController.detailArrayList.get(closest).getGeometry()[0];
        double y= GeometryController.detailArrayList.get(closest).getGeometry()[1];
        showNotification(s,x,y);

    }


    void loadLocation() {
        try {
            //new NearestBloodBanks().RetrieveFeedTask().execute();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (stringBuffer.length() == 0) {
                        //Log.d("Messege", "buffer reading");
                    }
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




