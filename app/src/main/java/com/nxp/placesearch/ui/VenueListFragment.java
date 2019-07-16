package com.nxp.placesearch.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.IdlingResource;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nxp.placesearch.FavoriteVenueViewModel;
import com.nxp.placesearch.IdlingResource.VenueSearchIdlingResource;
import com.nxp.placesearch.PlaceSearchViewModel;
import com.nxp.placesearch.R;
import com.nxp.placesearch.adapter.VenueAdapter;
import com.nxp.placesearch.database.FavoriteVenue;
import com.nxp.placesearch.model.Venue;
import com.nxp.placesearch.utils.AppUtils;
import com.nxp.placesearch.utils.LogUtils;
import com.jakewharton.rxbinding3.widget.RxTextView;
import com.jakewharton.rxbinding3.widget.TextViewTextChangeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

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
public class VenueListFragment extends Fragment {
    private static final String TAG = LogUtils.makeLogTag(VenueListFragment.class);

    private ArrayList<Venue> mVenueList = new ArrayList<>();
    private Map<String, Boolean> mFavoriteMap = new HashMap<>();
    private VenueAdapter mAdapter;
    private Activity mActivity;
    private OnFragmentInteractionListener mListener;
    private FloatingActionButton mFloatingActionButton;
    private PlaceSearchViewModel mPlaceSearchViewModel;
    private FavoriteVenueViewModel mFavoriteVenueViewModel;
    private EditText mSearchPlaceEdtTxt;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    // The Idling Resource which will be null in production.
    @Nullable
    private VenueSearchIdlingResource mVenueSearchIdlingResource;

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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_venue_list, container, false);

        mFloatingActionButton = rootView.findViewById(R.id.mapFab);
        mFloatingActionButton.setOnClickListener(view ->
                mListener.onMapFloatingActionButtonClicked());
        mFloatingActionButton.hide();

        mSearchPlaceEdtTxt = rootView.findViewById(R.id.searchPlaceEdtVw);

        RecyclerView recyclerView = rootView.findViewById(R.id.venueListRecyclerVw);
        mAdapter = new VenueAdapter(mActivity, mVenueList, mFavoriteMap, mListener);
        recyclerView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPlaceSearchViewModel = ViewModelProviders.of((MainActivity) mActivity).get(PlaceSearchViewModel.class);
        mPlaceSearchViewModel.getVenueList().observe(getViewLifecycleOwner(), venues -> {
            if (null != venues && venues.size() > 0) {
                mVenueList.clear();
                mVenueList.addAll(venues);
                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }
                if (mFloatingActionButton != null) {
                    mFloatingActionButton.show();
                }
                if (mVenueSearchIdlingResource != null) {
                    mVenueSearchIdlingResource.endSearch();
                }
            }
        });

        Disposable disposable = RxTextView.textChangeEvents(mSearchPlaceEdtTxt)
                .skipInitialValue()
                .debounce(200, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(searchPlace());
        mCompositeDisposable.add(disposable);

        mFavoriteVenueViewModel = ViewModelProviders.of((MainActivity) mActivity).get(FavoriteVenueViewModel.class);
        mFavoriteVenueViewModel.getAllFavoriteVenues();
        mFavoriteVenueViewModel.getFavoriteVenuesLiveData().observe(getViewLifecycleOwner(), favoriteVenues -> {
            mFavoriteMap.clear();
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                mFavoriteMap.putAll(favoriteVenues.stream().collect(
                        Collectors.toMap(FavoriteVenue::getId, FavoriteVenue::isFavorite)));
            } else {
                for (FavoriteVenue favoriteVenue : favoriteVenues) {
                    mFavoriteMap.put(favoriteVenue.getId(), favoriteVenue.isFavorite());
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mSearchPlaceEdtTxt != null) {
            mSearchPlaceEdtTxt.clearFocus();
        }
    }

    @Override
    public void onDestroy() {
        mVenueList.clear();
        mVenueList = null;
        mCompositeDisposable.clear();
        super.onDestroy();
    }

    /**
     * Update favorite map from VenueDetailFragment using this method and notify adapter to update
     * the UI after updating favorite.
     *
     * @param id : Updated Favorite Venue id
     */
    public void updateFavorite(String id) {
        FavoriteVenue favoriteVenue = new FavoriteVenue();
        favoriteVenue.setId(id);
        favoriteVenue.setFavorite(true);
        if (mFavoriteMap != null) {
            if (mFavoriteMap.containsKey(id)) {
                mFavoriteMap.remove(id);
                if (mFavoriteVenueViewModel != null) {
                    mFavoriteVenueViewModel.removeFromFavorite(favoriteVenue);
                }
            } else {
                mFavoriteMap.put(id, true);
                if (mFavoriteVenueViewModel != null) {
                    mFavoriteVenueViewModel.addToFavorite(favoriteVenue);
                }
            }
        }

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private DisposableObserver<TextViewTextChangeEvent> searchPlace() {
        return new DisposableObserver<TextViewTextChangeEvent>() {
            @Override
            public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                if (mVenueSearchIdlingResource != null) {
                    mVenueSearchIdlingResource.beginSearch();
                }
                String query = textViewTextChangeEvent.getText().toString();
                LogUtils.checkIf(TAG, "Search query: " + query);
                if (mFloatingActionButton != null && mFloatingActionButton.isOrWillBeShown()) {
                    mFloatingActionButton.hide();
                }
                if (mVenueList != null && mVenueList.size() > 0 && query.length() <= 1) {
                    mVenueList.clear();
                    mAdapter.notifyDataSetChanged();
                }
                if (AppUtils.getInstance().isInternetAvailable(mActivity)) {
                    mPlaceSearchViewModel.venueSearch(query);
                } else {
                    //Todo: network error dialog.
                    LogUtils.info(TAG, "Looks like your internet connection is taking a nap!");
                }
            }

            @Override
            public void onError(Throwable e) {
                LogUtils.error(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                LogUtils.error(TAG, "onComplete");
            }
        };
    }

    /**
     * Only called from test, creates and returns a new {@link VenueSearchIdlingResource}.
     */
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mVenueSearchIdlingResource == null) {
            mVenueSearchIdlingResource = new VenueSearchIdlingResource();
        }
        return mVenueSearchIdlingResource;
    }

    // This interface can be implemented by the Activity, parent Fragment
    public interface OnFragmentInteractionListener {
        //Venue Item Selected from the list of venue items
        void onVenueSelected(Venue venue);

        //Listener for floating action button click on Venue list screen
        void onMapFloatingActionButtonClicked();
    }
}
