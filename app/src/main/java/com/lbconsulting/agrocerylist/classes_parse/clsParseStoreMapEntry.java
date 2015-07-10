package com.lbconsulting.agrocerylist.classes_parse;

/**
 * This class hold store map information.
 */
public class clsParseStoreMapEntry {

    private long groupID;
    private long locationID;

    public clsParseStoreMapEntry(long groupID, long locationID) {
        this.groupID = groupID;
        this.locationID = locationID;
    }

    public long getGroupID() {
        return groupID;
    }

    public void setGroupID(long groupID) {
        this.groupID = groupID;
    }

    public long getLocationID() {
        return locationID;
    }

    public void setLocationID(long locationID) {
        this.locationID = locationID;
    }



}
