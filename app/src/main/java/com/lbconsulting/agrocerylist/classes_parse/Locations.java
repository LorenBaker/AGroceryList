package com.lbconsulting.agrocerylist.classes_parse;

/**
 * a ParseObject that holds Parse Locations data
 */

import android.database.Cursor;

import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.database.LocationsTable;
import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Locations")
public class Locations extends ParseObject {
    private static final String AUTHOR = "author";

    public Locations() {
        // A default constructor is required.
    }

    public void setLocation(String locationName, long sortKey) {
        setLocationName(locationName);
        setSortKey(sortKey);
        setAuthor(ParseUser.getCurrentUser());
    }

    public void setLocationCursor(Cursor cursor) {
        if (cursor == null) {
            return;
        }
        // String currentRow = DatabaseUtils.dumpCurrentRowToString(cursor);
        setLocationName(cursor.getString(cursor.getColumnIndex(LocationsTable.COL_LOCATION_NAME)));
        setSortKey(cursor.getLong(cursor.getColumnIndex(LocationsTable.COL_SORT_KEY)));
        setAuthor(ParseUser.getCurrentUser());
    }

    public long getSortKey() {
        return getLong(LocationsTable.COL_SORT_KEY);
    }

    public void setSortKey(long sortKey) {
        put(LocationsTable.COL_SORT_KEY, sortKey);
    }

    public String getLocationName() {
        return getString(LocationsTable.COL_LOCATION_NAME);
    }

    public void setLocationName(String locationName) {
        put(LocationsTable.COL_LOCATION_NAME, locationName);
    }

    public ParseUser getAuthor() {
        return getParseUser(AUTHOR);
    }

    public void setAuthor(ParseUser currentUser) {
        put(AUTHOR, currentUser);
    }

    public static ParseQuery<Locations> getQuery() {
        return ParseQuery.getQuery(Locations.class);
    }

    public static ParseObject getLocation(String parseObjectID) {
        ParseObject location = null;
        try {
            ParseQuery<Locations> query = getQuery();
            query.whereEqualTo("objectID", parseObjectID);
            List locations = query.find();
            if (locations!=null && locations.size() > 0) {
                location = (ParseObject) locations.get(0);
            }
        } catch (ParseException e) {
            MyLog.e("Locations", "getLocation: ParseException: " + e.getMessage());
        }
        return location;
    }
    public static void saveLocationToParse(Locations location, int saveType) {
        ParseACL locationACL = new ParseACL(ParseUser.getCurrentUser());
        locationACL.setPublicReadAccess(true);
        locationACL.setPublicWriteAccess(true);
        location.setACL(locationACL);
        try {
            switch (saveType) {
                case clsParseUtils.SAVE_THIS_THREAD:
                    // use SAVE_THIS_THREAD if this method is being called in an AsyncTask or background thread.
                    location.save();
                    MyLog.i("Locations", "saveLocationToParse: SAVE(): name = " + location.getLocationName());
                    break;

                case clsParseUtils.SAVE_IN_BACKGROUND:
                    location.saveInBackground();
                    MyLog.i("Locations", "saveLocationToParse: SAVE_IN_BACKGROUND(): name = " + location.getLocationName());
                    break;

                case clsParseUtils.SAVE_EVENTUALLY:
                    location.saveEventually();
                    MyLog.i("LocationsUtils", "saveLocationToParse: SAVE_EVENTUALLY(): name = " + location.getLocationName());
                    break;
            }

        } catch (ParseException e) {
            MyLog.e("Locations", "saveLocationToParse: ParseException: " + e.getMessage());
        }
    }
}


