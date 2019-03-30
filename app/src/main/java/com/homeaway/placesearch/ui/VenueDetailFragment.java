package com.homeaway.placesearch.ui;

import android.app.Activity;

import com.google.android.material.appbar.CollapsingToolbarLayout;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.homeaway.placesearch.R;

/**
 * A fragment representing a single Venue detail screen.
 * This fragment is either contained in a {@link VenueSearchActivity}
 * in two-pane mode (on tablets) or a {@link VenueDetailActivity}
 * on handsets.
 */
public class VenueDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public VenueDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(null);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.venue_detail, container, false);

//        // Show the dummy content as text in a TextView.
//        if (mItem != null) {
//            ((TextView) rootView.findViewById(R.id.venue_detail)).setText(mItem.details);
//        }

        return rootView;
    }
}
