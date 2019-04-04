package com.homeaway.placesearch;

import com.homeaway.placesearch.utils.RetrofitUtils;


/**
 * Enables injection of data sources.
 */
public class Injection {

    public static WebService provideWebService() {
        return RetrofitUtils.getInstance().getService();
    }

    public static ViewModelFactory provideViewModelFactory() {
        WebService webService = provideWebService();
        return new ViewModelFactory(webService);
    }
}
