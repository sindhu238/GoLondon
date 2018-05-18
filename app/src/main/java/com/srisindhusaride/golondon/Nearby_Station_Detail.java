package com.srisindhusaride.golondon;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.srisindhusaride.golondon.Utils.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.ButtCap;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.srisindhusaride.golondon.R.color.tfl;
import static com.srisindhusaride.golondon.R.drawable.marker;
import static com.srisindhusaride.golondon.Utils.Constants.GOOGLE_MAPS_DISTANCE_API;
import static com.srisindhusaride.golondon.Utils.Constants.lineName;


/**
 * @since 02/03/17.
 */

public class Nearby_Station_Detail extends AppCompatActivity implements OnMapReadyCallback {

    private MapFragment mapFragment;
    private ImageView mArrowImageView;
    private LinearLayout mLineNamesLinearLayout;
    private ImageView mClose;
    private LinearLayout mServicesBtn;
    private LinearLayout mServicesListLinearLayout;
    private ProgressBar mProgressBar;
    private BottomSheetBehavior mBottomSheetBehavior;
    View mBottomSheet;

    Marker markerClicked = null;
    private Activity mContext;
    private Marker mCurrentLocationMarker;
    private GoogleMap mGoogleMap;
    LineDetails_Fragment mLineFragment;
    Fragment mLineNewsFragment;
    HashMap<String, String> mStationNameWithLatLng = new HashMap<>();
    private HashMap<String, String> mReasonForDelays = new HashMap<>();

    private final String TAG = "NearbyStationDetail";
    private HashMap<String, String> mStationDetailsMap = new HashMap<>();
    private HashMap<String, String> mMarkerInfo = new HashMap<>();
    private LatLng mStationLatLng;
    private ListMultimap<String, String> mLineStationAccToDirectionMap = ArrayListMultimap.create();
    private Polyline mPolyline = null;

    int mNumberOfLines = 0;
    int countOfLinesDownloaded = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.nearby_station_detail);
        mContext = this;

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);


        ArrayList<String> lineNames = getIntent().getStringArrayListExtra(lineName);
        mStationLatLng =  getIntent().getParcelableExtra("LatLng");

        new GetRoute().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        mNumberOfLines = lineNames.size();
        for (int i=0; i<lineNames.size() ; i++) {
            String lineName;
            if (lineNames.get(i).contains(" & ")) {
                lineName = lineNames.get(i).replace(" & ","-");
            } else
                lineName = lineNames.get(i);
            new GetLineInformation().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, lineName);

        }

        String name = getIntent().getStringExtra(Constants.stationName);
        if (name.toLowerCase().contains("underground")) {
            name = name.split("Underground")[0];
        }
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(name);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mArrowImageView = (ImageView) findViewById(R.id.arrow);

        mLineNamesLinearLayout = (LinearLayout) findViewById(R.id.lineName_layout);

        mBottomSheet = findViewById(R.id.bottomSheet);
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);

        mBottomSheet.setVisibility(View.GONE);
        mClose = (ImageView) findViewById(R.id.close_id);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCloseFragment();
            }
        });

        mServicesListLinearLayout = (LinearLayout) findViewById(R.id.servicesLinearLayout);
        mServicesBtn = (LinearLayout) findViewById(R.id.services_btn);

        mServicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mServicesListLinearLayout.getChildCount() == 0) {

                    mArrowImageView.animate().rotation(90).start();

                    offsetTime = 0;
                    for (String key : mStationDetailsMap.keySet()) {
                        if (key.toLowerCase().contentEquals("car park")
                                && mStationDetailsMap.get(key).contains("yes")) {
                            addTextViewforServices(key);
                        }
                        if (key.toLowerCase().contentEquals("payphones")
                                && !mStationDetailsMap.get(key).contains("0")) {
                            addTextViewforServices(key);
                        }
                        if (key.toLowerCase().contentEquals("cash machines")
                                && !mStationDetailsMap.get(key).contains("0")) {
                            addTextViewforServices(key);
                        }
                        if (key.toLowerCase().contentEquals("waiting room")
                                && mStationDetailsMap.get(key).contains("yes")) {
                            addTextViewforServices(key);
                        }
                        if (key.toLowerCase().contentEquals("photo booths")
                                && !mStationDetailsMap.get(key).contains("0")) {
                            addTextViewforServices(key);
                        }
                        if (key.toLowerCase().contentEquals("toilets")
                                && mStationDetailsMap.get(key).contains("yes")) {
                            addTextViewforServices(key);
                        }
                        if (key.toLowerCase().contentEquals("wifi")
                                && mStationDetailsMap.get(key).contains("yes")) {
                            addTextViewforServices(key);
                        }
                        if (key.toLowerCase().contentEquals("escalators")
                                && !mStationDetailsMap.get(key).contains("0")) {
                            addTextViewforServices(key);
                        }
                        if (key.toLowerCase().contentEquals("ticket halls")
                                && !mStationDetailsMap.get(key).contains("0")) {
                            addTextViewforServices(key);
                        }
                    }
                } else {
                    mArrowImageView.animate().rotation(0).start();
                    removeServicesList();
                }


            }
        });

        TextView lineDetailsTV= (TextView) findViewById(R.id.lineDetailsTV);
        lineDetailsTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                else if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        for (int i = 0; i < lineNames.size(); i++) {
            String lineNameTemp;
            if (lineNames.get(i).contains(" & ")) {
                lineNameTemp = lineNames.get(i).replace(" & ","-");
            } else
                lineNameTemp = lineNames.get(i);

            View view = LayoutInflater.from(mContext).inflate(R.layout.line_service_layout, null);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0,12,0,0);
            view.setLayoutParams(layoutParams);
            final TextView lineNameTV = (TextView) view.findViewById(R.id.lineName);
            TextView serviceStatusTV = (TextView) view.findViewById(R.id.serviceStatus);
            new GetLineStatus().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, lineNameTemp, serviceStatusTV);

            String tempLineText1 = lineNames.get(i);
            lineNameTV.setText(tempLineText1);
            if (tempLineText1.toLowerCase().contains("piccadilly")) {
                lineNameTV.setBackgroundColor(ContextCompat.getColor(mContext, R.color.piccadilly));
            } else if (tempLineText1.toLowerCase().contains("bakerloo")) {
                lineNameTV.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bakerloo));
            } else if (tempLineText1.toLowerCase().contains("central")) {
                lineNameTV.setBackgroundColor(ContextCompat.getColor(mContext, R.color.central));
            } else if (tempLineText1.toLowerCase().contains("district")) {
                lineNameTV.setBackgroundColor(ContextCompat.getColor(mContext, R.color.district));
            } else if (tempLineText1.toLowerCase().contains("hammersmith")) {
                lineNameTV.setBackgroundColor(ContextCompat.getColor(mContext, R.color.hammersmith));
            } else if (tempLineText1.toLowerCase().contains("jubilee")) {
                lineNameTV.setBackgroundColor(ContextCompat.getColor(mContext, R.color.jubilee));
            } else if (tempLineText1.toLowerCase().contains("metropolitan")) {
                lineNameTV.setBackgroundColor(ContextCompat.getColor(mContext, R.color.metropolitan));
            } else if (tempLineText1.toLowerCase().contains("northern")) {
                lineNameTV.setBackgroundColor(ContextCompat.getColor(mContext, R.color.northern));
            } else if (tempLineText1.toLowerCase().contains("victoria")) {
                lineNameTV.setBackgroundColor(ContextCompat.getColor(mContext, R.color.victoria));
            } else if (tempLineText1.toLowerCase().contains("waterloo")) {
                lineNameTV.setBackgroundColor(ContextCompat.getColor(mContext, R.color.waterloo));
            } else if (tempLineText1.toLowerCase().contains("london overground")) {
                lineNameTV.setBackgroundColor(ContextCompat.getColor(mContext, R.color.overground));
            } else if (tempLineText1.toLowerCase().contains("tfl rail")) {
                lineNameTV.setBackgroundColor(ContextCompat.getColor(mContext, tfl));
            } else if (tempLineText1.toLowerCase().contains("dlr")) {
                lineNameTV.setBackgroundColor(ContextCompat.getColor(mContext, R.color.dlr));
            } else if (tempLineText1.toLowerCase().contains("tram")) {
                lineNameTV.setBackgroundColor(ContextCompat.getColor(mContext, R.color.tram));
            }
            mLineNamesLinearLayout.addView(view);
            lineNameTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String lineNameTemp = lineNameTV.getText().toString();
                    if (lineNameTemp.contains(" & ")) {
                        lineNameTemp = lineNameTemp.replace(" & ","-");
                    }
                    onLineNameClicked(lineNameTemp);

                }
            });
            serviceStatusTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String lineNameTemp = lineNameTV.getText().toString();
                    if (lineNameTemp.contains(" & ")) {
                        lineNameTemp = lineNameTemp.replace(" & ","-");
                    }
                    onServiceStatusClicked(lineNameTemp);
                }
            });
        }

        JSONArray mStationProperties;
        try {
            if (getIntent().getStringExtra(Constants.stationProperties) != null) {
                mStationProperties = new JSONArray(getIntent().getStringExtra(Constants.stationProperties));
                for (int i= 0; i< mStationProperties.length() ; i++) {
                    String key = ((JSONObject)mStationProperties.get(i)).getString("key");
                    String value = ((JSONObject)mStationProperties.get(i)).getString("value");
                    mStationDetailsMap.put(key, value);
                }
            } else {
                mServicesBtn.setVisibility(View.GONE);
            }


        } catch(JSONException e) {
            Log.e(TAG, e.getLocalizedMessage());
        }
    }
    static int offsetTime = 0;
    boolean isFirstDisplayedService = true;

    private void addTextViewforServices(final String key) {
            if (!isFirstDisplayedService)
                offsetTime +=20;

            isFirstDisplayedService = false;

            View view = LayoutInflater.from(mContext).inflate(R.layout.services_station_layout, null);

            final ImageView imageView = (ImageView) view.findViewById(R.id.service_image);
            final TextView textView = (TextView) view.findViewById(R.id.service_desc);
            textView.setAlpha(0);

            if (key.toLowerCase().contains("car park") )
                imageView.setImageResource(R.drawable.carpark);
            else if (key.toLowerCase().contains("escalators"))
                imageView.setImageResource(R.drawable.escalator);
            else if (key.toLowerCase().contains("toilets"))
                imageView.setImageResource(R.drawable.toilet);
            else if (key.toLowerCase().contains("photo booths"))
                imageView.setImageResource(R.drawable.photobooth);
            else if (key.toLowerCase().contains("payphone"))
                imageView.setImageResource(R.drawable.payphone);
            else if (key.toLowerCase().contains("wifi"))
                imageView.setImageResource(R.drawable.wifi);
            else if (key.toLowerCase().contains("waiting room"))
                imageView.setImageResource(R.drawable.waitingroom);
            else if (key.toLowerCase().contains("ticket halls"))
                imageView.setImageResource(R.drawable.tickethall);
            else if (key.toLowerCase().contains("cash machines"))
                imageView.setImageResource(R.drawable.cashmachine);

            Animation inFromRight = new TranslateAnimation(
                    Animation.RELATIVE_TO_PARENT, 1f,
                    Animation.RELATIVE_TO_PARENT, 0,
                    Animation.RELATIVE_TO_PARENT, 0,
                    Animation.RELATIVE_TO_PARENT, 0);
            inFromRight.setDuration(200);
            inFromRight.setStartOffset(offsetTime);


            imageView.setAnimation(inFromRight);
            mServicesListLinearLayout.addView(view);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (textView.getAlpha() == 0) {
                        textView.setText(key);

                       Animation alphaAnimation = setAnimationForServiceDescription(0, 1, textView);
                        textView.startAnimation(alphaAnimation);
                    } else {
                        Animation alphaAnimation = setAnimationForServiceDescription(1, 0, textView);
                        textView.startAnimation(alphaAnimation);
                    }
                }
            });
    }

    private void onCloseFragment() {
//        mLineNamesLinearLayout.setVisibility(View.VISIBLE);
        mBottomSheet.setVisibility(View.VISIBLE);
        mServicesBtn.setVisibility(View.VISIBLE);

        if (mLineFragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(mLineFragment).commit();
            mLineFragment = null;
        } else if (mLineNewsFragment != null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.remove(mLineNewsFragment).commit();
            mLineNewsFragment = null;
        }
        mClose.setVisibility(View.GONE);
    }

    private Animation setAnimationForServiceDescription(int fromAlpha, final int toAlpha, final TextView textView) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(fromAlpha, toAlpha);
        alphaAnimation.setInterpolator(new LinearInterpolator());
        alphaAnimation.setDuration(200);
        alphaAnimation.setFillAfter(true);
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                textView.setAlpha(toAlpha);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        return  alphaAnimation;
    }

    private void removeServicesList() {
        Animation outToRight = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0
                , Animation.RELATIVE_TO_PARENT, 1f, Animation.RELATIVE_TO_PARENT, 0
                , Animation.RELATIVE_TO_PARENT, 0);

        outToRight.setDuration(200);
        outToRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mServicesListLinearLayout.removeAllViews();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mServicesListLinearLayout.startAnimation(outToRight);
    }

    private void onLineNameClicked(String lineName) {

        mBottomSheet.setVisibility(View.GONE);

        mLineFragment = LineDetails_Fragment.init(mLineStationAccToDirectionMap
                , getIntent().getStringExtra(Constants.stationName), lineName );
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(R.id.line_fragment, mLineFragment).commit();

//        mLineNamesLinearLayout.setVisibility(View.GONE);
        mClose.setVisibility(View.VISIBLE);
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 1f,
                Animation.RELATIVE_TO_PARENT, 0);
        animation.setDuration(200);
        animation.setStartOffset(250);
        mClose.startAnimation(animation);
    }

    private void onServiceStatusClicked(String lineName) {

        mLineNewsFragment = Line_News_Fragment.init( lineName, mReasonForDelays.get(lineName));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(R.id.line_fragment, mLineNewsFragment).commit();

//        mLineNamesLinearLayout.setVisibility(View.GONE);
        mBottomSheet.setVisibility(View.GONE);
        mClose.setVisibility(View.VISIBLE);
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0,
                Animation.RELATIVE_TO_PARENT, 0, Animation.RELATIVE_TO_PARENT, 1f,
                Animation.RELATIVE_TO_PARENT, 0);
        animation.setDuration(200);
        animation.setStartOffset(250);
        mClose.startAnimation(animation);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.station_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            //Refresh map
            placeMarkerOnMap(new LatLng(Constants.latitude, Constants.longitude), "Current Location");
            if (mServicesListLinearLayout.getChildCount() != 0) {
                mArrowImageView.animate().rotation(0).start();
                removeServicesList();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mGoogleMap = googleMap;
        placeMarkerOnMap(new LatLng(Constants.latitude, Constants.longitude), "Current Location");

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.google_map_style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        final View infoWindow = getLayoutInflater().inflate(R.layout.info_window_nearby_staion, null);
        final TextView stationName = (TextView) infoWindow.findViewById(R.id.placeName);

        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {

                if (markerClicked !=null && markerClicked.isInfoWindowShown()) {
                    markerClicked.hideInfoWindow();
                }

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                Double lat = 0.0;
                Double lng = 0.0;

                for (String key :mStationNameWithLatLng.keySet()) {
                    String name = mStationNameWithLatLng.get(key);

                    if (stationName.getText().toString().contentEquals("Current Location")) {
                        lat = Constants.latitude;
                        lng = Constants.longitude;
                    }
                    else if (name.contentEquals(stationName.getText())) {
                        lat = Double.valueOf(key.split("::")[0]);
                        lng = Double.valueOf(key.split("::")[1]);
                    }
                }
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                StreetView_DialogFragment streetViewDialog = StreetView_DialogFragment.newInstance(lat, lng);

                streetViewDialog.show(getFragmentManager().beginTransaction(), "Street View Dialog");
            }
        });

        mGoogleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                markerClicked = marker;
                String name = mMarkerInfo.get(marker.getId());
                stationName.setText(name);

                return infoWindow;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
    }

    private void placeMarkerOnMap(LatLng latLng, String title) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        //Remove marker if marker is already present
        if (mCurrentLocationMarker != null) {
            mCurrentLocationMarker.remove();
        }

        mCurrentLocationMarker = mGoogleMap.addMarker(markerOptions);
        mCurrentLocationMarker.setIcon(BitmapDescriptorFactory.fromResource(marker));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));
        mMarkerInfo.put(mCurrentLocationMarker.getId(), "Current Location");

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        mBottomSheetBehavior.setPeekHeight(140);
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onBackPressed() {
        if (mLineFragment != null || mLineNewsFragment != null) {
            onCloseFragment();
        } else {
            super.onBackPressed();
        }
    }


    private class GetLineInformation extends AsyncTask<String, Void, List<List<LatLng>>> {
        String lineName;
        int colorForLine;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<List<LatLng>> doInBackground(final String... strings) {
            lineName = strings[0];
            String urlString = getUrl(lineName);
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader;
            String line;
            InputStream inputStream;
            StringBuilder json_result = new StringBuilder();

            try {
                Log.v(TAG, "line url:"+urlString);
                System.setProperty("http.keepAlive", "false");
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(2000);
                urlConnection.setConnectTimeout(2000);
                urlConnection.setRequestMethod("GET");
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                while ((line = bufferedReader.readLine()) != null) {
                    json_result.append(line);
                }

            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new GetLineInformation().execute(strings[0]);
                    }
                });
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            JSONArray lineStrings = getDataFromJSON(json_result.toString());
            List<List<LatLng>> listOfLatLngList = new ArrayList<>();

            for (int i=0; i<lineStrings.length() ; i++) {
                String points= "";
                try {
                    points = lineStrings.getString(i).substring(2, lineStrings.getString(i).length() -2);
                } catch (JSONException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
                final List<LatLng> latLngList = new ArrayList<>();
                for (int j=0; j<points.split("],").length ; j++) {
                    String latLngStr;
                    if (j == points.split("],").length-1) {
                        latLngStr =  points.split("],")[j].substring(1, points.split("],")[j].length()-1);
                    } else
                        latLngStr = points.split("],")[j].substring(1);
                    final Double lat = Double.valueOf(latLngStr.split(",")[1]);
                    final Double lng = Double.valueOf(latLngStr.split(",")[0]);
                    final LatLng latLng = new LatLng(lat, lng);
                    latLngList.add(latLng);


                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //Set markers with icons and marker info
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.station_marker)).position(latLng);
                            Marker marker= mGoogleMap.addMarker(markerOptions);

                            for (int l=0 ;l<mStations.length(); l++) {
                                try {
                                    JSONObject jsonObject = mStations.getJSONObject(l);
                                    if (String.valueOf(lat).contentEquals(jsonObject.getString("lat"))) {
                                        String name = jsonObject.getString("name");
                                        mMarkerInfo.put(marker.getId(), name);

                                    } else {
                                        if (mStationNameWithLatLng.keySet().contains(lat+"::"+lng)) {
                                            mMarkerInfo.put(marker.getId(), mStationNameWithLatLng.get(lat+"::"+lng));
                                        }
                                    }
                                }catch (JSONException e) {
                                    Log.e(TAG, e.getLocalizedMessage());
                                }
                            }
                        }
                    });
                }
                listOfLatLngList.add(latLngList);
            }
            return listOfLatLngList;
        }

        private String getUrl(String lineName) {
            String url = "https://api.tfl.gov.uk/Line/"+ lineName
                    +"/Route/Sequence/outbound?serviceTypes=regular";
            String appid = "&appid=" + Constants.app_id_tfl + "&api_key=" + Constants.app_key_tfl;
            return  url+appid;
        }

        private JSONArray getDataFromJSON(String json) {
            JSONArray lineStrings = new JSONArray();
            HashMap<String, JSONArray> naptonIdsList = new HashMap<>();

            String lineId;
            try {
                JSONObject jsonObject = new JSONObject(json);
                lineStrings = jsonObject.getJSONArray("lineStrings");
                lineId = jsonObject.getString("lineId");
                JSONArray orderedLineRoutes = jsonObject.getJSONArray("orderedLineRoutes");
                for (int i=0; i<orderedLineRoutes.length(); i++) {
                    naptonIdsList.put(((JSONObject)orderedLineRoutes.get(i)).getString("name")
                            , ((JSONObject)orderedLineRoutes.get(i)).getJSONArray("naptanIds"));
                }
                JSONArray stopPointSequences = jsonObject.getJSONArray("stopPointSequences");
                HashMap<String, String> tempMap = new HashMap<>();
                for (int i=0 ;i<stopPointSequences.length() ; i++) {
                    JSONArray stopPoint = ((JSONObject)stopPointSequences.get(i)).getJSONArray("stopPoint");
                    for (int j=0 ;j<stopPoint.length() ; j++) {
                        String id = ((JSONObject)stopPoint.get(j)).getString("id");
                       tempMap.put(id, ((JSONObject)stopPoint.get(j)).getString("name"));
                        mStationNameWithLatLng.put(((JSONObject)stopPoint.get(j)).getDouble("lat") +"::" +
                                        ((JSONObject)stopPoint.get(j)).getDouble("lon"),
                                ((JSONObject)stopPoint.get(j)).getString("name"));
                    }
                }
                for (String key : naptonIdsList.keySet()) {
                    JSONArray jsonArrayForNaptonIds = naptonIdsList.get(key);
                    for (int i = 0; i < jsonArrayForNaptonIds.length(); i++) {
                        for (String id: tempMap.keySet()) {
                            if (id.contentEquals(jsonArrayForNaptonIds.get(i).toString())) {
                                mLineStationAccToDirectionMap.put(lineId+"::"+key, tempMap.get(id));
                            }
                        }
                    }
                }
                mStations = jsonObject.getJSONArray("stations");

            } catch (JSONException e) {
                Log.e(TAG,  "error"+e.getLocalizedMessage());
            }

            return lineStrings;
        }
        JSONArray mStations = new JSONArray();

        @Override
        protected void onPostExecute(List<List<LatLng>> listOfLatLngList) {
            super.onPostExecute(listOfLatLngList);
            countOfLinesDownloaded++;
            if (countOfLinesDownloaded == mNumberOfLines && mClose.getVisibility() == View.GONE) {
                Log.v(TAG, "count::"+ countOfLinesDownloaded+":"+mNumberOfLines);
                mProgressBar.setVisibility(View.GONE);
                mBottomSheet.setVisibility(View.VISIBLE);
            }

            //Set colors according to lines
            colorForLine = ContextCompat.getColor(mContext, R.color.piccadilly);
            if (lineName.toLowerCase().contains("piccadilly")) {
                colorForLine = ContextCompat.getColor(mContext, R.color.piccadilly);
            } else if (lineName.toLowerCase().contains("bakerloo")) {
                colorForLine = ContextCompat.getColor(mContext, R.color.bakerloo);
            } else if (lineName.toLowerCase().contains("central")) {
                colorForLine = ContextCompat.getColor(mContext, R.color.central);
            } else if (lineName.toLowerCase().contains("district")) {
                colorForLine = ContextCompat.getColor(mContext, R.color.district);
            } else if (lineName.toLowerCase().contains("hammersmith")) {
                colorForLine = ContextCompat.getColor(mContext, R.color.hammersmith);
            } else if (lineName.toLowerCase().contains("jubilee")) {
                colorForLine = ContextCompat.getColor(mContext, R.color.jubilee);
            } else if (lineName.toLowerCase().contains("metropolitan")) {
                colorForLine = ContextCompat.getColor(mContext, R.color.metropolitan);
            } else if (lineName.toLowerCase().contains("northern")) {
                colorForLine = ContextCompat.getColor(mContext, R.color.northern);
            } else if (lineName.toLowerCase().contains("victoria")) {
                colorForLine = ContextCompat.getColor(mContext, R.color.victoria);
            } else if (lineName.toLowerCase().contains("waterloo")) {
                colorForLine = ContextCompat.getColor(mContext, R.color.waterloo);
            } else if (lineName.toLowerCase().contains("london-overground")) {
                colorForLine = ContextCompat.getColor(mContext, R.color.overground);
            } else if (lineName.toLowerCase().contains("tfl-rail")) {
                colorForLine = ContextCompat.getColor(mContext, R.color.tfl);
            } else if (lineName.toLowerCase().contains("dlr")) {
                colorForLine = ContextCompat.getColor(mContext, R.color.dlr);
            } else if (lineName.toLowerCase().contains("tram")) {
                colorForLine = ContextCompat.getColor(mContext, R.color.tram);
            }
            for (List<LatLng> latLngList : listOfLatLngList)
                mGoogleMap.addPolyline(new PolylineOptions().zIndex(30)
                    .addAll(latLngList).width(5).color(colorForLine).endCap(new ButtCap())
                    .startCap(new ButtCap()).geodesic(true));
        }
    }

    private class GetLineStatus extends AsyncTask<Object, Void, String>{
        String lineName;
        TextView serviceStatus;
        @Override
        protected String doInBackground(Object... strings) {
            lineName = (String) strings[0];
            serviceStatus = (TextView) strings[1];
            String urlString = "https://api.tfl.gov.uk/Line/"+lineName+"/Status?detail=true" +
                    "&appid=" + Constants.app_id_tfl + "&api_key=" + Constants.app_key_tfl;
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader;
            String line;
            InputStream inputStream;
            StringBuilder json_result = new StringBuilder();
            Log.v(TAG, urlString);

            try {
                System.setProperty("http.keepAlive", "false");
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(2000);
                urlConnection.setConnectTimeout(2000);
                urlConnection.setRequestMethod("GET");
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                while ((line = bufferedReader.readLine()) != null) {
                    json_result.append(line);
                }

            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new GetLineStatus().execute(lineName, serviceStatus);
                    }
                });
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return  getDataFromJSON(json_result.toString());
        }

        private String getDataFromJSON(String json) {
            Log.v(TAG, "json: "+json);
            ArrayList<String> statusSeverityDescription = new ArrayList<>();

            try {
                JSONArray jsonArray = new JSONArray(json);
                JSONObject  jsonObject = (JSONObject) jsonArray.get(0);
                JSONArray lineStatuses = jsonObject.getJSONArray("lineStatuses");
                JSONObject jsonObject1 = lineStatuses.getJSONObject(0);
                statusSeverityDescription.add(((JSONObject) lineStatuses.get(0)).getString("statusSeverityDescription"));

                if (jsonObject1.has("reason")) {
                    mReasonForDelays.put(lineName,jsonObject1.getString("reason"));
                }

            } catch (JSONException e) {
                Log.e(TAG, e.getLocalizedMessage());
                new GetLineStatus().execute(lineName, serviceStatus);
            }
            if (statusSeverityDescription.size() == 0) {
                this.cancel(true);
                return "";
            } else
                return statusSeverityDescription.get(0);

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            new GetLineStatus().execute(lineName, serviceStatus);
        }

        @Override
        protected void onPostExecute(String status) {
            super.onPostExecute(status);
            if (!status.toLowerCase().contains("good service")) {
                serviceStatus.setTextColor(ContextCompat.getColor(mContext, R.color.central));
            }
            serviceStatus.setText(status);
            mProgressBar.setVisibility(View.GONE);
//            isLineStatusDownloaded = true;
//            if (isLineStationsDownloaded) {
////                mLineNamesLinearLayout.setVisibility(View.VISIBLE);
//                mBottomSheet.setVisibility(View.VISIBLE);
//                mProgressBar.setVisibility(View.GONE);
//            }
        }
    }

    private class GetRoute extends AsyncTask<Void, Void, List<List<LatLng>>> {
        @Override
        protected List<List<LatLng>> doInBackground(Void... voids) {
            String urlString = "https://maps.googleapis.com/maps/api/directions/json" +
                    "?origin="+Constants.latitude+","+Constants.longitude+"&destination=" + mStationLatLng.latitude
                    +"," + mStationLatLng.longitude+"&api_key=" + GOOGLE_MAPS_DISTANCE_API;
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader;
            String line;
            InputStream inputStream;
            StringBuilder json_result = new StringBuilder();
            Log.v(TAG, urlString);

            try {
                System.setProperty("http.keepAlive", "false");
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(1000);
                urlConnection.setRequestMethod("GET");
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                while ((line = bufferedReader.readLine()) != null) {
                    json_result.append(line);
                }

            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new GetRoute().execute();
                    }
                });
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return  getDataFromJSON(json_result.toString());
        }

       private List<List<LatLng>> getDataFromJSON(String json) {
            List<List<LatLng>> location_latlng = new ArrayList<>();
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray routes = jsonObject.getJSONArray("routes");
                JSONObject jsonObject1 = routes.getJSONObject(0);
                JSONArray legs = jsonObject1.getJSONArray("legs");
                JSONArray steps = legs.getJSONObject(0).getJSONArray("steps");
                for (int i=0 ; i< steps.length(); i++) {
                    JSONObject polyline= steps.getJSONObject(i).getJSONObject("polyline");
                    String points = polyline.getString("points");
                    List<LatLng> latLngList = decodePoly(points);
                    location_latlng.add(latLngList);
                }
            }catch (JSONException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
            return location_latlng;
        }

        private List<LatLng> decodePoly(String encoded) {

            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {
                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }

            return poly;
        }

        @Override
        protected void onPostExecute(List<List<LatLng>>latLngs) {
            super.onPostExecute(latLngs);
            List<PatternItem> pattern = Arrays.asList(new Dot(), new Gap(10));
            List<LatLng> points = new ArrayList<>();
            for (int i=0 ;i< latLngs.size() ;i++) {
                for (int j=0;j<latLngs.get(i).size() ; j++) {
                    points.add(latLngs.get(i).get(j));
                }
            }
            if (mPolyline != null) {
                mPolyline.remove();
            }
            mPolyline = mGoogleMap.addPolyline(new PolylineOptions().addAll(points).color(ContextCompat.getColor(
                    mContext, R.color.route_dot)).zIndex(40).width(14).pattern(pattern));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Finish this activity if this fragment stays in background for 3 minutes
        Handler handlerForOnStop = new Handler();
        handlerForOnStop.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3 * 60 * 1000);
    }
}
