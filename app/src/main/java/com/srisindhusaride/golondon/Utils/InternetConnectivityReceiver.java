package com.srisindhusaride.golondon.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.srisindhusaride.golondon.MainApplication;

/**
 * @since 25/07/17.
 */

public class InternetConnectivityReceiver
        extends BroadcastReceiver {

    public static InternetConnectivityReceiverListener connectivityReceiverListener;

    private static Snackbar snackbar;

    public InternetConnectivityReceiver() {
        super();

    }

    @Override
    public void onReceive(Context context, Intent arg1) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null
                    && activeNetwork.isConnectedOrConnecting();

            if (connectivityReceiverListener != null) {
                connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) MainApplication.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }


    public interface InternetConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }

    public static void displaySnackBar(View view) {
        snackbar = Snackbar.make(view, "No active internet connection available!!"
                , Snackbar.LENGTH_INDEFINITE);
        snackbar.show();

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
    }

    public static void dismissSnackBar() {
        if (snackbar != null)
            snackbar.dismiss();
    }
}