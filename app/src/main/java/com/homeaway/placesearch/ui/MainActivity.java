package com.homeaway.placesearch.ui;


import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.homeaway.placesearch.Injection;
import com.homeaway.placesearch.R;
import com.homeaway.placesearch.VenueListViewModel;
import com.homeaway.placesearch.ViewModelFactory;
import com.homeaway.placesearch.model.Venue;
import com.homeaway.placesearch.utils.LogUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

public class MainActivity extends AppCompatActivity implements TextWatcher,
        VenueListFragment.OnFragmentInteractionListener,
        VenueMapFragment.OnFragmentInteractionListener,
        VenueDetailFragment.OnFragmentInteractionListener {
    private static final String TAG = LogUtils.makeLogTag(MainActivity.class);
    private static final String TAG_LIST = "List_FRGMENT",
            TAG_MAP = "MAP_FRAGMENT", TAG_DETAIL = "DETAIL_FRAGMENT";
    private static final long sDelayInMillSeconds = 200;
    private static Handler sHandler;
    private static Runnable sRunnable;
    private VenueListFragment mVenueListFragment;
    private FloatingActionButton mFloatingActionButton;
    private VenueListViewModel mVenueListViewModel;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        mToolbar.setTitle(getTitle());

        mFloatingActionButton = findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(view -> {
            hideKeyboard();
            loadVenueMapFragment();
        });
        mFloatingActionButton.hide();

        EditText edtTxtVw = findViewById(R.id.edtVwSearchPlace);
        edtTxtVw.addTextChangedListener(this);

        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(this);
        mVenueListViewModel = ViewModelProviders.of(this, mViewModelFactory).get(VenueListViewModel.class);
        mVenueListViewModel.init(null);
        mVenueListViewModel.getVenueList().observe(this, venueList -> {
            if (null != venueList && venueList.size() > 0) {
                if (mFloatingActionButton != null) {
                    mFloatingActionButton.show();
                }
            }
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
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (sHandler != null && sRunnable != null) {
            sHandler.removeCallbacks(sRunnable);
        }
    }

    @Override
    public void afterTextChanged(final Editable place) {
        if (mFloatingActionButton != null && mFloatingActionButton.isOrWillBeShown()) {
            mFloatingActionButton.hide();
        }
        if (mVenueListFragment != null) {
            mVenueListFragment.afterQueryTextChange(place.toString());
        }

        if (!TextUtils.isEmpty(place) && place.length() >= 2) {
            sHandler = new Handler();
            sRunnable = () -> {
                if (isInternetAvailable(MainActivity.this)) {
                    mVenueListViewModel.init(place.toString());
                } else {
                    LogUtils.info(TAG, "Looks like your internet connection is taking a nap!");
                }
            };
            sHandler.postDelayed(sRunnable, sDelayInMillSeconds);
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            if (getSupportFragmentManager().getBackStackEntryCount() == 2) {
                if (mToolbar != null) {
                    mToolbar.setVisibility(View.VISIBLE);
                }
                if (mFloatingActionButton != null) {
                    mFloatingActionButton.show();
                }
            }
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
            finish();
        }

    }

    private void loadVenueMapFragment() {
        VenueMapFragment venueMapFragment = VenueMapFragment.newInstance();
        loadFragment(venueMapFragment, TAG_MAP);
        if (mToolbar != null) {
            mToolbar.setVisibility(View.GONE);
        }
        if (mFloatingActionButton != null && mFloatingActionButton.isOrWillBeShown()) {
            mFloatingActionButton.hide();
        }
    }

    private void loadVenueListFragment() {
        mVenueListFragment = VenueListFragment.newInstance();
        loadFragment(mVenueListFragment, TAG_LIST);
    }

    private void loadVenueDetailFragment() {
        VenueDetailFragment venueDetailFragment = VenueDetailFragment.newInstance();
        loadFragment(venueDetailFragment, TAG_DETAIL);
        if (mFloatingActionButton != null && mFloatingActionButton.isOrWillBeShown()) {
            mFloatingActionButton.hide();
        }
        if (mToolbar != null) {
            mToolbar.setVisibility(View.GONE);
        }
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