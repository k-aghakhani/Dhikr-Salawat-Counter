package com.example.dhikr_salawat_counter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private int counter = 0;
    private Spinner spinnerDhikr;
    private TextView tvCounter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Handle system bars insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize views
        spinnerDhikr = findViewById(R.id.spinner_dhikr);
        tvCounter = findViewById(R.id.tv_counter);
        Button btnCount = findViewById(R.id.btn_count);
        Button btnReset = findViewById(R.id.btn_reset);
        Button btnSave = findViewById(R.id.btn_save);

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("DhikrCounterPrefs", MODE_PRIVATE);

        // Set up spinner with Dhikr items
        String[] dhikrItems = {
                getResources().getString(R.string.AllahuAkbar),
                getResources().getString(R.string.Alhamdulillah),
                getResources().getString(R.string.SubhanAllah),
                getResources().getString(R.string.Astaghfirullah),
                getResources().getString(R.string.Lailahaillallah),
                getResources().getString(R.string.Lahawlawala),
                getResources().getString(R.string.Salawat),
        };

        // Create and customize ArrayAdapter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dhikrItems) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                ((TextView) view).setGravity(Gravity.CENTER); // Center align spinner items
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                ((TextView) view).setGravity(Gravity.CENTER); // Center align dropdown items
                view.setPadding(0, 20, 0, 20);  // Add vertical padding between dropdown items
                return view;
            }
        };

        spinnerDhikr.setAdapter(adapter);

        // Add listener to spinner to handle item selection
        spinnerDhikr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedDhikr = spinnerDhikr.getSelectedItem().toString();
                counter = sharedPreferences.getInt(selectedDhikr, 0); // Load saved counter for selected Dhikr
                tvCounter.setText(String.valueOf(counter));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tvCounter.setText("0"); // Default counter value when nothing is selected
            }
        });

        // Handle count button click to increment counter
        btnCount.setOnClickListener(v -> {
            counter++;
            tvCounter.setText(String.valueOf(counter));
        });

        // Handle reset button click to reset counter
        btnReset.setOnClickListener(v -> {
            counter = 0;
            tvCounter.setText(String.valueOf(counter));
        });

        // Handle save button click to save counter value for selected Dhikr
        btnSave.setOnClickListener(v -> {
            String selectedDhikr = spinnerDhikr.getSelectedItem().toString();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(selectedDhikr, counter);
            editor.apply();
            Toast.makeText(MainActivity.this, getResources().getString(R.string.progress_saved), Toast.LENGTH_SHORT).show();
        });
    }

    // Load the saved counter for the selected Dhikr
    private void loadCounter() {
        String selectedDhikr = spinnerDhikr.getSelectedItem().toString();
        counter = sharedPreferences.getInt(selectedDhikr, 0); // Retrieve saved count
        tvCounter.setText(String.valueOf(counter));
    }
}

