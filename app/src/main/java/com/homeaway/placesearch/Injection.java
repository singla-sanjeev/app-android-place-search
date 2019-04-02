package com.homeaway.placesearch;

import android.content.Context;

import com.homeaway.placesearch.utils.RetrofitUtils;


/**
 * Enables injection of data sources.
 */
public class Injection {

    public static WebService provideWebService(Context context) {
        return RetrofitUtils.getInstance().getService(context);
    }

    public static ViewModelFactory provideViewModelFactory(Context context) {
        WebService webService = provideWebService(context);
        return new ViewModelFactory(webService);
    }
}
