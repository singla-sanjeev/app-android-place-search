package com.homeaway.placesearch;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.homeaway.placesearch.database.FavoriteVenue;
import com.homeaway.placesearch.repository.FavoriteVenueRepository;
import com.homeaway.placesearch.utils.LogUtils;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FavoriteVenueViewModel extends AndroidViewModel {
    private final FavoriteVenueRepository mFavoriteVenueRepository;
    private CompositeDisposable mCompositeDisposable;
    private MutableLiveData<List<FavoriteVenue>> mFavoriteVenuesLiveData;

    public FavoriteVenueViewModel(Application application) {
        super(application);
        this.mFavoriteVenueRepository = new FavoriteVenueRepository(application);
        mCompositeDisposable = new CompositeDisposable();
        mFavoriteVenuesLiveData = new MutableLiveData<>();
    }

    public void getAllFavoriteVenues() {
        Disposable favoriteShowsDisposable = mFavoriteVenueRepository.getAllFavoriteVenue()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onSuccess, this::onError);
        mCompositeDisposable.add(favoriteShowsDisposable);
    }

    private void onError(Throwable throwable) {
        LogUtils.checkIf("FavoriteVenueViewModel", throwable.getMessage());
    }

    private void onSuccess(List<FavoriteVenue> favoriteVenues) {
        mFavoriteVenuesLiveData.setValue(favoriteVenues);
    }

    public LiveData<List<FavoriteVenue>> getFavoriteVenuesLiveData() {
        return mFavoriteVenuesLiveData;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        mCompositeDisposable.clear();
    }

    public void addToFavorite(FavoriteVenue favoriteVenue) {
        mFavoriteVenueRepository.insertIntoFavorites(favoriteVenue);
    }

    public void removeFromFavorite(FavoriteVenue favoriteVenue) {
        mFavoriteVenueRepository.removeFromFavorites(favoriteVenue);
    }
}