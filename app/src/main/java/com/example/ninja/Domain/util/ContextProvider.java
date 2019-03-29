package com.example.ninja.Domain.util;

import android.app.Application;
import android.content.Context;

public class ContextProvider extends Application {

    /**
     * Keeps a reference of the application context
     */
    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();

        sContext = getApplicationContext();

    }

    public static Context getContext() {
        return sContext;
    }

}
