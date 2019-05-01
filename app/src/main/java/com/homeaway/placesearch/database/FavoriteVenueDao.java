package com.homeaway.placesearch.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface FavoriteVenueDao {

    @Query("SELECT * FROM favorite_venue")
    Single<List<FavoriteVenue>> getAllFavouriteVenue();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(FavoriteVenue favoriteVenue);

    @Delete
    int remove(FavoriteVenue favoriteVenue);

    @Query("SELECT count(*) FROM favorite_venue where id LIKE :id")
    int isFavouriteVenue(String id);
}
