package com.homeaway.placesearch.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

/**
 * {@link VenueListFragment} class provides list of venue list item based on searching criteria.
 * This is the main screen of the app which display a search input, and using typeahead search
 * against the Foursquare API. Search results are displayed list format. Each list item is providing
 * the name of the place (e.g., Flitch Coffee), the category of the place (e.g., Coffee Shop),
 * the icon from the response, the distance from the center of Seattle (47.6062° N, 122.3321° W)
 * to the place, and whether the place has been favorited by the user.
 * Clicking a list item is launching the details screen for that place.
 * When a search results are available, the main screen shows a Floating Action Button.
 * Clicking the Floating Action Button is launching a full-screen map {@VenueMapFragment}
 * with a pin for every search result.
 * Activities that contain this fragment must implement the
 * {@link VenueMapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VenueMapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VenueListFragment extends Fragment implements TextWatcher {
    private static final String TAG = LogUtils.makeLogTag(VenueListFragment.class);

    private ArrayList<Venue> mVenueList = new ArrayList<>();
    private VenueAdapter mAdapter;
    private Map<String, Venue> mFavoriteMap;
    private Activity mActivity;
    private OnFragmentInteractionListener mListener;
    private FloatingActionButton mFloatingActionButton;

    public static VenueListFragment newInstance() {
        return new VenueListFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        if (context instanceof VenueMapFragment.OnFragmentInteractionListener) {
            mListener = (VenueListFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        retrieveFavoriteListFromSharedPreference();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_venue_list, container, false);

        mFloatingActionButton = rootView.findViewById(R.id.mapFab);
        mFloatingActionButton.setOnClickListener(view ->
                mListener.onMapFloatingActionButtonClicked());
        mFloatingActionButton.hide();

        EditText edtTxtVw = rootView.findViewById(R.id.searchPlaceEdtVw);
        edtTxtVw.addTextChangedListener(this);
        ViewModelFactory mViewModelFactory = Injection.provideViewModelFactory();
        VenueListViewModel venueListViewModel = ViewModelProviders.of((MainActivity) mActivity,
                mViewModelFactory).get(VenueListViewModel.class);
        venueListViewModel.getVenueList().observe(this, venues -> {
            if (null != venues && venues.size() > 0) {
                mVenueList.clear();
                mVenueList.addAll(venues);
                mAdapter.notifyDataSetChanged();
                if (mFloatingActionButton != null) {
                    mFloatingActionButton.show();
                }
            }
        });

        RecyclerView recyclerView = rootView.findViewById(R.id.venueListRecyclerVw);
        mAdapter = new VenueAdapter(mActivity, mVenueList, mFavoriteMap, mListener);
        recyclerView.setAdapter(mAdapter);
        return rootView;
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mVenueList.clear();
        mVenueList = null;
        saveFavoriteListToSharedPreference();
        super.onDestroy();
    }

    /**
     * Update favorite map from VenueDetailFragment using this method and notify adapter to update
     * the UI after updating favorite.
     *
     * @param id : Venue id
     */
    public void updateFavorite(String id) {
        if (mFavoriteMap != null) {
            if (mFavoriteMap.containsKey(id)) {
                mFavoriteMap.remove(id);
            } else {
                mFavoriteMap.put(id, null);
            }
        }
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Fetch/Retrieve saved favorite items from preference and load it in favorite map
     */
    private void retrieveFavoriteListFromSharedPreference() {
        mFavoriteMap = new HashMap<>();
        Set<String> favoriteSet = null;
        try {
            favoriteSet = PreferenceUtils.getInstance(mActivity).
                    getStringSet(PreferenceUtils.FAVORITE_LIST);
        } catch (Exception e) {
            LogUtils.error(TAG, e.toString());
        }
        if (favoriteSet != null && favoriteSet.size() > 0) {
            for (String id : favoriteSet) {
                mFavoriteMap.put(id, null);
            }
        }
    }

    /**
     * Save selected favorite list item to preference
     */
    private void saveFavoriteListToSharedPreference() {
        assert mFavoriteMap != null;
        if (mFavoriteMap.size() <= 0) {
            return;
        }
        PreferenceUtils.getInstance(mActivity).
                putStringSet(PreferenceUtils.FAVORITE_LIST, mFavoriteMap.keySet());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mListener.onSearchTextChanged();
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (mFloatingActionButton != null && mFloatingActionButton.isOrWillBeShown()) {
            mFloatingActionButton.hide();
        }
        if (mVenueList != null && mVenueList.size() > 0 && editable.length() <= 1) {
            mVenueList.clear();
            mAdapter.notifyDataSetChanged();
        }
        mListener.afterSearchTextChanged(editable.toString());
    }

    // This interface can be implemented by the Activity, parent Fragment
    public interface OnFragmentInteractionListener {
        //Venue Item Selected from the list of venue items
        void onVenueSelected(Venue venue);

        //Search text change listener
        void onSearchTextChanged();

        //After search text changed listener
        void afterSearchTextChanged(String query);

        //Listener for floating action button click on Venue list screen
        void onMapFloatingActionButtonClicked();
    }
}
