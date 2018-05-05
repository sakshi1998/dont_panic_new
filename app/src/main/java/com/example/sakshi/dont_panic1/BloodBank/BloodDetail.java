package com.example.sakshi.dont_panic1.BloodBank;

import java.util.Arrays;

public class BloodDetail {
    private String Bloodbankname;
    /** ratting variable */
    private String rating;
    /** openingHours variable */
    private String openingHours;
    /** address variable */
    private String address;
    /** geometry - latitude and longitude array */
    private double[] geometry;


    public String getHospitalName() {
        return Bloodbankname;
    }


    public void setHospitalName(String hospitalName) {
        this.Bloodbankname = hospitalName;
    }


    public String getRating() {
        return rating;
    }


    public void setRating(String rating) {
        this.rating = rating;
    }


    public String getOpeningHours() {
        return openingHours;
    }


    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public double[] getGeometry() {
        return geometry;
    }


    public void setGeometry(double[] geometry) {
        this.geometry = geometry;
    }

    @Override
    public String toString() {
        return "NearbyHospitalsDetail{" +
                ", hospitalName='" + Bloodbankname + '\'' +
                ", rating=" + rating +
                ", openingHours='" + openingHours + '\'' +
                ", address='" + address + '\'' +
                ", geometry=" + Arrays.toString(geometry) +
                '}';
    }
}
