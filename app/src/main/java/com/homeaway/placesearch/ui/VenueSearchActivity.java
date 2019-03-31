package com.homeaway.placesearch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.homeaway.placesearch.R;
import com.homeaway.placesearch.adapter.VenueAdapter;
import com.homeaway.placesearch.model.Venue;
import com.homeaway.placesearch.model.VenueSearchResponse;
import com.homeaway.placesearch.utils.LogUtils;
import com.homeaway.placesearch.utils.PreferenceUtils;
import com.homeaway.placesearch.utils.RetrofitUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VenueSearchActivity extends AppCompatActivity implements TextWatcher {
    private static final String TAG = LogUtils.makeLogTag(VenueSearchActivity.class);
    private ArrayList<Venue> mVenueList = new ArrayList<>();
    private VenueAdapter mAdapter;
    private FloatingActionButton mFloatingActionButton;
    private Map<String, Venue> mFavoriteMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        mFloatingActionButton = findViewById(R.id.fab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchVenueMapActivity();
            }
        });
        mFloatingActionButton.hide();

        EditText edtTxtVw = findViewById(R.id.edtVwSearchPlace);
        edtTxtVw.addTextChangedListener(this);

        retrieveFavoriteListFromSharedPreference();

        RecyclerView recyclerView = findViewById(R.id.venue_list);
        mAdapter = new VenueAdapter(this, mVenueList, mFavoriteMap);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(final Editable place) {
        if (mFloatingActionButton != null && mFloatingActionButton.isOrWillBeShown()) {
            mFloatingActionButton.hide();
        }
        if (mVenueList != null) {
            mVenueList.clear();
            mAdapter.notifyDataSetChanged();
        }

        if (!TextUtils.isEmpty(place) && place.length() >= 3) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fetchVenueList(place.toString());
                }
            }, 500);

        }
    }

    private void launchVenueMapActivity() {
        Intent intent = new Intent(this, VenueMapActivity.class);
        intent.putParcelableArrayListExtra(VenueMapActivity.VENUE_LIST_BUNDLE_ID, mVenueList);
        startActivity(intent);
    }

    private void fetchVenueList(String query) {
        Callback<VenueSearchResponse> responseCallback = new Callback<VenueSearchResponse>() {
            @Override
            public void onResponse(Call<VenueSearchResponse> call, Response<VenueSearchResponse> response) {
                if (response != null && response.isSuccessful()) {
                    VenueSearchResponse venueSearchResponse = response.body();
                    if (venueSearchResponse != null && venueSearchResponse.getMeta() != null
                            && venueSearchResponse.getMeta().getCode() == 200) {
                        if (venueSearchResponse.getResponse() != null) {
                            List<Venue> venues = venueSearchResponse.getResponse().getVenues();
                            if (null != venues && venues.size() > 0) {
                                mVenueList.clear();
                                mVenueList.addAll(venues);
                                mAdapter.notifyDataSetChanged();
                                if (mFloatingActionButton != null) {
                                    mFloatingActionButton.show();
                                }
                            }
                        }
                    }
                    LogUtils.checkIf(TAG, response.toString());
                }
            }

            @Override
            public void onFailure(Call<VenueSearchResponse> call, Throwable throwable) {
                LogUtils.checkIf(TAG, "Throwable: " + throwable.toString());
            }
        };
        RetrofitUtils.getInstance().getService(this).venueSearch(
                getString(R.string.client_id),
                getString(R.string.client_secret),
                getString(R.string.near),
                query,
                "20190330",
                20).enqueue(responseCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVenueList.clear();
        mVenueList = null;
        saveFavoriteListToSharedPreference();
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
