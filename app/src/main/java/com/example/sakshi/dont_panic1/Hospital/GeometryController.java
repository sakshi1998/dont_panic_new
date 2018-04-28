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

package com.example.sakshi.dont_panic1.Hospital;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilterWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by Sakshi on 26-Feb-18.
 */

public class GeometryController {

    public static boolean loading;

    public static ArrayList<NearbyHospitalsDetail> detailArrayList = new ArrayList();
    public static java.lang.StringBuffer stringBuffer = new StringBuffer();

    public static void manipulateData(StringBuffer buffer) {

        loading = true;
        try {

            detailArrayList.clear();

            JSONObject jsonpObject = new JSONObject(buffer.toString());

            JSONArray array = jsonpObject.getJSONArray("results");

            Log.d("array", "df" + array.length());

            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject jsonObject = array.getJSONObject(i);
                    NearbyHospitalsDetail hospitalsDetail = new NearbyHospitalsDetail();

                    if (jsonObject.getString("name") != null)
                        hospitalsDetail.setHospitalName(jsonObject.getString("name"));
                    else hospitalsDetail.setHospitalName("Not Available");

                    try {
                        hospitalsDetail.setRating(String.valueOf(jsonObject.getDouble("rating")));

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
            }

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

                String csv=" ";

                for (int j = 0; j < jReviewArray.length(); j++) {

                    JSONObject jReview = jReviewArray.getJSONObject(j);
                    String review= jReview.getString("text") ;
                    String rating=jReview.getString("rating");

                    //csv+=review+","+rating+"|";
                   /* Log.v("hgh",csv);

                    final Path path;


                    try
                    {
                        File file = new File("home/sakshi/data.txt");
                        Log.v("fvv","Achievment");

                        BufferedReader reader = new BufferedReader(new FileReader(file));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            Log.v("dc",line);
                        }
                    }
                    catch (Exception e)
                    {
                        System.err.format("Exception occurred trying to read '%s'.", "yupp");
                        e.printStackTrace();
                        return null;
                    }

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        path = Paths.get("/home/sakshi/Desktop");

                        final Path txt = path.resolve("data.txt");
                        final Path csv1 = path.resolve("data.csv");

                        final Charset utf8 = Charset.forName("UTF-8");
                        try (
                                final Scanner scanner = new Scanner(Files.newBufferedReader(txt, utf8));
                                final PrintWriter pw = new PrintWriter(Files.newBufferedWriter(csv1, utf8, StandardOpenOption.CREATE_NEW))) {
                            while (scanner.hasNextLine()) {
                                pw.println(scanner.nextLine().replace('|', ','));
                            }
                        }

                    }

                    /*PrintWriter out =new PrintWriter(new BufferedWriter(new FileWriter("com.example.sakshi.dont_panic1.Hospital.data.csv")));
                    out.println(csv);
                    out.close();*/
                }


                stringBuffer=buffer1;
                return buffer1;
            } catch (Exception e) {
                return null;
            }

        }

    }
}
