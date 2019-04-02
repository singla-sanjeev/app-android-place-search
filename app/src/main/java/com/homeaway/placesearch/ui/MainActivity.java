package com.homeaway.placesearch.ui;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.homeaway.placesearch.R;
import com.homeaway.placesearch.model.Venue;
import com.homeaway.placesearch.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements TextWatcher, VenueListFragment.OnVenueListItemListener {
    private static final String TAG = LogUtils.makeLogTag(MainActivity.class);
    private VenueListFragment mVenueListFragment;
    private FloatingActionButton mFloatingActionButton;
    private List<Venue> mVenueList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        mVenueListFragment = VenueListFragment.newInstance();
        loadFragment(mVenueListFragment);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof VenueListFragment) {
            VenueListFragment venueListFragment = (VenueListFragment) fragment;
            venueListFragment.setOnVenueListItemListener(this);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mVenueListFragment.onQueryTextChanged();
    }

    @Override
    public void afterTextChanged(final Editable place) {
        if (mFloatingActionButton != null && mFloatingActionButton.isOrWillBeShown()) {
            mFloatingActionButton.hide();
        }
        mVenueListFragment.afterQueryTextChange(place.toString());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void launchVenueMapActivity() {
        Intent intent = new Intent(this, VenueMapActivity.class);
        intent.putParcelableArrayListExtra(VenueMapActivity.VENUE_LIST_BUNDLE_ID, (ArrayList<? extends Parcelable>) mVenueList);
        startActivity(intent);
    }

    private void loadFragment(Fragment fragment) {
        // create a FragmentManager
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        // create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        // replace the FrameLayout with new Fragment
        fragmentTransaction.add(R.id.constraintLytFragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit(); // save the changes
    }

    @Override
    public void onVenueSelected(Venue venue) {

    }

    @Override
    public void onVenueListChanged(List<Venue> venueList) {
        mVenueList = venueList;
        if (mFloatingActionButton != null) {
            mFloatingActionButton.show();
        }
    }
}