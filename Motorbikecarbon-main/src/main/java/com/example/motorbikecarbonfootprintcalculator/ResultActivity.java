package com.example.motorbikecarbonfootprintcalculator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ResultActivity extends AppCompatActivity {

    DatabaseReference motorbikeRef;
    DatabaseReference motorbikeKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        motorbikeRef = FirebaseDatabase.getInstance().getReference().child("Motorbike_Carbon");
        motorbikeKeyRef = FirebaseDatabase.getInstance().getReference().child("Motorbike_Carbon_Data");

        String userEntryID_Carbon = "MotorbikeCarbon"; // Replace with the actual key you want to retrieve from "Motorbike_Carbon"
        String userEntryID_Carbon_Data = "MotorbikeCarbon_Data"; // Replace with the actual key you want to retrieve from "Motorbike_Carbon_Data"

        motorbikeRef.child(userEntryID_Carbon).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot_Carbon) {
                if (dataSnapshot_Carbon.exists()) {
                    int efficiency_Carbon = dataSnapshot_Carbon.child("Efficiency").getValue(Integer.class);
                    int mileage_Carbon = dataSnapshot_Carbon.child("Mileage").getValue(Integer.class);

                    Log.d("ResultActivity", "Efficiency_Carbon: " + efficiency_Carbon);
                    Log.d("ResultActivity", "Mileage_Carbon: " + mileage_Carbon);

                    motorbikeKeyRef.child(userEntryID_Carbon_Data).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot_Carbon_Data) {
                            if (dataSnapshot_Carbon_Data.exists()) {
                                double efficiency_Carbon_Data = dataSnapshot_Carbon_Data.child("Efficiency_Data").getValue(Double.class);
                                double mileage_Carbon_Data = dataSnapshot_Carbon_Data.child("Mileage_Data").getValue(Double.class);

                                Log.d("ResultActivity", "Efficiency_Carbon_Data: " + efficiency_Carbon_Data);
                                Log.d("ResultActivity", "Mileage_Carbon_Data: " + mileage_Carbon_Data);

                                double multipliedEfficiency = efficiency_Carbon * efficiency_Carbon_Data;
                                double multipliedMileage = mileage_Carbon * mileage_Carbon_Data;

                                Log.d("ResultActivity", "Multiplied_Efficiency: " + multipliedEfficiency);
                                Log.d("ResultActivity", "Multiplied_Mileage: " + multipliedMileage);

                                // Update TextViews or perform other operations with the multiplied values
                                TextView textViewEfficiency = findViewById(R.id.efficiencydata);
                                TextView textViewMileage = findViewById(R.id.mileagedata);

                                textViewEfficiency.setText(String.valueOf(multipliedEfficiency+ "  kg/km"));
                                textViewMileage.setText(String.valueOf(multipliedMileage+"  kg/h"));
                            } else {
                                Toast.makeText(ResultActivity.this, "No data found in 'Motorbike_Carbon_Data'", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError_Carbon_Data) {
                            Toast.makeText(ResultActivity.this, "Failed to retrieve data from 'Motorbike_Carbon_Data'", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(ResultActivity.this, "No data found in 'Motorbike_Carbon'", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError_Carbon) {
                Toast.makeText(ResultActivity.this, "Failed to retrieve data from 'Motorbike_Carbon'", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
