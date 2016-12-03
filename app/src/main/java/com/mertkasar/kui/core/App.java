package com.mertkasar.kui.core;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

public class App extends Application {
    private final String TAG = App.class.getSimpleName();

    public static App instance;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        Log.d(TAG, "onCreate: App created");
    }

    @Override
    public void onTerminate() {
        Log.d(TAG, "onTerminate: App terminated");
        super.onTerminate();
    }

    public static App getInstance() {
        return instance;
    }

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
