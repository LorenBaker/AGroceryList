package com.lbconsulting.agrocerylist.classes_parse;

/**
 * a ParseObject that holds store map data
 * storeMapObjectName is the unique store name comprised of the store's chain name + Zip5 + zip4
 * jsonContent is a JSON string that holds the store's group location for each groupID
 * timestamp is the Android system time expressed in milliseconds
 */

import com.parse.ParseObject;
import com.parse.ParseClassName;

@ParseClassName("StoreMaps")
public class StoreMaps extends ParseObject {

    public StoreMaps() {
        // A default constructor is required.
    }

    public String getMapName() {
        return getString("mapName");
    }
    public void setMapName(String mapName) {
        put("mapName", mapName);
    }

    public String getJsonContent() {
        return getString("jsonContent");
    }
    public void setJsonContent(String jsonContent) {
        put("jsonContent", jsonContent);
    }

    public long getTimestamp() {
        return getLong("timestamp");
    }
    public void setTimestamp(long timestamp) {
        put("timestamp", timestamp);
    }

}


