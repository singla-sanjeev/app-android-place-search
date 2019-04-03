package com.homeaway.placesearch;

import com.homeaway.placesearch.model.Venue;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class VenueListViewModel extends ViewModel {
    private MutableLiveData<List<Venue>> mVenueList;
    private MutableLiveData<Venue> mVenue = new MutableLiveData<>();
    private PlaceSearchRepository mPlaceSearchRepository;

    public VenueListViewModel(WebService webService) {
        this.mPlaceSearchRepository = new PlaceSearchRepository(webService);
    }

    public void init(String query) {
        mVenueList = mPlaceSearchRepository.venueSearch(query);
    }

    public LiveData<List<Venue>> getVenueList() {
        return this.mVenueList;
    }

    public LiveData<Venue> getSelectedVenue() {
        return mVenue;
    }

    public void setSelectedVenue(Venue venue) {
        mVenue.setValue(venue);
    }
}
