package com.nxp.placesearch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.nxp.placesearch.FavoriteVenueViewModel;
import com.nxp.placesearch.R;
import com.nxp.placesearch.database.FavoriteVenue;
import com.nxp.placesearch.databinding.VenueListContentBinding;
import com.nxp.placesearch.model.Venue;
import com.nxp.placesearch.ui.MainActivity;
import com.nxp.placesearch.ui.VenueListFragment;
import com.nxp.placesearch.utils.AppUtils;

import java.util.List;
import java.util.Map;

public class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.ViewHolder> {
    private Context mContext;
    private List<Venue> mVenueList;
    private Map<String, Boolean> mFavoriteMap;
    private double mCenterOfSeattleLatitude;
    private double mCenterOfSeattleLongitude;
    private FavoriteVenueViewModel mFavoriteVenueViewModel;
    private VenueListFragment.OnFragmentInteractionListener mOnFragmentInteractionListener;

    public VenueAdapter(Context context, List<Venue> venueList, Map<String, Boolean> favoriteMap,
                        VenueListFragment.OnFragmentInteractionListener onFragmentInteractionListener) {
        mContext = context;
        mVenueList = venueList;
        mFavoriteMap = favoriteMap;
        //initialize centre of Seattle value latitude and longitude values
        mCenterOfSeattleLatitude = Double.parseDouble(mContext.getResources().getString(R.string.centre_of_seattle_latitude));
        mCenterOfSeattleLongitude = Double.parseDouble(mContext.getResources().getString(R.string.centre_of_seattle_longitude));
        mOnFragmentInteractionListener = onFragmentInteractionListener;
        mFavoriteVenueViewModel = ViewModelProviders.of((MainActivity) context).get(FavoriteVenueViewModel.class);
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
        //Usage of Data binding for venue list items mapping
        private VenueListContentBinding mVenueListContentBinding;
        private ImageView mFavoriteIcon;
        private ImageView mCategoryIcon;

        ViewHolder(@NonNull VenueListContentBinding venueListContentBinding) {
            super(venueListContentBinding.getRoot());
            mVenueListContentBinding = venueListContentBinding;
            mFavoriteIcon = itemView.findViewById(R.id.favoriteIconImgVw);
            mCategoryIcon = itemView.findViewById(R.id.categoryIconImgVw);
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
                //Categories icon url creation with prefix, resolution and suffix values.
                String iconUrl = venue.getCategories().get(0).getIcon().getPrefix().replace("\\", "") + "bg_64" +
                        venue.getCategories().get(0).getIcon().getSuffix();
                //Loading of category icon from cache or url using picasso async loading of image content.
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
                FavoriteVenue favoriteVenue = new FavoriteVenue();
                favoriteVenue.setId(venue.getId());
                favoriteVenue.setFavorite(true);
                if (venue.isFavorite()) {
                    venue.setFavorite(false);
                    if (mFavoriteMap != null) {
                        mFavoriteMap.remove(venue.getId());
                    }
                    mFavoriteIcon.setImageResource(R.drawable.ic_favorite_border);
                    //Removing favorite from persistence storage using Room database wrapper.
                    if (mFavoriteVenueViewModel != null) {
                        mFavoriteVenueViewModel.removeFromFavorite(favoriteVenue);
                    }
                } else {
                    venue.setFavorite(true);
                    if (mFavoriteMap != null) {
                        mFavoriteMap.put(venue.getId(), true);
                    }
                    mFavoriteIcon.setImageResource(R.drawable.ic_favorite);
                    //Adding favorite into persistence storage using Room database wrapper.
                    if (mFavoriteVenueViewModel != null) {
                        mFavoriteVenueViewModel.addToFavorite(favoriteVenue);
                    }
                }
            });
            itemView.setOnClickListener(view -> {
                mOnFragmentInteractionListener.onVenueSelected(venue);
            });
        }
    }
}
