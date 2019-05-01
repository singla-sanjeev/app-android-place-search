package com.homeaway.placesearch.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import com.homeaway.placesearch.PlaceSearchViewModel;
import com.homeaway.placesearch.R;
import com.homeaway.placesearch.model.Venue;
import com.homeaway.placesearch.utils.LogUtils;

public class MainActivity extends AppCompatActivity implements
        VenueListFragment.OnFragmentInteractionListener,
        VenueMapFragment.OnFragmentInteractionListener,
        VenueDetailFragment.OnFragmentInteractionListener {
    private static final String TAG = LogUtils.makeLogTag(MainActivity.class);
    private static final String TAG_LIST = "List_FRGMENT",
            TAG_MAP = "MAP_FRAGMENT", TAG_DETAIL = "DETAIL_FRAGMENT";
    private VenueListFragment mVenueListFragment;
    private PlaceSearchViewModel mPlaceSearchViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialise view model using View Model provider
        mPlaceSearchViewModel = ViewModelProviders.of(this)
                .get(PlaceSearchViewModel.class);
        //Initialise Venue list Mutable live data object without making http call.
        mPlaceSearchViewModel.venueSearch(null);
        loadVenueListFragment();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
            finish();
        }
    }

    //Load Venue search result list fragment
    private void loadVenueListFragment() {
        mVenueListFragment = VenueListFragment.newInstance();
        loadFragment(mVenueListFragment, TAG_LIST);
    }

    //Load Venue map fragment
    private void loadVenueMapFragment() {
        hideKeyboard();
        VenueMapFragment venueMapFragment = VenueMapFragment.newInstance();
        loadFragment(venueMapFragment, TAG_MAP);
    }

    //Load venue detail fragment
    private void loadVenueDetailFragment() {
        VenueDetailFragment venueDetailFragment = VenueDetailFragment.newInstance();
        loadFragment(venueDetailFragment, TAG_DETAIL);
    }

    //Load a fragment
    private void loadFragment(Fragment fragment, String tag) {
        // create a FragmentManager
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.constraintLytFragmentContainer, fragment);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit(); // save the changes
    }

    //Hide soft key pad
    private void hideKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a
        // window token from it
        if (view == null) {
            view = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onVenueSelected(Venue venue) {
        hideKeyboard();
        mPlaceSearchViewModel.setSelectedVenue(venue);
        loadVenueDetailFragment();
    }

    @Override
    public void onMapFloatingActionButtonClicked() {
        loadVenueMapFragment();
    }

    @Override
    public void onMarkerInfoWindowClicked(Venue venue) {
        mPlaceSearchViewModel.setSelectedVenue(venue);
        loadVenueDetailFragment();
    }

    @Override
    public void onFavoriteIconClicked(String id) {
        if (mVenueListFragment != null) {
            mVenueListFragment.updateFavorite(id);
        }
    }
}