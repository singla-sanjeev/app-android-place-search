package com.nxp.placesearch.IdlingResource;

import androidx.annotation.Nullable;
import androidx.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

public class VenueSearchIdlingResource implements IdlingResource, VenueSearchListener {

    @Nullable
    private volatile ResourceCallback mResourceCallback;

    // Idleness is controlled with this boolean.
    private AtomicBoolean mIsIdleNow = new AtomicBoolean(true);

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return mIsIdleNow.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mResourceCallback = callback;
    }

    @Override
    public void beginSearch() {
        mIsIdleNow.set(false);
    }

    @Override
    public void endSearch() {
        mIsIdleNow.set(true);
        if (mIsIdleNow.get() && mResourceCallback != null) {
            mResourceCallback.onTransitionToIdle();
        }
    }
}