package com.example.bankingapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, LocationListener {

    // Location
    private TextView locality;
    private LocationManager locationManager;
    private long minTime = 500;
    private float minDistance = 1;
    private static final int MY_PERMISSION_GPS = 1;
    // Store location as string for further use
    public String loc;

    // Attributes for login screen
    private Button login;
    private EditText username;
    private EditText password;
    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        locality = (TextView) findViewById(R.id.locality);
        setUpLocation();

        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);

        login = (Button)findViewById(R.id.login);

        dbManager = new DatabaseManager(this);

        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:

                final String uName = username.getText().toString();
                final String pWord = password.getText().toString();

                // Check if person exist in database
                boolean isExist = dbManager.getPerson(uName, pWord);

                if(isExist) {
                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
                    main.putExtra("uName", uName);
                    main.putExtra("loc", loc);
                    startActivity(main);
                    username.setText(null);
                    password.setText(null);
                }
                else {
                    username.setText(null);
                    password.setText(null);
                    Toast.makeText(LoginActivity.this, "Username or Password Incorrect, please try again.", Toast.LENGTH_LONG).show();
                }
        }
    }

    private void setUpLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        //Check if permission exists, if not ask the user

        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(LoginActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSION_GPS);
        } else { // permission granted

            // switch on location tracking
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, this);
        }
    }

    public void onLocationChanged(Location location) { // what happens when a location change is detected
        String addressName = "";

        if (location != null) {
            // get a geocoder object
            Geocoder geo = new Geocoder(LoginActivity.this.getApplicationContext(),
                    Locale.getDefault());

            // call the method to translate GPS to address(es)
            List<Address> addresses = null;
            try {
                addresses = geo.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addresses.size() > 0) {
                addressName = addresses.get(0).getAdminArea() + ", " +
                        addresses.get(0).getCountryName();
                loc = addresses.get(0).getCountryName();
            }
        }
        locality.setText("Current Location: " + addressName);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_GPS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplication(), "Permission Granted!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplication(), "Please switch on permission to get location!", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
