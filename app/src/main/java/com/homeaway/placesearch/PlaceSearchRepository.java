package com.homeaway.placesearch;

import android.text.TextUtils;

import com.homeaway.placesearch.model.Venue;
import com.homeaway.placesearch.model.VenueSearchResponse;
import com.homeaway.placesearch.utils.LogUtils;

import java.util.List;

import javax.inject.Singleton;

import androidx.lifecycle.MutableLiveData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@Singleton
public class PlaceSearchRepository {
    private static final String TAG = LogUtils.makeLogTag(PlaceSearchRepository.class);
    final MutableLiveData<List<Venue>> mVenueListMutableLiveData = new MutableLiveData<>();
    private final String FOURSQUARE_CLIENT_ID = "J4ZEAZE2EBKD25N3XMYJYPHHZJOIICDD5MTEN0JOMYLTRNRD";
    private final String FOURSQUARE_CLIENT_SECRET = "ONN0QNYB5GKVBHMOBINOO330FB2ICQFECAV3LCVM2VYBF4AW";
    private final String NEAR = "Seattle, WA";
    private final String VERSION = "20190401";
    private final int LIMIT = 50;
    private WebService mWebService;

    public PlaceSearchRepository(WebService webService) {
        mWebService = webService;
    }

    public MutableLiveData<List<Venue>> venueSearch(String query) {
        if (TextUtils.isEmpty(query) || query.length() < 2) {
            mVenueListMutableLiveData.setValue(null);
            return mVenueListMutableLiveData;
        }
        mWebService.venueSearch(FOURSQUARE_CLIENT_ID, FOURSQUARE_CLIENT_SECRET,
                NEAR, query, VERSION, LIMIT).enqueue(new Callback<VenueSearchResponse>() {
            @Override
            public void onResponse(Call<VenueSearchResponse> call, Response<VenueSearchResponse> response) {
                if (response != null && response.isSuccessful()) {
                    VenueSearchResponse venueSearchResponse = response.body();
                    if (venueSearchResponse != null && venueSearchResponse.getMeta() != null
                            && venueSearchResponse.getMeta().getCode() == 200) {
                        if (venueSearchResponse.getResponse() != null) {
                            List<Venue> venues = venueSearchResponse.getResponse().getVenues();
                            if (null != venues && venues.size() > 0) {
                                mVenueListMutableLiveData.setValue(venues);
                            }
                        }
                    }
                    LogUtils.checkIf(TAG, response.toString());
                }
            }

            @Override
            public void onFailure(Call<VenueSearchResponse> call, Throwable throwable) {
                LogUtils.checkIf(TAG, "Throwable: " + throwable.toString());
            }
        });
        return mVenueListMutableLiveData;
    }
}