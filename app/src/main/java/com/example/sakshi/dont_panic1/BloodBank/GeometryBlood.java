/*
 * %W% %E% Zain-Ul-Abedin
 *
 * Copyright (c) 2017-2018. All Rights Reserved.
 *
 * This software is the confidential and proprietary information of ZainMustafaaa.
 * You shall not disclose such Confidential Information and
 * shall use it only in accordance with the terms of the license agreement
 * for learning purposes.
 *
 */

package com.example.sakshi.dont_panic1.BloodBank;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Sakshi on 26-Feb-18.
 */

public class GeometryBlood {

    public static boolean loading;

    public static ArrayList<BloodDetail> detailArrayList = new ArrayList();
    public static java.lang.StringBuffer stringBuffer = new StringBuffer();

    public static void manipulateData(StringBuffer buffer) {

        loading = true;
        try {

            detailArrayList.clear();

            JSONObject jsonpObject = new JSONObject(buffer.toString());

            JSONArray array = jsonpObject.getJSONArray("results");

            Log.d("array", "df" + array.length());

            /*for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject jsonObject = array.getJSONObject(i);
                    BloodDetail hospitalsDetail = new BloodDetail();

                    if (jsonObject.getString("name") != null)
                        //hospitalsDetail.setHospitalName(jsonObject.getString("name"));
                    else hospitalsDetail.setHospitalName("Not Available");

                    try {
                        //hospitalsDetail.setRating(String.valueOf(jsonObject.getDouble("rating")));

                    } catch (Exception e) {
                        hospitalsDetail.setRating("Not Available");
                    }
                    try {


                        String res = jsonObject.getString("place_id");
                        new ReviewsTask().execute(res);



                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.v("daf", "error");
                    }

                    try {
                        if (jsonObject.getJSONObject("opening_hours").getBoolean("open_now"))
                            hospitalsDetail.setOpeningHours("Opened");
                        else hospitalsDetail.setOpeningHours("closed");
                    } catch (Exception e) {
                        hospitalsDetail.setOpeningHours("Not Available");
                    }

                    hospitalsDetail.setAddress(jsonObject.getString("vicinity"));
                    hospitalsDetail.setGeometry(new double[]{jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                            jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng")});

                    detailArrayList.add(hospitalsDetail);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }


        loading = false;
        Log.d("Array Loaded with size ", "Size of " + detailArrayList.size());
    }

    static class ReviewsTask extends AsyncTask<String, String, StringBuffer> {

        @Override
        protected StringBuffer doInBackground(String... placeid) {
            try {
                String res = placeid[0];
                URL url = new java.net.URL("https://maps.googleapis.com/maps/api/place/details/json?placeid=" + res + "&key=AIzaSyBJ8O_MgT3BHO164RrKWRyQPAR6M2avEbg");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = url.openStream();

                int ch = -1;
                StringBuffer buffer1 = new StringBuffer();
                while ((ch = in.read()) != -1) {
                    buffer1.append((char) ch);
                }

                JSONObject jObj = new JSONObject(buffer1.toString());
                JSONObject jResult = jObj.getJSONObject("result");
                JSONArray jReviewArray = jResult.getJSONArray("reviews");

                for (int j = 0; j < jReviewArray.length(); j++) {
                    JSONObject jReview = jReviewArray.getJSONObject(j);
                    Log.e("review", jReview.getString("text") + "\n\n");

                }
                stringBuffer=buffer1;
                return buffer1;
            } catch (Exception e) {
                return null;
            }

        }

    }
}
