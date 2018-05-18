package com.srisindhusaride.golondon;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;

/**
 * @since 18/04/17.
 */

public class StreetView_DialogFragment extends DialogFragment {

    View mRootView = null;
    Double mLatitudeOfMarker;
    Double mLongitudeOfMarker;
    StreetViewPanoramaFragment mPanoramaFragment =null;

    public static StreetView_DialogFragment newInstance(Double lat, Double lng) {
        StreetView_DialogFragment fragment = new StreetView_DialogFragment();
        Bundle args = new Bundle();
        args.putDouble("lat", lat);
        args.putDouble("lng", lng);
        fragment.setArguments(args);
        return  fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        mLongitudeOfMarker = getArguments().getDouble("lng");
        mLatitudeOfMarker = getArguments().getDouble("lat");

        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.streetview_dialog, null);
            mPanoramaFragment = (StreetViewPanoramaFragment) getFragmentManager()
                    .findFragmentById(R.id.streetViewPanorama);
        }

        mPanoramaFragment.getStreetViewPanoramaAsync(new OnStreetViewPanoramaReadyCallback() {
            @Override
            public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
                streetViewPanorama.setPosition(new LatLng(mLatitudeOfMarker, mLongitudeOfMarker));
            }
        });

        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mPanoramaFragment != null)
            getFragmentManager().beginTransaction().remove(mPanoramaFragment).commit();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}
