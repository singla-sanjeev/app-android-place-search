package com.homeaway.placesearch.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

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
import com.homeaway.placesearch.PlaceSearchViewModel;
import com.homeaway.placesearch.R;
import com.homeaway.placesearch.databinding.FragmentVenueDetailBinding;
import com.homeaway.placesearch.model.Venue;
import com.homeaway.placesearch.utils.AppUtils;

import java.util.ArrayList;
import java.util.Locale;

/**
 * {@link VenueDetailFragment} class display venue details for a place using a collapsible
 * toolbar layout to show a map in the upper half of the screen, with two pins -- one,
 * the location of search result, and the other, the center of Seattle.
 * The bottom half of the details screen is providing details about the place,
 * including whether or not the place is favorited, and include a link to
 * the place&rsquo;s website (if it exists). Clicking this link open an external browser installed
 * on the device..
 * Main Activity is containing this fragment and implementing
 * {@link VenueDetailFragment.OnFragmentInteractionListener} to handle interaction events.
 * {@link VenueDetailFragment#newInstance} factory method can be used to create an
 * instance of this fragment.
 */
public class VenueDetailFragment extends Fragment implements OnMapReadyCallback {
    private static final int MAP_ZOOM_LEVEL = 14; //8 Venues
    private Activity mActivity;
    private View mView;
    private GoogleMap mGoogleMap;
    private Venue mVenue;
    private PlaceSearchViewModel mPlaceSearchViewModel;
    private FragmentVenueDetailBinding mFragmentVenueDetailBinding;
    private FloatingActionButton mFavoriteFloatingActionButton;
    private OnFragmentInteractionListener mListener;

    public VenueDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment VenueDetailFragment.
     */
    public static VenueDetailFragment newInstance() {
        return new VenueDetailFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mFragmentVenueDetailBinding = DataBindingUtil.inflate(inflater,
                R.layout.fragment_venue_detail, container, false);
        mView = mFragmentVenueDetailBinding.getRoot();

        Toolbar mToolbar = mView.findViewById(R.id.detailToolbar);
        ((AppCompatActivity) mActivity).setSupportActionBar(mToolbar);
        // Show the Up button in the action bar.
        ActionBar actionBar = ((AppCompatActivity) mActivity).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setNavigationOnClickListener(view -> {
            mActivity.onBackPressed();
        });

        //capture the size of the devices screen
        if (mActivity != null) {
            Display display = mActivity.getWindowManager().getDefaultDisplay();
            Point outSize = new Point();
            display.getSize(outSize);

            AppBarLayout appBarLayout = mView.findViewById(R.id.venueDetailAppBar);
            CoordinatorLayout.LayoutParams layoutParams =
                    new CoordinatorLayout.LayoutParams(outSize.x, outSize.y / 2);

            appBarLayout.setLayoutParams(layoutParams);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        FragmentManager fragmentManager = getChildFragmentManager();
        SupportMapFragment supportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.googleMap);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }

        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mPlaceSearchViewModel = ViewModelProviders.of((MainActivity) mActivity).get(PlaceSearchViewModel.class);
        mPlaceSearchViewModel.getSelectedVenue().observe(getViewLifecycleOwner(), venue -> {
            if (venue != null) {
                mVenue = venue;
            }
            initViews();
        });
    }

    private void initViews() {
        if (mVenue != null) {
            mFavoriteFloatingActionButton = mView.findViewById(R.id.favoriteFab);
            mFavoriteFloatingActionButton.setOnClickListener(view -> {
                favoriteClicked();
            });
            if (mVenue.isFavorite()) {
                mFavoriteFloatingActionButton.setImageResource(R.drawable.ic_favorite);
            } else {
                mFavoriteFloatingActionButton.setImageResource(R.drawable.ic_favorite_border);
            }
            if (!TextUtils.isEmpty(mVenue.getName())) {
                mFragmentVenueDetailBinding.setVenueName(mVenue.getName());
            }

            if (!TextUtils.isEmpty(mVenue.getUrl())) {
                mFragmentVenueDetailBinding.setUrl(mVenue.getUrl());
            }

            if (mVenue.getCategories() != null && mVenue.getCategories().size() > 0) {
                if (!TextUtils.isEmpty(mVenue.getCategories().get(0).getName())) {
                    mFragmentVenueDetailBinding.setCategoryName(mVenue.getCategories().get(0).getName());
                }
                String iconUrl = mVenue.getCategories().get(0).getIcon().getPrefix().replace("\\", "") + "bg_64" +
                        mVenue.getCategories().get(0).getIcon().getSuffix();
                ImageView categoryIcon = mView.findViewById(R.id.imgViewCategoryIcon);
                AppUtils.getInstance().loadCategoryImage(mActivity, iconUrl, categoryIcon);
            }
            if (mVenue.getLocation() != null && mVenue.getLocation().getFormattedAddress().size() > 0) {
                mFragmentVenueDetailBinding.setFormattedAddress((ArrayList<String>) mVenue.getLocation().getFormattedAddress());
            }
            if (mVenue.getLocation() != null) {
                double centerOfSeattleLatitude = Double.parseDouble(mActivity.getResources().getString(R.string.centre_of_seattle_latitude));
                double centerOfSeattleLongitude = Double.parseDouble(mActivity.getResources().getString(R.string.centre_of_seattle_longitude));
                mFragmentVenueDetailBinding.setDistance(String.format(Locale.getDefault(), mActivity.getString(R.string.distance),
                        AppUtils.getInstance().getDistance(centerOfSeattleLatitude, centerOfSeattleLongitude,
                                mVenue.getLocation().getLat(), mVenue.getLocation().getLng())));
            }
        }
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
        MarkerOptions markerOptions = null;
        LatLng latLng = null;
        if (mVenue != null && mVenue.getLocation() != null) {
            latLng = new LatLng(mVenue.getLocation().getLat(), mVenue.getLocation().getLng());
        }
        if (latLng != null) {
            markerOptions = new MarkerOptions().position(latLng);
        }
        if (markerOptions != null) {
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin));
            mGoogleMap.addMarker(markerOptions);
        }

        latLng = new LatLng(Float.valueOf(getString(R.string.centre_of_seattle_latitude)),
                Float.valueOf(getString(R.string.centre_of_seattle_longitude)));
        markerOptions = new MarkerOptions().position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_pin));
        mGoogleMap.addMarker(markerOptions);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng)
                .zoom(MAP_ZOOM_LEVEL).bearing(0).tilt(0).build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void favoriteClicked() {
        if (mVenue != null) {
            mFavoriteFloatingActionButton.hide();
            if (mVenue.isFavorite()) {
                mVenue.setFavorite(false);
                mFavoriteFloatingActionButton.setImageResource(R.drawable.ic_favorite_border);
            } else {
                mVenue.setFavorite(true);
                mFavoriteFloatingActionButton.setImageResource(R.drawable.ic_favorite);
            }
            mFavoriteFloatingActionButton.show();
            mListener.onFavoriteIconClicked(mVenue);
        }
    }

    public interface OnFragmentInteractionListener {
        void onFavoriteIconClicked(Venue venue);
    }
}
