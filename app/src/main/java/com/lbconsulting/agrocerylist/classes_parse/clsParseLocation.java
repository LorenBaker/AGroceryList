package com.lbconsulting.agrocerylist.classes_parse;

/**
 * This class holds public location data.
 */
public class clsParseLocation {
    private long locationID;
    private String locationName;

    public clsParseLocation(long locationID, String locationName) {
        this.locationID = locationID;
        this.locationName = locationName;
    }

    public long getLocationID() {
        return locationID;
    }

    public void setLocationID(long locationID) {
        this.locationID = locationID;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    @Override
    public String toString() {
        return locationName;
    }
}
