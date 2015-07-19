package com.lbconsulting.agrocerylist.classes_parse;

/**
 * a ParseObject that holds Parse Locations data
 */

import android.database.Cursor;

import com.lbconsulting.agrocerylist.database.GroupsTable;
import com.lbconsulting.agrocerylist.database.LocationsTable;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Locations")
public class Locations extends ParseObject {
    private static final String COL_LOCATION_ID = "locationID";
    private static final String AUTHOR = "author";

    public Locations() {
        // A default constructor is required.
    }

    public void setLocation(long id, String groupLocation) {
        setLocationID(id);
        setLocationName(groupLocation);
        setAuthor(ParseUser.getCurrentUser());
    }

    public void setLocationsCursor(Cursor cursor) {
        if (cursor == null) {
            return;
        }
        // String currentRow = DatabaseUtils.dumpCurrentRowToString(cursor);
        setLocationID(cursor.getLong(cursor.getColumnIndex(LocationsTable.COL_LOCATION_ID)));
        setLocationName(cursor.getString(cursor.getColumnIndex(LocationsTable.COL_LOCATION_NAME)));
        setAuthor(ParseUser.getCurrentUser());
    }

    public long getLocationID() {
        return getLong(COL_LOCATION_ID);
    }

    public void setLocationID(long locationID) {
        put(COL_LOCATION_ID, locationID);
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



}


