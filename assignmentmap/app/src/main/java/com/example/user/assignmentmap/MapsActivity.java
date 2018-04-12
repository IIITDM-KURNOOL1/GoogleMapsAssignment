package com.example.user.assignmentmap;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double sourceLattitude,sourceLongitude,destinationLattitude,destinationLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        destinationLattitude = intent.getDoubleExtra("Lattitude",0);
        destinationLongitude = intent.getDoubleExtra("Longitude",0);
        sourceLattitude = intent.getDoubleExtra("MyLattitude",0);
        sourceLongitude = intent.getDoubleExtra("MyLongitude",0);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        mMap.clear();

        // Add a marker in Sydney and move the camera
        LatLng myLocation = new LatLng(sourceLattitude,sourceLongitude);
        mMap.addMarker(new MarkerOptions().position(myLocation).title("Your Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));

        // Add a marker in Sydney and move the camera
        LatLng destination = new LatLng(destinationLattitude,destinationLongitude);
        mMap.addMarker(new MarkerOptions().position(destination).title("Destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(destination));

        String url = getDirectionUrl();
        GetDirectionsData getDirectionsData = new GetDirectionsData();
        Object dataTransfer[] = new Object[3];
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        dataTransfer[2] = new LatLng(destinationLattitude,destinationLongitude);
        getDirectionsData.execute(dataTransfer);
    }

    private String getDirectionUrl()
    {
        StringBuilder googleDirectionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionUrl.append("origin="+sourceLattitude+","+sourceLongitude);
        googleDirectionUrl.append("&destination="+destinationLattitude+","+destinationLongitude);
        googleDirectionUrl.append("&key="+"AIzaSyDhH1K5isxwIuOTmcWN6XcNFFKGo8fjXHU");

        return googleDirectionUrl.toString();
    }
}
