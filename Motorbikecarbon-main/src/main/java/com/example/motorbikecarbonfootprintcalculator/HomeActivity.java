package com.example.motorbikecarbonfootprintcalculator;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    Button buttonCalculate;
    DatabaseReference databaseReference;
    EditText editmileage, editefficiency;
    Spinner spinnermileag, spinnerselec, spinnerefficienc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        editmileage =findViewById(R.id.editmileage);
        editefficiency = findViewById(R.id.editefficiency);

        spinnermileag = findViewById(R.id.spinnermileage);
        spinnerselec = findViewById(R.id.spinnerselect);
        spinnerefficienc = findViewById(R.id.spinnerefficiency);

        List<String> spinmile = new ArrayList<>();
        spinmile.add("km");
        spinmile.add("miles");
        ArrayAdapter<String> mileAdapter = new ArrayAdapter<>
                (HomeActivity.this, android.R.layout.simple_spinner_item, spinmile);
        mileAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnermileag.setAdapter(mileAdapter);

        List<String> spinsel = new ArrayList<>();
        spinsel.add("-select type-");
        spinsel.add("small motorbike/moped/scooter up to 125cc");
        spinsel.add("medium motorbike over 125cc and up to 500cc");
        spinsel.add("large motorbike over 500cc");
        ArrayAdapter<String> selAdapter = new ArrayAdapter<>
                (HomeActivity.this, android.R.layout.simple_spinner_item, spinsel);
        selAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerselec.setAdapter(selAdapter);

        List<String> spineffic = new ArrayList<>();
        spineffic.add("g/km");
        spineffic.add("L/100km");
        spineffic.add("mpg (UK)");
        spineffic.add("mpg (US)");
        ArrayAdapter<String> efficAdapter = new ArrayAdapter<>
                (HomeActivity.this, android.R.layout.simple_spinner_item, spineffic);
        efficAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerefficienc.setAdapter(efficAdapter);

        buttonCalculate = findViewById(R.id.btnCalculate);
        buttonCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Calculate button clicked");
                databaseReference = FirebaseDatabase.getInstance().getReference().child("Motorbike_Carbon");
                String mileagedb = editmileage.getText().toString().trim();
                String efficiencydb = editefficiency.getText().toString().trim();

                saveDataToFirebase(mileagedb, efficiencydb);
            }
        });

        // Xử lý khi người dùng chọn một option trên Spinner
        spinnermileag.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedValue = position + 1;
                String selectedOption = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "Selected Mileage Unit: " + selectedOption);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void saveDataToFirebase(String mileagedb, String efficiencydb) {
        Log.d(TAG, "Saving data to Firebase...");

        try {
            double mileageValue = Double.parseDouble(mileagedb);
            double efficiencyValue = Double.parseDouble(efficiencydb);

            String MotorbikeKey = databaseReference.push().getKey(); // Generate a dynamic key

            Map<String, Object> entryMap = new HashMap<>();
            entryMap.put("Mileage", mileageValue);
            entryMap.put("Efficiency", efficiencyValue);

            if (databaseReference != null) {
                databaseReference.child("MotorbikeCarbon").setValue(entryMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Data saved successfully.");
                                    Intent intent = new Intent(HomeActivity.this, ResultActivity.class);
                                    startActivity(intent);
                                } else {
                                    Log.e(TAG, "Failed to save data.", task.getException());
                                    Toast.makeText(HomeActivity.this, "Failed to save data.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                Log.e(TAG, "Database reference is null.");
                Toast.makeText(HomeActivity.this, "Failed to save data. Database reference is null.", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Failed to parse mileage or efficiency.", e);
            Toast.makeText(HomeActivity.this, "Invalid input. Please enter valid numbers for mileage and efficiency.", Toast.LENGTH_SHORT).show();
        }
    }

}