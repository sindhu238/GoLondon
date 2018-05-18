package com.srisindhusaride.golondon.Utils;

import android.view.View;

/**
 * @since  14/02/17.
 *
 * Used for declaring constants used by intent service class
 * @see com.srisindhusaride.golondon.GetAddressIntentService
 */

public class Constants {

    public static final int SUCCESS_RESULT = 0;
    public static final int FAILURE_RESULT = 1;
    private static final String PACKAGE_NAME =
            "com.google.android.gms.location.sample.locationaddress";
    public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
    public static final String RESULT_DATA_KEY = PACKAGE_NAME +
            ".RESULT_DATA_KEY";
    public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
            ".LOCATION_DATA_EXTRA";

    public static final String app_key_tfl = "8832cbb9a0c5728fcbc97d42688b4ae6";
    public static final String app_id_tfl = "979c862f";
    public static final String GOOGLE_MAPS_DISTANCE_API = "AIzaSyA6YG1aSgv1HuUpHFxBICu4kCPFvQkbyoo";

    //Used in Main Activity
    public static double latitude = 0;
    public static double longitude = 0;
    public static String currentAddress = "";
    public static boolean isLocationChangedByUser = false;
    public static String doNotShowLocationPermissionDialog_sp = "MainActivity_SP";
    public static String isDontShowAgainAlertDialog = "isDontShowAgainAlertDialog";

    public static boolean isAutoCompleteCalledBus = false;
    public static boolean isRefreshCalledBus = false;
    public static boolean isAutoCompleteCalledTube = false;
    public static boolean isRefreshCalledTube = false;

    public static View mainLayout;

    public static String stationName= "StationName";
    public static String lineName = "LineName";
    public static String latlng = "LatLng";
    public static String stationProperties = "StationProperties";

    //Used in Favorites activity
    public static String personal_sp = "PERSONAL SP";
    public static String favorite_sp = "FAVORITE_SP";
    public static String home_station_name = "HOME STATION NAME";
    public static String home_station_id = "HOME STATION ID";
    public static String work_station_name = "OFFICE STATION NAME";
    public static String work_station_id = "OFFICE STATION ID";
    public static String favorite_gson = "FAVORITE SET";

    public static String revealXFab = "REVEAL X FAB";
    public static String revealYFab = "REVEAL Y FAB";
    public static String lat = "lat";
    public static String lng= "lng";
    public static String address = "ADDRESS";

}
