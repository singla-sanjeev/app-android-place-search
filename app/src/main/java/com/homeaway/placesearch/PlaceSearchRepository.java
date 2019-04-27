package com.homeaway.placesearch;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.homeaway.placesearch.model.FourSquareResponse;
import com.homeaway.placesearch.model.Venue;
import com.homeaway.placesearch.utils.LogUtils;
import com.homeaway.placesearch.utils.RetrofitUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceSearchRepository {
    private static final String TAG = LogUtils.makeLogTag(PlaceSearchRepository.class);
    private static PlaceSearchRepository sInstance;
    private final MutableLiveData<List<Venue>> mVenueListMutableLiveData = new MutableLiveData<>();
    private final MutableLiveData<Venue> mVenueMutableLiveData = new MutableLiveData<>();
    private final String FOURSQUARE_CLIENT_ID = "J4ZEAZE2EBKD25N3XMYJYPHHZJOIICDD5MTEN0JOMYLTRNRD";
    private final String FOURSQUARE_CLIENT_SECRET = "ONN0QNYB5GKVBHMOBINOO330FB2ICQFECAV3LCVM2VYBF4AW";
    private final String NEAR = "Seattle, WA";
    private final String VERSION = "20190427";
    private final int LIMIT = 50;
    private WebService mWebService;

    private PlaceSearchRepository() {
        mWebService = RetrofitUtils.getInstance().getService();
    }

    /**
     * This method returns the single instance of PreferenceUtils
     *
     * @return single instance of the class
     */
    public static synchronized PlaceSearchRepository getInstance() {
        if (sInstance == null) {
            sInstance = new PlaceSearchRepository();
        }
        return sInstance;
    }

    public MutableLiveData<List<Venue>> venueSearch(String query) {
        if (TextUtils.isEmpty(query) || query.length() < 2) {
            mVenueListMutableLiveData.setValue(null);
            return mVenueListMutableLiveData;
        }
        mWebService.venueSearch(FOURSQUARE_CLIENT_ID, FOURSQUARE_CLIENT_SECRET,
                NEAR, query, VERSION, LIMIT).enqueue(new Callback<FourSquareResponse>() {
            @Override
            public void onResponse(Call<FourSquareResponse> call, Response<FourSquareResponse> response) {
                if (response.isSuccessful()) {
                    FourSquareResponse fourSquareResponse = response.body();
                    if (fourSquareResponse != null && fourSquareResponse.getMeta() != null
                            && fourSquareResponse.getMeta().getCode() == 200) {
                        if (fourSquareResponse.getResponse() != null) {
                            List<Venue> venues = fourSquareResponse.getResponse().getVenues();
                            if (null != venues && venues.size() > 0) {
                                mVenueListMutableLiveData.postValue(venues);
                            }
                        }
                    }
                    LogUtils.checkIf(TAG, response.toString());
                }
            }

            @Override
            public void onFailure(Call<FourSquareResponse> call, Throwable throwable) {
                LogUtils.checkIf(TAG, "Throwable: " + throwable.toString());
            }
        });
        return mVenueListMutableLiveData;
    }

    public MutableLiveData<Venue> venueDetail(String venueId) {
        mWebService.venueDetail(venueId, FOURSQUARE_CLIENT_ID, FOURSQUARE_CLIENT_SECRET,
                VERSION).enqueue(new Callback<FourSquareResponse>() {
            @Override
            public void onResponse(Call<FourSquareResponse> call, Response<FourSquareResponse> response) {
                if (response.isSuccessful()) {
                    FourSquareResponse fourSquareResponse = response.body();
                    if (fourSquareResponse != null && fourSquareResponse.getMeta() != null
                            && fourSquareResponse.getMeta().getCode() == 200) {
                        if (fourSquareResponse.getResponse() != null) {
                            Venue venue = fourSquareResponse.getResponse().getVenue();
                            mVenueMutableLiveData.postValue(venue);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<FourSquareResponse> call, Throwable throwable) {
                LogUtils.checkIf(TAG, "Throwable: " + throwable.toString());
            }
        });
        return mVenueMutableLiveData;
    }
}