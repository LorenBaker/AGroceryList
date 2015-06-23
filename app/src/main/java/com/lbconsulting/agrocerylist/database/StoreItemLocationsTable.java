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

import java.util.ArrayList;


/**
 * SQLite table to hold Store location data
 */
public class StoreItemLocationsTable {


    // Users data table
    // Version 1
    public static final String TABLE_STORE_ITEM_LOCATIONS = "tblStoreItemLocations";
    public static final String COL_STORE_ITEM_LOCATION_ID = "_id";
    public static final String COL_STORE_ITEM_LOCATION = "storeItemLocation";


    public static final String[] PROJECTION_ALL = {COL_STORE_ITEM_LOCATION_ID, COL_STORE_ITEM_LOCATION};

    public static final String CONTENT_PATH = TABLE_STORE_ITEM_LOCATIONS;

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting."
            + TABLE_STORE_ITEM_LOCATIONS;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting."
            + TABLE_STORE_ITEM_LOCATIONS;
    public static final Uri CONTENT_URI = Uri.parse("content://" + aGroceryListContentProvider.AUTHORITY + "/" + CONTENT_PATH);

    private static final String SORT_STORE_ITEM_LOCATION_ID = COL_STORE_ITEM_LOCATION_ID + " ASC ";


    // Database creation SQL statements
    private static final String CREATE_DATA_TABLE = "create table "
            + TABLE_STORE_ITEM_LOCATIONS
            + " ("
            + COL_STORE_ITEM_LOCATION_ID + " integer primary key autoincrement, "
            + COL_STORE_ITEM_LOCATION + " text  DEFAULT '' "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_DATA_TABLE);
        MyLog.i("StoreItemLocationsTable", "onCreate: " + TABLE_STORE_ITEM_LOCATIONS + " created.");
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // This wipes out all of the user data and create an empty table
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_STORE_ITEM_LOCATIONS);
        onCreate(database);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Create Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static void createInitialLocationsIfNeeded(Context context) {
        if (isTableEmpty(context)) {
            String[] initialLocations = context.getResources().getStringArray(R.array.initial_location_list);
            ContentResolver cr = context.getContentResolver();
            Uri uri = CONTENT_URI;
            ContentValues values;

            for (String location : initialLocations) {
                values = new ContentValues();
                values.put(COL_STORE_ITEM_LOCATION, location);
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
            values.put(COL_STORE_ITEM_LOCATION, aisleName);
            cr.insert(uri, values);
            aisleNumber++;
        }
    }


// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Read Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static boolean isTableEmpty(Context context) {
        boolean result = true;
        Cursor cursor = getAllLocationsCursor(context);
        if (cursor != null) {
            result = cursor.getCount() == 0;
            cursor.close();
        }
        return result;
    }

    public static int getNumberOfAisles(Context context) {
        int numberOfAisles = -1;
        Cursor cursor = getAllLocationsCursor(context);
        if (cursor != null) {
            numberOfAisles = cursor.getCount() - 8;
            cursor.close();
        }
        return numberOfAisles;
    }

    private static Cursor getAllLocationsCursor(Context context) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = null;
        String[] selectionArgs = null;
        try {
            ContentResolver cr = context.getContentResolver();
            cursor = cr.query(uri, projection, selection, selectionArgs, SORT_STORE_ITEM_LOCATION_ID);
        } catch (Exception e) {
            MyLog.e("StoreItemLocationsTable", "getAllLocationsCursor: Exception; " + e.getMessage());
        }
        return cursor;
    }

    public static ArrayList<String> getAllItemLocations(Context context) {
        createInitialLocationsIfNeeded(context);
        ArrayList<String> itemLocations = new ArrayList<>();
        Cursor cursor = getAllLocationsCursor(context);
        if (cursor != null && cursor.getCount() > 0) {
            String locationName;
            while (cursor.moveToNext()) {
                locationName = cursor.getString(cursor.getColumnIndex(COL_STORE_ITEM_LOCATION));
                itemLocations.add(locationName);
            }
            cursor.close();
        }
        return itemLocations;
    }


// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Update Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////


// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Delete Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int deleteAllProducts(Context context) {
        int numberOfDeletedRecords = 0;
        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String where = null;
        String[] selectionArgs = null;
        numberOfDeletedRecords = cr.delete(uri, where, selectionArgs);

        return numberOfDeletedRecords;
    }
}
