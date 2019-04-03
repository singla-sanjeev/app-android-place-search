package com.homeaway.placesearch.adapter;

import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.homeaway.placesearch.R;
import com.homeaway.placesearch.model.Venue;
import com.homeaway.placesearch.ui.VenueListFragment;
import com.homeaway.placesearch.utils.LogUtils;
import com.homeaway.placesearch.utils.RetrofitUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.ViewHolder> {
    private static final String TAG = LogUtils.makeLogTag(VenueAdapter.class);
    private Context mContext;
    private List<Venue> mVenueList;
    private Map<String, Venue> mFavoriteMap;
    private double mCenterOfSeattleLatitude;
    private double mCenterOfSeattleLongitude;
    private VenueListFragment.OnFragmentInteractionListener mOnFragmentInteractionListener;

    public VenueAdapter(Context context, List<Venue> venueList, Map<String, Venue> favoriteMap,
                        VenueListFragment.OnFragmentInteractionListener onFragmentInteractionListener) {
        mContext = context;
        mVenueList = venueList;
        mFavoriteMap = favoriteMap;
        mCenterOfSeattleLatitude = Double.parseDouble(mContext.getResources().getString(R.string.centre_of_seattle_latitude));
        mCenterOfSeattleLongitude = Double.parseDouble(mContext.getResources().getString(R.string.centre_of_seattle_longitude));
        mOnFragmentInteractionListener = onFragmentInteractionListener;
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
        if (venue.getCategories() != null && venue.getCategories().size() > 0) {
            holder.mCategoryTxtVw.setText(venue.getCategories().get(0).getName());
        } else {
            holder.mCategoryTxtVw.setVisibility(View.INVISIBLE);
        }
        if (venue.getLocation() != null) {
            holder.mDistanceTxtVw.setText(String.format(mContext.getString(R.string.distance), getDistance(venue.getLocation().getLat(), venue.getLocation().getLng())));
        }

        if (venue.getCategories() != null && venue.getCategories().size() > 0) {
            holder.mCategoryIcon.setVisibility(View.INVISIBLE);
            String iconUrl = venue.getCategories().get(0).getIcon().getPrefix().replace("\\", "") + "bg_64" +
                    venue.getCategories().get(0).getIcon().getSuffix();
            loadCategoryImage(iconUrl, holder);
        }

        if (mFavoriteMap != null && mFavoriteMap.containsKey(venue.getId())) {
            venue.setFavorite(true);
            holder.mFavoriteIcon.setImageResource(R.drawable.ic_favorite);
        } else {
            venue.setFavorite(false);
            holder.mFavoriteIcon.setImageResource(R.drawable.ic_favorite_border);
        }
        holder.mFavoriteIcon.setOnClickListener(v -> {
            if (venue.isFavorite()) {
                venue.setFavorite(false);
                if (mFavoriteMap != null) {
                    mFavoriteMap.remove(venue.getId());
                }
                holder.mFavoriteIcon.setImageResource(R.drawable.ic_favorite_border);
            } else {
                venue.setFavorite(true);
                if (mFavoriteMap != null) {
                    mFavoriteMap.put(venue.getId(), venue);
                }
                holder.mFavoriteIcon.setImageResource(R.drawable.ic_favorite);
            }
        });
        holder.itemView.setOnClickListener(view -> mOnFragmentInteractionListener.onVenueSelected(venue));
    }

    private void loadCategoryImage(String imageUrl, final ViewHolder holder) {
        Picasso picasso = RetrofitUtils.getInstance().getPicassoImageDownloader(mContext);
        picasso.load(imageUrl).into(holder.mCategoryIcon, new Callback() {

            @Override
            public void onSuccess() {
                holder.mCategoryIcon.setVisibility(View.VISIBLE);
                LogUtils.checkIf(TAG, "loadCategoryImage: onSuccess");
            }

            @Override
            public void onError() {
                holder.mCategoryIcon.setVisibility(View.VISIBLE);
                LogUtils.checkIf(TAG, "loadCategoryImage: onError");
            }
        });
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;
        if (mVenueList != null) {
            itemCount = mVenueList.size();
        }
        return itemCount;
    }

    private String getDistance(double latitude, double longitude) {
        float distance[] = new float[3];
        Location.distanceBetween(mCenterOfSeattleLatitude, mCenterOfSeattleLongitude, latitude, longitude, distance);  //in meters
        float distanceInMiles = (float) (distance[0] * 0.00062137);
        return String.format(Locale.getDefault(), "%.2f Miles", distanceInMiles);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameTxtVw;
        private TextView mCategoryTxtVw;
        private TextView mDistanceTxtVw;
        private ImageView mFavoriteIcon;
        private ImageView mCategoryIcon;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            mNameTxtVw = itemView.findViewById(R.id.txtVwName);
            mCategoryTxtVw = itemView.findViewById(R.id.txtVwCategory);
            mDistanceTxtVw = itemView.findViewById(R.id.txtVwDistance);
            mFavoriteIcon = itemView.findViewById(R.id.imgVwFavoriteIcon);
            mCategoryIcon = itemView.findViewById(R.id.imgViewCategoryIcon);
        }
    }
}
