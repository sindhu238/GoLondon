package com.srisindhusaride.golondon.DI.Module;

import com.srisindhusaride.golondon.DI.Scope.ActivityScope;
import com.srisindhusaride.golondon.Model.StationArrivalPOJO;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

import java.util.HashMap;

import dagger.Module;
import dagger.Provides;

/**
 * @since  17/02/17.
 */

@Module
public class NearbyStationsAndStopsModule {
    private String stationName;
    private String lineName;
    private String platformName;
    private String destinationName;
    private int timeToStation;
    private String currentLocation;
    private String towards;
    private long timeToWalkToStation;
    private HashMap<String, JSONArray> commonProperties;
    private HashMap<String, LatLng> stationLatLng;
    private String stationID;

    public NearbyStationsAndStopsModule(String stationName, String lineName, String platformName,
                                        String destinationName, int timeToStation,
                                        String currentLocation, String towards,long timeToWalkToStation,
                                        HashMap<String, JSONArray> commonProperties, HashMap<String, LatLng> stationLatLng, String stationID) {
        this.stationName = stationName;
        this.lineName = lineName;
        this.platformName = platformName;
        this.destinationName = destinationName;
        this.timeToStation = timeToStation;
        this.currentLocation = currentLocation;
        this.towards = towards;
        this.timeToWalkToStation = timeToWalkToStation;
        this.commonProperties = commonProperties;
        this.stationLatLng = stationLatLng;
        this.stationID = stationID;
    }

    @ActivityScope
    @Provides
    StationArrivalPOJO providesStationArrivalPOJO() {
        return new StationArrivalPOJO(stationName,lineName,platformName,destinationName,timeToStation,currentLocation,
                towards, timeToWalkToStation, commonProperties, stationLatLng, stationID );
    }
}
