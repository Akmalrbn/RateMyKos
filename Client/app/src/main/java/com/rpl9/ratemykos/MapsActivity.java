package com.rpl9.ratemykos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;
    private Button saveButton;
    private LatLng currentMarkerPosition;
    public static double latitu, longitu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        saveButton = findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSaveButtonClick();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Set a listener for marker click to update the current marker position
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                currentMarkerPosition = marker.getPosition();
                return false;
            }
        });

        // Set a listener for map click to add a marker
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (marker != null) {
                    marker.remove(); // Remove previous marker
                }

                // Add a new marker at the clicked position
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Marker"));
            }
        });

        // Set an initial camera position (e.g., center on a specific location)
        LatLng UI = new LatLng(-6.3606, 106.8272);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UI, 15));
    }

//    private void onSaveButtonClick() {
//        if (currentMarkerPosition != null) {
//            // Save latitude and longitude to global variables
//            latitude = currentMarkerPosition.latitude;
//            longitude = currentMarkerPosition.longitude;
//
//            Toast.makeText(MapsActivity.this, "Latitude: " + latitude + ", Longitude: " + longitude + " saved", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(MapsActivity.this, "No marker to save", Toast.LENGTH_SHORT).show();
//        }
//    }
private void onSaveButtonClick() {
    if (currentMarkerPosition != null) {
        // Save latitude and longitude to global variables
        latitu = currentMarkerPosition.latitude;
        longitu = currentMarkerPosition.longitude;

        // Create an Intent to send back the result
        Intent resultIntent = new Intent();
        resultIntent.putExtra("SELECTED_LATITUDE", latitu);
        resultIntent.putExtra("SELECTED_LONGITUDE", longitu);

        setResult(RESULT_OK, resultIntent);
        finish(); // Close MapsActivity
    } else {
        Toast.makeText(MapsActivity.this, "No marker to save", Toast.LENGTH_SHORT).show();
    }
}

}
