package com.homeaway.placesearch.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.homeaway.placesearch.Injection;
import com.homeaway.placesearch.R;
import com.homeaway.placesearch.VenueListViewModel;
import com.homeaway.placesearch.ViewModelFactory;
import com.homeaway.placesearch.model.Venue;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

/**
 * {@link VenueMapFragment} class launch a full screen Google map screen with a pin
 * for every search result. Clicking a pin is showing the name of the place on the map,
 * and clicking on name is opening venue details screen with details of that place.
 * Activities that contain this fragment must implement the
 * {@link VenueMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VenueMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VenueMapFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {
    private static final int MAP_ZOOM_LEVEL = 14; //8 Venues
    private Activity mActivity;
    private GoogleMap mGoogleMap;
    private List<Venue> mVenueList = new ArrayList<>();
    private VenueListViewModel mVenueListViewModel;

    private OnFragmentInteractionListener mListener;

    public VenueMapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VenueMapFragment.
     */
    public static VenueMapFragment newInstance() {
        return new VenueMapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_venue_map, container, false);

        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory();
        mVenueListViewModel = ViewModelProviders.of((MainActivity) mActivity, mViewModelFactory).get(VenueListViewModel.class);
        mVenueListViewModel.getVenueList().observe(this, new Observer<List<Venue>>() {
            @Override
            public void onChanged(List<Venue> venues) {
                if (null != venues && venues.size() > 0) {
                    mVenueList.clear();
                    mVenueList.addAll(venues);
                }
            }
        });
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        ViewGroup rootViewGroup = mView.findViewById(R.id.mapVwFrmLyt);
        rootViewGroup.requestTransparentRegion(rootViewGroup);
        FragmentManager fragmentManager = getChildFragmentManager();
        SupportMapFragment mSupportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.googleMap);
        if (mSupportMapFragment != null) {
            mSupportMapFragment.getMapAsync(this);
        }
        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        markVenueLocationsOnMap();
    }

    private void markVenueLocationsOnMap() {
        assert mGoogleMap != null;
        mGoogleMap.setOnMarkerClickListener(this);
        MarkerOptions markerOptions;
        LatLng latLng;
        if (mVenueList != null && mVenueList.size() > 0) {
            for (Venue venue : mVenueList) {
                latLng = new LatLng(venue.getLocation().getLat(), venue.getLocation().getLng());
                markerOptions = new MarkerOptions().position(latLng);
                markerOptions.title(venue.getName());
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin));
                mGoogleMap.addMarker(markerOptions);
            }
        }
        latLng = new LatLng(Float.valueOf(getString(R.string.centre_of_seattle_latitude)), Float.valueOf(getString(R.string.centre_of_seattle_longitude)));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)
                .zoom(MAP_ZOOM_LEVEL).bearing(0).tilt(0).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        mGoogleMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        CameraPosition cameraPosition = new CameraPosition.Builder().target(marker.getPosition())
                .zoom(MAP_ZOOM_LEVEL).bearing(0).tilt(0).build();
        if (mGoogleMap != null) {
            mGoogleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (mVenueList == null) {
            return;
        }

        //Find Venue from list using marker position.
        for (Venue venue : mVenueList) {
            if (venue.getLocation().getLat() == marker.getPosition().latitude &&
                    venue.getLocation().getLng() == marker.getPosition().longitude) {
                mListener.onMarkerInfoWindowClicked(venue);
                break;
            }
        }
    }

    public interface OnFragmentInteractionListener {
        void onMarkerInfoWindowClicked(Venue venue);
    }
}
