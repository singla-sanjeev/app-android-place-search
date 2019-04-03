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
import com.homeaway.placesearch.Injection;
import com.homeaway.placesearch.R;
import com.homeaway.placesearch.VenueListViewModel;
import com.homeaway.placesearch.ViewModelFactory;
import com.homeaway.placesearch.databinding.FragmentVenueDetailBinding;
import com.homeaway.placesearch.model.Venue;
import com.homeaway.placesearch.utils.AppUtils;
import com.homeaway.placesearch.utils.LogUtils;

import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link VenueDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VenueDetailFragment extends Fragment implements OnMapReadyCallback {
    public static final int MAP_ZOOM_LEVEL = 14; //8 Venues
    private static final String TAG = LogUtils.makeLogTag(VenueDetailFragment.class);
    private Activity mActivity;
    private View mView;
    private GoogleMap mMap;
    private Venue mVenue;
    private VenueListViewModel mVenueListViewModel;
    private FragmentVenueDetailBinding mFragmentVenueDetailBinding;
    private Toolbar mToolbar;
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
        VenueDetailFragment fragment = new VenueDetailFragment();
        return fragment;
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

        mToolbar = mView.findViewById(R.id.detail_toolbar);
        ((AppCompatActivity) mActivity).setSupportActionBar(mToolbar);

        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(mActivity);
        mVenueListViewModel = ViewModelProviders.of((MainActivity) mActivity, mViewModelFactory).get(VenueListViewModel.class);
        mVenueListViewModel.getSelectedVenue().observe(this, new Observer<Venue>() {
            @Override
            public void onChanged(Venue venue) {
                mVenue = venue;
                initViews();
            }
        });


        //capture the size of the devices screen
        if (mActivity != null) {
            Display display = mActivity.getWindowManager().getDefaultDisplay();
            Point outSize = new Point();
            display.getSize(outSize);

            AppBarLayout appBarLayout = mView.findViewById(R.id.app_bar);
            CoordinatorLayout.LayoutParams layoutParams =
                    new CoordinatorLayout.LayoutParams(outSize.x, outSize.y / 2);

            appBarLayout.setLayoutParams(layoutParams);
        }

        // Show the Up button in the action bar.
        ActionBar actionBar = ((AppCompatActivity) mActivity).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        FragmentManager fragmentManager = getChildFragmentManager();
        SupportMapFragment supportMapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.googleMap);
        if (supportMapFragment != null) {
            supportMapFragment.getMapAsync(this);
        }

        return mView;
    }

    private void initViews() {
        if (mVenue != null) {
            FloatingActionButton fab = mView.findViewById(R.id.favoriteFab);
            fab.setOnClickListener(view -> {
                favoriteClicked(view);
            });
            if (mVenue.isFavorite()) {
                fab.setImageResource(R.drawable.ic_favorite);
            } else {
                fab.setImageResource(R.drawable.ic_favorite_border);
            }
            if (!TextUtils.isEmpty(mVenue.getName())) {
                mFragmentVenueDetailBinding.setName(mVenue.getName());
            }
            if (!TextUtils.isEmpty(mVenue.getUrl())) {
                mFragmentVenueDetailBinding.setUrl(mVenue.getUrl());
            }
            if (mVenue.getCategories() != null && mVenue.getCategories().size() > 0) {
                if (!TextUtils.isEmpty(mVenue.getCategories().get(0).getName())) {
                    mFragmentVenueDetailBinding.setCategory(mVenue.getCategories().get(0));
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
        mMap = googleMap;
        markVenueLocationsOnMap();
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

    private void favoriteClicked(View view) {
        if (mVenue != null) {
            if (mVenue.isFavorite()) {
                ((FloatingActionButton) view).setImageResource(R.drawable.ic_favorite_border);
                mVenue.setFavorite(false);
            } else {
                ((FloatingActionButton) view).setImageResource(R.drawable.ic_favorite);
                mVenue.setFavorite(true);
            }
            mListener.onFavoriteIconClicked(mVenue.getId());
        }
    }

    public interface OnFragmentInteractionListener {
        void onFavoriteIconClicked(String id);
    }
}
