package com.example.notemeapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.Image;
import android.os.Build;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;


public class MapsActivity extends FragmentActivity implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMapClickListener,
        GoogleMap.OnMarkerClickListener,
        LocationListener{
    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Marker[] allMarkers; // In order hide markers outside of radius, do we want?
    LocationRequest mLocationRequest;
    Circle circle;
    double iMiles = 100; // Initializer
    SeekBar seekBar;
    ImageButton sendToMyProfileBtn;
    SharedPreferences pref;
    String loggedInUser;
    EditText newNoteName;
    EditText newNoteDescription;
    ImageView newNoteImage;

    private static final String SERVER_ADDRESS = "http://192.168.1.55:8080/getallmarkers";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        seekBar = (SeekBar)findViewById(R.id.seekBarRadius);
        seekBar.getProgress();
        sendToMyProfileBtn = (ImageButton)findViewById(R.id.myProfileBtn);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Retrieve logged in username
        loggedInUser = getLoggedInUser();
        if (loggedInUser == null){
            // Retrieve failed -> Send to MainActivity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
        }

        // MyProfile page
        sendToMyProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), MyProfileActivity.class);
                startActivity(intent);
            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                // TODO Auto-generated method stub
                //seekBarValue.setText(String.valueOf(progress));
                iMiles = 100 - progress; // Handle reversed seekBar
                Log.e("level", String.valueOf(progress));
                zoomInOutCamera(mLastLocation);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }
        });
    }

    private String getLoggedInUser(){
        pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
        return pref.getString("users_username", null); // get username as String, Null if empty
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.setOnMarkerClickListener(this);

        //TODO: Load markers from DB

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // TODO send to AddNewNoteActivity the marker in order to extract, send marker.getPosition() - DONE?
        Intent intent = new Intent(this, AddNewNoteActivity.class);
        intent.putExtra("Marker_Position_To_Extract", marker.getPosition());
        Log.e("Marker is + " ,marker.getPosition().toString());
        startActivity(intent);
        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // New Marker!

        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position for the marker
        markerOptions.position(latLng);

        // Setting the title for the marker.
        // This will be displayed on taping the marker
        markerOptions.title(latLng.latitude + " : " + latLng.longitude);

        // Clears the previously touched position
        //mMap.clear();

        // Animating to the touched position
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        // Placing a marker on the touched position
        mMap.addMarker(markerOptions);

        Intent intent = new Intent(this, AddNewNoteActivity.class);
        intent.putExtra("Marker_Position_To_Extract", markerOptions.getPosition());
        Log.e("Marker is + " ,markerOptions.getPosition().toString());
        startActivity(intent);
        //finish();

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);

        zoomInOutCamera(location);

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    private void zoomInOutCamera(Location location){
        // Zoom in, animating the camera.
        double iMeter = iMiles * 1609.34;

        if (circle!= null){
            circle.remove();
        }
        //circle.remove();
        circle = mMap.addCircle(new CircleOptions()
                .center(new LatLng(location.getLatitude(), location.getLongitude()))
                .radius(iMeter) // Converting Miles into Meters...
                .strokeColor(Color.RED)
                .strokeWidth(5));
        circle.isVisible();
        float currentZoomLevel = getZoomLevel(circle);
        float animateZoom = currentZoomLevel + 5;

        Log.e("Zoom Level:", currentZoomLevel + "");
        Log.e("Zoom Level Animate:", animateZoom + "");

        // Zoom could also be calculated: getBaseLog(2, 40000 / (km / 2))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), animateZoom));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(currentZoomLevel), 2000, null);
        Log.e("Circle Lat Long:", location.getLatitude() + ", " + location.getLongitude());
        mLastLocation = location;
    }

    private float getZoomLevel(Circle circle) {
        float zoomLevel=0;
        if (circle != null){
            double radius = circle.getRadius();
            double scale = radius / 500;
            zoomLevel =(int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel +.5f;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(this,
                        new String[] {
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[] {
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) ==
                            PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void addMarkerToDB(LatLng latLng){
        try {
            MongoClientURI uri = new MongoClientURI("mongodb://localhost:27017/addmarker");
            MongoClient client = new MongoClient(uri);

            MongoDatabase db = client.getDatabase(uri.getDatabase());
            MongoCollection<BasicDBObject> collection = db.getCollection("markers", BasicDBObject.class);

            // FIX!!!
            BasicDBObject document = new BasicDBObject();
            document.put("name", "mkyong");
            document.put("age", 30);
            collection.insertOne(document);

            MongoCursor iterator = collection.find().iterator();

            while (iterator.hasNext()) {
                System.out.println(iterator.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
