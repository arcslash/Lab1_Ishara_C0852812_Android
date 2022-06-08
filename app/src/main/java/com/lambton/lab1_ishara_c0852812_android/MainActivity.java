package com.lambton.lab1_ishara_c0852812_android;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnPolylineClickListener, GoogleMap.OnPolygonClickListener, GoogleMap.OnMapClickListener {

    MapView mapView;
    GoogleMap googleMap;

    EditText etLocation;
    Button btnAddLocation;
    LocationManager locationManager;
    Geocoder geocoder;

    // Marker Labels and current markers
    final String[] MARKER_LABELS = new String[]{"A", "B", "C", "D"};
    final int POLYGON_SIDES = 4;


    ArrayList<MarkerOptions> currentMarkers = new ArrayList<MarkerOptions>();
    ArrayList<Polyline> currentPolyLines = new ArrayList<Polyline>();


    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView=findViewById(R.id.mapView);
        etLocation=findViewById(R.id.etLocation);
        btnAddLocation=findViewById(R.id.btnAdd);
        Bundle mapbundle = null;
        if (savedInstanceState != null)
        {
            mapbundle=savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapView.onCreate(mapbundle);
        mapView.getMapAsync(this);
        mapView.setClickable(true);
        locationPermissionRequest();
        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentTextLocation = etLocation.getText().toString();
                Log.d("BUTTON_CLICK", "User Added Location: " + currentTextLocation);
                LatLng latLng = getLocationFromAddress(currentTextLocation);
                Log.d("BUTTON_CLICK", "User Added Location Latlang: " + latLng);
                // check for north america
                if(latLng != null){
                    addMarker(latLng);
                }else{
                    Toast.makeText(MainActivity.this,"Location Not found",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    private void getLocationUpdates(){
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("PERMISSION", "Permission Not Given");


        }else{
            Log.d("PERMISSION", "Location Permission Granted");
//            locationManager = (LocationManager)  this.getSystemService(Context.LOCATION_SERVICE);
//            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

        }
    }

    public LatLng getLocationFromAddress(String strAddress) {

        Geocoder coder = new Geocoder(this);
        List<Address> address;
        LatLng p1 = null;

        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null || address.size() == 0) {
                return null;
            }
            Log.d("GEOCODE", "Got the location:" + address);
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            p1 = new LatLng( (location.getLatitude()),
                    (location.getLongitude()));

            return p1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void locationPermissionRequest(){
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                            Boolean fineLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_FINE_LOCATION, false);
                            Boolean coarseLocationGranted = result.getOrDefault(
                                    Manifest.permission.ACCESS_COARSE_LOCATION,false);
                            if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                                Toast.makeText(this,"Precise location access granted",Toast.LENGTH_SHORT).show();
                            } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                                Toast.makeText(this,"Only approximate location access granted",Toast.LENGTH_SHORT).show();
                            } else {
                                // No location access granted.
                                Toast.makeText(this,"No location access granted",Toast.LENGTH_SHORT).show();
                            }
                        }
                );
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    @Override
    public void onPolygonClick(@NonNull Polygon polygon) {

    }

    @Override
    public void onPolylineClick(@NonNull Polyline polyline) {

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Adding map controls
        this.googleMap = googleMap;
        googleMap.setMinZoomPreference(12);
        googleMap.setIndoorEnabled(true);
        UiSettings uiSettings = googleMap.getUiSettings();
        uiSettings.setIndoorLevelPickerEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        uiSettings.setMapToolbarEnabled(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);

        LatLng ny = new LatLng(43.6532, -79.3832);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(ny));



        googleMap.setOnMapLongClickListener(this);
        googleMap.setOnMapClickListener(this);
        googleMap.setOnPolygonClickListener(this);
        googleMap.setOnPolylineClickListener(this);
    }

    private String findNextMarker(){
        for(String markerLabel: MARKER_LABELS){

        }
        return "A";
    }

    private void addMarker(LatLng latLng){
        String currentMarker = findNextMarker();
        MarkerOptions markerOption = new MarkerOptions().position(latLng).title(currentMarker);
        googleMap.addMarker(markerOption).showInfoWindow();
        currentMarkers.add(markerOption);
        if(currentMarkers.size() > 1){
            //draw polyline
            Log.d("MAP_CLICK", "Adding polyline");
            Polyline polyline = googleMap.addPolyline(new PolylineOptions()
                    .clickable(true)
                    .color(Color.RED)
                    .add(
                            currentMarkers.get(currentMarkers.size() - 2).getPosition(),
                            currentMarkers.get(currentMarkers.size() - 1).getPosition())
            );
            currentPolyLines.add(polyline);

            if(currentMarkers.size() > POLYGON_SIDES - 1){

                PolygonOptions opts=new PolygonOptions();

                for (MarkerOptions marker: currentMarkers) {
                    opts.add(marker.getPosition());
                }
                Polygon polygon = googleMap.addPolygon(opts.strokeColor(Color.RED).fillColor(Color.parseColor("#4300ff00")));
            }

        }
    }
//    @Override
//    public void onLocationChanged(@NonNull Location location) {
//
//    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        Log.d("MAP_CLICK", "Map Long click" + latLng);


    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        Log.d("MAP_CLICK", "Map just click" + latLng);
        addMarker(latLng);
    }
}