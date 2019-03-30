package com.homeaway.placesearch.ui;

import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.homeaway.placesearch.R;
import com.homeaway.placesearch.model.Venue;

import java.util.ArrayList;

public class VenueMapActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;
    public static final String VENUE_LIST_BUNDLE_ID = "VENUE_LIST_BUNDLE_ID";
    private ArrayList<Venue> mVenueList;
    public static final int MAP_ZOOM_LEVEL = 14; //8 Venues


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        assert getIntent() != null;
        mVenueList = getIntent().getParcelableArrayListExtra(VENUE_LIST_BUNDLE_ID);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void markVenueLocationsOnMap() {

        if (mMap == null) {
            return;
        }
        mMap.setOnMarkerClickListener(this);
        MarkerOptions markerOptions;
        LatLng latLng;
        for (Venue venue : mVenueList) {
            latLng = new LatLng(venue.getLocation().getLat(), venue.getLocation().getLng());
            markerOptions = new MarkerOptions().position(latLng);
            markerOptions.title(venue.getName());
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin));
            mMap.addMarker(markerOptions);
        }
        latLng = new LatLng(Float.valueOf(getString(R.string.centre_of_seattle_latitude)), Float.valueOf(getString(R.string.centre_of_seattle_longitude)));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)
                .zoom(MAP_ZOOM_LEVEL).bearing(0).tilt(0).build();
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        mMap.setOnInfoWindowClickListener(this);
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
        markVenueLocationsOnMap();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(marker.getPosition())
                .zoom(MAP_ZOOM_LEVEL).bearing(0).tilt(0).build();
        if (mMap != null) {
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        marker.showInfoWindow();

        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(this, VenueDetailActivity.class);
        startActivity(intent);
    }
}