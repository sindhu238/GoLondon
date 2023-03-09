package com.srisindhusaride.golondon;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.srisindhusaride.golondon.Utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @since 14/02/17.
 */

public class GetAddressIntentService extends IntentService {

    private static final String TAG = "GetAddressIntentService";
    String errorMessage = "";

    private ResultReceiver mResultReciever;
    private List<Address> addresses = null;

    public GetAddressIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        mResultReciever = intent.getParcelableExtra(Constants.RECEIVER);

        if (mResultReciever == null) {
            Log.wtf(TAG, "No receiver received. There is nowhere to send the results.");
            return;
        }

        // Get the location passed to this service through an extra.
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);

        if (location == null) {
            errorMessage = "No location data provided";
            Log.wtf(TAG, errorMessage);
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException e) {
                errorMessage = "Service not available";
                Log.e(TAG, e.getLocalizedMessage());
            } catch (IllegalArgumentException e) {
                errorMessage = "Invalid latitude and longitude values";
                Log.e(TAG, e.getLocalizedMessage() + "Latitude: "+location.getLatitude()
                        +" Longitude: "+location.getLongitude());
            }

            if (addresses == null || addresses.size() == 0) {
                if (errorMessage.isEmpty()) {
                    errorMessage = "No addresses found";
                    Log.e(TAG, errorMessage);
                }
                deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            } else {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<>();

                // Fetch the address lines using {@code getAddressLine},
                // join them, and send them to the thread. The {@link android.location.address}
                // class provides other options for fetching address details that you may prefer
                // to use. Here are some examples:
                // getLocality() ("Mountain View", for example)
                // getAdminArea() ("CA", for example)
                // getPostalCode() ("94043", for example)
                // getCountryCode() ("US", for example)
                // getCountryName() ("United States", for example)
                for(int i = 0; i < address.getMaxAddressLineIndex() || i == 0; i++) {
                    addressFragments.add(address.getAddressLine(i));
                }
                deliverResultToReceiver(Constants.SUCCESS_RESULT,
                        TextUtils.join(", ", addressFragments));
            }
        }
    }

    /**
     * Sends a resultCode and message to the receiver.
     */
    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mResultReciever.send(resultCode, bundle);
    }
}
