package com.homeaway.placesearch.utils;

import android.content.Context;
import android.location.Location;
import android.widget.ImageView;

import com.homeaway.placesearch.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Locale;

public class AppUtils {
    private static final String TAG = LogUtils.makeLogTag(AppUtils.class);
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

    public String getDistance(double centerOfSeattleLatitude, double centerOfSeattleLongitude, double latitude, double longitude) {
        float distance[] = new float[3];
        Location.distanceBetween(centerOfSeattleLatitude, centerOfSeattleLongitude, latitude, longitude, distance);  //in meters
        float distanceInMiles = (float) (distance[0] * 0.00062137);
        return String.format(Locale.getDefault(), "%.2f Miles", distanceInMiles);
    }

    public void loadCategoryImage(Context context, String imageUrl, ImageView imageView) {
        Picasso picasso = RetrofitUtils.getInstance().getPicassoImageDownloader(context);
        picasso.load(imageUrl).placeholder(R.drawable.ic_category_placeholder).into(imageView, new Callback() {

            @Override
            public void onSuccess() {
                LogUtils.checkIf(TAG, "loadCategoryImage: onSuccess");
            }

            @Override
            public void onError() {
                LogUtils.checkIf(TAG, "loadCategoryImage: onError");
            }
        });
    }
}
