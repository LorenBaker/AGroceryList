package com.lbconsulting.agrocerylist.classes_parse;

import java.util.ArrayList;

/**
 * This class holds an array of clsParseLocation
 */
public class clsParseLocationArray {
    private ArrayList<clsParseLocation> locations;

    public clsParseLocationArray(ArrayList<clsParseLocation> locations){
        this.locations = locations;
    }

    public ArrayList<clsParseLocation> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<clsParseLocation> locations) {
        this.locations = locations;
    }
}
