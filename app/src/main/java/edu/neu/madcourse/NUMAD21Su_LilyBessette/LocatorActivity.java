package edu.neu.madcourse.NUMAD21Su_LilyBessette;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import java.text.DecimalFormat;

public class LocatorActivity extends AppCompatActivity {
    Button back;
    TextView longitude;
    TextView latitude;
    LocationManager locationManager;
    double gps_longitude;
    double gps_latitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);
        back = findViewById(R.id.backlocator);
        latitude = findViewById(R.id.locator_latitude);
        longitude = findViewById(R.id.locator_longitude);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(LocatorActivity.this, MainActivity.class);
            startActivity(intent);
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled =  locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (gpsEnabled) {
            Toast.makeText(LocatorActivity.this, "GPS Enabled.", Toast.LENGTH_SHORT).show();
            if (ContextCompat.checkSelfPermission(LocatorActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                Location gps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (gps != null) {
                    Toast.makeText(LocatorActivity.this, "GPS not null.", Toast.LENGTH_SHORT).show();
                    gps_latitude = gps.getLatitude();
                    gps_longitude =  gps.getLongitude();
                    latitude.setText(  new DecimalFormat("#.0#").format(gps_latitude));
                    longitude.setText( new DecimalFormat("#.0#").format(gps_longitude));
                } else {
                    Toast.makeText(LocatorActivity.this, "Location unavailable.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



}

