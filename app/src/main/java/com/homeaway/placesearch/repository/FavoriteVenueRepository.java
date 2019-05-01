package com.homeaway.placesearch.repository;

import android.app.Application;

import com.homeaway.placesearch.database.FavoriteVenue;
import com.homeaway.placesearch.database.FavoriteVenueDao;
import com.homeaway.placesearch.database.VenueSearchRoomDatabase;

import java.util.List;

import io.reactivex.Single;

public class FavoriteVenueRepository {
    private final FavoriteVenueDao mFavoriteVenueDao;

    public FavoriteVenueRepository(Application application) {
        VenueSearchRoomDatabase venueSearchRoomDatabase = VenueSearchRoomDatabase.getDatabase(application);
        this.mFavoriteVenueDao = venueSearchRoomDatabase.favoriteVenueDao();
    }

    public Single<List<FavoriteVenue>> getAllFavoriteVenue() {
        return mFavoriteVenueDao.getAllFavouriteVenue();
    }

    public boolean isFavoriteVenue(String id) {
        return mFavoriteVenueDao.isFavouriteVenue(id) > 0;
    }

    public void insertIntoFavorites(FavoriteVenue favoriteVenue) {
        mFavoriteVenueDao.insert(favoriteVenue);
    }

    public void removeFromFavorites(FavoriteVenue favoriteVenue) {
        mFavoriteVenueDao.remove(favoriteVenue);
    }
}
