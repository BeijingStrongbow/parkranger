package com.github.beijingstrongbow.parkranger;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by ericd on 4/21/2018.
 */

public class LocationHandler implements ActivityCompat.OnRequestPermissionsResultCallback {

    private LocationManager locationManager;

    private LocationListener listener;

    private double latitude;
    private double longitude;

    private Activity activity;

    private static LocationHandler h = null;

    public static LocationHandler getInstance() throws RuntimeException {
        if(h == null) {
            throw new RuntimeException("LocationHandler not initialized");
        }

        return h;
    }

    public static LocationHandler getInstance(Activity a, LocationManager loc) {
        if(h == null) {
            h = new LocationHandler(a, loc);
        }

        return h;
    }

    private LocationHandler(Activity a, LocationManager loc) {
        locationManager = loc;
        activity = a;
        String locationProvider = LocationManager.GPS_PROVIDER;

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            addLocationListener(locationManager);
        }
        else {
            String[] permissions = {"android.permission.ACCESS_FINE_LOCATION"};
            activity.requestPermissions(permissions, 5);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        addLocationListener(locationManager);
    }

    private void addLocationListener(LocationManager locationManager) {
        listener = new LocationListener() {
            public void onLocationChanged(Location location) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {
                System.out.println("GPS enabled!");
            }

            public void onProviderDisabled(String provider) {}
        };
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, listener);
        }
    }

    public double getLatitude() {
        return latitude;
    }
    public double getLongitude() {
        return longitude;
    }
}
