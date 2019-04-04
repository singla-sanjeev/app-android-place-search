package com.homeaway.placesearch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.homeaway.placesearch.R;
import com.homeaway.placesearch.databinding.VenueListContentBinding;
import com.homeaway.placesearch.model.Venue;
import com.homeaway.placesearch.ui.VenueListFragment;
import com.homeaway.placesearch.utils.AppUtils;
import com.homeaway.placesearch.utils.LogUtils;

import java.util.List;
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
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        VenueListContentBinding venueListContentBinding =
                VenueListContentBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(venueListContentBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Venue venue = mVenueList.get(position);
        holder.bind(venue);
    }

    @Override
    public int getItemCount() {
        int itemCount = 0;
        if (mVenueList != null) {
            itemCount = mVenueList.size();
        }
        return itemCount;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private VenueListContentBinding mVenueListContentBinding;
        private ImageView mFavoriteIcon;
        private ImageView mCategoryIcon;

        ViewHolder(@NonNull VenueListContentBinding venueListContentBinding) {
            super(venueListContentBinding.getRoot());
            mVenueListContentBinding = venueListContentBinding;
            mFavoriteIcon = itemView.findViewById(R.id.imgVwFavoriteIcon);
            mCategoryIcon = itemView.findViewById(R.id.imgViewCategoryIcon);
        }

        public void bind(Venue venue) {
            mVenueListContentBinding.setVenueName(venue.getName());
            if (venue.getCategories() != null && venue.getCategories().size() > 0) {
                mVenueListContentBinding.setCategoryName(venue.getCategories().get(0).getName());
            }
            if (venue.getLocation() != null) {
                mVenueListContentBinding.setDistance(String.format(mContext.getString(R.string.distance),
                        AppUtils.getInstance().getDistance(mCenterOfSeattleLatitude,
                                mCenterOfSeattleLongitude, venue.getLocation().getLat(), venue.getLocation().getLng())));
            }
            if (venue.getCategories() != null && venue.getCategories().size() > 0) {
                String iconUrl = venue.getCategories().get(0).getIcon().getPrefix().replace("\\", "") + "bg_64" +
                        venue.getCategories().get(0).getIcon().getSuffix();
                AppUtils.getInstance().loadCategoryImage(mContext, iconUrl, mCategoryIcon);
            }

            if (mFavoriteMap != null && mFavoriteMap.containsKey(venue.getId())) {
                venue.setFavorite(true);
                mFavoriteIcon.setImageResource(R.drawable.ic_favorite);
            } else {
                venue.setFavorite(false);
                mFavoriteIcon.setImageResource(R.drawable.ic_favorite_border);
            }
            mFavoriteIcon.setOnClickListener(v -> {
                if (venue.isFavorite()) {
                    venue.setFavorite(false);
                    if (mFavoriteMap != null) {
                        mFavoriteMap.remove(venue.getId());
                    }
                    mFavoriteIcon.setImageResource(R.drawable.ic_favorite_border);
                } else {
                    venue.setFavorite(true);
                    if (mFavoriteMap != null) {
                        mFavoriteMap.put(venue.getId(), venue);
                    }
                    mFavoriteIcon.setImageResource(R.drawable.ic_favorite);
                }
            });
            itemView.setOnClickListener(view -> mOnFragmentInteractionListener.onVenueSelected(venue));
        }
    }
}
