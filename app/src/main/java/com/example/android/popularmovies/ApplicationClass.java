package com.example.android.popularmovies;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

public class ApplicationClass extends Application {

    // This flag should be set to true to enable VectorDrawable support for API < 21
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
