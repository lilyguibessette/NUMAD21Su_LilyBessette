package edu.neu.madcourse.NUMAD21Su_LilyBessette;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import java.text.DecimalFormat;


public class LocatorActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String TAG = LocatorActivity.class.getSimpleName();

    Button back;
    TextView longitude;
    TextView latitude;
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
        updateLocationData();
    }


    protected void updateLocationData() {
        if (checkPermissions()) {
            getLocation();
        } else {
            Log.i(TAG, "Requesting permission");
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            return true;
        } else if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.activity_locator),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(LocatorActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                            Manifest.permission.ACCESS_COARSE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Snackbar.make(
                    findViewById(R.id.activity_locator),
                    R.string.location_already_denied,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Dismiss
                        }
                    })
                    .show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Toast.makeText(LocatorActivity.this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setLocationText(Location location) {
        gps_latitude = location.getLatitude();
        gps_longitude = location.getLongitude();
        latitude.setText(new DecimalFormat("#.0#").format(gps_latitude));
        longitude.setText(new DecimalFormat("#.0#").format(gps_longitude));
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            fusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener() {
                @SuppressLint("MissingPermission")
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        Location location = (Location) task.getResult();
                        if (location != null) {
                            setLocationText(location);
                        } else {
                            LocationRequest locationRequest = new LocationRequest()
                                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                    .setInterval(100)
                                    .setFastestInterval(10)
                                    .setNumUpdates(1);
                            LocationCallback locationCallback = new LocationCallback() {
                                @Override
                                public void onLocationResult(LocationResult locationResult) {
                                    Location location = locationResult.getLastLocation();
                                    setLocationText(location);
                                }
                            };
                        }
                    } else {
                        Log.w(TAG, "getLocation:exception", task.getException());
                        Toast.makeText(LocatorActivity.this, "Location unavailable.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Snackbar.make(
                    findViewById(R.id.activity_locator),
                    R.string.location_tracking_off,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Dismiss
                        }
                    })
                    .show();
        }
    }
}
