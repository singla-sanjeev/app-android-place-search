package com.homeaway.placesearch.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.homeaway.placesearch.R;

import com.homeaway.placesearch.model.Venue;
import com.homeaway.placesearch.model.VenueSearchResponse;
import com.homeaway.placesearch.utils.LogUtils;
import com.homeaway.placesearch.utils.RetrofitUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity representing a list of Venues. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link VenueDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class VenueSearchActivity extends AppCompatActivity {
    private static final String TAG = LogUtils.makeLogTag(VenueSearchActivity.class);
    private ArrayList<Venue> mVenueList = new ArrayList<>();
    private SimpleItemRecyclerViewAdapter mSimpleItemRecyclerViewAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchVenueMapActivity();
            }
        });

        if (findViewById(R.id.venue_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
        }
        fetchVenueList("Coffee");
        View recyclerView = findViewById(R.id.venue_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void launchVenueMapActivity() {
        Intent intent = new Intent(this, VenueMapActivity.class);
        intent.putParcelableArrayListExtra(VenueMapActivity.VENUE_LIST_BUNDLE_ID, mVenueList);
        startActivity(intent);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        mSimpleItemRecyclerViewAdapter = new SimpleItemRecyclerViewAdapter(this, mVenueList);
        recyclerView.setAdapter(mSimpleItemRecyclerViewAdapter);
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final VenueSearchActivity mParentActivity;
        private final List<Venue> mValues;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Venue venue = (Venue) view.getTag();

                    Context context = view.getContext();
                    Intent intent = new Intent(context, VenueDetailActivity.class);
                    intent.putExtra(VenueDetailFragment.ARG_ITEM_ID, venue.getId());
                    context.startActivity(intent);

            }
        };

        SimpleItemRecyclerViewAdapter(VenueSearchActivity parent,
                                      List<Venue> items) {
            mValues = items;
            mParentActivity = parent;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.venue_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mName.setText(mValues.get(position).getName());

            holder.mCategory.setText(mValues.get(position).getCategories().get(0).getName());

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mName;
            final TextView mCategory;

            ViewHolder(View view) {
                super(view);
                mName = (TextView) view.findViewById(R.id.id_name);
                mCategory = (TextView) view.findViewById(R.id.id_category);
            }
        }
    }

    private void fetchVenueList(String query) {
        Callback<VenueSearchResponse> responseCallback = new Callback<VenueSearchResponse>() {
            @Override
            public void onResponse(Call<VenueSearchResponse> call, Response<VenueSearchResponse> response) {
                if (response != null && response.isSuccessful()) {
                    VenueSearchResponse venueSearchResponse = response.body();
                    if(venueSearchResponse != null && venueSearchResponse.getMeta() != null && venueSearchResponse.getMeta().getCode() == 200) {
                        mVenueList.addAll(venueSearchResponse.getResponse().getVenues());
                        mSimpleItemRecyclerViewAdapter.notifyDataSetChanged();
                    }
                    LogUtils.checkIf(TAG, response.toString());
                }
            }

            @Override
            public void onFailure(Call<VenueSearchResponse> call, Throwable throwable) {
                LogUtils.checkIf(TAG, "Throwable: " + throwable.toString());
            }
        };
        RetrofitUtils.getInstance().getService(this).venueSearch(getString(R.string.client_id), getString(R.string.client_secret), getString(R.string.near), query, "20190330", 20).enqueue(responseCallback);
    }
}
