package edu.neu.madcourse.NUMAD21Su_LilyBessette;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import java.text.DecimalFormat;
import static android.location.LocationManager.*;

public class LocatorActivity extends AppCompatActivity  {
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String TAG = LocatorActivity.class.getSimpleName();

    private Location gps_location;
    private FusedLocationProviderClient fusedLocationClient;
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

        if (!checkPermissions()) {
            requestPermissions();
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = locationManager.isProviderEnabled(GPS_PROVIDER);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (gpsEnabled & checkPermissions()) {
            getLocation();
        } else {
            Toast.makeText(LocatorActivity.this, "GPS disabled.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        fusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            gps_location = task.getResult();

                            gps_latitude = gps_location.getLatitude();
                            gps_longitude =  gps_location.getLongitude();
                            latitude.setText(  new DecimalFormat("#.0#").format(gps_latitude));
                            longitude.setText( new DecimalFormat("#.0#").format(gps_longitude));
                        } else {
                            Log.w(TAG, "getLocation:exception", task.getException());
                            Toast.makeText(LocatorActivity.this, "Location unavailable.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean checkPermissions() {
        return  PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            Snackbar.make(
                    findViewById(R.id.activity_locator_request),
                    R.string.permission_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.confirm, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(LocatorActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    })
                    .show();
        } else {
            Log.i(TAG, "Requesting permission");
            ActivityCompat.requestPermissions(LocatorActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

}

