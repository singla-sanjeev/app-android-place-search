package com.homeaway.placesearch.ui;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.homeaway.placesearch.R;
import com.homeaway.placesearch.databinding.ActivityVenueDetailBinding;
import com.homeaway.placesearch.model.Venue;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;

/**
 * An activity representing a single Venue detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link VenueSearchActivity}.
 */
public class VenueDetailActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String VENUE_BUNDLE_ID = "VENUE_BUNDLE_ID";
    public static final int MAP_ZOOM_LEVEL = 14; //8 Venues
    private Venue mVenue;
    private GoogleMap mMap;
    private ActivityVenueDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_venue_detail);
        Toolbar toolbar = findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        assert getIntent() != null;
        mVenue = getIntent().getParcelableExtra(VENUE_BUNDLE_ID);
        binding.setVenue(mVenue);
        binding.setCategory(mVenue.getCategories().get(0));
        FloatingActionButton fab = findViewById(R.id.fab);
        if (mVenue != null && mVenue.isFavorite()) {
            fab.setImageResource(R.drawable.ic_favorite);
        } else {
            fab.setImageResource(R.drawable.ic_favorite_border);
        }

        fab.setOnClickListener(view -> favoriteClicked());
        //capture the size of the devices screen
        Display display = getWindowManager().getDefaultDisplay();
        Point outSize = new Point();
        display.getSize(outSize);

        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        CoordinatorLayout.LayoutParams layoutParams =
                new CoordinatorLayout.LayoutParams(outSize.x, outSize.y / 2);

        appBarLayout.setLayoutParams(layoutParams);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void favoriteClicked(){
        //TODO: Need to add action
    }

    private void markVenueLocationsOnMap() {
        if (mMap == null) {
            return;
        }
        MarkerOptions markerOptions = null;
        LatLng latLng = null;
        if (mVenue != null) {
            latLng = new LatLng(mVenue.getLocation().getLat(), mVenue.getLocation().getLng());
        }
        if (latLng != null) {
            markerOptions = new MarkerOptions().position(latLng);
        }
        if (markerOptions != null) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin));
            mMap.addMarker(markerOptions);
        }

        latLng = new LatLng(Float.valueOf(getString(R.string.centre_of_seattle_latitude)),
                Float.valueOf(getString(R.string.centre_of_seattle_longitude)));
        markerOptions = new MarkerOptions().position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin));
        mMap.addMarker(markerOptions);


        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)
                .zoom(MAP_ZOOM_LEVEL).bearing(0).tilt(0).build();
        if (mMap != null) {
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        markVenueLocationsOnMap();
    }
}
