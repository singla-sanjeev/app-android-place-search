package com.homeaway.placesearch;

import com.homeaway.placesearch.model.VenueSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {


    /**
     * API to fetch venue using FourSquare APIs
     *
     *
     *
     */
    @GET("venues/search")
    Call<VenueSearchResponse> venueSearch(@Query("client_id") String clientId, @Query("client_secret") String clientSecret, @Query("near") String near, @Query("query") String query, @Query("v") String version, @Query("limit") int limit);

}