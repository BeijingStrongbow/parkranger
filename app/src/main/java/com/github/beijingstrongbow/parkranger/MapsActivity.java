package com.github.beijingstrongbow.parkranger;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

    private LocationManager locationManager;

    private FirebaseHandler handler;

    private LocationHandler locationHandler;

    private ArrayList<Marker> markers;

    TextView nametxt;
    TextView groupidtxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = FirebaseHandler.getInstance();
        setContentView(R.layout.activity_maps);

        // Show name and groupID
        nametxt = (TextView) findViewById(R.id.nametxt);
        groupidtxt = (TextView) findViewById(R.id.groupidtxt);

        nametxt.setText(handler.getName());
        groupidtxt.setText(handler.getGroupID());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Define a listener that responds to location updates

        locationHandler = LocationHandler.getInstance();

        // SOS Button

        Button sosbtn = (Button) findViewById(R.id.sosbtn);
        sosbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, SosMessage.class);
                startActivity(intent);
            }
        });

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
        mMap.setOnInfoWindowClickListener(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
            markers = new ArrayList<Marker>();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationHandler.getLatitude(), locationHandler.getLongitude()), 18));

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
                ArrayList<User> rangers = handler.getRangerLocations();

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
                        options.zIndex(5);
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
                        System.out.println("abc"+ sos.get(i).message);
                        options.title(sos.get(i).message);
                        options.zIndex(15);
                        System.out.println("here" + sos.get(i).snippet);
                        if(sos.get(i).snippet != null && !sos.get(i).snippet.equals("") && !sos.get(i).snippet.equals("null")) {
                            options.snippet(sos.get(i).snippet);
                        }

                        markers.add(mMap.addMarker(options));
                    }
                }
                for(int i = 0; i < rangers.size(); i++) {
                    System.out.println("asdf");
                    MarkerOptions options = new MarkerOptions();
                    options.draggable(false);
                    options.position(new LatLng(rangers.get(i).latitude, rangers.get(i).longitude));
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    options.title(rangers.get(i).name);
                    options.zIndex(10);
                    markers.add(mMap.addMarker(options));
                }

                h.postDelayed(this, 1000);
            }
        };

        h.postDelayed(updater, 1000);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        handler.handleSOS(marker.getTitle());
        System.out.println("abcdef123");
    }
}
