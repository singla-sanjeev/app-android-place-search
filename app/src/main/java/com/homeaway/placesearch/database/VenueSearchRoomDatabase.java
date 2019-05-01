package com.homeaway.placesearch.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {FavoriteVenue.class}, version = 1, exportSchema = false)
public abstract class VenueSearchRoomDatabase extends RoomDatabase {
    public abstract FavoriteVenueDao favoriteVenueDao();

    private static volatile VenueSearchRoomDatabase INSTANCE;

    public static VenueSearchRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (VenueSearchRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            VenueSearchRoomDatabase.class, "venue_search_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}