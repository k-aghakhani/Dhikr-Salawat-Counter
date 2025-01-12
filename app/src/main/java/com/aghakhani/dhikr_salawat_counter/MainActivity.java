package com.aghakhani.dhikr_salawat_counter;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private int counter = 0;
    private Spinner spinnerDhikr;
    private TextView tvCounter;
    private SharedPreferences sharedPreferences;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> dhikrList;

    private final String[] defaultDhikrItems = {};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);


        // Set up spinner with Dhikr items
        String[] defaultDhikrItems = {
                getResources().getString(R.string.AllahuAkbar),
                getResources().getString(R.string.Alhamdulillah),
                getResources().getString(R.string.SubhanAllah),
                getResources().getString(R.string.Astaghfirullah),
                getResources().getString(R.string.Lailahaillallah),
                getResources().getString(R.string.Lahawlawala),
                getResources().getString(R.string.Salawat),
        };


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
        ImageButton navSettings = findViewById(R.id.nav_settings);
        ImageButton navAdd = findViewById(R.id.nav_add);

        // Initialize shared preferences
        sharedPreferences = getSharedPreferences("DhikrCounterPrefs", MODE_PRIVATE);

        // Initialize Dhikr list and adapter
        dhikrList = new ArrayList<>(Arrays.asList(defaultDhikrItems));
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dhikrList) {
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
                view.setPadding(30, 20, 30, 20); // Add vertical padding between dropdown items
                return view;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_item); // Apply the same layout for dropdown items
        spinnerDhikr.setAdapter(adapter);

        // Add listener to spinner to handle item selection
        spinnerDhikr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadCounter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                tvCounter.setText("0");
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
            //Toast.makeText(MainActivity.this, getString(R.string.progress_saved), Toast.LENGTH_SHORT).show();
            custom_toast(getString(R.string.progress_saved));

        });

        // Initialize settings button
        navSettings.setOnClickListener(v -> showContactDialog());

        // Initialize add Dhikr button
        navAdd.setOnClickListener(v -> showAddDhikrDialog());
    }

    private void showAddDhikrDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_dhikr, null);
        builder.setView(dialogView);

        EditText etDhikrName = dialogView.findViewById(R.id.et_dhikr_name);
        Button btnDeleteDhikr = dialogView.findViewById(R.id.btn_delete_dhikr);

        String selectedDhikr = spinnerDhikr.getSelectedItem().toString();
        if (!Arrays.asList(defaultDhikrItems).contains(selectedDhikr)) {
            btnDeleteDhikr.setVisibility(View.VISIBLE);
        }

        AlertDialog dialog = builder.create();

        dialogView.findViewById(R.id.btn_add_dhikr).setOnClickListener(v -> {
            String newDhikr = etDhikrName.getText().toString().trim();
            if (!newDhikr.isEmpty() && !dhikrList.contains(newDhikr)) {
                dhikrList.add(newDhikr);
                adapter.notifyDataSetChanged();
                spinnerDhikr.setSelection(dhikrList.indexOf(newDhikr));
                //  Toast.makeText(this, getString(R.string.dhikr_added), Toast.LENGTH_SHORT).show();
                custom_toast(getString(R.string.dhikr_added));
            } else {
                //  Toast.makeText(this, getString(R.string.invalid_or_duplicate_dhikr), Toast.LENGTH_SHORT).show();
                custom_toast(getString(R.string.invalid_or_duplicate_dhikr));
            }
            dialog.dismiss();
        });

        btnDeleteDhikr.setOnClickListener(v -> {
            if (dhikrList.contains(selectedDhikr)) {
                dhikrList.remove(selectedDhikr);
                adapter.notifyDataSetChanged();
                spinnerDhikr.setSelection(0);
                //   Toast.makeText(this, getString(R.string.dhikr_removed), Toast.LENGTH_SHORT).show();
                custom_toast(getString(R.string.dhikr_removed));
            }
            dialog.dismiss();
        });

        dialog.show();
    }

    private void showContactDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_contact, null);
        builder.setView(dialogView);
        builder.setTitle("add")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void loadCounter() {
        String selectedDhikr = spinnerDhikr.getSelectedItem().toString();
        counter = sharedPreferences.getInt(selectedDhikr, 0);
        tvCounter.setText(String.valueOf(counter));
    }

    private void custom_toast(String s) {
        LayoutInflater inflater = getLayoutInflater();
        View customToastLayout = inflater.inflate(R.layout.custom_toast, null);
        // Set the text and image inside the Toast
        TextView toastMessage = customToastLayout.findViewById(R.id.toast_message);
        //  ImageView toastIcon = customToastLayout.findViewById(R.id.toast_icon);
        toastMessage.setText(s);
        //  toastIcon.setImageResource(R.drawable.ic_info);
        // Create and display Toast
        Toast customToast = new Toast(getApplicationContext());
        customToast.setDuration(Toast.LENGTH_LONG);
        customToast.setView(customToastLayout);
        customToast.show();

    }
}
