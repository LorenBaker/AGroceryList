package com.lbconsulting.agrocerylist.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.clsLocation;
import com.parse.ParseObject;

import java.util.ArrayList;

public class LocationsTable {

    public static final String TABLE_LOCATIONS = "tblLocations";
    public static final String COL_ID = "_id";
    // Parse fields
    public static final String COL_LOCATION_ID = "locationID";
    public static final String COL_LOCATION_NAME = "locationName";
    public static final String COL_SORT_KEY = "sortKey";
    // SQLite only fields
    public static final String COL_DIRTY = "dirty";
    public static final String COL_CHECKED = "checked";

    public static final String[] PROJECTION_ALL = {COL_ID, COL_LOCATION_ID, COL_LOCATION_NAME,
            COL_SORT_KEY, COL_DIRTY, COL_CHECKED};

    public static final String CONTENT_PATH = TABLE_LOCATIONS;
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting."
            + TABLE_LOCATIONS;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting."
            + TABLE_LOCATIONS;
    public static final Uri CONTENT_URI = Uri.parse("content://" + aGroceryListContentProvider.AUTHORITY + "/" + CONTENT_PATH);

    public static final String SORT_ORDER_LOCATION_NAME = COL_LOCATION_NAME + " ASC";
    public static final String SORT_ORDER_SORT_KEY = COL_SORT_KEY + " ASC";

    private static long mExistingLocationID;

    // Database creation SQL statements
    private static final String CREATE_TABLE = "create table "
            + TABLE_LOCATIONS
            + " ("
            + COL_ID + " integer primary key, "
            + COL_LOCATION_ID + " text default '', "
            + COL_LOCATION_NAME + " text collate nocase default '', "
            + COL_SORT_KEY + " integer default 0, "
            + COL_DIRTY + " integer default 0, "
            + COL_CHECKED + " integer default 0 "
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

    public static void resetTable(SQLiteDatabase database) {
        MyLog.i(TABLE_LOCATIONS, "Resetting table");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        onCreate(database);
    }
// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Create Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////

/*    public static void createInitialLocationsIfNeeded(Context context) {
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
    }*/

/*    public static void createNewLocation(Context context, clsParseLocation location) {
        ContentResolver cr = context.getContentResolver();

        try {
            Uri uri = CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put(COL_ID, location.getLocationID());
            values.put(COL_LOCATION_NAME, location.getLocationName());
            cr.insert(uri, values);
        } catch (Exception e) {
            MyLog.e("LocationsTable", "createNewLocation: Exception: " + e.getMessage());
        }
    }*/

    public static void createNewLocation(Context context, ParseObject location) {
        String locationID = location.getObjectId();
        if (locationExists(context, locationID, true)) {
            // TODO: Location exists ... do you want to update the location's fields??
            return;
        }
        String locationName = location.getString(COL_LOCATION_NAME);
        long sortKey = location.getLong(COL_SORT_KEY);
        if (!locationName.isEmpty() && !locationID.isEmpty()) {

            try {
                ContentResolver cr = context.getContentResolver();
                Uri uri = CONTENT_URI;
                ContentValues values = new ContentValues();
                values.put(COL_LOCATION_ID, locationID);
                values.put(COL_LOCATION_NAME, locationName);
                values.put(COL_SORT_KEY, sortKey);
                cr.insert(uri, values);

            } catch (Exception e) {
                MyLog.e("LocationsTable", "createNewLocation: Exception: " + e.getMessage());
            }
        } else {
            MyLog.e("LocationsTable", "createNewLocation: Either locationName or locationID is empty.");
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

        // TODO: Upload any new aisles to Parse 
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

    public static Cursor getLocationCursor(Context context, long ID) {
        Cursor cursor = null;
        if (ID > 0) {
            Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(ID));
            String[] projection = PROJECTION_ALL;
            String selection = null;
            String selectionArgs[] = null;
            String sortOrder = null;
            ContentResolver cr = context.getContentResolver();
            try {
                cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
            } catch (Exception e) {
                MyLog.e("LocationsTable", "getLocationCursor: Exception: " + e.getMessage());
            }
        } else {
            MyLog.e("LocationsTable", "getLocationCursor: Invalid groupID");
        }
        return cursor;
    }

    private static Cursor getLocationCursor(Context context, String locationFieldValue, boolean isParseObjectID) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection;

        if (isParseObjectID) {
            selection = COL_LOCATION_ID + " = ?";
        } else {
            selection = COL_LOCATION_NAME + " = ?";
        }
        String selectionArgs[] = new String[]{locationFieldValue};
        String sortOrder = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("LocationsTable", "getLocationCursor: Exception: " + e.getMessage());
        }
        return cursor;
    }

    private static boolean locationExists(Context context, String locationFieldValue, boolean isParseObjectID) {
        mExistingLocationID = -1;
        boolean result = false;
        Cursor cursor = getLocationCursor(context, locationFieldValue, isParseObjectID);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                mExistingLocationID = cursor.getLong(cursor.getColumnIndex(COL_ID));
                result = true;
            }
            cursor.close();
        }
        return result;

    }

    private static boolean isTableEmpty(Context context) {
        boolean result = true;
        Cursor cursor = getAllLocationsCursor(context, SORT_ORDER_SORT_KEY);
        if (cursor != null) {
            result = cursor.getCount() == 0;
            cursor.close();
        }
        return result;
    }

    public static int getNumberOfAisles(Context context) {
        int numberOfAisles = -1;
        Cursor cursor = getAllLocationsCursor(context, SORT_ORDER_SORT_KEY);
        if (cursor != null) {
            numberOfAisles = cursor.getCount() - 9;
            cursor.close();
        }
        return numberOfAisles;
    }

    public static Cursor getAllLocationsCursor(Context context, String sortOrder) {
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
        // createInitialLocationsIfNeeded(context);
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
        Cursor cursor = getAllLocationsCursor(context, SORT_ORDER_SORT_KEY);
        if (cursor != null && cursor.getCount() > 0) {
            clsLocation location;
            long locationID;
            String locationName;
            while (cursor.moveToNext()) {
                locationID = cursor.getLong(cursor.getColumnIndex(COL_ID));
                locationName = cursor.getString(cursor.getColumnIndex(COL_LOCATION_NAME));
                location = new clsLocation(locationID, locationName);
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
            String selection = COL_ID + " = ?";
            String[] selectionArgs = {String.valueOf(locationID)};
            numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);
            StoreMapsTable.resetLocationID(context, locationID);
        }
        return numberOfDeletedRecords;
    }


    public static int clear(Context context) {
        int numberOfDeletedRecords;

        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String selection = null;
        String[] selectionArgs = null;
        numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);

        return numberOfDeletedRecords;
    }


}


