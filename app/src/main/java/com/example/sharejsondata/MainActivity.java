package com.example.sharejsondata;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        LocationListener, GpsUtils.OnGpsListener {

    private Button send, receive;
    private final int MY_PERMISSION_REQUEST = 1;
    private TextView locationUpdates;
    private LocationManager locationManager;
    private WifiManager wifiManager;
    private WifiConfiguration wifiConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        send = findViewById(R.id.senderButton);
        receive = findViewById(R.id.receiverButton);
        locationUpdates = findViewById(R.id.locationUpdates);

        send.setOnClickListener(this);
        receive.setOnClickListener(this);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if ((ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {

            askPermission();
        }else{
            new GpsUtils(this).turnGPSOn(this);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    2000, 10, this);
        }
    }

    private void askPermission() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION}, MY_PERMISSION_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {



                    if ((ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED)
                            && (ContextCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED)) {

                        return;
                    }
                    new GpsUtils(this).turnGPSOn(this);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            2000, 1, this);

                }else{
                    showMessage("It is mandatory to accept all permissions...");
                }
                break;
        }
    }


    private void showMessage(String message){

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        askPermission();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .create();

        alertDialog.show();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.senderButton:
                intent = new Intent(MainActivity.this,ServerActivity.class);
                startActivity(intent);
                break;
            case R.id.receiverButton:
                intent = new Intent(MainActivity.this,ClientActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    public void onLocationChanged(Location location) {

        Toast.makeText(
                getBaseContext(),
                "Location changed: Lat: " + location.getLatitude() + " Lng: "
                        + location.getLongitude(), Toast.LENGTH_SHORT).show();
        String longitude = "Longitude: " + location.getLongitude();
        Log.d("rough", longitude);
        String latitude = "Latitude: " + location.getLatitude();
        Log.d("rough", latitude);

        /*------- To get city name from coordinates -------- */
        String cityName = null;
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = gcd.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                cityName = addresses.get(0).getLocality();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        String s = longitude + "\n" + latitude + "\n\nMy Current City is: "
                + cityName;
        locationUpdates.setText(s);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void gpsStatus(boolean isGPSEnable) {
        Log.d("rough","Gps Status: "+isGPSEnable);
    }
}
