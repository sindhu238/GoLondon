package com.srisindhusaride.golondon;

import static com.srisindhusaride.golondon.Utils.Constants.latitude;
import static com.srisindhusaride.golondon.Utils.Constants.longitude;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.srisindhusaride.golondon.Utils.Constants;
import com.srisindhusaride.golondon.Utils.InternetConnectivityReceiver;

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


/**
 * @author Sindhu
 * @version 1.0
 * @since 8-02-2017
 */

public class MainActivity1 extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, InternetConnectivityReceiver.InternetConnectivityReceiverListener {

    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 11;
    static ImageView mFavorites_fab;
    static MenuItem menuItem;
    private static Activity mContext;
    private final String REQUESTING_LOCATION_UPDATES_KEY = "REQUEST_LOCATION_UPDATES";
    private final String LOCATION_KEY = "LOCATION";
    private final String TAG = "MainActivity";
    private final int REQUEST_CHECK_SETTINGS = 12;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

//    @Inject
//    Nearby_Fragment nearby_fragment;

    private CoordinatorLayout mainLayout;
    private MapFragment mapFragment;
    private AppBarLayout mAppBarLayout;
    private GoogleApiClient mGoogleApiClient;
    private Location mCurrentLocation;
    private Location mUserRequestedLocation;
    private String mCurrentAddress;
    private double mCurrentAddressLatitude;
    private double mCurrentAddressLongitude;
    private Marker mCurrentLocationMarker;
    private LocationRequest mLocationRequest;
    private boolean mRequestingLocationUpdates = false; //Used to check if used has turned on Location Updates
    private GoogleMap mGoogleMap;
    private AddressResultReceiver mResultReceiver; //Receiver to get result from intent service
    private LatLng intentLatLng = null;
    private String intentAddress = "";
    private boolean isPlaceAutoFillIntentOpened = false;

    boolean doubleBackToExitPressedOnce = false;
    private boolean mDontShowLocationPermissionRequestDialog = false;


    static void vibrateFab() {
        Animation vibrateAnimation = AnimationUtils.loadAnimation(mContext, R.anim.vibrate);
        vibrateAnimation.setRepeatCount(3);
        mFavorites_fab.startAnimation(vibrateAnimation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main1);
        mContext = this;

        mainLayout = (CoordinatorLayout) findViewById(R.id.main_layout);
        Constants.mainLayout = mainLayout;
        //Inject dependencies
//        ObjectFactoryActivityScopeComponent component = DaggerObjectFactoryActivityScopeComponent.builder().objectFactoryActivityScopeModule(new ObjectFactoryActivityScopeModule()).build();
//        component.inject(this);

        //To reset the constants values when activity is created newly
        latitude = 0.0;
        longitude = 0.0;
        Constants.currentAddress = "";
        Constants.isLocationChangedByUser = false;

        if (getIntent() != null) {
            Double lat = getIntent().getDoubleExtra(Constants.lat, 0);
            Double lng = getIntent().getDoubleExtra(Constants.lng, 0);
//            intentAddress = getIntent().getStringExtra(address);
            intentLatLng = new LatLng(lat, lng);
        }
        //Handles activity moving from background and foreground
        updateValuesFromBundle(savedInstanceState);

        if (InternetConnectivityReceiver.isConnected())
            mResultReceiver = new AddressResultReceiver(new Handler());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);

        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

//        getFragmentManager().beginTransaction().add(R. (), "");

        DisplayMetrics displayMetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        int requiredHeightInPX = height - 550;
        ViewGroup.LayoutParams layoutParams = mapFragment.getView().getLayoutParams();
        layoutParams.height = requiredHeightInPX;
        mapFragment.getView().setLayoutParams(layoutParams);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar_layout);

        //Disable scrolling when user scrolls on map. User can only scroll only when he scrolls in
        //scrollable layout like Swipe Refresh Layout
        if (mAppBarLayout.getLayoutParams() != null) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mAppBarLayout.getLayoutParams();
            AppBarLayout.Behavior behavior = new AppBarLayout.Behavior();
            behavior.setDragCallback(new AppBarLayout.Behavior.DragCallback() {
                @Override
                public boolean canDrag(@NonNull AppBarLayout appBarLayout) {
                    return false;
                }
            });
            params.setBehavior(behavior);
        }

        mFavorites_fab = (ImageView) findViewById(R.id.favorites_fab);
        mFavorites_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (InternetConnectivityReceiver.isConnected()) {

                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mContext, view, "transition");

                    int revealX = (int) (view.getX() + view.getWidth() / 2);
                    int revealY = (int) (view.getY() + view.getHeight() / 2);

//                    Intent intent = new Intent(mContext, Favorites_Activity.class);
//                    intent.putExtra(Constants.revealXFab, revealX);
//                    intent.putExtra(Constants.revealYFab, revealY);
//
//                    ActivityCompat.startActivity(mContext, intent, optionsCompat.toBundle());
                } else {
                    InternetConnectivityReceiver.displaySnackBar(mainLayout);
                }
            }
        });
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    private LocationRequest createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(30000);
        mLocationRequest.setFastestInterval(30000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return mLocationRequest;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(createLocationRequest());

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                //final LocationSettingsStates settingsStates = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        mLocationRequest = createLocationRequest();
                        startLocationUpdates();
                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(mContext, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    //Callback for startResolutionForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_PERMISSION_ACCESS_FINE_LOCATION) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    startLocationUpdates();
                    break;
                case Activity.RESULT_CANCELED:
                    Log.i(TAG, "User disagreed to change location settings");

            }
        } else if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            isPlaceAutoFillIntentOpened = true;
            switch (resultCode) {
                case RESULT_OK:
                    Place place = PlaceAutocomplete.getPlace(this, data);

                    placeMarkerOnMap(place.getLatLng(), "Your Location");
                    if (InternetConnectivityReceiver.isConnected()) {
                        Constants.isAutoCompleteCalledBus = true;
                        Constants.isAutoCompleteCalledTube = true;
                        latitude = place.getLatLng().latitude;
                        longitude = place.getLatLng().longitude;

//                        new GetNearbyBusLocationsForMap().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                        new GetNearbyTrainLocationsForMap().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//                        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, nearby_fragment).commit();
//                        nearby_fragment.updateUiWidgets(place.getAddress().toString());

                        Constants.isLocationChangedByUser = true;
                    } else {
                        InternetConnectivityReceiver.displaySnackBar(mainLayout);
                    }

                case RESULT_CANCELED:
                    break;
                case PlaceAutocomplete.RESULT_ERROR:
                    Status status = PlaceAutocomplete.getStatus(this, data);
                    Log.e(TAG, status.getStatusMessage());
            }
        }
    }

    //Callback for requestPermissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MY_PERMISSION_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //DO CHECK PERMISSION
                    if (mContext.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        //DO OP WITH LOCATION SERVICE
                        mRequestingLocationUpdates = true;
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    }
                }
            }

        }
    }

    //Start getting location updates if user granted permission
    private void startLocationUpdates() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //DO CHECK PERMISSION
            if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mRequestingLocationUpdates = true;
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

            } else {
                SharedPreferences sp = mContext.getSharedPreferences(Constants.doNotShowLocationPermissionDialog_sp, MODE_PRIVATE);
                final SharedPreferences.Editor editor = sp.edit();

                if (!sp.getBoolean(Constants.isDontShowAgainAlertDialog, false)) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                        new AlertDialog.Builder(mContext).setTitle("LOCATION PERMISSION").setMessage("Go London would require your location access permissions to " + "get nearby tube stations and bus stops. If not interested in sharing your location," + " you can still search for particular location").setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(mContext, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
                            }
                        }).setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        }).setNeutralButton("Never", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDontShowLocationPermissionRequestDialog = true;
                                editor.putBoolean(Constants.isDontShowAgainAlertDialog, mDontShowLocationPermissionRequestDialog);
                                editor.apply();
                            }
                        }).show();

                    } else {
                        ActivityCompat.requestPermissions(mContext, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
                    }
                }


            }
        }

    }

    //Sets location to previous stage when app goes to background and becomes active again
    void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(REQUESTING_LOCATION_UPDATES_KEY);
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (intentLatLng.longitude != 0.0 && intentLatLng.latitude != 0.0) {
            mCurrentAddressLatitude = intentLatLng.latitude;
            mCurrentAddressLongitude = intentLatLng.longitude;
        } else {
            mCurrentAddressLatitude = location.getLatitude();
            mCurrentAddressLongitude = location.getLongitude();
        }
        if (!(mCurrentLocation != null && mCurrentLocation.getLatitude() == location.getLatitude() && mCurrentLocation.getLongitude() == location.getLongitude())) {
            if (!Constants.isLocationChangedByUser) {
                placeMarkerOnMap(new LatLng(mCurrentAddressLatitude, mCurrentAddressLongitude), "Your Location");
                //To get address from latitude and longitude of current location
                startIntentService(mCurrentLocation);
            } else {
                placeMarkerOnMap(new LatLng(latitude, longitude), "Your location");
                mUserRequestedLocation = new Location("Temp Location");
                mUserRequestedLocation.setLatitude(latitude);
                mUserRequestedLocation.setLongitude(longitude);
                startIntentService(mUserRequestedLocation);
            }
        }

        if (!Constants.isLocationChangedByUser) {
            mCurrentLocation = location;
            latitude = mCurrentAddressLatitude;
            longitude = mCurrentAddressLongitude;

            new GetNearbyBusLocationsForMap().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            new GetNearbyTrainLocationsForMap().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

//        if (InternetConnectivityReceiver.isConnected())
//            getSupportFragmentManager().beginTransaction().replace(R.id.main_content, nearby_fragment).commit();
//        else {
//            InternetConnectivityReceiver.displaySnackBar(mainLayout);
//        }


    }

    private void startIntentService(Location location) {
        Intent intent = new Intent(mContext, GetAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(Constants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(Constants.LOCATION_DATA_EXTRA, location);

        startService(intent);
    }

    void placeMarkerOnMap(LatLng latLng, String title) {
        mGoogleMap.clear();

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(title);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

        mCurrentLocationMarker = mGoogleMap.addMarker(markerOptions);
//        mCurrentLocationMarker.setIcon(BitmapDescriptorFactory.fromResource(marker));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));

    }

//    private BitmapDescriptor getMarkerFromIconGenerator(String letter) {
////        Drawable markerBgBrawable = ContextCompat.getDrawable(mContext, R.drawable.stops_marker_circle);
////        IconGenerator iconGenerator = new IconGenerator(mContext);
////        iconGenerator.setBackground(markerBgBrawable);
//
//
//        LayoutInflater myInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View view = myInflater.inflate(R.layout.marker_text, null, false);
//
//        TextView textView = (TextView) view.findViewById(R.id.text);
//        textView.setText(letter);
//
//        iconGenerator.setContentView(view);
//        if (letter.length() == 1) iconGenerator.setContentPadding(18, 10, 5, 15);
//        else iconGenerator.setContentPadding(14, 10, 16, 15);
//        iconGenerator.makeIcon(letter);
//        return BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon());
//    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;


        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
//            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.google_map_style_json));
//
//            if (!success) {
//                Log.e(TAG, "Style parsing failed.");
//            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        mGoogleMap.setPadding(0, 0, 0, 60);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mapFragment.getMapAsync(this);
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MainApplication.getInstance().setConnectivityListener(this);
    }

    /**
     * Callback for {@link InternetConnectivityReceiver}
     *
     * @param isConnected Parameter that tells if internet is connected or not
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        try {
            if (!isConnected) InternetConnectivityReceiver.displaySnackBar(mainLayout);
            else {
                InternetConnectivityReceiver.dismissSnackBar();
//                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, nearby_fragment).commit();
                mResultReceiver = new AddressResultReceiver(new Handler());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        outState.putParcelable(LOCATION_KEY, mCurrentLocation);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // mGoogleMap.clear();
        if (mGoogleApiClient.isConnected())
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        this.menuItem = item;
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_search) {
            try {
                AutocompleteFilter typeFilter = new AutocompleteFilter.Builder().setCountry("GB").build();
                Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).setFilter(typeFilter).build(this);

                //Gets result to onActivityResult() function
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (GooglePlayServicesRepairableException e) {
                Log.e(TAG, e.getLocalizedMessage());
            } catch (GooglePlayServicesNotAvailableException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }

        if (id == R.id.action_refresh) {
            if (InternetConnectivityReceiver.isConnected()) {
                Constants.isRefreshCalledBus = true;
                Constants.isRefreshCalledTube = true;

                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ImageView iv = (ImageView) inflater.inflate(R.layout.rotate_refresh_menu, null);

                Animation rotation = AnimationUtils.loadAnimation(this, R.anim.rotate_one_circle);

                iv.startAnimation(rotation);
                item.setActionView(iv);


                //Refresh map
                if (!Constants.isLocationChangedByUser) startLocationUpdates();

                //Refresh data displayed to user
//                getSupportFragmentManager().beginTransaction().detach(nearby_fragment).attach(nearby_fragment).commit();
            } else {
                InternetConnectivityReceiver.displaySnackBar(mainLayout);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            super.onReceiveResult(resultCode, resultData);

            if (resultCode == Constants.SUCCESS_RESULT) {
                mCurrentAddress = resultData.getString(Constants.RESULT_DATA_KEY);
//                if (intentLatLng.latitude == 0.0) nearby_fragment.updateUiWidgets(mCurrentAddress);
//                else nearby_fragment.updateUiWidgets(intentAddress);
            } else {
                if (Constants.isLocationChangedByUser && mUserRequestedLocation != null)
                    startIntentService(mUserRequestedLocation);
                else startIntentService(mCurrentLocation);
            }
        }
    }

    int countForBus = 0;
    int countForTrain = 0;
    ArrayList<String> namesListBus;
    ArrayList<String> namesListTube;

    private class GetNearbyBusLocationsForMap extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String urlString = "https://api.tfl.gov.uk/Stoppoint?lat=" + latitude + "&lon=" + longitude + "&stoptypes=NaptanBusCoachStation,NaptanPublicBusCoachTram&radius=1000&useStopPointHierarchy=true" + "&categories=none&app_id=" + Constants.app_id_tfl + "&app_key=" + Constants.app_key_tfl;

            Log.v(TAG, "url:" + urlString);

            HttpURLConnection urlConnection = null;

            BufferedReader bufferedReader;
            String line;
            InputStream inputStream;
            StringBuilder json_result = new StringBuilder();
            try {
                System.setProperty("http.keepAlive", "false");
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(3000);
                urlConnection.setConnectTimeout(3000);
                urlConnection.setRequestMethod("GET");
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                while ((line = bufferedReader.readLine()) != null) {
                    json_result.append(line);
                }
                getDataFromJSON(json_result.toString());

            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                if (mContext != null) mContext.runOnUiThread(new Runnable() {
                    public void run() {
                        if (InternetConnectivityReceiver.isConnected()) {
                            countForBus++;
                            if (countForBus < 5) new GetNearbyBusLocationsForMap().execute();
                        }
                    }
                });
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        private void getDataFromJSON(String json) {

            namesListBus = new ArrayList<>();

            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray stopPoints = jsonObject.getJSONArray("stopPoints");
                if (stopPoints.length() != 0) {
                    int lengthOfStopsList;

                    //To get stops points only within 20
                    if (stopPoints.length() <= 20 && stopPoints.length() != 0) {
                        lengthOfStopsList = stopPoints.length();
                    } else {
                        lengthOfStopsList = 20;
                    }
                    for (int i = 0; i < lengthOfStopsList; i++) {
                        JSONObject jsonObject1 = stopPoints.getJSONObject(i);

                        JSONArray modes = jsonObject1.getJSONArray("modes");
                        for (int j = 0; j < modes.length(); j++) {
                            if (modes.get(j).toString().contentEquals("bus")) {
                                JSONArray lineGroup = jsonObject1.getJSONArray("lineGroup");

                                if (lineGroup.length() > 0) {
                                    String commonName = jsonObject1.getString("commonName");
                                    Double lat = jsonObject1.getDouble("lat");
                                    Double lon = jsonObject1.getDouble("lon");
                                    String stopLetter = jsonObject1.optString("stopLetter");

                                    final String letter;
                                    if (stopLetter.contains("->"))
                                        letter = stopLetter.replace("->", "");
                                    else letter = stopLetter;

                                    if (!stopLetter.isEmpty())
                                        commonName = commonName + " :: " + letter;

                                    namesListBus.add(commonName);

                                    final MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.position(new LatLng(lat, lon));
                                    markerOptions.title(commonName);

                                    mContext.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Marker marker = mGoogleMap.addMarker(markerOptions);
//                                            marker.setIcon(getMarkerFromIconGenerator(letter));
                                        }
                                    });
                                }
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
    }

    private class GetNearbyTrainLocationsForMap extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            String baseUrl = "https://api.tfl.gov.uk/Stoppoint?";
            String types = "NaptanMetroStation";
            String urlString = baseUrl + "lat=" + latitude + "&lon=" + longitude + "&stoptypes=" + types + "&radius=1000" + "&useStopPointHierarchy=true&categories=facility&app_id=" + Constants.app_id_tfl + "&app_key=" + Constants.app_key_tfl;


            HttpURLConnection urlConnection = null;

            BufferedReader bufferedReader;
            String line;
            InputStream inputStream;
            StringBuilder json_result = new StringBuilder();
            try {
                System.setProperty("http.keepAlive", "false");
                System.setProperty("java.net.preferIPv4Stack", "true");

                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setReadTimeout(3000);
                urlConnection.setConnectTimeout(3000);
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                while ((line = bufferedReader.readLine()) != null) {
                    json_result.append(line);
                }
                getDataFromJSON(json_result.toString());

            } catch (Exception e) {
                Log.e(TAG, e.getLocalizedMessage());
                if (mContext != null) mContext.runOnUiThread(new Runnable() {
                    public void run() {
                        if (InternetConnectivityReceiver.isConnected()) {
                            countForTrain++;
                            if (countForTrain < 5) new GetNearbyTrainLocationsForMap().execute();
                        }
                    }
                });
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return null;
        }

        private void getDataFromJSON(String json) {

            namesListTube = new ArrayList<>();

            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONArray stopPoints = jsonObject.getJSONArray("stopPoints");

                for (int i = 0; i < stopPoints.length() && i < 20; i++) {
                    JSONObject jsonObject1 = stopPoints.getJSONObject(i);
                    final String commonName = jsonObject1.getString("commonName");
                    final double lat = jsonObject1.getDouble("lat");
                    final double lon = jsonObject1.getDouble("lon");
                    namesListTube.add(commonName);

                    final MarkerOptions markerOptions1 = new MarkerOptions();
                    markerOptions1.position(new LatLng(lat, lon));
                    markerOptions1.title(commonName);
                    markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Marker marker1 = mGoogleMap.addMarker(markerOptions1);
//                            marker1.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.train_station));
                        }
                    });
                }

                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                            @Override
                            public void onInfoWindowClick(Marker marker) {
                                for (int i = 0; i < namesListBus.size(); i++) {
                                    if (namesListBus.get(i).contentEquals(marker.getTitle())) {
                                        mAppBarLayout.setExpanded(false, true);
//                                        Nearby_Fragment.viewPager.setCurrentItem(0);
//                                        Nearby_Viewpager_Stops.mRecyclerView.smoothScrollToPosition(i);
                                        break;
                                    }
                                }
                                for (int i = 0; i < namesListTube.size(); i++) {
                                    if (namesListTube.get(i).contentEquals(marker.getTitle())) {
                                        mAppBarLayout.setExpanded(false, true);
//                                        Nearby_Fragment.viewPager.setCurrentItem(1);
//                                        Nearby_ViewPager_Stations.mRecyclerView.smoothScrollToPosition(i);
                                        break;
                                    }
                                }

                            }
                        });
                    }
                });

            } catch (JSONException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce || !isTaskRoot()) {
            super.onBackPressed();
        } else {
            if (isTaskRoot()) {
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        }
    }
}
