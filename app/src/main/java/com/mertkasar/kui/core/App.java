package com.mertkasar.kui.core;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

public class App extends Application {
    public static final String TAG = App.class.getSimpleName();

    public static App instance;

    public FirebaseAuth auth;

    public String uid;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        auth = FirebaseAuth.getInstance();

        uid = "-AbC68u";
        //uid = "-T68ucHg";
        //uid = "-BcA97f";

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

    public boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
