package com.lbconsulting.agrocerylist.classes_parse;

import java.util.ArrayList;

/**
 * This class holds an array of clsParseStoreMapEntryArray
 */
public class clsParseStoreMapEntryArray {
    private ArrayList<clsParseStoreMapEntry> storeMap;

    public clsParseStoreMapEntryArray(ArrayList<clsParseStoreMapEntry> storeMap){
        this.storeMap = storeMap;
    }

    public ArrayList<clsParseStoreMapEntry> getStoreMap() {
        return storeMap;
    }

    public void setStoreMap(ArrayList<clsParseStoreMapEntry> storeMap) {
        this.storeMap = storeMap;
    }
}
