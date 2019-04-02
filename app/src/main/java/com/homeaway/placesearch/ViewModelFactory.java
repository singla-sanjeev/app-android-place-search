package com.homeaway.placesearch;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Factory for ViewModels
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    private final WebService mWebService;

    public ViewModelFactory(WebService webService) {
        mWebService = webService;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(VenueListViewModel.class)) {
            return (T) new VenueListViewModel(mWebService);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
