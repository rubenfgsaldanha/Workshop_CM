package com.example.rbensaldanha.cm_workshop;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int MY_LOCATION_REQUEST_CODE = 1000;
    private static final float DEFAULT_ZOOM = 15f;
    private static final String TAG = "MapsActivity";
    private static final int updates = 999999999;

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Marker locationMarker;
    private LatLng myLocation;
    private LatLng chosenLocal;
    private Button btn_normal, btn_satellite, btn_terrain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_maps2);

        btn_normal = (Button)findViewById(R.id.btn_normal);
        btn_satellite = (Button)findViewById(R.id.btn_satellite);
        btn_terrain = (Button)findViewById(R.id.btn_terrain);

        Intent intent = getIntent();
        double lat = intent.getDoubleExtra("latitude",0);
        double lon = intent.getDoubleExtra("longitude",0);
        chosenLocal = new LatLng(lat,lon);

        checkPermissions();
    }


    //Checks permissions
    private void checkPermissions(){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, MY_LOCATION_REQUEST_CODE);
        }
        else{
            initMap();
        }
    }

    //initializes map
    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                myLocation = new LatLng(locationResult.getLastLocation().getLatitude(), locationResult.getLastLocation().getLongitude());
                showLocation();
            }
        };
    }

    //shows location on the map
    private void showLocation(){
        Log.d(TAG,"Showing location:   lat = "+myLocation.latitude+"     long = "+myLocation.longitude);
        if(locationMarker != null){
            locationMarker.setPosition(myLocation);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
        }
        else{
            locationMarker = mMap.addMarker(new MarkerOptions().position(myLocation).title("You are here"));

            mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(DEFAULT_ZOOM));
        }
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
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        btn_normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });

        btn_satellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            }
        });

        btn_terrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            }
        });

        //detect long click
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions().position(latLng).title("Here"));

                //do calculations
                if(Math.abs(chosenLocal.latitude - latLng.latitude) < 1 && Math.abs(chosenLocal.longitude - latLng.longitude) < 1){
                    Toast.makeText(MapsActivity.this,"You get 50 points :D",Toast.LENGTH_SHORT).show();
                }
                else if(Math.abs(chosenLocal.latitude - latLng.latitude) < 3 && Math.abs(chosenLocal.longitude - latLng.longitude) < 3){
                    Toast.makeText(MapsActivity.this,"You get 25 points!",Toast.LENGTH_SHORT).show();
                }
                else if(Math.abs(chosenLocal.latitude - latLng.latitude) < 5 && Math.abs(chosenLocal.longitude - latLng.longitude) < 5){
                    Toast.makeText(MapsActivity.this,"You get 10 points!",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(MapsActivity.this,"You get 0 points =(",Toast.LENGTH_SHORT).show();
                }
            }
        });

        locationUpdates();
    }

    //gets updates on the location
    @SuppressLint("MissingPermission")
    private void locationUpdates(){
        Log.d(TAG, "getDeviceLocation: getting the device's current location");

        locationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(updates);

        mFusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FUSED LOCATION PROVIDER", e.getMessage());
            }
        });
    }

    //handles the permission request(s)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_LOCATION_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                initMap();
            }
        }
    }

    @Override
    protected void onDestroy() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mFusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onPause();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        mFusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FUSED LOCATION PROVIDER", e.getMessage());
            }
        });
        super.onResume();
    }
}
