package com.homeaway.placesearch.ui;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.homeaway.placesearch.Injection;
import com.homeaway.placesearch.R;
import com.homeaway.placesearch.VenueListViewModel;
import com.homeaway.placesearch.ViewModelFactory;
import com.homeaway.placesearch.model.Venue;
import com.homeaway.placesearch.utils.LogUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity implements VenueListFragment.OnFragmentInteractionListener,
        VenueMapFragment.OnFragmentInteractionListener,
        VenueDetailFragment.OnFragmentInteractionListener {
    private static final String TAG = LogUtils.makeLogTag(MainActivity.class);
    private static final String TAG_LIST = "List_FRGMENT",
            TAG_MAP = "MAP_FRAGMENT", TAG_DETAIL = "DETAIL_FRAGMENT";
    private static final long sDelayInMillSeconds = 200;
    private static Handler sHandler;
    private static Runnable sRunnable;
    private VenueListFragment mVenueListFragment;
    private VenueListViewModel mVenueListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(this);
        mVenueListViewModel = ViewModelProviders.of(this, mViewModelFactory).get(VenueListViewModel.class);
        mVenueListViewModel.init(null);
        mVenueListViewModel.getVenueList().observe(this, venueList -> {
            LogUtils.info(TAG, "Venue List Observer");
        });
        loadVenueListFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sHandler != null && sRunnable != null) {
            sHandler.removeCallbacks(sRunnable);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sHandler != null && sRunnable != null) {
            sHandler.removeCallbacks(sRunnable);
            sHandler = null;
            sRunnable = null;
        }
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

    private void loadVenueMapFragment() {
        hideKeyboard();
        VenueMapFragment venueMapFragment = VenueMapFragment.newInstance();
        loadFragment(venueMapFragment, TAG_MAP);
    }

    private void loadVenueListFragment() {
        mVenueListFragment = VenueListFragment.newInstance();
        loadFragment(mVenueListFragment, TAG_LIST);
    }

    private void loadVenueDetailFragment() {
        VenueDetailFragment venueDetailFragment = VenueDetailFragment.newInstance();
        loadFragment(venueDetailFragment, TAG_DETAIL);
    }

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

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onVenueSelected(Venue venue) {
        hideKeyboard();
        mVenueListViewModel.setSelectedVenue(venue);
        loadVenueDetailFragment();
    }

    @Override
    public void onSearchTextChanged() {
        if (sHandler != null && sRunnable != null) {
            sHandler.removeCallbacks(sRunnable);
        }
    }

    @Override
    public void afterSearchTextChanged(String query) {
        if (!TextUtils.isEmpty(query) && query.length() >= 2) {
            sHandler = new Handler();
            sRunnable = () -> {
                if (isInternetAvailable(MainActivity.this)) {
                    mVenueListViewModel.init(query.toString());
                } else {
                    LogUtils.info(TAG, "Looks like your internet connection is taking a nap!");
                }
            };
            sHandler.postDelayed(sRunnable, sDelayInMillSeconds);
        }
    }

    @Override
    public void onMapFloatingActionButtonClicked() {
        loadVenueMapFragment();
    }

    @Override
    public void onMarkerInfoWindowClicked(Venue venue) {
        mVenueListViewModel.setSelectedVenue(venue);
        loadVenueDetailFragment();
    }

    @Override
    public void onFavoriteIconClicked(String id) {
        if (mVenueListFragment != null) {
            mVenueListFragment.updateFavorite(id);
        }
    }

    private boolean isInternetAvailable(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnected();
    }
}