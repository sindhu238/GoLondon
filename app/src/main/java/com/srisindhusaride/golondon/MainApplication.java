package com.srisindhusaride.golondon;

import android.app.Application;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.srisindhusaride.golondon.Utils.InternetConnectivityReceiver;


/**
 * @author  Sindhu
 * @since 8-02-2017
 * @version 1.0
 */
public class MainApplication extends Application {

    private static MainApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new InternetConnectivityReceiver(), intentFilter);
        instance = this;
    }

    public static synchronized MainApplication getInstance() {
        return instance;
    }

    public void setConnectivityListener(InternetConnectivityReceiver.InternetConnectivityReceiverListener listener) {
        InternetConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
