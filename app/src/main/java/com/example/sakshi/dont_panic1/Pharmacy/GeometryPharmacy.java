package com.example.sakshi.dont_panic1.Pharmacy;

import android.util.Log;

import com.example.sakshi.dont_panic1.Hospital.NearbyHospitalsDetail;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GeometryPharmacy {

    public static boolean loading;

    public static ArrayList<PharmacyDetail> detailArrayList = new ArrayList();

    public static void manipulateData(StringBuffer buffer){

        loading = true;
        try {

            detailArrayList.clear();

            JSONObject jsonpObject = new JSONObject(buffer.toString());

            JSONArray array = jsonpObject.getJSONArray("results");


            for(int i=0; i<array.length(); i++){
                try {
                    JSONObject jsonObject = array.getJSONObject(i);
                    PharmacyDetail pharmacyDetail = new PharmacyDetail();

                    if(jsonObject.getString("name")!=null)  pharmacyDetail.setPharmacyName(jsonObject.getString("name"));
                    else pharmacyDetail.setPharmacyName("Not Available");

                    try {
                        pharmacyDetail.setRating(String.valueOf(jsonObject.getDouble("rating")));
                    }catch (Exception e){
                        pharmacyDetail.setRating("Not Available");
                    }

                    try {
                        if (jsonObject.getJSONObject("opening_hours").getBoolean("open_now"))  pharmacyDetail.setOpeningHours("Opened");
                        else pharmacyDetail.setOpeningHours("closed");
                    } catch (Exception e) {
                       pharmacyDetail.setOpeningHours("Not Available");
                    }

                    pharmacyDetail.setAddress(jsonObject.getString("vicinity"));
                    pharmacyDetail.setGeometry(new double[]{jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                            jsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng")});

                    detailArrayList.add(pharmacyDetail);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }


        loading = false;
        Log.d("Array Loaded with size ", "Size of "+detailArrayList.size());
    }
}
