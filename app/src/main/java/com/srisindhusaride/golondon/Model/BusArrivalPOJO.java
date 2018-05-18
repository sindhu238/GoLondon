package com.srisindhusaride.golondon.Model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * @since 21/04/17.
 */

public class BusArrivalPOJO {

    private String lineName;
    private String platformName;
    private String destinationName;
    private int timeToArrival;
    private String stationName;
    private LatLng latLng;
    private int timeToWalkToStation;
    private String stationID;
    private ArrayList<String> busNamesList;

    public  BusArrivalPOJO(String stationName, int timeToWalkToStation, LatLng latLng, String stationID, String platformName) {
        this.stationName = stationName;
        this.timeToWalkToStation = timeToWalkToStation;
        this.latLng = latLng;
        this.stationID = stationID;
        this.platformName = platformName;
    }

    public  BusArrivalPOJO(String stationName, int timeToArrival, String stationID, String platformName,
                           String destinationName, String lineName) {
        this.stationName = stationName;
        this.timeToArrival = timeToArrival;
        this.destinationName = destinationName;
        this.lineName = lineName;
        this.stationID = stationID;
        this.platformName = platformName;
    }

    public BusArrivalPOJO(String lineName, String destinationName
            , int timeToStation, String stationName, LatLng latLng, int distance, String stationID
            , ArrayList<String> busNamesList) {
        this.lineName = lineName;
        this.destinationName = destinationName;
        this.timeToArrival = timeToStation;
        this.stationName = stationName;
        this.latLng = latLng;
        this.timeToWalkToStation = distance;
        this.stationID = stationID;
        this.busNamesList = busNamesList;
    }

    public ArrayList<String> getBusNamesList() {
        return busNamesList;
    }

    public String getStationID() {
        return stationID;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public int getTimeToWalkToStation() {
        return timeToWalkToStation;
    }

    public String getLineName() {
        return lineName;
    }

    public String getPlatformName() {
        return platformName;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public int getTimeToArrival() {
        return timeToArrival;
    }

    public String getStationName() {
        return stationName;
    }
}
