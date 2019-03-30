package com.homeaway.placesearch;

import com.homeaway.placesearch.model.VenueSearchRequest;
import com.homeaway.placesearch.model.VenueSearchResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIInterface {


    /**
     * API to register device info such as Android id, model, latitude, longitude etc.
     *
     * @param venueSearchRequest deviceInfoRequest object having Android id,
     *                          platform, model, resolution etc.
     */
    @POST("venues/search")
    Call<VenueSearchResponse> venueSearch(@Body VenueSearchRequest venueSearchRequest);

}