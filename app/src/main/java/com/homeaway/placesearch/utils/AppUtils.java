package com.homeaway.placesearch.utils;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.ImageView;

import com.homeaway.placesearch.R;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class AppUtils {
    private static AppUtils sInstance;

    /**
     * Constructor of the singleton class
     */
    private AppUtils() {

    }

    /**
     * This method returns the single instance of PreferenceUtils
     *
     * @return single instance of the class
     */
    public static synchronized AppUtils getInstance() {
        if (sInstance == null) {
            sInstance = new AppUtils();
        }
        return sInstance;
    }

    /**
     * Check availability of internet connection for making any http call.
     *
     * @param context : Activity/Application context
     * @return : true or false based on internet connection availability
     */
    public boolean isInternetAvailable(Context context) {
        if (context == null) {
            return false;
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null) {
            return false;
        }

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnected();
    }

    public String getDistance(double centerOfSeattleLatitude, double centerOfSeattleLongitude, double latitude, double longitude) {
        float[] distance = new float[3];
        Location.distanceBetween(centerOfSeattleLatitude, centerOfSeattleLongitude, latitude, longitude, distance);  //in meters
        float distanceInMiles = (float) (distance[0] * 0.00062137);
        return String.format(Locale.getDefault(), "%.2f Miles", distanceInMiles);
    }

    public void loadCategoryImage(Context context, String imageUrl, ImageView imageView) {
        Picasso picasso = RetrofitUtils.getInstance().getPicassoImageDownloader(context);
        picasso.load(imageUrl).placeholder(R.drawable.ic_category_placeholder).into(imageView);
    }
}
