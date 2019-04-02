package com.homeaway.placesearch.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.homeaway.placesearch.Injection;
import com.homeaway.placesearch.R;
import com.homeaway.placesearch.VenueListViewModel;
import com.homeaway.placesearch.ViewModelFactory;
import com.homeaway.placesearch.adapter.VenueAdapter;
import com.homeaway.placesearch.model.Venue;
import com.homeaway.placesearch.utils.LogUtils;
import com.homeaway.placesearch.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

public class VenueSearchActivity extends AppCompatActivity implements TextWatcher {
    private static final String TAG = LogUtils.makeLogTag(VenueSearchActivity.class);
    private static final long sDelayInMillSeconds = 200;
    public static Handler sHandler;
    public static Runnable sRunnable;
    private ArrayList<Venue> mVenueList = new ArrayList<>();
    private VenueAdapter mAdapter;
    private FloatingActionButton mFloatingActionButton;
    private Map<String, Venue> mFavoriteMap;
    private VenueListViewModel mVenueListViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mFloatingActionButton = findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(view -> launchVenueMapActivity());
        mFloatingActionButton.hide();

        EditText edtTxtVw = findViewById(R.id.edtVwSearchPlace);
        edtTxtVw.addTextChangedListener(this);

        retrieveFavoriteListFromSharedPreference();

        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory(this);
        mVenueListViewModel = ViewModelProviders.of(this, mViewModelFactory).get(VenueListViewModel.class);
        mVenueListViewModel.init(null);
        mVenueListViewModel.getVenueList().observe(VenueSearchActivity.this, venues -> {
            if (null != venues && venues.size() > 0) {
                mVenueList.clear();
                mVenueList.addAll(venues);
                mAdapter.notifyDataSetChanged();
                if (mFloatingActionButton != null) {
                    mFloatingActionButton.show();
                }
            }
        });

        RecyclerView mRecyclerView = findViewById(R.id.venue_list);
        mAdapter = new VenueAdapter(this, mVenueList, mFavoriteMap);
        mRecyclerView.setAdapter(mAdapter);
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
        mVenueList.clear();
        mVenueList = null;
        if (sHandler != null && sRunnable != null) {
            sHandler.removeCallbacks(sRunnable);
            sHandler = null;
            sRunnable = null;
        }
        saveFavoriteListToSharedPreference();
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
        if (mVenueList != null && mVenueList.size() > 0 && place.length() <= 1) {
            mVenueList.clear();
            mAdapter.notifyDataSetChanged();
        }

        if (!TextUtils.isEmpty(place) && place.length() >= 2) {
            sHandler = new Handler();
            sRunnable = () -> {
                if (isInternetAvailable(VenueSearchActivity.this)) {
                    mVenueListViewModel.init(place.toString());
                } else {
                    LogUtils.info(TAG, "Looks like your internet connection is taking a nap!");
                }
            };
            sHandler.postDelayed(sRunnable, sDelayInMillSeconds);
        }
    }

    private void launchVenueMapActivity() {
        if (sHandler != null && sRunnable != null) {
            sHandler.removeCallbacks(sRunnable);
            sHandler = null;
            sRunnable = null;
        }
        Intent intent = new Intent(this, VenueMapActivity.class);
        intent.putParcelableArrayListExtra(VenueMapActivity.VENUE_LIST_BUNDLE_ID, mVenueList);
        startActivity(intent);
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

    private void saveFavoriteListToSharedPreference() {
        assert mFavoriteMap != null;
        if (mFavoriteMap.size() <= 0) {
            return;
        }
        PreferenceUtils.getInstance(this).putStringSet(PreferenceUtils.FAVORITE_LIST, mFavoriteMap.keySet());
    }

    private void retrieveFavoriteListFromSharedPreference() {
        mFavoriteMap = new HashMap<>();
        Set<String> favoriteSet = null;
        try {
            favoriteSet = PreferenceUtils.getInstance(this).getStringSet(PreferenceUtils.FAVORITE_LIST);
        } catch (Exception e) {
            LogUtils.error(TAG, e.toString());
        }
        if (favoriteSet != null && favoriteSet.size() > 0) {
            for (String id : favoriteSet) {
                mFavoriteMap.put(id, null);
            }
        }
    }
}
