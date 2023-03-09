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
//import com.google.android.gms.maps.model.LatLng;
//import com.srisindhusaride.golondon.Model.BusArrivalPOJO;
//import com.srisindhusaride.golondon.Utils.Constants;
//import com.srisindhusaride.golondon.Utils.InternetConnectivityReceiver;
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
//import java.util.List;
//
///**
// * @since 14/02/17.
// */
//
//public class Nearby_Viewpager_Stops extends Fragment {
//
//
//    private final String TAG = "Nearby Stops";
//
//    //The main view that holds the fragment view
//    View mRootview = null;
//
//    //Recycler view that displays all the bus stops and their details
//    static RecyclerView mRecyclerView;
//
//    //Progress bar that shows if the data is still getting downloaded
//    MKLoader mProgressBar;
//
//    //It holds the context of the fragment
//    Context mContext;
//
//
//    //Text view that displays message saying that there are no stops nearby
//    TextView mNoStopsTV;
//
//    //Recycler view adapter that handles the data
//    Nearby_Stops_Adapter mStops_adapter;
//
//    //List containing arrival details of a particular stop
//    List<BusArrivalPOJO> mBusArrivalPOJOList = new ArrayList<>();
//    List<List<BusArrivalPOJO>> mBusArrivalDetailsPOJOList = new ArrayList<>();
//
//    LinearLayoutManager mLayoutManager;
//
//    Handler mHandler;
//    Handler mHandlerForOnStop;
//
//    boolean isResumed = true;
//    private static final int REFRESH_TIME = 60;
//    int radiusToSet = 1000;
//
//    boolean isNoStops_available_nearby = false;
//    boolean no_tfl_service_available = false;
//
//    private long prevTimeInMilliSeconds;
//    private long currentTimeInMilliSeconds;
//
//    //1 min = 60000 ms is the minimum time for every refresh
//    private final int minTimeDifferenceToRefresh = 60000;
//
//    /**
//     * If GetArrivals class is called for the first time then the variable
//     * {@link #mBusArrivalPOJOList} is to be cleared. So the data will be loaded freshly for every
//     * refresh that happens every one minute
//     */
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
//
//        if (mRootview == null) {
//            mRootview = inflater.inflate(R.layout.nearby_viewpager_stops, container, false);
//            mRecyclerView = (RecyclerView) mRootview.findViewById(R.id.recycler_view);
//            mProgressBar = (MKLoader) mRootview.findViewById(R.id.progressBar);
//            mNoStopsTV = (TextView) mRootview.findViewById(R.id.no_nearby_stops_text);
//        }
//
//        mNoStopsTV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mNoStopsTV.getText().toString().contentEquals(getString(R.string.no_nearby_stops_1km))) {
//                    if (InternetConnectivityReceiver.isConnected()) {
//                        radiusToSet = 2000;
//                        new GetNearbyStops().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, radiusToSet);
//                    } else {
//                        if (Constants.mainLayout != null)
//                            InternetConnectivityReceiver.displaySnackBar(Constants.mainLayout);
//                    }
//                }
//            }
//        });
//
//        mLayoutManager = new LinearLayoutManager(mContext);
//        mRecyclerView.setLayoutManager(mLayoutManager);
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
//        return mRootview;
//    }
//
//    int count = 0;
//
//    private class GetNearbyStops extends AsyncTask<Integer, Void, Void> {
//        int radius;
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            isNoStops_available_nearby = false;
//            no_tfl_service_available = false;
//            mNoStopsTV.setVisibility(View.GONE);
//            mProgressBar.setVisibility(View.VISIBLE);
//
//            mBusArrivalPOJOList.clear();
//            pos = mLayoutManager.findFirstVisibleItemPosition();
//            mStops_adapter.notifyDataSetChanged();
//        }
//
//        @Override
//        protected Void doInBackground(Integer... rad) {
//            radius = rad[0];
//            String urlString = "https://api.tfl.gov.uk/StopPoint?radius="+radius+"&stopTypes=NaptanBusCoachStation,NaptanPublicBusCoachTram&useStopPointHierarchy=true" +
//                    "&modes=bus&lat="+ Constants.latitude+"&lon="+Constants.longitude+"&categories=none&app_id=" + Constants.app_id_tfl + "&app_key=" + Constants.app_key_tfl;
//
//            Log.v(TAG, "url:" +urlString);
//            HttpURLConnection urlConnection = null;
//
//            BufferedReader bufferedReader;
//            String line;
//            InputStream inputStream;
//            StringBuilder json_result = new StringBuilder();
//            try {
//                System.setProperty("http.keepAlive", "false");
//                URL url = new URL(urlString);
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setReadTimeout(5000);
//                urlConnection.setConnectTimeout(2000);
//                urlConnection.setRequestMethod("GET");
//                urlConnection.connect();
//
//                inputStream = new BufferedInputStream(urlConnection.getInputStream());
//                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//
//                while ((line = bufferedReader.readLine()) != null) {
//                    json_result.append(line);
//                }
//                getDataFromJSON(json_result.toString());
//
//            } catch (Exception e) {
//                Log.e(TAG, e.getMessage());
//
//                if (e.getLocalizedMessage().contains(urlString)) {
//                    no_tfl_service_available = true;
//                }else {
//                    if (getActivity()!=null)
//                        getActivity().runOnUiThread(new Runnable() {
//                            public void run() {
//                                if (InternetConnectivityReceiver.isConnected()) {
//                                    count ++;
//                                    if (count < 15)
//                                        new GetNearbyStops().execute(radius);
//                                    else
//                                        Toast.makeText(mContext, "There is some problem with data loading. Please try" +
//                                                "again later" , Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//                }
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
//                Log.v(TAG, "json: "+ json);
//                JSONObject jsonObject = new JSONObject(json);
//                if (jsonObject.has("stopPoints")) {
//                    JSONArray stopPoints = jsonObject.getJSONArray("stopPoints");
//                    int lengthOfStopsList;
//
//                    if (stopPoints.length() == 0) {
//                        isNoStops_available_nearby = true;
//                    } else {
//
//                        //To get stops points only within 20
//                        if (stopPoints.length() <= 20 && stopPoints.length() != 0) {
//                            lengthOfStopsList = stopPoints.length();
//                        } else if (stopPoints.length() == 0) {
//                            lengthOfStopsList = 0;
//                            isNoStops_available_nearby = true;
//                        } else {
//                            lengthOfStopsList = 20;
//                        }
//
//
//                        for (int i = 0; i< lengthOfStopsList; i++) {
//                            JSONObject jsonObject1 = stopPoints.getJSONObject(i);
//                            int distance = Math.round(jsonObject1.getInt("distance") / 60);
//                            Double lat = jsonObject1.getDouble("lat");
//                            Double lon = jsonObject1.getDouble("lon");
//                            String platformName;
//                            if (jsonObject1.has("stopLetter"))
//                                platformName = jsonObject1.getString("stopLetter");
//                            else
//                                platformName = null;
//                            if (platformName != null && platformName.contains("->")) {
//                                platformName = platformName.replace("->","");
//                            }
//
//                            String commonName = jsonObject1.getString("commonName");
//                            JSONArray lineGroup = jsonObject1.getJSONArray("lineGroup");
//
//                            JSONArray lineModeGroups = jsonObject1.getJSONArray("lineModeGroups");
//                            ArrayList<String> busNamesList = new ArrayList<>();
//                            for (int a = 0; a < lineModeGroups.length(); a++) {
//                                JSONObject jsonObject2 = lineModeGroups.getJSONObject(a);
//                                String modeName = jsonObject2.getString("modeName");
//                                if (modeName.contentEquals("bus")) {
//                                    JSONArray lineIdentifier = jsonObject2.getJSONArray("lineIdentifier");
//                                    for (int b = 0; b < lineIdentifier.length(); b++) {
//                                        busNamesList.add((String) lineIdentifier.get(b));
//                                    }
//                                    if (InternetConnectivityReceiver.isConnected())
//                                        new GetBusArrivals().execute(lineGroup, new LatLng(lat, lon), distance, commonName, busNamesList);
//                                }
//                            }
//                            String naptanID;
//
//                            for (int j = 0; j < lineGroup.length(); j++) {
//                                JSONObject jsonObject2 = lineGroup.getJSONObject(j);
//                                if (jsonObject2.has("naptanIdReference")) {
//                                    naptanID = jsonObject2.getString("naptanIdReference");
//                                    Log.v(TAG, "latlng::" + commonName +":" +naptanID);
//                                    BusArrivalPOJO busArrivalPOJO = new
//                                            BusArrivalPOJO(commonName, distance, new LatLng(lat, lon), naptanID, platformName);
//                                    mBusArrivalPOJOList.add(busArrivalPOJO);
//                                }
//                            }
//                        }
//                    }
//                }
//            } catch (JSONException e) {
//                Log.e(TAG, e.getLocalizedMessage());
//            }
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            //mRecyclerView.setAdapter(mStops_adapter);
//            if (isNoStops_available_nearby) {
//                mProgressBar.setVisibility(View.GONE);
//                if (radius == 1000)
//                    mNoStopsTV.setText(R.string.no_nearby_stops_1km);
//                else if (radius == 2000)
//                    mNoStopsTV.setText(R.string.no_nearby_stops_2km);
//                mNoStopsTV.setVisibility(View.VISIBLE);
//                if (MainActivity.menuItem != null && MainActivity.menuItem.getActionView() != null) {
//                    MainActivity.menuItem.getActionView().clearAnimation();
//                    MainActivity.menuItem.setActionView(null);
//                }
//
//            } else if (no_tfl_service_available) {
//                mProgressBar.setVisibility(View.GONE);
//                mNoStopsTV.setVisibility(View.VISIBLE);
//                mNoStopsTV.setText(R.string.notflserivceavailable);
//                if (MainActivity.menuItem != null && MainActivity.menuItem.getActionView() != null) {
//                    MainActivity.menuItem.getActionView().clearAnimation();
//                    MainActivity.menuItem.setActionView(null);
//                }
//            } else{
//                mProgressBar.setVisibility(View.VISIBLE);
//                mNoStopsTV.setVisibility(View.GONE);
//            }
//        }
//    }
//
//    int pos;
//    private class GetBusArrivals extends AsyncTask<Object, Void, Void> {
//
//        /**
//         * It represents the position of the first visible position of recycler view
//         */
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            if (getActivity() != null)
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        mBusArrivalDetailsPOJOList.clear();
//                        mStops_adapter.notifyDataSetChanged();
//                    }
//                });
//        }
//        LatLng latLng;
//        String stopID;
//        int distance;
//        String commonName;
//        ArrayList<String> busNameList;
//
//        @SuppressWarnings("unchecked")
//        @Override
//        protected Void doInBackground(Object... strings) {
//
//            JSONArray lineGroup = (JSONArray) strings[0];
//            latLng = (LatLng) strings[1];
//            distance = (int) strings[2];
//            commonName = (String) strings[3];
//            busNameList = (ArrayList<String>) strings[4];
//
//            try {
//                for (int j=0; j< lineGroup.length(); j++) {
//                    JSONObject  jsonObject2 = lineGroup.getJSONObject(j);
//                    if (jsonObject2.has("naptanIdReference")) {
//                        stopID = jsonObject2.getString("naptanIdReference");
//                        String urlString = "https://api.tfl.gov.uk/StopPoint/"+stopID+"/Arrivals?app_id="
//                                + Constants.app_id_tfl + "&app_key=" + Constants.app_key_tfl;
//                        Log.v(TAG, urlString);
//                        HttpURLConnection urlConnection = null;
//
//                        BufferedReader bufferedReader;
//                        String line;
//                        InputStream inputStream;
//                        StringBuilder json_result = new StringBuilder();
//                        try {
//                            if (InternetConnectivityReceiver.isConnected()) {
//                                Log.v(TAG, urlString);
//                                System.setProperty("http.keepAlive", "false");
//                                URL url = new URL(urlString);
//                                urlConnection = (HttpURLConnection) url.openConnection();
//                                urlConnection.setReadTimeout(2000);
//                                urlConnection.setConnectTimeout(5000);
//                                urlConnection.setRequestMethod("GET");
//
//                                inputStream = new BufferedInputStream(urlConnection.getInputStream());
//                                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//
//                                while ((line = bufferedReader.readLine()) != null) {
//                                    json_result.append(line);
//                                }
//
//                                final List<BusArrivalPOJO> busArrivalPOJOs = getDataFromJSON(json_result.toString(), latLng, distance);
//                                if (busArrivalPOJOs != null && busArrivalPOJOs.size() > 0) {
//                                    if (getActivity() != null) {
//                                        getActivity().runOnUiThread(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                mBusArrivalDetailsPOJOList.add(busArrivalPOJOs);
//
//                                                if (mBusArrivalDetailsPOJOList.size() == 1 || mBusArrivalDetailsPOJOList.size() == 2
//                                                        || mBusArrivalDetailsPOJOList.size() == 5) {
//                                                    mRecyclerView.setAdapter(mStops_adapter);
//                                                } else {
//                                                    mStops_adapter.notifyItemChanged(mBusArrivalDetailsPOJOList.size() - 1);
//                                                }
//                                                if (mProgressBar.getVisibility() == View.VISIBLE)
//                                                    mProgressBar.setVisibility(View.GONE);
//                                            }
//                                        });
//                                    }
//                                }
//                            }
//                        } catch (Exception e) {
//                            Log.e(TAG, "timeout: "+ stopID + e.getLocalizedMessage());
//                            j= j-1;
//                        } finally {
//                            if (urlConnection != null) {
//                                urlConnection.disconnect();
//                            }
//                        }
//                    }
//
//
//                }
//            } catch (JSONException e) {
//                Log.e(TAG, e.getLocalizedMessage());
//            }
//            return null;
//        }
//
//        private List<BusArrivalPOJO> getDataFromJSON(String json, LatLng latLng, int distance) {
//            List<BusArrivalPOJO> busArrivalPOJOList = new ArrayList<>();
//            try {
//                Log.v(TAG,  json);
//                JSONArray jsonArray = new JSONArray(json);
//
//                if (jsonArray.length() > 0) {
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                        if (jsonObject.has("exceptionType") && jsonObject.getString("exceptionType").contentEquals("EntityNotFoundException")) {
//                            return null;
//                        } else {
//                            String lineName = jsonObject.getString("lineName");
//                            String destinationName = jsonObject.getString("destinationName");
//                            int timeToArrivalToStation = jsonObject.getInt("timeToStation");
//                            String stationName = jsonObject.getString("stationName");
//                            String naptanId = jsonObject.getString("naptanId");
//
//                            BusArrivalPOJO busArrivalPOJO = new BusArrivalPOJO(lineName, destinationName,
//                                    timeToArrivalToStation, stationName, latLng, distance, naptanId, busNameList);
//                            busArrivalPOJOList.add(busArrivalPOJO);
//                        }
//                    }
//                } else {
//                    BusArrivalPOJO busArrivalPOJO = new BusArrivalPOJO("empty", null,
//                            0, commonName, latLng, distance, stopID, busNameList);
//                    busArrivalPOJOList.add(busArrivalPOJO);
//                }
//
//            } catch (JSONException e) {
//                Log.e(TAG, e.getLocalizedMessage());
//            }
//            return busArrivalPOJOList;
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
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//
//        currentTimeInMilliSeconds = System.currentTimeMillis();
//
//        // Close handler if the fragment goes to background
//        if (mHandler != null)
//            mHandler.removeCallbacksAndMessages(null);
//
//        mHandler = new Handler();
//        isResumed = true;
//
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (InternetConnectivityReceiver.isConnected()) {
//                    if (Constants.isAutoCompleteCalledBus || Constants.isRefreshCalledBus ||
//                            (currentTimeInMilliSeconds - prevTimeInMilliSeconds > minTimeDifferenceToRefresh)) {
//                        mStops_adapter = new Nearby_Stops_Adapter(mBusArrivalPOJOList, mBusArrivalDetailsPOJOList);
//                        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//                        mRecyclerView.setAdapter(mStops_adapter);
//
//                        mProgressBar.setVisibility(View.VISIBLE);
//                        new GetNearbyStops().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, radiusToSet);
//                        prevTimeInMilliSeconds = currentTimeInMilliSeconds;
//                        Constants.isAutoCompleteCalledBus = false;
//                        Constants.isRefreshCalledBus = false;
//                    }
//                    mHandler.postDelayed(this, REFRESH_TIME * 1000);
//                    currentTimeInMilliSeconds = 0;
//                }
//            }
//        }, 1000);
//
//
//        if (mHandlerForOnStop != null) {
//            mHandlerForOnStop.removeCallbacks(null);
//        }
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        count = 0;
//        isResumed = false;
//        if (mHandler != null)
//            mHandler.removeCallbacksAndMessages(null);
//        mHandlerForOnStop = new Handler();
//        mHandlerForOnStop.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (!isResumed) {
//                    if (getActivity() != null)
//                        getActivity().finish();
//                }
//            }
//        }, 3 * 60 * 1000);
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        radiusToSet = 1000;
//        if (mHandler != null)
//            mHandler.removeCallbacksAndMessages(null);
//    }
//
//    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//    }
//}