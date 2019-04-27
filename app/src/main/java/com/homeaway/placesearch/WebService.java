package com.homeaway.placesearch;

import com.homeaway.placesearch.model.FourSquareResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WebService {

    /**
     * API to fetch venue using FourSquare APIs
     */
    @GET("venues/search")
    Call<FourSquareResponse> venueSearch(@Query("client_id") String clientId,
                                         @Query("client_secret") String clientSecret,
                                         @Query("near") String near, @Query("query") String query,
                                         @Query("v") String version,
                                         @Query("limit") int limit);

    @GET("venues/{venueId}")
    Call<FourSquareResponse> venueDetail(@Path("venueId") String venueId, @Query("client_id") String clientId,
                                         @Query("client_secret") String clientSecret, @Query("v") String version);
}