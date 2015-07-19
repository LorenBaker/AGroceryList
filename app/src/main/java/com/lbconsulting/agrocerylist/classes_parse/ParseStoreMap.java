package com.lbconsulting.agrocerylist.classes_parse;

/**
 * a ParseObject that holds store map data
 * storeMapObjectName is the unique store name comprised of the store's chain name + Zip5 + zip4
 * jsonContent is a JSON string that holds the store's group location for each groupID
 * timestamp is the Android system time expressed in milliseconds
 */

import com.parse.ParseObject;
import com.parse.ParseClassName;

@ParseClassName("ParseStoreMap")
public class ParseStoreMap extends ParseObject {

    private final String STORE_ID = "storeID";
    private final String MAP_NAME = "mapName";
    private final String JASON_CONTENT = "jsonContent";
    private final String TIMESTAMP = "timestamp";

    public ParseStoreMap() {
        // A default constructor is required.
    }

    public long getStoreID() {
        return getLong(STORE_ID);
    }
    public void setStoreID(long storeID) {
        put(STORE_ID, storeID);
    }

    public String getMapName() {
        return getString(MAP_NAME);
    }
    public void setMapName(String mapName) {
        put(MAP_NAME, mapName);
    }

    public String getJsonContent() {
        return getString(JASON_CONTENT);
    }
    public void setJsonContent(String jsonContent) {
        put(JASON_CONTENT, jsonContent);
    }

    public long getTimestamp() {
        return getLong(TIMESTAMP);
    }
    public void setTimestamp(long timestamp) {
        put(TIMESTAMP, timestamp);
    }

}


