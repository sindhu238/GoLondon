package com.srisindhusaride.golondon.Model;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @since  17/02/17.
 */

public class StationArrivalPOJO {

    private String stationName;
    private String lineName;
    private String platformName;
    private String destinationName;
    private int timeToStation;
    private String currentLocation;
    private String towards;
    private HashMap<String, JSONArray> commonProperties;
    private HashMap<String, LatLng> stationLatLng;
    private long timeToWalkToStation;
    private LatLng latLng;
    private String stationID;
    private ArrayList<String> lineNameList;

    public  StationArrivalPOJO(String stationName, int timeToStation, String destinationName, String stationID,
                               String platformName,String lineName, String towards) {
        this.stationName = stationName;
        this.timeToStation = timeToStation;
        this.destinationName = destinationName;
        this.stationID = stationID;
        this.platformName = platformName;
        this.lineName = lineName;
        this.towards = towards;
    }

    public  StationArrivalPOJO(String stationName, int timeToWalkToStation, LatLng latLng, String stationID,
                               ArrayList<String> lineNameList) {
        this.stationName = stationName;
        this.timeToWalkToStation = timeToWalkToStation;
        this.latLng = latLng;
        this.stationID = stationID;
        this.lineNameList = lineNameList;
    }

    public StationArrivalPOJO(String stationName, String lineName, String platformName,
                              String destinationName, int timeToStation, String currentLocation
            , String towards, long timeToWalkToStation, HashMap<String, JSONArray> commonProperties,
                              HashMap<String, LatLng> stationLatLng, String stationID) {
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

    public ArrayList<String> getLineNameList() {
        return lineNameList;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public String getStationID() {
        return stationID;
    }

    public HashMap<String, LatLng> getStationLatLng() {
        return stationLatLng;
    }

    public long getTimeToWalkToStation() {
        return timeToWalkToStation;
    }

    public HashMap<String, JSONArray> getCommonProperties() {
        return  commonProperties;
    }

    public String getPlatformName() {
        return platformName;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public int getTimeToStation() {
        return timeToStation;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public String getTowards() {
        return towards;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

}
