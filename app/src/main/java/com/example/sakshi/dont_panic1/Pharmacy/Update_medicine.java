package com.example.sakshi.dont_panic1.Pharmacy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sakshi.dont_panic1.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Update_medicine extends AppCompatActivity {
    EditText pharmacy_name, medicines,available;
    Button Update;
    String name,medicine;
    String num;
    DatabaseReference databaseReference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_pharmacy);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        pharmacy_name = (EditText) findViewById(R.id.name);
        medicines = (EditText) findViewById(R.id.numbers);
        available=(EditText)findViewById(R.id.availability);
        Update = (Button) findViewById(R.id.update);
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = pharmacy_name.getText().toString().toLowerCase();
                medicine=medicines.getText().toString().toLowerCase();
                num = available.getText().toString().toLowerCase();

                databaseReference.child("pharamcy").child(name).child("medicine").child(medicine).setValue(num);
                Toast.makeText(Update_medicine.this, "INFORMATION UPDATED", Toast.LENGTH_SHORT).show();


            }
        });
    }
}
