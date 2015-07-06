package com.lbconsulting.agrocerylist.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;
import com.lbconsulting.agrocerylist.classes.clsLocation;

import java.util.ArrayList;

public class LocationsTable {

    public static final String TABLE_LOCATIONS = "tblLocations";
    public static final String COL_LOCATION_ID = "_id";
    public static final String COL_LOCATION_NAME = "locationName";

    public static final String[] PROJECTION_ALL = {COL_LOCATION_ID, COL_LOCATION_NAME};

    public static final String CONTENT_PATH = TABLE_LOCATIONS;
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting."
            + TABLE_LOCATIONS;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting."
            + TABLE_LOCATIONS;
    public static final Uri CONTENT_URI = Uri.parse("content://" + aGroceryListContentProvider.AUTHORITY + "/" + CONTENT_PATH);

    public static final String SORT_ORDER_LOCATION_NAME = COL_LOCATION_NAME + " ASC";
    public static final String SORT_ORDER_LOCATION_ID = COL_LOCATION_ID + " ASC";

    // Database creation SQL statements
    private static final String CREATE_TABLE = "create table "
            + TABLE_LOCATIONS
            + " ("
            + COL_LOCATION_ID + " integer primary key autoincrement, "
            + COL_LOCATION_NAME + " text collate nocase DEFAULT '' "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
        MyLog.i("LocationsTable", "onCreate: " + TABLE_LOCATIONS + " created.");
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // This wipes out all of the user data and create an empty table
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        MyLog.i("LocationsTable", "onUpgrade: " + TABLE_LOCATIONS + " upgraded.");
        onCreate(database);
    }
// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Create Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void createInitialLocationsIfNeeded(Context context) {
        if (isTableEmpty(context)) {
            String[] initialLocations = context.getResources().getStringArray(R.array.initial_location_list);
            ContentResolver cr = context.getContentResolver();
            Uri uri = CONTENT_URI;
            ContentValues values;

            for (String location : initialLocations) {
                values = new ContentValues();
                values.put(COL_LOCATION_NAME, location);
                cr.insert(uri, values);
            }

            createNewAisles(context, MySettings.INITIAL_NUMBER_OF_AISLES);
        }
    }


    public static void createNewAisles(Context context, int maxNumberOfAisles) {

        int numberOfAisles = getNumberOfAisles(context);
        int numberOfAislesToCreate = maxNumberOfAisles - numberOfAisles;
        if (numberOfAislesToCreate < 1) {
            // Nothing to do
            return;
        }

        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        ContentValues values;
        int aisleNumber = numberOfAisles + 1;
        String aisleName;

        for (int i = 0; i < numberOfAislesToCreate; i++) {
            values = new ContentValues();
            aisleName = "Aisle " + aisleNumber;
            values.put(COL_LOCATION_NAME, aisleName);
            cr.insert(uri, values);
            aisleNumber++;
        }
    }


// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Read Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static boolean isTableEmpty(Context context) {
        boolean result = true;
        Cursor cursor = getAllLocationsCursor(context, SORT_ORDER_LOCATION_ID);
        if (cursor != null) {
            result = cursor.getCount() == 0;
            cursor.close();
        }
        return result;
    }

    public static int getNumberOfAisles(Context context) {
        int numberOfAisles = -1;
        Cursor cursor = getAllLocationsCursor(context, SORT_ORDER_LOCATION_ID);
        if (cursor != null) {
            numberOfAisles = cursor.getCount() - 9;
            cursor.close();
        }
        return numberOfAisles;
    }

    private static Cursor getAllLocationsCursor(Context context, String sortOrder) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = null;
        String[] selectionArgs = null;
        try {
            ContentResolver cr = context.getContentResolver();
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("LocationsTable", "getAllLocationsCursor: Exception; " + e.getMessage());
        }
        return cursor;
    }

    public static ArrayList<String> getAllItemLocations(Context context, String sortOrder) {
        createInitialLocationsIfNeeded(context);
        ArrayList<String> itemLocations = new ArrayList<>();
        Cursor cursor = getAllLocationsCursor(context, sortOrder);
        if (cursor != null && cursor.getCount() > 0) {
            String locationName;
            while (cursor.moveToNext()) {
                locationName = cursor.getString(cursor.getColumnIndex(COL_LOCATION_NAME));
                itemLocations.add(locationName);
            }
            cursor.close();
        }
        return itemLocations;
    }

    public static long getNumberOfLocations(Context context) {
        long numberOfLocations = 0;
        Cursor cursor = getAllLocationsCursor(context, null);
        if (cursor != null) {
            numberOfLocations = cursor.getCount();
            cursor.close();
        }
        return numberOfLocations;
    }

// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Update Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int updateLocationName(Context context, long locationID, String locationName) {
        int numberOfUpdatedRecords = -1;
        // cannot update the default location
        if (locationID > 1) {
            ContentResolver cr = context.getContentResolver();
            Uri GroupUri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(locationID));
            String selection = null;
            String[] selectionArgs = null;
            ContentValues cv = new ContentValues();
            cv.put(COL_LOCATION_NAME, locationName);
            numberOfUpdatedRecords = cr.update(GroupUri, cv, selection, selectionArgs);
        } else {
            MyLog.e("LocationsTable", "updateLocationName: Invalid locationID.");
        }
        return numberOfUpdatedRecords;
    }

    public static ArrayList<clsLocation> getLocationsArray(Context context) {
        ArrayList<clsLocation> list = new ArrayList<>();
        Cursor cursor = getAllLocationsCursor(context, SORT_ORDER_LOCATION_ID);
        if (cursor != null && cursor.getCount() > 0) {
            clsLocation location;
            long locationID;
            String locationName;
            while (cursor.moveToNext()) {
                locationID = cursor.getLong(cursor.getColumnIndex(COL_LOCATION_ID));
                locationName = cursor.getString(cursor.getColumnIndex(COL_LOCATION_NAME));
                location = new clsLocation(locationID,locationName);
                list.add(location);
            }
        }

        return list;
    }


// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Delete Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int deleteLocation(Context context, long locationID) {
        int numberOfDeletedRecords = 0;
        // don't delete the default location
        if (locationID > 1) {
            ContentResolver cr = context.getContentResolver();
            Uri uri = CONTENT_URI;
            String selection = COL_LOCATION_ID + " = ?";
            String[] selectionArgs = {String.valueOf(locationID)};
            numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);
            StoreMapsTable.resetLocationID(context, locationID);
        }
        return numberOfDeletedRecords;
    }
}


