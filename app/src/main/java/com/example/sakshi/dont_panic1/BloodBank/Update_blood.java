package com.example.sakshi.dont_panic1.BloodBank;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sakshi.dont_panic1.Hospital.Update_Availability;
import com.example.sakshi.dont_panic1.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Update_blood extends AppCompatActivity {
    EditText blood_bank, blood_type,available;
    Button Update;
    String name,bloodtype;
    String num;
    DatabaseReference databaseReference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_blood);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        blood_bank = (EditText) findViewById(R.id.name);
        blood_type = (EditText) findViewById(R.id.numbers);
        available=(EditText)findViewById(R.id.availability);
        Update = (Button) findViewById(R.id.update);
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = blood_bank.getText().toString().toLowerCase();
                bloodtype=blood_type.getText().toString().toLowerCase();
                num = available.getText().toString();

                databaseReference.child("bloodbank").child(name).child("bloodtype").child(bloodtype).setValue(num);
                Toast.makeText(Update_blood.this, "INFORMATION UPDATED", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
