//package com.srisindhusaride.golondon;
//
//import android.content.Context;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.annotation.Nullable;
//import androidx.core.app.Fragment;
//import android.support.v7.widget.DefaultItemAnimator;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.AlphaAnimation;
//import android.view.animation.Animation;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.srisindhusaride.golondon.DI.Component.DaggerNearbyStationsAndStopsComponent;
//import com.srisindhusaride.golondon.DI.Component.NearbyStationsAndStopsComponent;
//import com.srisindhusaride.golondon.DI.Module.NearbyStationsAndStopsModule;
//import com.srisindhusaride.golondon.Model.StationArrivalPOJO;
//import com.srisindhusaride.golondon.Utils.Constants;
//import com.srisindhusaride.golondon.Utils.InternetConnectivityReceiver;
//import com.google.android.gms.maps.model.LatLng;
//import com.tuyenmonkey.mkloader.MKLoader;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import javax.inject.Inject;
//
//import static com.srisindhusaride.golondon.Utils.Constants.latitude;
//import static com.srisindhusaride.golondon.Utils.Constants.longitude;
//
///**
// * @since 14/02/17.
// */
//
//public class Nearby_ViewPager_Stations extends Fragment {
//
//    private View mRootView = null;
//    static RecyclerView mRecyclerView;
//    MKLoader mProgressBar;
//    private TextView mNoStations_TV;
//
//    private Nearby_Stations_Adapter mAdapter;
//    private Context mContext;
//    Handler mHandler;
//    Handler mHandlerForOnStop;
//    private ArrayList<ArrayList<StationArrivalPOJO>> mStationDetailsList = new ArrayList<>();
//    private ArrayList<StationArrivalPOJO> mStationPOJOList = new ArrayList<>();
//    private HashMap<String, Double> timeToWalkToStation = new HashMap<>();
//    private HashMap<String, JSONArray> mStationProperties = new HashMap<>();
//    private HashMap<String, LatLng> mStationLatLng = new HashMap<>();
//
//    boolean resumed = true;
//
//    private static final String TAG = "NearbyViewPagerStations";
//    private static final int REFRESH_TIME = 60;
//    int mRadiusToSet = 1000;
//
//    private long prevTimeInMilliSeconds;
//    private long currentTimeInMilliSeconds;
//    boolean no_tfl_service_available = false;
//    boolean no_stations_nearby = false;
//
//    //1 min = 60000 ms is the minimum time for every refresh
//    private final int minTimeDifferenceToRefresh = 60000;
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        mContext = context;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        if (mRootView == null) {
//            mRootView = inflater.inflate(R.layout.nearby_viewpager_stations, container, false);
//            mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
//            mProgressBar = (MKLoader) mRootView.findViewById(R.id.progressBar);
//            mNoStations_TV = (TextView) mRootView.findViewById(R.id.no_nearby_station_text);
//        }
//
//        mNoStations_TV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mNoStations_TV.getText().toString().contentEquals(getString(R.string.no_nearby_station_1km))) {
//                    if (InternetConnectivityReceiver.isConnected()) {
//                        mRadiusToSet = 2000;
//                        new GetAllStationsAndStopsAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mRadiusToSet);
//                    } else {
//                        if (Constants.mainLayout != null)
//                            InternetConnectivityReceiver.displaySnackBar(Constants.mainLayout);
//                    }
//                }
//            }
//        });
//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        mAdapter = new Nearby_Stations_Adapter(mStationPOJOList, mStationDetailsList);
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerView.setAdapter(mAdapter);
//
//        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    AlphaAnimation toOne = new AlphaAnimation(0f, 1f);
//                    toOne.setAnimationListener(new Animation.AnimationListener() {
//                        @Override
//                        public void onAnimationStart(Animation animation) {
//                            MainActivity.mFavorites_fab.setAlpha(1f);
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animation animation) {
//
//                        }
//                    });
//                    toOne.setDuration(200);
//                    toOne.setFillAfter(true);
//                    MainActivity.mFavorites_fab.startAnimation(toOne);
//                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
//                    if (MainActivity.mFavorites_fab.getAlpha() == 1.0) {
//                        AlphaAnimation toZero = new AlphaAnimation(1f, 0f);
//                        toZero.setDuration(200);
//                        toZero.setFillAfter(true);
//                        toZero.setAnimationListener(new Animation.AnimationListener() {
//                            @Override
//                            public void onAnimationStart(Animation animation) {
//                                MainActivity.mFavorites_fab.setAlpha(0f);
//                            }
//
//                            @Override
//                            public void onAnimationEnd(Animation animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationRepeat(Animation animation) {
//
//                            }
//                        });
//                        MainActivity.mFavorites_fab.startAnimation(toZero);
//                    }
//
//                }
//                return false;
//            }
//        });
//
//
//        return mRootView;
//    }
//
//    //Gets the url for getting all details of stations and stops available
//    private String getUrl(int radius) {
//
//        String baseUrl = "https://api.tfl.gov.uk/Stoppoint?";
//        String types = "NaptanMetroStation";
//        return baseUrl + "lat=" + latitude + "&lon=" + longitude + "&stoptypes=" + types + "&radius="
//                + radius + "&useStopPointHierarchy=true&categories=facility&app_id=" + Constants.app_id_tfl + "&app_key=" + Constants.app_key_tfl;
//
//    }
//
//    private String getUrlForStationDetails(String naptonId) {
//        String baseUrl = "https://api.tfl.gov.uk/StopPoint/";
//        String appid = "?appid=" + Constants.app_id_tfl + "&api_key=" + Constants.app_key_tfl;
//
//        String url = baseUrl + naptonId + "/Arrivals" + appid;
//        Log.v(TAG,"station url: "+ url);
//        return url;
//    }
//
//    int count = 0;
//
//    private class GetAllStationsAndStopsAsyncTask extends AsyncTask<Integer, Void, Void> {
//
//
//        private ArrayList<HashMap<String,String>> mStationNaptonIDsWithStationNames = new ArrayList<>();
//        private ArrayList<String> napton_id = new ArrayList<>();
//        int radius;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            no_stations_nearby = false;
//            no_tfl_service_available = false;
//            mNoStations_TV.setVisibility(View.GONE);
//            mProgressBar.setVisibility(View.VISIBLE);
//                mStationPOJOList.clear();
//                if (getActivity() != null) {
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mAdapter.notifyDataSetChanged();
//                        }
//                    });
//            }
//        }
//
//        @Override
//        protected Void doInBackground(final Integer... rad) {
//            radius = rad[0];
//            String urlString = getUrl(radius);
//            Log.v(TAG, "url:" +urlString);
//
//            HttpURLConnection urlConnection = null;
//
//            BufferedReader bufferedReader;
//            String line;
//            InputStream inputStream;
//            StringBuilder json_result = new StringBuilder();
//            try {
//                System.setProperty("http.keepAlive", "false");
//
//                    URL url = new URL(urlString);
//
//                    long urlOpenConnecionStartTime = System.currentTimeMillis();
//
//                    urlConnection = (HttpURLConnection) url.openConnection();
//                    Log.v(TAG, "time taken for open connetion = " + (System.currentTimeMillis() - urlOpenConnecionStartTime));
//                    urlConnection.setReadTimeout(1000);
//                    urlConnection.setConnectTimeout(2000);
//                    urlConnection.setRequestMethod("GET");
//                    long urlConnecionStartTime = System.currentTimeMillis();
//                    urlConnection.connect();
//                    Log.v(TAG, "time taken for connecting connetion = "+ (System.currentTimeMillis() - urlConnecionStartTime));
//
//                    long actualCallStartTime = System.currentTimeMillis();
//                    inputStream = new BufferedInputStream(urlConnection.getInputStream());
//                    Log.v(TAG, "time taken for input connetion = " + (System.currentTimeMillis() - actualCallStartTime));
//                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//
//                    while ((line = bufferedReader.readLine()) != null) {
//                        json_result.append(line);
//                    }
//                    getDataFromJSON(json_result.toString());
//
//            } catch (Exception e) {
//                Log.e(TAG, e.getLocalizedMessage());
//                if (e.getLocalizedMessage().contains(urlString)) {
//                    no_tfl_service_available = true;
//                } else {
//                    if (getActivity()!= null)
//                        getActivity().runOnUiThread(new Runnable() {
//                            public void run() {
//                                if (InternetConnectivityReceiver.isConnected()) {
//                                    count ++;
//                                    if (count < 15)
//                                        new GetAllStationsAndStopsAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, radius);
//                                    else
//                                        Toast.makeText(mContext, "There is some problem with data loading. Please try again later",
//                                                Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                }
//
//            } finally {
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//            }
//            return null;
//        }
//
//        private void getDataFromJSON(String json) {
//            try {
//                String commonName = "";
//
//                JSONObject jsonObject = new JSONObject(json);
//                JSONArray stopPoints = jsonObject.getJSONArray("stopPoints");
//                if (stopPoints.length() == 0) {
//                    no_stations_nearby = true;
//                } else
//                if (getActivity() != null)
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mProgressBar.setVisibility(View.VISIBLE);
//                        }
//                    });
//                for (int i = 0; i < stopPoints.length() && i < 20; i++) {
//                    JSONArray modes = ((JSONObject) stopPoints.get(i)).getJSONArray("modes");
//
//                    if (modes.length() > 0) {
//                        String stationNaptanId = ((JSONObject) stopPoints.get(i)).getString("naptanId");
//                        commonName = ((JSONObject) stopPoints.get(i)).getString("commonName");
//                        Double distance = ((JSONObject) stopPoints.get(i)).getDouble("distance");
//                        Double lat = ((JSONObject) stopPoints.get(i)).getDouble("lat");
//                        Double lng = ((JSONObject) stopPoints.get(i)).getDouble("lon");
//                        LatLng latLng = new LatLng(lat, lng);
//                        JSONArray commonProperties = ((JSONObject) stopPoints.get(i)).getJSONArray("additionalProperties");
//                        timeToWalkToStation.put(commonName, distance);
//                        mStationProperties.put(commonName, commonProperties);
//                        mStationLatLng.put(commonName, latLng);
//                        JSONArray lineModeGroups = ((JSONObject) stopPoints.get(i)).getJSONArray("lineModeGroups");
//
//                        ArrayList<String> lineNamesList = new ArrayList<>();
//                        for (int a = 0 ; a < lineModeGroups.length() ; a++) {
//                            JSONObject lineObjects = lineModeGroups.getJSONObject(a);
//                            String modeName = lineObjects.getString("modeName");
//                            if (modeName.contentEquals("tube") || modeName.contentEquals("dlr") || modeName.contentEquals("overground")) {
//                                JSONArray linesArray = lineObjects.getJSONArray("lineIdentifier");
//                                for (int b = 0; b < linesArray.length() ; b++) {
//                                    lineNamesList.add((String) linesArray.get(b));
//                                }
//                            }
//                        }
//
//                        for (int j = 0; j < modes.length(); j++) {
//                            if (modes.get(j).toString().contains("tube") || modes.get(j).toString().contains("dlr")
//                                    || modes.get(j).toString().contains("overground")) {
//                                HashMap<String, String> tempMap = new HashMap<>();
//                                tempMap.put(stationNaptanId, commonName);
//                                mStationNaptonIDsWithStationNames.add(tempMap);
//                                napton_id.add(stationNaptanId);
//                            }
//                        }
//
//                        final StationArrivalPOJO stationArrivalPOJO = new StationArrivalPOJO(commonName, (int) Math.round(distance), latLng,
//                                stationNaptanId, lineNamesList);
//
//                        mStationPOJOList.add(stationArrivalPOJO);
//
//                        if (getActivity() != null)
//                            getActivity().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    mProgressBar.setVisibility(View.GONE);
//                                    mRecyclerView.setAdapter(mAdapter);
//                                }
//                            });
//                    }
//                }
//                if (napton_id.size() > 0) {
//                    if (InternetConnectivityReceiver.isConnected())
//                        new GetStationPlatformDetails().executeOnExecutor(THREAD_POOL_EXECUTOR, napton_id, mStationNaptonIDsWithStationNames);
//                } else {
//                    if (getActivity() != null)
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                mProgressBar.setVisibility(View.GONE);
//                                if (radius == 1000)
//                                    mNoStations_TV.setText(R.string.no_nearby_station_1km);
//                                else if (radius == 2000)
//                                    mNoStations_TV.setText(R.string.no_nearby_station_2km);
//                                mNoStations_TV.setVisibility(View.VISIBLE);
//
//                            }
//                        });
//                }
//            } catch (JSONException e) {
//                Log.e(TAG, e.getLocalizedMessage());
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            if (no_tfl_service_available) {
//                mProgressBar.setVisibility(View.GONE);
//                mNoStations_TV.setText(R.string.notflserivceavailable);
//                mNoStations_TV.setVisibility(View.VISIBLE);
//                if (MainActivity.menuItem != null && MainActivity.menuItem.getActionView() != null) {
//                    MainActivity.menuItem.getActionView().clearAnimation();
//                    MainActivity.menuItem.setActionView(null);
//                }
//            } else if (no_stations_nearby) {
//                if (radius == 1000)
//                    mNoStations_TV.setText(R.string.no_nearby_station_1km);
//                else if (radius == 2000)
//                    mNoStations_TV.setText(R.string.no_nearby_station_2km);
//                mNoStations_TV.setVisibility(View.VISIBLE);
//                mNoStations_TV.setVisibility(View.VISIBLE);
//                mProgressBar.setVisibility(View.GONE);
//
//                if (MainActivity.menuItem != null && MainActivity.menuItem.getActionView() != null) {
//                    MainActivity.menuItem.getActionView().clearAnimation();
//                    MainActivity.menuItem.setActionView(null);
//                }
//            }
//        }
//    }
//
//    public class GetStationPlatformDetails extends AsyncTask<Object, Void, Void> {
//        ArrayList<HashMap<String, String>> naptonIdWithStationName;
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            mStationDetailsList.clear();
//            if (getActivity() != null) {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mAdapter.notifyDataSetChanged();
//                    }
//                });
//            }
//        }
//
//        @SuppressWarnings("unchecked")
//        @Override
//        protected Void doInBackground(Object... arrayLists) {
//            ArrayList<String> naptonId = (ArrayList<String>) arrayLists[0];
//            naptonIdWithStationName = (ArrayList<HashMap<String, String>>) arrayLists[1];
//            int length;
//            if (naptonId.size() < 20)
//                 length = naptonId.size();
//            else
//                length = 20;
//
//            for (int i=0; i< length ;i++) {
//                String id = naptonId.get(i);
//                Log.v(TAG, "station details" + id);
//                String urlString = getUrlForStationDetails(id);
//                HttpURLConnection urlConnection = null;
//                BufferedReader bufferedReader;
//                String line;
//                InputStream inputStream;
//                StringBuilder json_result = new StringBuilder();
//
//
//                try {
//                    if (InternetConnectivityReceiver.isConnected()) {
//                        URL url = new URL(urlString);
//                        Log.v(TAG, urlString);
//                        System.setProperty("http.keepAlive", "false");
//                        urlConnection = (HttpURLConnection) url.openConnection();
//                        urlConnection.setReadTimeout(2000);
//                        urlConnection.setConnectTimeout(2000);
//                        urlConnection.setRequestMethod("GET");
//                        inputStream = new BufferedInputStream(urlConnection.getInputStream());
//                        bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                        while ((line = bufferedReader.readLine()) != null) {
//                            json_result.append(line);
//                        }
//                    }
//
//                }
//                catch (Exception e) {
//                    Log.e(TAG, e.getLocalizedMessage());
//                    i -= 1;
//
//                } finally {
//                    if (urlConnection != null) {
//                        urlConnection.disconnect();
//                    }
//                }
//                getStationDataFromJSON(json_result.toString(), id, i);
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//
//            if (MainActivity.menuItem != null && MainActivity.menuItem.getActionView() != null) {
//                MainActivity.menuItem.getActionView().clearAnimation();
//                MainActivity.menuItem.setActionView(null);
//            }
//        }
//
//        @Inject
//        StationArrivalPOJO stationPojo;
//
//        private void getStationDataFromJSON(String json, String naptonId, int row) {
//            ArrayList<StationArrivalPOJO> groupPojo = new ArrayList<>();
//            try {
//
//                JSONArray jsonArray = new JSONArray(json);
//                if (jsonArray.length() > 0) {
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                        String stationName = jsonObject.getString("stationName");
//                        String lineName = jsonObject.getString("lineName");
//                        String platformName = jsonObject.getString("platformName");
//                        String destinationName;
//                        if (jsonObject.has("destinationName"))
//                            destinationName = jsonObject.getString("destinationName");
//                        else
//                            destinationName = "";
//                        int timeToStation = jsonObject.getInt("timeToStation");
//                        String currentLocation = jsonObject.getString("currentLocation");
//                        String towards = jsonObject.getString("towards");
//                        String naptanId = jsonObject.getString("naptanId");
//
//                        long timeToWalkToStationInMin = Math.round(timeToWalkToStation.get(stationName) / 60);
//
//                        NearbyStationsAndStopsComponent component = DaggerNearbyStationsAndStopsComponent
//                                .builder().nearbyStationsAndStopsModule(new NearbyStationsAndStopsModule(stationName, lineName, platformName,
//                                        destinationName, timeToStation, currentLocation, towards
//                                        , timeToWalkToStationInMin, mStationProperties, mStationLatLng, naptanId))
//                                .build();
//                        component.inject(this);
//
//                        groupPojo.add(stationPojo);
//                    }
//                    //Checks if tfl is giving data about arrival predictions of the particular station.
//                    mStationDetailsList.add(groupPojo);
//                } else {
//                    groupPojo.add(new StationArrivalPOJO(naptonIdWithStationName.get(row).get(naptonId), null, null, null, 0, null, null, 0, mStationProperties, mStationLatLng, naptonId));
//                    mStationDetailsList.add(groupPojo);
//                }
//                    if (getActivity() != null)
//                        getActivity().runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (mStationDetailsList.size() == 2 || mStationDetailsList.size() == 3)
//                                    mRecyclerView.setAdapter(mAdapter);
//                                else
//                                    mAdapter.notifyItemChanged(mStationDetailsList.size()-1);
//                                // mAdapter.notifyDataSetChanged();
//                                //mRecyclerView.setAdapter(mAdapter);
//                            }
//                        });
//
//
//
//            } catch (JSONException e) {
//                Log.e(TAG, e.getLocalizedMessage());
//            }
//
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        count = 0;
//        currentTimeInMilliSeconds = System.currentTimeMillis();
//
//        // Close handler if the fragment goes to background
//        if (mHandler != null)
//            mHandler.removeCallbacksAndMessages(null);
//
//        mHandler = new Handler();
//        resumed = true;
//
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (InternetConnectivityReceiver.isConnected()) {
//                    if (Constants.isAutoCompleteCalledTube || Constants.isRefreshCalledTube ||
//                            (currentTimeInMilliSeconds - prevTimeInMilliSeconds > minTimeDifferenceToRefresh)) {
//                        new GetAllStationsAndStopsAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mRadiusToSet);
//                        prevTimeInMilliSeconds = currentTimeInMilliSeconds;
//                        Constants.isAutoCompleteCalledTube = false;
//                        Constants.isRefreshCalledTube = false;
//                    }
//                    currentTimeInMilliSeconds = 0;
//                    mHandler.postDelayed(this, REFRESH_TIME * 1000);
//                }
//            }
//        }, 1000);
//
//        if (mHandlerForOnStop != null) {
//            mHandlerForOnStop.removeCallbacks(null);
//        }
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        mRadiusToSet = 1000;
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        Log.v(TAG, "on stop called");
//        resumed = false;
//
//        // Close handler if the fragment goes to background
//        if (mHandler != null)
//            mHandler.removeCallbacksAndMessages(null);
//
//        // Finish this activity if this fragment stays in background for 3 minutes
//        mHandlerForOnStop = new Handler();
//            mHandlerForOnStop.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (!resumed) {
//                    if (getActivity() != null)
//                        getActivity().finish();
//                }
//            }
//        }, 3 * 60 * 1000);
//    }
//
//    /**
//     *
//     * @param isVisibleToUser Sets to true when the fragment is visible to the user
//     *
//     * This functions checks if the fragment is visible to user and if the fragment is not loaded
//     * before then, GetAllStationsAndStopsAsyncTask().execute() gets called immediately when user
//     * clicks of this tab
//     */
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//
////        if (isVisibleToUser) {
////            Log.v(TAG, "is visi");
////            new GetAllStationsAndStopsAsyncTask().execute();
////        }
//    }
//}
