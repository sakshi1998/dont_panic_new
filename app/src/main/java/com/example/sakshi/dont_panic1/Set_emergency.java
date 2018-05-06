package com.example.sakshi.dont_panic1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.sakshi.dont_panic1.BloodBank.NearestBloodBanks;
import com.example.sakshi.dont_panic1.Hospital.NearestHospital;
import com.example.sakshi.dont_panic1.Pharmacy.NearestPharmacy;

public class Set_emergency extends AppCompatActivity {
     RadioGroup radioGroup;
     RadioButton rButton1,rButton2 , rButton3;
     Button go;
    Button ButtonView;
    CardView gethospital,updateinfo,getroute,hospital_login,emergency,getpharmacy;
    public static double lat,lo;
    public static String s;
    public static ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggestion);


        radioGroup=(RadioGroup)findViewById(R.id.radioGroup);
        rButton1=(RadioButton)findViewById(R.id.radioButton1);
        rButton2=(RadioButton)findViewById(R.id.radioButton2);
        rButton3=(RadioButton)findViewById(R.id.radioButton3);
        go=(Button)findViewById(R.id.go);




       go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Set_emergency.this, MapsActivity.class);
                if(rButton1.isChecked()){
                    new NearestHospital(Set_emergency.this);
                    lat=NearestHospital.x;
                    lo=NearestHospital.y;
                    s=NearestHospital.s;

                }else if(rButton2.isChecked()){
                    new NearestBloodBanks(Set_emergency.this);
                    lat=NearestHospital.x;
                    lo=NearestHospital.y;
                    s=NearestHospital.s;

                }

                else if(rButton3.isChecked())
                {
                    new NearestPharmacy(Set_emergency.this);
                    lat=NearestHospital.x;
                    lo=NearestHospital.y;
                    s=NearestHospital.s;
                }

                startActivity(intent);
            }
        });
    }
}




