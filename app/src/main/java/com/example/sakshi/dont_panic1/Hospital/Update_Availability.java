package com.example.sakshi.dont_panic1.Hospital;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sakshi.dont_panic1.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Update_Availability extends AppCompatActivity {
    EditText hosp_name, num_of_beds;
    Button Update;
    String name;
    int num;
    DatabaseReference databaseReference;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_availability);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        hosp_name = (EditText) findViewById(R.id.name);
        num_of_beds = (EditText) findViewById(R.id.numbers);
        Update = (Button) findViewById(R.id.update);
        Update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = hosp_name.getText().toString().toLowerCase();
                num = Integer.parseInt(num_of_beds.getText().toString());
                databaseReference.child("Availability").child(name).child("number_of_beds").setValue(num);
                Toast.makeText(Update_Availability.this, "INFORMATION UPDATED", Toast.LENGTH_SHORT).show();
            }
        });
    }
}