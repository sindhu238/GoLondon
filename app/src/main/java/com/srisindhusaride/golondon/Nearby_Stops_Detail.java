package com.srisindhusaride.golondon;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import com.srisindhusaride.golondon.Utils.Constants;
import com.tuyenmonkey.mkloader.MKLoader;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.srisindhusaride.golondon.R.drawable.marker;
import static com.srisindhusaride.golondon.Utils.Constants.GOOGLE_MAPS_DISTANCE_API;

/**
 * @since  19/07/17.
 */

public class Nearby_Stops_Detail extends AppCompatActivity implements OnMapReadyCallback {

    private MapFragment mapFragment;
    private BottomSheetBehavior mBottomSheetBehavior;
    private View bottomSheet;
    private Spinner mSpinner;
    private TextView mStatusTV;
    private TextView mStatusDescriptionTV;
    private RecyclerView mRecyclerView;
    private List<String> mDirectionList = new ArrayList<>();
    private String mSpinnerSelected;
    private RecyclerView.Adapter mAdapter;

    private Activity mContext;
    private Marker mCurrentLocationMarker;
    private Marker mDestLocationMarker;
    private GoogleMap mGoogleMap;

    private final String TAG = "NearbyStopsDetail";
    private HashMap<String, String> mMarkerInfo = new HashMap<>();
    private LatLng mStationLatLng;
    private Polyline mPolyline = null;
    Marker markerClicked = null;
    String mDestStationName;
    TextView viewClickedPreviously;

    private ListMultimap<String, String> mLineStationAccToDirectionMap = ArrayListMultimap.create();
    HashMap<String, String> mStationNameWithLatLng = new HashMap<>();
    private MKLoader mProgressBarForStatus;
    private MKLoader mProgressBarForLine;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.nearby_stops_detail);
        mContext = this;

        mStationLatLng =  getIntent().getParcelableExtra(Constants.latlng);
        ArrayList lineNameList = getIntent().getStringArrayListExtra(Constants.lineName);

        mProgressBarForStatus = (MKLoader) findViewById(R.id.progressBarForStatus);
        mProgressBarForLine = (MKLoader) findViewById(R.id.progressBarForLine);
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        bottomSheet = findViewById(R.id.bottomSheet);
        mStatusTV = (TextView) findViewById(R.id.serviceStatus);
        mStatusDescriptionTV = (TextView) findViewById(R.id.statusReason);
        mSpinner = (Spinner) findViewById(R.id.direction_spinner);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayout busNamesListLayout = (LinearLayout) findViewById(R.id.lineNamesLayout);

        new GetRoute().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        new GetLineInformation().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String) lineNameList.get(0));
        new GetLineStatus().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String) lineNameList.get(0));

        for (int i = 0; i < lineNameList.size(); i++) {

            LayoutInflater layoutInflater = (LayoutInflater.from(mContext));
            View view = layoutInflater.inflate(R.layout.textview_sample, null);
            final TextView textView = (TextView) view;
            textView.setText((String) lineNameList.get(i));


            if (i != 0) {
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                layoutParams.setMargins(4, 0, 0, 0);
                textView.setLayoutParams(layoutParams);
            } else {
                textView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.cardViewBG));
                textView.setTextSize(17);
                textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                viewClickedPreviously = textView;
            }

            busNamesListLayout.addView(textView);

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    textView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.colorAccent));
                    textView.setTextColor(ContextCompat.getColor(mContext, R.color.cardViewBG));
                    textView.setTextSize(17);
                    textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
                    viewClickedPreviously.setBackgroundColor(ContextCompat.getColor(mContext, R.color.cardViewBG));
                    viewClickedPreviously.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextSecondary));
                    viewClickedPreviously.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
                    viewClickedPreviously.setTextSize(14);
                    viewClickedPreviously = textView;
                    new GetLineInformation().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, textView.getText().toString());
                    new GetLineStatus().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, textView.getText().toString());
                }
            });
        }

        mDestStationName = getIntent().getStringExtra(Constants.stationName);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(mDestStationName);


        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        //Set bottom sheet height to 3/4th of screen size as default
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int screenHeight = displayMetrics.heightPixels;

        final int TOOLBAR_HEIGHT = 48;

        int requiredHeightInPX = screenHeight - TOOLBAR_HEIGHT - 600;

        ViewGroup.LayoutParams layoutParamsOfBottomSheet = bottomSheet.getLayoutParams();
        layoutParamsOfBottomSheet.height = requiredHeightInPX;
        bottomSheet.setLayoutParams(layoutParamsOfBottomSheet);

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    else if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }

                else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    if (mapFragment.getView() != null)
                        mapFragment.getView().setEnabled(true);
                    mGoogleMap.getUiSettings().setAllGesturesEnabled(true);

                } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    if (mapFragment.getView() != null)
                        mapFragment.getView().setEnabled(false);
                    mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        mRecyclerView.setLayoutManager(linearLayoutManager);

        //As recycler view is in nested scroll view it does'nt scroll smoothly.
        //So nested scrolling is disabled
        mRecyclerView.setNestedScrollingEnabled(false);

        mAdapter = new LineDetails_RecyclerViewAdapter(mLineStationAccToDirectionMap, mSpinnerSelected);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        /*
         * When Bottom Sheet is expanded and if user clicks on area outside bottom sheet then the
         * bottom sheet is collapsed automatically and vice versa
         */
        boolean isClick = true;
        float mDownX = 0;
        float mDownY = 0;
        final float SCROLL_THRESHOLD = 10;

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getX();
                mDownY = ev.getY();
                isClick = true;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (isClick) {
                    Rect rect = new Rect();
                    bottomSheet.getGlobalVisibleRect(rect);
                    if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        if (!rect.contains((int)ev.getRawX(), (int)ev.getRawY())) {
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        }
                    } else if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                        if (rect.contains((int)ev.getRawX(), (int)ev.getRawY())) {
                            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isClick && (Math.abs(mDownX - ev.getX()) > SCROLL_THRESHOLD || Math.abs(mDownY - ev.getY()) > SCROLL_THRESHOLD)) {
                    isClick = false;
                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
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

                if (stationName.getText().toString().contentEquals("Current Location")) {
                    lat = Constants.latitude;
                    lng = Constants.longitude;
                }
                else if (stationName.getText().toString().contentEquals(mDestStationName)) {
                    lat = mStationLatLng.latitude;
                    lng = mStationLatLng.longitude;
                }
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
        } else if (mDestLocationMarker != null) {
            mDestLocationMarker.remove();
        }

        mCurrentLocationMarker = mGoogleMap.addMarker(markerOptions);
        mMarkerInfo.put(mCurrentLocationMarker.getId(), "Current Location");

        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(mStationLatLng);
        markerOptions1.title(getIntent().getStringExtra(Constants.stationName));
        mDestLocationMarker = mGoogleMap.addMarker(markerOptions1);
        mMarkerInfo.put(mDestLocationMarker.getId(), mDestStationName);

        mCurrentLocationMarker.setIcon(BitmapDescriptorFactory.fromResource(marker));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));

    }

    @Override
    protected void onStart() {
        super.onStart();
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
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

    private class GetLineInformation extends AsyncTask<String, Void, Void> {
        String lineName;
        ArrayAdapter<String> spinnerAdapter;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBarForLine.setVisibility(View.VISIBLE);

            spinnerAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, mDirectionList);

            if (mDirectionList.size() > 0) {
                mDirectionList.clear();
                mSpinner.setAdapter(spinnerAdapter);
            }

            if (mLineStationAccToDirectionMap.size() > 0) {
                mLineStationAccToDirectionMap.clear();
                mRecyclerView.setAdapter(mAdapter);
            }

        }

        private String getUrl(String lineName) {
            String url = "https://api.tfl.gov.uk/Line/"+ lineName
                    +"/Route/Sequence/all?";
            String appid = "appid=" + Constants.app_id_tfl + "&api_key=" + Constants.app_key_tfl;
            return  url+appid;
        }

        @Override
        protected Void doInBackground(final String... strings) {
            lineName = strings[0];
            String urlString = getUrl(lineName);
            HttpURLConnection urlConnection = null;
            BufferedReader bufferedReader;
            String line;
            InputStream inputStream;
            StringBuilder json_result = new StringBuilder();

            try {
                Log.v(TAG, urlString);
                System.setProperty("http.keepAlive", "false");
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(4000);
                urlConnection.setConnectTimeout(4000);
                urlConnection.setRequestMethod("GET");
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                while ((line = bufferedReader.readLine()) != null) {
                    json_result.append(line);
                }

            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());

                this.cancel(true);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            getDataFromJSON(json_result.toString());
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new GetLineInformation().execute(lineName);
                }
            });
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
            } catch (JSONException e) {
                Log.e(TAG,  "error"+e.getLocalizedMessage());
            }
            return lineStrings;
        }

        @Override
        protected void onPostExecute(Void avoid) {
            super.onPostExecute(avoid);

            Set<String> directionSet = new HashSet<>();
            for (String key: mLineStationAccToDirectionMap.keySet()) {
                if (key.toLowerCase().contains(lineName.toLowerCase())) {
                    directionSet.add(key.split("::")[1]);
                }
            }

            //Copy data from set to list
            for (String set: directionSet) {
                String newString = set.replace("&harr;","-") +"     ";
                mDirectionList.add(newString);
            }
            if (mDirectionList.size() > 0)
                mSpinnerSelected = mDirectionList.get(0);

            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinner.setAdapter(spinnerAdapter);
            mProgressBarForLine.setVisibility(View.GONE);

            mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
                    mSpinnerSelected = adapterView.getItemAtPosition(pos).toString();
                    mAdapter = new LineDetails_RecyclerViewAdapter(mLineStationAccToDirectionMap, mSpinnerSelected);
                    mRecyclerView.setAdapter(mAdapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });

        }
    }

    private class GetLineStatus extends AsyncTask<Object, Void, String>{
        String lineName;
        String reason = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBarForStatus.setVisibility(View.VISIBLE);
            mStatusTV.setText("");
        }

        @Override
        protected String doInBackground(Object... strings) {
            lineName = (String) strings[0];
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
                        new GetLineStatus().execute(lineName);
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
                    reason = jsonObject1.getString("reason");
                }

            } catch (JSONException e) {
                Log.e(TAG, e.getLocalizedMessage());
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new GetLineStatus().execute(lineName);
                    }
                });
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
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    new GetLineStatus().execute(lineName);
                }
            });
        }

        @Override
        protected void onPostExecute(String status) {
            super.onPostExecute(status);
            mProgressBarForStatus.setVisibility(View.GONE);
            mStatusTV.setText(status);
            if (status.contains("Good")) {
                mStatusTV.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.good_status)
                        , null, null, null);
                mStatusDescriptionTV.setVisibility(View.GONE);
            } else
                mStatusTV.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(mContext, R.drawable.bad_status)
                        , null, null, null);            if (!reason.equals("")) {
                mStatusDescriptionTV.setText(reason);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

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
