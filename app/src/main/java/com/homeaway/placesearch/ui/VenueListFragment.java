package com.homeaway.placesearch.ui;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.List;
import java.util.Map;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

public class VenueListFragment extends Fragment {
    private static final String TAG = LogUtils.makeLogTag(VenueListFragment.class);

    private static final long sDelayInMillSeconds = 200;
    public static Handler sHandler;
    public static Runnable sRunnable;
    public ArrayList<Venue> mVenueList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private VenueAdapter mAdapter;
    private ViewModelFactory mViewModelFactory;
    private Map<String, Venue> mFavoriteMap;
    private VenueListViewModel mVenueListViewModel;
    private Activity mActivity;
    private OnVenueListItemListener mCallback;

    public static VenueListFragment newInstance() {
        return new VenueListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrieveFavoriteListFromSharedPreference();
        mAdapter = new VenueAdapter(mActivity, mVenueList, mFavoriteMap);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_venue_list, container, false);

        mRecyclerView = view.findViewById(R.id.venueListRecyclerVw);
        mRecyclerView.setAdapter(mAdapter);

        mViewModelFactory = Injection.provideViewModelFactory(mActivity);
        mVenueListViewModel = ViewModelProviders.of(this, mViewModelFactory).get(VenueListViewModel.class);
        mVenueListViewModel.init(null);
        mVenueListViewModel.getVenueList().observe((MainActivity) mActivity, new Observer<List<Venue>>() {
            @Override
            public void onChanged(List<Venue> venues) {
                if (null != venues && venues.size() > 0) {
                    mVenueList.clear();
                    mVenueList.addAll(venues);
                    mAdapter.notifyDataSetChanged();
                    mCallback.onVenueListChanged(mVenueList);
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (sHandler != null && sRunnable != null) {
            sHandler.removeCallbacks(sRunnable);
        }
    }

    @Override
    public void onDestroyView() {
        mVenueList.clear();
        mVenueList = null;
        if (sHandler != null && sRunnable != null) {
            sHandler.removeCallbacks(sRunnable);
            sHandler = null;
            sRunnable = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        saveFavoriteListToSharedPreference();
        super.onDestroy();
    }

    public void onQueryTextChanged() {
        if (sHandler != null && sRunnable != null) {
            sHandler.removeCallbacks(sRunnable);
        }
    }

    public void afterQueryTextChange(String query) {
        if (mVenueList != null && mVenueList.size() > 0 && query.length() <= 1) {
            mVenueList.clear();
            mAdapter.notifyDataSetChanged();
        }

        if (!TextUtils.isEmpty(query) && query.length() >= 2) {
            sHandler = new Handler();
            sRunnable = new Runnable() {
                @Override
                public void run() {
                    if (isInternetAvailable(mActivity)) {
                        mVenueListViewModel.init(query.toString());
                    } else {
                        LogUtils.info(TAG, "Looks like your internet connection is taking a nap!");
                    }
                }
            };
            sHandler.postDelayed(sRunnable, sDelayInMillSeconds);
        }

    }

    private boolean isInternetAvailable(Context context) {
        boolean isConnected = false;

        if (context == null) {
            return isConnected;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return isConnected;
        }

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        isConnected = activeNetwork != null && activeNetwork.isConnected();

        return isConnected;
    }

    private void retrieveFavoriteListFromSharedPreference() {
        mFavoriteMap = new HashMap<>();
        Set<String> favoriteSet = null;
        try {
            favoriteSet = PreferenceUtils.getInstance(mActivity).getStringSet(PreferenceUtils.FAVORITE_LIST);
        } catch (Exception e) {
            LogUtils.error(TAG, e.toString());
        }
        if (favoriteSet != null && favoriteSet.size() > 0) {
            for (String id : favoriteSet) {
                mFavoriteMap.put(id, null);
            }
        }
    }

    private void saveFavoriteListToSharedPreference() {
        assert mFavoriteMap != null;
        if (mFavoriteMap.size() <= 0) {
            return;
        }
        PreferenceUtils.getInstance(mActivity).
                putStringSet(PreferenceUtils.FAVORITE_LIST, mFavoriteMap.keySet());
    }

    public void setOnVenueListItemListener(OnVenueListItemListener callback) {
        this.mCallback = callback;
    }

    // This interface can be implemented by the Activity, parent Fragment
    public interface OnVenueListItemListener {
        public void onVenueSelected(Venue venue);

        public void onVenueListChanged(List<Venue> venueList);
    }
}
