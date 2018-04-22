package com.github.beijingstrongbow.parkranger;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback  {

    private GoogleMap mMap;

    private LocationManager locationManager;

    private FirebaseHandler handler;

    private LocationHandler locationHandler;

    private ArrayList<Marker> markers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = FirebaseHandler.getInstance();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Define a listener that responds to location updates

        locationHandler = LocationHandler.getInstance();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
            markers = new ArrayList<Marker>();
        startMapUpdater();
    }

    private void startMapUpdater() {
        final Handler h = new Handler();
;
        Runnable updater = new Runnable() {
            @Override
            public void run() {
                ArrayList<User> users = handler.getLocations();
                ArrayList<SOS> sos = handler.getSOS();

                for(int i = 0; i < markers.size(); i++) {
                    markers.get(i).remove();
                }
                markers.clear();
                if(users.size() > 0) {
                    for (int i = 0; i < users.size(); i++) {
                        MarkerOptions options = new MarkerOptions();
                        options.draggable(false);
                        options.title(users.get(i).name);
                        options.position(new LatLng(users.get(i).latitude, users.get(i).longitude));
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        markers.add(mMap.addMarker(options));
                    }
                }
                if(sos.size() > 0) {
                    System.out.println(sos.size());
                    for (int i = 0; i < sos.size(); i++) {
                        System.out.println("asdf");
                        MarkerOptions options = new MarkerOptions();
                        options.draggable(false);
                        options.position(new LatLng(sos.get(i).latitude, sos.get(i).longitude));
                        markers.add(mMap.addMarker(options));
                    }
                }

                h.postDelayed(this, 1000);
            }
        };

        h.postDelayed(updater, 1000);
    }
}
