package com.nxp.placesearch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.nxp.placesearch.model.Venue;
import com.nxp.placesearch.repository.PlaceSearchRepository;

import java.util.List;

public class PlaceSearchViewModel extends ViewModel {
    private MutableLiveData<List<Venue>> mVenueList;
    private MutableLiveData<Venue> mVenue;
    private PlaceSearchRepository mPlaceSearchRepository;

    public PlaceSearchViewModel() {
        this.mPlaceSearchRepository = PlaceSearchRepository.getInstance();
    }

    public void venueSearch(String query) {
        mVenueList = mPlaceSearchRepository.venueSearch(query);
    }

    public LiveData<List<Venue>> getVenueList() {
        return this.mVenueList;
    }

    public LiveData<Venue> getSelectedVenue() {
        return mVenue;
    }

    public void setSelectedVenue(Venue venue) {
        mVenue = mPlaceSearchRepository.venueDetail(venue);
    }
}