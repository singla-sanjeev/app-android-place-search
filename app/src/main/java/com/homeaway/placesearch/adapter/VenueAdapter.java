package com.homeaway.placesearch.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.homeaway.placesearch.R;
import com.homeaway.placesearch.model.Venue;
import com.homeaway.placesearch.ui.VenueDetailActivity;
import com.homeaway.placesearch.ui.VenueDetailFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.ViewHolder> {

    private Context mContext;
    private List<Venue> mVenueList;

    public VenueAdapter(Context context, List<Venue> venueList) {
        mContext = context;
        mVenueList = venueList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.venue_list_content, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Venue venue = mVenueList.get(position);
        holder.mNameTxtVw.setText(venue.getName());
        holder.mCategoryTxtVw.setText(venue.getCategories().get(0).getName());
        holder.mDistanceTxtVw.setText(String.format(mContext.getString(R.string.distance), getDistance(venue.getLocation().getLat(), venue.getLocation().getLng())));
        //TODO: Need to set icon
        //holder.mCatIcon.setImageResource(R.drawable.ic_category_placeholder);
        holder.mFavIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:Need to add fav action
                holder.mFavIcon.setImageResource(R.drawable.ic_favorite);
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, VenueDetailActivity.class);
                intent.putExtra(VenueDetailFragment.ARG_ITEM_ID, venue.getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mVenueList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNameTxtVw;
        private TextView mCategoryTxtVw;
        private TextView mDistanceTxtVw;
        private ImageView mFavIcon;
        private ImageView mCatIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mNameTxtVw = itemView.findViewById(R.id.txtVwName);
            mCategoryTxtVw = itemView.findViewById(R.id.txtVwCategory);
            mDistanceTxtVw = itemView.findViewById(R.id.txtVwDistance);
            mFavIcon = itemView.findViewById(R.id.iconFav);
            mCatIcon = itemView.findViewById(R.id.iconCat);
        }
    }

    private String getDistance(double latitude, double longitude){
        float distance=0;
        Location location=new Location("Current location");
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        Location centerLocation=new Location(mContext.getResources().getString(R.string.near));
        double centerLat = Double.parseDouble(mContext.getResources().getString(R.string.centre_of_seattle_latitude));
        double centerLong = Double.parseDouble(mContext.getResources().getString(R.string.centre_of_seattle_longitude));
        centerLocation.setLatitude(centerLat);
        centerLocation.setLongitude(centerLong);


        //TODO: Need to convert distance in miles
       distance = location.distanceTo(centerLocation);  //in meters
        //distance =crntLocation.distanceTo(newLocation) / 1000; // in km

        return String.valueOf(distance);
    }
}
