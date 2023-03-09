//package com.srisindhusaride.golondon;
//
//import static android.os.AsyncTask.THREAD_POOL_EXECUTOR;
//import static com.srisindhusaride.golondon.Utils.Constants.favorite_sp;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Typeface;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.CardView;
//import android.util.Log;
//import android.util.TypedValue;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewAnimationUtils;
//import android.view.ViewGroup;
//import android.view.ViewTreeObserver;
//import android.view.animation.AlphaAnimation;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.view.animation.TranslateAnimation;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RadioButton;
//import android.widget.RelativeLayout;
//import android.widget.ScrollView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.Nullable;
//import androidx.core.content.ContextCompat;
//import androidx.core.util.ArraySet;
//
//import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
//import com.google.android.gms.common.GooglePlayServicesRepairableException;
//import com.google.android.gms.common.api.Status;
//import com.google.android.gms.location.places.AutocompleteFilter;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlaceAutocomplete;
//import com.google.common.collect.ArrayListMultimap;
//import com.google.common.collect.Multimap;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.srisindhusaride.golondon.Model.BusArrivalPOJO;
//import com.srisindhusaride.golondon.Model.StationArrivalPOJO;
//import com.srisindhusaride.golondon.Utils.Constants;
//import com.srisindhusaride.golondon.Utils.InternetConnectivityReceiver;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.BufferedInputStream;
//import java.io.BufferedReader;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.lang.reflect.Type;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//import java.util.TreeMap;
//
///**
// * @since 12/07/17.
// */
//
//public class Favorites_Activity extends AppCompatActivity {
//
//    private View mRootLayout;
//    private LinearLayout mHome_linearLayout;
//    private LinearLayout mWork_linearLayout;
//    private RadioButton mRadioButton_home;
//    private RadioButton mRadioButton_work;
//    TextView favorites_no_info_TV;
//    TextView work_no_info_TV;
//    TextView mWork_stationNameTV;
//    TextView mHome_stationNameTV;
//    CardView home_cardView;
//    CardView work_cardView;
//    TextView home_no_info_TV;
//    private LinearLayout favoritesLinearLayout;
//    private ScrollView mScrollView;
//    private ImageView mAdd_fab;
//
//    SharedPreferences favoritesSP;
//    SharedPreferences personalSP;
//
//    Gson gson;
//    Type type;
//    private Menu menu;
//    private MenuItem refreshMenuItem;
//    int scrollY;
//
//    private final String TAG = "FAVORITES_ACT";
//    private final String HOME = "HOME";
//    private final String WORK = "WORK";
//    private final String FAVORITE = "FAVORITE";
//
//    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
//
//    private Activity mContext;
//    ArrayList<BusArrivalPOJO> busArrivalPOJOArrayList = new ArrayList<>();
//    ArrayList<StationArrivalPOJO> stationArrivalPOJOArrayList = new ArrayList<>();
//
//    ArrayList<BusArrivalPOJO> busArrivalPOJOArrayListFavorites = new ArrayList<>();
//    ArrayList<StationArrivalPOJO> stationArrivalPOJOArrayListFavorites = new ArrayList<>();
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setContentView(R.layout.favorites_activity);
//        mContext = this;
//
//        final Intent intent = getIntent();
//        mRootLayout = findViewById(R.id.rootLayout);
//        favoritesLinearLayout = (LinearLayout) findViewById(R.id.favorites_linearlayout);
//
//        if (savedInstanceState == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
//                && intent.hasExtra(Constants.revealXFab) && intent.hasExtra(Constants.revealYFab)) {
//
//            mRootLayout.setVisibility(View.INVISIBLE);
//
//            final int revealX = intent.getIntExtra(Constants.revealXFab, 0);
//            final int revealY = intent.getIntExtra(Constants.revealYFab, 0);
//
//            final ViewTreeObserver viewTreeObserver = mRootLayout.getViewTreeObserver();
//            if (viewTreeObserver.isAlive()) {
//                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                    @Override
//                    public void onGlobalLayout() {
//                        revealActivity(revealX, revealY);
//                        mRootLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                    }
//                });
//            }
//        } else {
//            mRootLayout.setVisibility(View.VISIBLE);
//        }
//
//        mScrollView = (ScrollView) findViewById(R.id.scrollView);
//        home_no_info_TV = (TextView) findViewById(R.id.home_no_station_info);
//        work_no_info_TV = (TextView) findViewById(R.id.work_no_station_info);
//        favorites_no_info_TV = (TextView) findViewById(R.id.favorites_no_station_info);
//
//        home_cardView = (CardView) findViewById(R.id.home_cardView);
//        work_cardView = (CardView) findViewById(R.id.work_cardView);
//
//        mRadioButton_home = (RadioButton) findViewById(R.id.radioButton_home);
//        mRadioButton_work = (RadioButton) findViewById(R.id.radioButton_work);
//
//        mRadioButton_home.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mRadioButton_home.isChecked()) {
//                    mRadioButton_home.setChecked(false);
//                } else
//                    mRadioButton_home.setChecked(true);
//            }
//        });
//
//        home_cardView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                if (!mRadioButton_home.isChecked()) {
//                    Animation rightToLeft = new TranslateAnimation(100, 0, 0, 0);
//                    mRadioButton_home.setVisibility(View.VISIBLE);
//                    mRadioButton_home.startAnimation(rightToLeft);
//                    rightToLeft.setFillAfter(true);
//                    rightToLeft.setDuration(200);
//
//                    rightToLeft.setAnimationListener(new Animation.AnimationListener() {
//                        @Override
//                        public void onAnimationStart(Animation animation) {
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            mRadioButton_home.setChecked(true);
//                            menu.findItem(R.id.delete).setVisible(true);
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animation animation) {
//                        }
//                    });
//                }
//                return false;
//            }
//        });
//
//        mRadioButton_work.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mRadioButton_work.isChecked())
//                    mRadioButton_work.setChecked(false);
//                else
//                    mRadioButton_work.setChecked(true);
//            }
//        });
//
//        work_cardView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                if (!mRadioButton_work.isChecked()) {
//
//                    Animation rightToLeft = new TranslateAnimation(100, 0, 0, 0);
//                    mRadioButton_work.setVisibility(View.VISIBLE);
//                    mRadioButton_work.startAnimation(rightToLeft);
//                    rightToLeft.setFillAfter(true);
//                    rightToLeft.setDuration(200);
//
//                    rightToLeft.setAnimationListener(new Animation.AnimationListener() {
//                        @Override
//                        public void onAnimationStart(Animation animation) {
//                        }
//
//                        @Override
//                        public void onAnimationEnd(Animation animation) {
//                            mRadioButton_work.setChecked(true);
//                            menu.findItem(R.id.delete).setVisible(true);
//                        }
//
//                        @Override
//                        public void onAnimationRepeat(Animation animation) {
//                        }
//                    });
//                }
//                return false;
//            }
//        });
//
//        mHome_linearLayout = (LinearLayout) findViewById(R.id.home_station_linearLayout);
//        mWork_linearLayout = (LinearLayout) findViewById(R.id.work_station_linearLayout);
//
//        mHome_stationNameTV = (TextView) findViewById(R.id.homeStationName);
//        mWork_stationNameTV = (TextView) findViewById(R.id.workStationName);
//
//        personalSP = mContext.getSharedPreferences(Constants.personal_sp, Context.MODE_PRIVATE);
//
//        mAdd_fab = (ImageView) findViewById(R.id.add_fab);
//        mAdd_fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                try {
//                    AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
//                            .setCountry("GB")
//                            .build();
//                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
//                            .setFilter(typeFilter)
//                            .build(mContext);
//
//                    //Gets result to onActivityResult() function
//                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
//                } catch (GooglePlayServicesRepairableException e) {
//                    Log.e(TAG, e.getLocalizedMessage());
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    Log.e(TAG, e.getLocalizedMessage());
//                }
//            }
//        });
//
//        mScrollView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
//                    AlphaAnimation toOne = new AlphaAnimation(0f, 1f);
//                    toOne.setAnimationListener(new Animation.AnimationListener() {
//                        @Override
//                        public void onAnimationStart(Animation animation) {
//                            mAdd_fab.setAlpha(1f);
//                            mAdd_fab.setVisibility(View.VISIBLE);
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
//                    mAdd_fab.startAnimation(toOne);
//                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
//                    if (mAdd_fab.getAlpha() == 1.0) {
//                        AlphaAnimation toZero = new AlphaAnimation(1f, 0f);
//                        toZero.setDuration(200);
//                        toZero.setFillAfter(true);
//                        toZero.setAnimationListener(new Animation.AnimationListener() {
//                            @Override
//                            public void onAnimationStart(Animation animation) {
//                                mAdd_fab.setAlpha(0f);
//                                mAdd_fab.setVisibility(View.GONE);
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
//                        mAdd_fab.startAnimation(toZero);
//                    }
//
//                }
//                return false;
//            }
//        });
//
//        AppRater.app_launched(mContext);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
//            switch (resultCode) {
//                case RESULT_OK:
//                    Place place = PlaceAutocomplete.getPlace(this, data);
//
//                    Intent intent = new Intent(mContext, MainActivity.class);
//                    intent.putExtra(Constants.lat, place.getLatLng().latitude);
//                    intent.putExtra(Constants.lng, place.getLatLng().longitude);
//                    intent.putExtra(Constants.address, place.getAddress().toString());
//                    startActivity(intent);
//
//                case RESULT_CANCELED:
//                    break;
//                case PlaceAutocomplete.RESULT_ERROR:
//                    Status status = PlaceAutocomplete.getStatus(this, data);
//                    Log.e(TAG, status.getStatusMessage());
//            }
//        }
//    }
//
//
//    private void revealActivity(int x, int y) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            int finalRadius = Math.max(mRootLayout.getWidth(), mRootLayout.getHeight());
//
//            android.animation.Animator animator = ViewAnimationUtils.createCircularReveal(mRootLayout, x, y, 0, finalRadius);
//            animator.setDuration(300);
//
//            mRootLayout.setVisibility(View.VISIBLE);
//            animator.start();
//        } else
//            finish();
//    }
//
//    int count = 0;
//
//    private String downloadData(final String stationID, final String category) {
//        String urlString = "https://api.tfl.gov.uk/StopPoint/" + stationID + "/Arrivals?app_id="
//                + Constants.app_id_tfl + "&app_key=" + Constants.app_key_tfl;
//        Log.v(TAG, "url: " + urlString);
//        HttpURLConnection urlConnection = null;
//
//        BufferedReader bufferedReader;
//        String line;
//        InputStream inputStream;
//        StringBuilder json_result = new StringBuilder();
//        try {
//            if (InternetConnectivityReceiver.isConnected()) {
//                System.setProperty("http.keepAlive", "false");
//                URL url = new URL(urlString);
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setReadTimeout(2000);
//                urlConnection.setConnectTimeout(5000);
//                urlConnection.setRequestMethod("GET");
//
//                inputStream = new BufferedInputStream(urlConnection.getInputStream());
//                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//
//                while ((line = bufferedReader.readLine()) != null) {
//                    json_result.append(line);
//                }
//            }
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            count++;
//            if (count < 15)
//                downloadData(stationID, category);
//            else
//                Toast.makeText(mContext, "There is some problem with data loading. Please try" +
//                        "again later", Toast.LENGTH_SHORT).show();
//
//        } finally {
//            if (urlConnection != null) {
//                urlConnection.disconnect();
//            }
//        }
//        return getDataFromJSON(json_result.toString(), category);
//
//    }
//
//    private String getDataFromJSON(String json, String category) {
//
//        String modeName = "";
//        try {
//            JSONArray jsonArray = new JSONArray(json);
//            if (jsonArray.length() > 0) {
//                for (int i = 0; i < jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    String busName, platformName = "", destinationName, stationName, naptanId, towards;
//                    int timeToArrivalToStation;
//                    stationName = jsonObject.getString("stationName");
//                    naptanId = jsonObject.getString("naptanId");
//                    if (jsonObject.has("lineName"))
//                        busName = jsonObject.getString("lineName");
//                    else
//                        busName = "";
//                    if (jsonObject.has("platformName")) {
//                        platformName = jsonObject.getString("platformName");
//                        if (platformName.contentEquals("null")) {
//                            String gson_str = favoritesSP.getString(Constants.favorite_gson, "");
//                            if (!gson_str.contentEquals("")) {
//                                TreeMap<String, String> treeMap = gson.fromJson(gson_str, type);
//
//                                for (String keyOfMap : treeMap.keySet()) {
//                                    if (treeMap.get(keyOfMap).contentEquals(naptanId) && keyOfMap.contains("::")) {
//                                        platformName = keyOfMap.split(" :: ")[1];
//                                        break;
//                                    } else {
//                                        platformName = "";
//                                    }
//                                }
//                            }
//                        }
//                    } else {
//                        String gson_str = favoritesSP.getString(Constants.favorite_gson, "");
//                        if (!gson_str.contentEquals("")) {
//                            TreeMap<String, String> treeMap = gson.fromJson(gson_str, type);
//                            for (String keyOfMap : treeMap.keySet()) {
//                                if (treeMap.get(keyOfMap).contentEquals(naptanId) && keyOfMap.contains("::")) {
//                                    platformName = keyOfMap.split(" :: ")[1];
//                                    break;
//                                } else {
//                                    platformName = "";
//                                }
//                            }
//                        }
//                    }
//                    if (jsonObject.has("destinationName"))
//                        destinationName = jsonObject.getString("destinationName");
//                    else
//                        destinationName = "";
//                    if (jsonObject.has("timeToStation"))
//                        timeToArrivalToStation = jsonObject.getInt("timeToStation");
//                    else
//                        timeToArrivalToStation = 0;
//                    if (jsonObject.has("modeName"))
//                        modeName = jsonObject.getString("modeName");
//                    else
//                        modeName = "";
//                    if (jsonObject.has("towards"))
//                        towards = jsonObject.getString("towards");
//                    else
//                        towards = "";
//
//                    if (!category.contentEquals(FAVORITE)) {
//                        if (modeName.contentEquals("bus")) {
//                            BusArrivalPOJO busArrivalPOJO = new BusArrivalPOJO(stationName, timeToArrivalToStation,
//                                    naptanId, platformName, destinationName, busName);
//                            busArrivalPOJOArrayList.add(busArrivalPOJO);
//                        } else {
//                            StationArrivalPOJO stationArrivalPOJO = new StationArrivalPOJO(stationName, timeToArrivalToStation, destinationName, naptanId, platformName
//                                    , busName, towards);
//                            stationArrivalPOJOArrayList.add(stationArrivalPOJO);
//                        }
//                    } else {            //Favorites
//                        if (modeName.contentEquals("bus")) {
//                            final BusArrivalPOJO busArrivalPOJO = new BusArrivalPOJO(stationName, timeToArrivalToStation,
//                                    naptanId, platformName, destinationName, busName);
//                            busArrivalPOJOArrayListFavorites.add(busArrivalPOJO);
//
//                        } else {
//                            final StationArrivalPOJO stationArrivalPOJO = new StationArrivalPOJO(stationName, timeToArrivalToStation, destinationName, naptanId, platformName
//                                    , busName, towards);
//                            stationArrivalPOJOArrayListFavorites.add(stationArrivalPOJO);
//
//                        }
//                    }
//                }
//                if (category.contentEquals(FAVORITE)) {
//                    if (busArrivalPOJOArrayListFavorites.size() > 0) {
//                        addCardViewToFavoritesList(busArrivalPOJOArrayListFavorites, null);
//                    } else if (stationArrivalPOJOArrayListFavorites.size() > 0) {
//                        addCardViewToFavoritesList(null, stationArrivalPOJOArrayListFavorites);
//                    }
//                }
//            }
//        } catch (JSONException e) {
//            Log.e(TAG, e.getLocalizedMessage());
//        }
//        return modeName;
//    }
//
//    HashMap<String, View> mCardViewsToBeDeleted = new HashMap<>();
//
//    private void addCardViewToFavoritesList(final ArrayList<BusArrivalPOJO> busArrivalPOJOList
//            , final ArrayList<StationArrivalPOJO> stationArrivalPOJOList) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                LayoutInflater inflater = LayoutInflater.from(mContext);
//                final View cardView = inflater.inflate(R.layout.favorites_cardview, null);
//
//                TypedValue typedValue = new TypedValue();
//                mContext.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true);
//                cardView.setBackgroundResource(typedValue.resourceId);
//
//                LinearLayout cardViewLinearLayout = (LinearLayout) cardView.findViewById(R.id.linear_layout);
//
//                final RadioButton radioButton = (RadioButton) cardView.findViewById(R.id.radioButton);
//
//                final TextView stationNameTV = (TextView) cardView.findViewById(R.id.stationName);
//                stationNameTV.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
//
//
//                radioButton.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (radioButton.isChecked()) {
//                            radioButton.setChecked(false);
//                            mCardViewsToBeDeleted.remove(stationNameTV.getText().toString());
//                        } else
//                            radioButton.setChecked(true);
//
//                    }
//                });
//
//                if (!radioButton.isChecked()) {
//                    cardView.setOnLongClickListener(new View.OnLongClickListener() {
//                        @Override
//                        public boolean onLongClick(View view) {
//                            mCardViewsToBeDeleted.put(stationNameTV.getText().toString(), cardView);
//
//                            Animation rightToLeft = new TranslateAnimation(100, 0, 0, 0);
//                            radioButton.setVisibility(View.VISIBLE);
//                            radioButton.startAnimation(rightToLeft);
//                            rightToLeft.setFillAfter(true);
//                            rightToLeft.setDuration(200);
//
//                            rightToLeft.setAnimationListener(new Animation.AnimationListener() {
//                                @Override
//                                public void onAnimationStart(Animation animation) {
//                                }
//
//                                @Override
//                                public void onAnimationEnd(Animation animation) {
//                                    radioButton.setChecked(true);
//                                    menu.findItem(R.id.delete).setVisible(true);
//                                }
//
//                                @Override
//                                public void onAnimationRepeat(Animation animation) {
//                                }
//                            });
//                            return false;
//                        }
//                    });
//                }
//
//                if (busArrivalPOJOList != null && busArrivalPOJOList.size() > 0) {   // Bus list
//                    String stationName;
//                    if (!busArrivalPOJOList.get(0).getPlatformName().contentEquals("")) {
//                        stationName = busArrivalPOJOList.get(0).getStationName()
//                                + " :: " + busArrivalPOJOList.get(0).getPlatformName();
//                    } else {
//                        stationName = busArrivalPOJOList.get(0).getStationName();
//                    }
//                    stationNameTV.setText(stationName);
//                    busAddLayoutToCardView(cardViewLinearLayout, busArrivalPOJOList);
//
//                } else if (stationArrivalPOJOList != null && stationArrivalPOJOList.size() > 0) {    //Train list
//                    String stationName = stationArrivalPOJOList.get(0).getStationName();
//                    stationNameTV.setText(stationName);
//                    stationsAddLayoutToCardView(cardViewLinearLayout, stationArrivalPOJOList, FAVORITE);
//                }
//
//                (favoritesLinearLayout).addView(cardView);
//            }
//        });
//    }
//
//    private synchronized void busAddLayoutToCardView(LinearLayout linearLayout
//            , ArrayList<BusArrivalPOJO> busArrivalPOJOArrayList) {
//
//        //Sort the bus details according to bus arrival timings
//        for (int j = 0; j < busArrivalPOJOArrayList.size(); j++) {
//            Collections.sort(busArrivalPOJOArrayList, new Comparator<BusArrivalPOJO>() {
//                @Override
//                public int compare(BusArrivalPOJO busArrivalPOJO, BusArrivalPOJO t1) {
//                    return busArrivalPOJO.getTimeToArrival() - t1.getTimeToArrival();
//                }
//            });
//        }
//
//        //Group all times of those having same bus names
//        Set<String> uniqueBusNamesSet = new HashSet<>();
//        for (int m = 0; m < busArrivalPOJOArrayList.size(); m++) {
//            uniqueBusNamesSet.add(busArrivalPOJOArrayList.get(m).getLineName());
//        }
//
//        ArrayList<Multimap<String, Integer>> allBusesForParticularStation = new ArrayList<>();
//        for (String key : uniqueBusNamesSet) {
//            Multimap<String, Integer> busWithTimes = ArrayListMultimap.create();
//            for (int s = 0; s < busArrivalPOJOArrayList.size(); s++) {
//                if (key.contentEquals(busArrivalPOJOArrayList.get(s).getLineName())) {
//                    busWithTimes.put(key + "::" + busArrivalPOJOArrayList.get(s).getDestinationName()
//                            , busArrivalPOJOArrayList.get(s).getTimeToArrival());
//                }
//            }
//            allBusesForParticularStation.add(busWithTimes);
//        }
//
//        for (int t = 0; t < allBusesForParticularStation.size(); t++) {
//            Multimap<String, Integer> map = allBusesForParticularStation.get(t);
//            for (String key : map.keySet()) {
//                List<Integer> values = new ArrayList<>(map.get(key));
//
//                RelativeLayout relativeLayout = new RelativeLayout(mContext);
//                relativeLayout.setPadding(16, 16, 16, 16);
//
//                LinearLayout linearLayoutForBusName = new LinearLayout(mContext);
//                linearLayoutForBusName.setOrientation(LinearLayout.VERTICAL);
//
//                final TextView busNameTV = new TextView(mContext);
//                busNameTV.setText(key.split("::")[0]);
//                busNameTV.setTypeface(Typeface.create("sans-serif-normal", Typeface.NORMAL));
//                busNameTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));
//
//                final TextView destinationNameTV = new TextView(mContext);
//                destinationNameTV.setText(key.split("::")[1]);
//                destinationNameTV.setTextSize(12);
//                destinationNameTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextSecondary));
//
//                linearLayoutForBusName.addView(busNameTV);
//                linearLayoutForBusName.addView(destinationNameTV);
//
//                LinearLayout linearLayoutForTimes = new LinearLayout(mContext);
//                linearLayoutForTimes.setOrientation(LinearLayout.VERTICAL);
//
//                RelativeLayout timeRelativeLayout = new RelativeLayout(mContext);
//                timeRelativeLayout.setId(R.id.time_relativeLayout_id);
//
//                LinearLayout timeArrowLinearLayout = new LinearLayout(mContext);
//                timeArrowLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
//                timeArrowLinearLayout.setId(R.id.time_id);
//                timeArrowLinearLayout.setGravity(Gravity.CENTER_VERTICAL);
//                timeArrowLinearLayout.setPadding(6, 6, 6, 6);
//
//                RelativeLayout.LayoutParams timeArrowLP = new RelativeLayout.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                timeArrowLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                timeArrowLinearLayout.setLayoutParams(timeArrowLP);
//
//
//                //Recent Time text view
//                TextView timeTV = new TextView(mContext);
//                int timeSec = values.get(0);
//                int timeInMin = Math.round(timeSec / 60);
//                if (timeInMin == 0) {
//                    String timeText = "due";
//                    timeTV.setText(timeText);
//                } else {
//                    String timeInMinText = timeInMin + " min";
//                    timeTV.setText(timeInMinText);
//                }
//                timeTV.setTextColor(ContextCompat.getColor(mContext, R.color.textColorAccent));
//                timeTV.setPadding(0, 0, 16, 0);
//
//                //Remaining times text view
//                final TextView remainingTimesTV = new TextView(mContext);
//                remainingTimesTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextSecondary));
//                remainingTimesTV.setTextSize(11);
//                remainingTimesTV.setVisibility(View.GONE);
//
//                for (int l = 1; l < values.size() && l < 4; l++) {
//                    String tempTime;
//                    if (l == 3 || l == values.size() - 1) {
//                        tempTime = values.get(l) / 60 + " min";
//                    } else
//                        tempTime = values.get(l) / 60 + " min, ";
//                    remainingTimesTV.append(tempTime);
//                }
//
//                RelativeLayout.LayoutParams remainingTimeLayoutParams = new RelativeLayout.LayoutParams
//                        (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                remainingTimeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                remainingTimeLayoutParams.addRule(RelativeLayout.BELOW, R.id.time_id);
//                remainingTimesTV.setLayoutParams(remainingTimeLayoutParams);
//
//                timeArrowLinearLayout.addView(timeTV);
//
//                if (values.size() > 1) {
//
//                    //Down arrow button
//                    final ImageView imageButton = new ImageView(mContext);
//                    imageButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.arrow_down));
//
//                    timeArrowLinearLayout.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            if (remainingTimesTV.getVisibility() == View.GONE) {
//                                remainingTimesTV.setVisibility(View.VISIBLE);
//                                imageButton.setImageResource(R.drawable.arrow_down);
//                                imageButton.animate().rotation(180).start();
//
//                            } else {
//                                remainingTimesTV.setVisibility(View.GONE);
//                                imageButton.setImageResource(R.drawable.arrow_top);
//                                imageButton.animate().rotation(-180).start();
//                            }
//                        }
//                    });
//
//                    timeArrowLinearLayout.addView(imageButton);
//                }
//
//                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                        ViewGroup.LayoutParams.WRAP_CONTENT);
//                layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//                layoutParams2.addRule(RelativeLayout.CENTER_VERTICAL);
//                timeRelativeLayout.setLayoutParams(layoutParams2);
//
//                timeRelativeLayout.addView(timeArrowLinearLayout);
//                timeRelativeLayout.addView(remainingTimesTV);
//
//
//                View horLine = new View(mContext);
//                RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                        1);
//                horLine.setLayoutParams(layoutParams3);
//                horLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.horizontal_line));
//                horLine.setPadding(0, 5, 0, 5);
//
//                relativeLayout.addView(linearLayoutForBusName);
//                relativeLayout.addView(timeRelativeLayout);
//
//                linearLayout.addView(relativeLayout);
//                if (t < allBusesForParticularStation.size() - 1)
//                    linearLayout.addView(horLine);
//
//            }
//        }
//    }
//
//    private synchronized void stationsAddLayoutToCardView(LinearLayout linearLayout
//            , ArrayList<StationArrivalPOJO> stationArrivalPOJOArrayList, String category) {
//
//        final ArrayList<String> mUniqueStationName = new ArrayList<>();  //Unique platform names of a station got from hash set 'uniqStatNames'
//        HashSet<String> uniqStatNames = new HashSet<>(); //Unique platform names of a station
//        for (int i = 0; i < stationArrivalPOJOArrayList.size(); i++) {
//            uniqStatNames.add(stationArrivalPOJOArrayList.get(i).getPlatformName()
//                    + stationArrivalPOJOArrayList.get(i).getTowards()
//                    + stationArrivalPOJOArrayList.get(i).getLineName());
//        }
//        for (String it : uniqStatNames) {
//            mUniqueStationName.add(it);
//        }
//
//        for (int j = 0; j < mUniqueStationName.size(); j++) {
//            final ArrayList<StationArrivalPOJO> arrivalPOJOs = new ArrayList<>();   //Platform wise categorized train details
//            for (int i = 0; i < stationArrivalPOJOArrayList.size(); i++) {
//                if (mUniqueStationName.get(j).contains(stationArrivalPOJOArrayList.get(i).getPlatformName())
//                        && mUniqueStationName.get(j).contains(stationArrivalPOJOArrayList.get(i).getTowards())
//                        && mUniqueStationName.get(j).contains(stationArrivalPOJOArrayList.get(i).getLineName())) {
//                    arrivalPOJOs.add(stationArrivalPOJOArrayList.get(i));
//                }
//            }
//
//            Collections.sort(arrivalPOJOs, new Comparator<StationArrivalPOJO>() {
//                @Override
//                public int compare(StationArrivalPOJO stationArrivalPOJO, StationArrivalPOJO t1) {
//                    return stationArrivalPOJO.getTimeToStation() - t1.getTimeToStation();
//                }
//            });
//
//            RelativeLayout relativeLayout = new RelativeLayout(mContext);
//            relativeLayout.setPadding(16, 16, 16, 16);
//            LinearLayout platformLinearLayout = new LinearLayout(mContext);
//            platformLinearLayout.setOrientation(LinearLayout.VERTICAL);
//
//            final TextView platformName = new TextView(mContext);
//            platformName.setText(arrivalPOJOs.get(0).getPlatformName());
//            platformName.setTypeface(Typeface.create("sans-serif-normal", Typeface.NORMAL));
//            platformName.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));
//
//            final TextView towards = new TextView(mContext);
//            final String towardsText = arrivalPOJOs.get(0).getTowards() + " - "
//                    + arrivalPOJOs.get(0).getLineName();
//            towards.setText(towardsText);
//            towards.setTextSize(12);
//            towards.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextSecondary));
//
//            platformLinearLayout.addView(platformName);
//            platformLinearLayout.addView(towards);
//
//            RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//            layoutParams1.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//            platformLinearLayout.setLayoutParams(layoutParams1);
//
//            RelativeLayout timeRelativeLayout = new RelativeLayout(mContext);
//            timeRelativeLayout.setId(R.id.time_relativeLayout_id);
//
//            LinearLayout timeArrowLinearLayout = new LinearLayout(mContext);
//            timeArrowLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
//            timeArrowLinearLayout.setId(R.id.time_id);
//            timeArrowLinearLayout.setGravity(Gravity.CENTER_VERTICAL);
//            timeArrowLinearLayout.setPadding(6, 6, 6, 6);
//
//            RelativeLayout.LayoutParams timeArrowLP = new RelativeLayout.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            timeArrowLP.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//            timeArrowLinearLayout.setLayoutParams(timeArrowLP);
//
//            //Recent Time text view
//            final TextView timeTV = new TextView(mContext);
//            int timeSec = arrivalPOJOs.get(0).getTimeToStation();
//            int timeInMin = Math.round(timeSec / 60);
//            if (timeInMin == 0) {
//                String timeText = "due";
//                timeTV.setText(timeText);
//            } else {
//                String timeInMinText = timeInMin + " min";
//                timeTV.setText(timeInMinText);
//            }
//            timeTV.setTextColor(ContextCompat.getColor(mContext, R.color.textColorAccent));
//            timeTV.setPadding(0, 0, 16, 0);
//
//            //Remaining times text view
//            final TextView remainingTimesTV = new TextView(mContext);
//            remainingTimesTV.setTextColor(ContextCompat.getColor(mContext, R.color.colorTextSecondary));
//            remainingTimesTV.setTextSize(11);
//            remainingTimesTV.setVisibility(View.GONE);
//
//            for (int i = 1; i < arrivalPOJOs.size() && i < 4; i++) {
//                String tempTime;
//                if (i == 3 || i == arrivalPOJOs.size() - 1) {
//                    tempTime = arrivalPOJOs.get(i).getTimeToStation() / 60 + " min";
//                } else
//                    tempTime = arrivalPOJOs.get(i).getTimeToStation() / 60 + " min, ";
//                remainingTimesTV.append(tempTime);
//            }
//            RelativeLayout.LayoutParams remainingTimeLayoutParams = new RelativeLayout.LayoutParams
//                    (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            remainingTimeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//            remainingTimeLayoutParams.addRule(RelativeLayout.BELOW, R.id.time_id);
//            remainingTimesTV.setLayoutParams(remainingTimeLayoutParams);
//
//            timeArrowLinearLayout.addView(timeTV);
//
//            if (arrivalPOJOs.size() > 1) {
//                //Down arrow button
//                final ImageView imageButton = new ImageView(mContext);
//                imageButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.arrow_down));
//
//                timeArrowLinearLayout.setOnClickListener(
//                        new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                if (remainingTimesTV.getVisibility() == View.GONE) {
//                                    remainingTimesTV.setVisibility(View.VISIBLE);
//                                    imageButton.setImageResource(R.drawable.arrow_down);
//                                    imageButton.animate().rotation(180).start();
//                                } else {
//                                    remainingTimesTV.setVisibility(View.GONE);
//                                    imageButton.setImageResource(R.drawable.arrow_top);
//                                    imageButton.animate().rotation(-180).start();
//                                }
//                            }
//                        });
//
//                timeArrowLinearLayout.addView(imageButton);
//            }
//
//            RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT);
//            layoutParams2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//            layoutParams2.addRule(RelativeLayout.CENTER_VERTICAL);
//            timeRelativeLayout.setLayoutParams(layoutParams2);
//
//            timeRelativeLayout.addView(timeArrowLinearLayout);
//            timeRelativeLayout.addView(remainingTimesTV);
//
//            View horLine = new View(mContext);
//            RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                    1);
//            horLine.setLayoutParams(layoutParams3);
//            horLine.setBackgroundColor(ContextCompat.getColor(mContext, R.color.horizontal_line));
//            horLine.setPadding(0, 5, 0, 5);
//
//            relativeLayout.addView(platformLinearLayout);
//            relativeLayout.addView(timeRelativeLayout);
//
//            linearLayout.addView(relativeLayout);
//            if (j != mUniqueStationName.size() - 1) {
//                linearLayout.addView(horLine);
//            }
//        }
//    }
//
//    private class GetArrivals extends AsyncTask<Object, Void, String> {
//
//        String category;
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            busArrivalPOJOArrayList = new ArrayList<>();
//            stationArrivalPOJOArrayList = new ArrayList<>();
//        }
//
//        @Override
//        protected String doInBackground(Object... strings) {
//            final String stationID = (String) strings[0];
//            category = (String) strings[1];
//            String modeName = "";
//            if (!stationID.contentEquals(""))
//                modeName = downloadData(stationID, category);
//            return modeName;
//        }
//
//        @Override
//        protected void onPostExecute(String modeName) {
//            super.onPostExecute(modeName);
//
//            String gsonString = favoritesSP.getString(Constants.favorite_gson, "");
//            if (gsonString.isEmpty()) {
//                if (refreshMenuItem != null && refreshMenuItem.getActionView() != null) {
//                    refreshMenuItem.getActionView().clearAnimation();
//                    refreshMenuItem.setActionView(null);
//                }
//            }
//
//            if (modeName.contentEquals("bus")) {
//
//                if (category.contentEquals(HOME)) {
//                    mHome_linearLayout.removeAllViews();
//                    busAddLayoutToCardView(mHome_linearLayout, busArrivalPOJOArrayList);
//                } else {
//                    mWork_linearLayout.removeAllViews();
//                    busAddLayoutToCardView(mWork_linearLayout, busArrivalPOJOArrayList);
//                }
//            } else {
//                if (category.contentEquals(HOME)) {
//                    mHome_linearLayout.removeAllViews();
//                    stationsAddLayoutToCardView(mHome_linearLayout, stationArrivalPOJOArrayList, HOME);
//                } else {
//                    mWork_linearLayout.removeAllViews();
//                    stationsAddLayoutToCardView(mWork_linearLayout, stationArrivalPOJOArrayList, WORK);
//                }
//            }
//        }
//    }
//
//    private class GetArrivalsForFavorites extends AsyncTask<Object, Void, Void> {
//
//        @SuppressWarnings("Unchecked")
//        @Override
//        protected Void doInBackground(Object... objects) {
//            ArrayList<String> stationIDList = (ArrayList<String>) objects[0];
//            String category = (String) objects[1];
//            for (int i = 0; i < stationIDList.size(); i++) {
//                if (!stationIDList.get(i).contentEquals("")) {
//                    busArrivalPOJOArrayListFavorites = new ArrayList<>();
//                    stationArrivalPOJOArrayListFavorites = new ArrayList<>();
//                    downloadData(stationIDList.get(i), category);
//                }
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            mScrollView.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    if (scrollY != 0)
//                        mScrollView.smoothScrollTo(0, scrollY);
//                }
//            }, 100);
//            if (refreshMenuItem != null && refreshMenuItem.getActionView() != null) {
//                refreshMenuItem.getActionView().clearAnimation();
//                refreshMenuItem.setActionView(null);
//            }
//        }
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        this.menu = menu;
//        getMenuInflater().inflate(R.menu.favorites_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        menu.findItem(R.id.delete).setVisible(false);
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        if (item.getItemId() == R.id.delete) {
//            String gson_str = favoritesSP.getString(Constants.favorite_gson, "");
//            if (!gson_str.contentEquals("")) {
//                TreeMap<String, String> treeMap = gson.fromJson(gson_str, type);
//
//                for (String key : mCardViewsToBeDeleted.keySet()) {
//                    for (String keyOfMap : treeMap.keySet()) {
//                        if (keyOfMap.contentEquals(key)) {
//                            (favoritesLinearLayout).removeView(mCardViewsToBeDeleted.get(key));
//                            treeMap.remove(keyOfMap);
//
//                            String to_gson_str = gson.toJson(treeMap);
//                            favoritesSP.edit().putString(Constants.favorite_gson, to_gson_str).apply();
//                            break;
//                        }
//                    }
//                }
//            }
//
//            if (mRadioButton_home.isChecked()) {
//                personalSP.edit().remove(Constants.home_station_name).apply();
//                mHome_linearLayout.removeAllViews();
//                home_cardView.setVisibility(View.GONE);
//                home_no_info_TV.setVisibility(View.VISIBLE);
//            }
//            if (mRadioButton_work.isChecked()) {
//                personalSP.edit().remove(Constants.work_station_name).apply();
//                mWork_linearLayout.removeAllViews();
//                work_cardView.setVisibility(View.GONE);
//                work_no_info_TV.setVisibility(View.VISIBLE);
//            }
//            item.setVisible(false);
//
//        } else if (item.getItemId() == R.id.refresh) {
//            if (InternetConnectivityReceiver.isConnected()) {
//                refreshMenuItem = item;
//                scrollY = mScrollView.getScrollY();
//                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                ImageView iv = (ImageView) inflater.inflate(R.layout.rotate_refresh_menu, null);
//
//                Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_one_circle);
//
//                iv.startAnimation(rotation);
//                refreshMenuItem.setActionView(iv);
//                refreshAllData();
//            } else {
//                InternetConnectivityReceiver.displaySnackBar(mRootLayout);
//            }
//        }
//        return super.onOptionsItemSelected(item);
//
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        refreshAllData();
//    }
//
//    private void refreshAllData() {
//
//        String home_stationName = personalSP.getString(Constants.home_station_name, "");
//        if (home_stationName.isEmpty()) {
//            home_no_info_TV.setVisibility(View.VISIBLE);
//            home_cardView.setVisibility(View.GONE);
//        } else {
//            home_no_info_TV.setVisibility(View.GONE);
//            home_cardView.setVisibility(View.VISIBLE);
//
//            String home_stationID = personalSP.getString(Constants.home_station_id, "");
//            mHome_stationNameTV.setText(home_stationName);
//            mHome_stationNameTV.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
//            new GetArrivals().execute(home_stationID, HOME);
//        }
//
//        android.os.Handler handler = new android.os.Handler();
//
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                String work_stationName = personalSP.getString(Constants.work_station_name, "");
//                if (work_stationName.isEmpty()) {
//                    work_no_info_TV.setVisibility(View.VISIBLE);
//                    work_cardView.setVisibility(View.GONE);
//                } else {
//
//                    work_no_info_TV.setVisibility(View.GONE);
//                    work_cardView.setVisibility(View.VISIBLE);
//                    String work_stationID = personalSP.getString(Constants.work_station_id, "");
//                    Log.v(TAG, "work station:" + work_stationID);
//                    mWork_stationNameTV.setText(work_stationName);
//                    mWork_stationNameTV.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
//                    new GetArrivals().execute(work_stationID, WORK);
//                }
//            }
//        }, 1000);
//        ArrayList<String> stationIDList = new ArrayList<>();
//
//        favoritesSP = getSharedPreferences(favorite_sp, Context.MODE_PRIVATE);
//        gson = new Gson();
//        type = new TypeToken<TreeMap<String, String>>() {
//        }.getType();
//        String gsonString = favoritesSP.getString(Constants.favorite_gson, "");
//        if (gsonString.isEmpty()) {
//            favorites_no_info_TV.setVisibility(View.VISIBLE);
//
//        } else {
//            favorites_no_info_TV.setVisibility(View.GONE);
//            TreeMap<String, String> favStationMap = gson.fromJson(gsonString, type);
//            Set<String> stationIDSet = new ArraySet<>();
//            for (String key : favStationMap.keySet()) {
//                Log.v(TAG, "stations names fav" + key + ";" + favStationMap.get(key));
//                stationIDSet.add(favStationMap.get(key));
//            }
//            for (String key : stationIDSet) {
//                stationIDList.add(key);
//            }
//            favoritesLinearLayout.removeAllViews();
//            new GetArrivalsForFavorites().executeOnExecutor(THREAD_POOL_EXECUTOR, stationIDList, FAVORITE);
//
//        }
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        Handler mHandlerForOnStop = new Handler();
//        mHandlerForOnStop.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                finish();
//            }
//        }, 3 * 60 * 1000);
//    }
//}
