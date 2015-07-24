package com.lbconsulting.agrocerylist.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lbconsulting.agrocerylist.classes.MyLog;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

public class StoresTable {

    public static final String TABLE_STORES = "tblStores";
    public static final String COL_ID = "_id";
    // Parse fields
    public static final String COL_STORE_ID = "storeID";
    public static final String COL_STORE_CHAIN_ID = "storeChain";
    public static final String COL_STORE_REGIONAL_NAME = "storeRegionalName";
    public static final String COL_SORT_KEY = "sortKey";
    public static final String COL_ADDRESS1 = "address1";   // number and street
    public static final String COL_ADDRESS2 = "address2";   // ste, etc.
    public static final String COL_CITY = "city";
    public static final String COL_STATE = "state";
    public static final String COL_ZIP = "zip";
    public static final String COL_COUNTRY = "country";
    public static final String COL_LATITUDE = "latitude";
    public static final String COL_LONGITUDE = "longitude";
    public static final String COL_WEBSITE_URL = "websiteURL";
    public static final String COL_PHONE_NUMBER = "phoneNumber";
    // SQLite only fields
    public static final String COL_DIRTY = "dirty";
    public static final String COL_CHECKED = "checked";

    // Parse only field names
    public static final String COL_PARSE_LOCATION = "location";

    public static final String[] PROJECTION_ALL = {COL_ID, COL_STORE_ID, COL_STORE_CHAIN_ID,
            COL_STORE_REGIONAL_NAME,COL_SORT_KEY,
            COL_ADDRESS1, COL_ADDRESS2, COL_CITY, COL_STATE, COL_ZIP, COL_COUNTRY,
            COL_LATITUDE, COL_LONGITUDE, COL_WEBSITE_URL, COL_PHONE_NUMBER,
            COL_DIRTY, COL_CHECKED
    };

    public static final String CONTENT_PATH = TABLE_STORES;

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
            + "vnd.lbconsulting." + TABLE_STORES;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
            + "vnd.lbconsulting." + TABLE_STORES;

    public static final Uri CONTENT_URI = Uri.parse("content://" + aGroceryListContentProvider.AUTHORITY + "/" + CONTENT_PATH);

    public static final String SORT_ORDER_SORT_KEY = COL_SORT_KEY + " ASC";

    // TODO: Create a new table to hold store theme colors, and any other user specific settings.
    // Database creation SQL statements
    private static final String CREATE_TABLE =
            "create table " + TABLE_STORES
                    + " ("
                    + COL_ID + " integer primary key, "
                    + COL_STORE_ID + " text default '', "
                    + COL_STORE_CHAIN_ID + " text default '', "
                    + COL_STORE_REGIONAL_NAME + " text collate nocase default '', "
                    + COL_SORT_KEY + " integer default 0, "
                    + COL_ADDRESS1 + " text collate nocase default '', "
                    + COL_ADDRESS2 + " text collate nocase default '', "
                    + COL_CITY + " text collate nocase default '', "
                    + COL_STATE + " text collate nocase default '', "
                    + COL_ZIP + " text collate nocase default '', "
                    + COL_COUNTRY + " text collate nocase default '', "
                    + COL_LATITUDE + " real default 0.0, "
                    + COL_LONGITUDE + " real default 0.0, "
                    + COL_WEBSITE_URL + " text default '', "
                    + COL_PHONE_NUMBER + " text default '', "
                    + COL_DIRTY + " integer default 0, "
                    + COL_CHECKED + " integer default 0 "
                    + ");";

    private static long mExistingStoreID = -1;

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
        MyLog.i("StoresTable", "onCreate: " + TABLE_STORES + " created.");
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        MyLog.i(TABLE_STORES, ": Upgrading database from version " + oldVersion + " to version " + newVersion
                + ". NO CHANGES REQUIRED.");
        // This wipes out all of the user data and create an empty table
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_STORES);
        onCreate(database);
    }

    public static void resetTable(SQLiteDatabase database) {
        MyLog.i(TABLE_STORES, "Resetting table");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_STORES);
        onCreate(database);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Create Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
/*    public static long createNewStore(Context context, long storeChainID,
                                      String storeRegionalName,
                                      String address1,  // number and street
                                      String address2,  // ste, etc
                                      String city, String state, String zipCode) {
        long newStoreID = -1;
        storeRegionalName = storeRegionalName.trim();
        address1 = address1.trim();
        address2 = address2.trim();
        city = city.trim();
        state = state.trim();
        zipCode = zipCode.trim();
        if (storeChainID > 0 && !storeRegionalName.isEmpty() && !zipCode.isEmpty()) {
            if (storeExists(context, storeChainID, storeRegionalName)) {
                // the store exists in the table ... so return its id
                newStoreID = mExistingStoreID;
            } else {
                // store does not exist in the table ... so add it
                String storeChainName = StoreChainsTable.getStoreChainName(context, storeChainID);
                String parseStoreMapName = storeChainName + "_" + zipCode;
                try {
                    ContentResolver cr = context.getContentResolver();
                    Uri uri = CONTENT_URI;
                    ContentValues cv = new ContentValues();
                    cv.put(COL_ID, storeChainID);
                    cv.put(COL_STORE_REGIONAL_NAME, storeRegionalName);
                    cv.put(COL_ADDRESS1, address1); // number and street
                    cv.put(COL_ADDRESS2, address2); // ste, etc
                    cv.put(COL_CITY, city);
                    cv.put(COL_STATE, state);
                    cv.put(COL_ZIP, zipCode);
                    cv.put(COL_PARSE_STORE_MAP_NAME, parseStoreMapName);
                    Uri newListUri = cr.insert(uri, cv);
                    if (newListUri != null) {
                        newStoreID = Long.parseLong(newListUri.getLastPathSegment());
                        cv = new ContentValues();
                        cv.put(COL_SORT_KEY, newStoreID);
                        cv.put(COL_COLOR_THEME_ID, getColorThemeID(newStoreID));
                        updateStoreFieldValues(context, newStoreID, cv);
                        StoreMapsTable.initializeStoreMap(context, newStoreID);
                    }
                } catch (Exception e) {
                    MyLog.i("StoresTable", "createNewStore: Exception " + e.getMessage());
                }

            }
        }
        return newStoreID;
    }*/

    public static void createNewStore(Context context, ParseObject store) {
        ContentResolver cr = context.getContentResolver();

        String StoreID = store.getObjectId();
        if(storeExists(context,StoreID)){
            // TODO: Store exists ... do you want to update the store's fields??
            return;
        }

        try {
            Uri uri = CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put(COL_STORE_ID, store.getObjectId());
            String test = store.getString(COL_STORE_CHAIN_ID);
            MyLog.d("StoresTable", "createNewStore: COL_STORE_CHAIN_ID = " +test);
            values.put(COL_STORE_CHAIN_ID, store.getString(COL_STORE_CHAIN_ID));
            values.put(COL_STORE_REGIONAL_NAME, store.getString(COL_STORE_REGIONAL_NAME));
            values.put(COL_SORT_KEY, store.getLong(COL_SORT_KEY));
            values.put(COL_ADDRESS1, store.getString(COL_ADDRESS1));
            values.put(COL_ADDRESS2, store.getString(COL_ADDRESS2));
            values.put(COL_CITY, store.getString(COL_CITY));
            values.put(COL_STATE, store.getString(COL_STATE));
            values.put(COL_ZIP, store.getString(COL_ZIP));
            values.put(COL_COUNTRY, store.getString(COL_COUNTRY));
            ParseGeoPoint geoPoint = store.getParseGeoPoint(COL_PARSE_LOCATION);
            if (geoPoint != null) {
                values.put(COL_LATITUDE, geoPoint.getLatitude());
                values.put(COL_LONGITUDE, geoPoint.getLongitude());
            }
            values.put(COL_PHONE_NUMBER, store.getString(COL_PHONE_NUMBER));
            values.put(COL_WEBSITE_URL, store.getString(COL_WEBSITE_URL));

            cr.insert(uri, values);
        } catch (Exception e) {
            MyLog.e("StoresTable", "createNewStore: Exception: " + e.getMessage());
        }
    }

    private static long getColorThemeID(long newStoreID) {
        // TODO: implement default color theme selection
        return 1;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Read Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static Cursor getStoreCursor(Context context, long storeID) {
        Cursor cursor = null;
        if (storeID > 0) {
            Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(storeID));
            String[] projection = PROJECTION_ALL;
            String selection = null;
            String selectionArgs[] = null;
            String sortOrder = null;
            ContentResolver cr = context.getContentResolver();
            try {
                cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
            } catch (Exception e) {
                MyLog.e("StoresTable", "getStoreChainCursor: Exception: " + e.getMessage());
            }
        } else {
            MyLog.e("StoresTable", "getStoreChainCursor: Invalid storeID");
        }
        return cursor;
    }

    public static Cursor getStoreCursor(Context context, String parseStoreID) {
        Cursor cursor = null;

        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = COL_STORE_ID + " = ?";
        String selectionArgs[] = new String[]{parseStoreID};
        String sortOrder = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("StoresTable", "getStoreChainCursor: Exception: " + e.getMessage());
        }

        return cursor;
    }

    public static Cursor getStoreCursor(Context context, String storeChainID, String storeName) {
        Cursor cursor = null;

        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = COL_STORE_CHAIN_ID + " = ? AND " + COL_STORE_REGIONAL_NAME + " = ?";
        String selectionArgs[] = new String[]{storeChainID, storeName};
        String sortOrder = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("StoresTable", "getStoreChainCursor: Exception: " + e.getMessage());
        }

        return cursor;
    }

    private static boolean storeExists(Context context, String storeChainID, String storeName) {
        mExistingStoreID = -1;
        boolean result = false;
        Cursor cursor = getStoreCursor(context, storeChainID, storeName);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                mExistingStoreID = cursor.getLong(cursor.getColumnIndex(COL_ID));
                result = true;
            }
            cursor.close();
        }
        return result;

    }

    private static boolean storeExists(Context context, String parseStoreID) {
        mExistingStoreID = -1;
        boolean result = false;
        Cursor cursor = getStoreCursor(context, parseStoreID);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                mExistingStoreID = cursor.getLong(cursor.getColumnIndex(COL_ID));
                result = true;
            }
            cursor.close();
        }
        return result;

    }

    public static Cursor getAllStoresCursor(Context context, String sortOrder) {
        Cursor cursor = null;

        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = null;
        String selectionArgs[] = null;

        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("StoresTable", "getAllStoresCursor: Exception: " + e.getMessage());
        }

        return cursor;
    }


    public static Cursor getAllStoresWithChainID(Context context, String storeChainID, String sortOrder) {
        Cursor cursor = null;

        Uri uri = CONTENT_URI;
        String[] projection = {COL_ID};
        String selection = COL_STORE_CHAIN_ID + " = ?";
        String selectionArgs[] = new String[]{storeChainID};

        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("StoresTable", "getAllStoresWithChainID: Exception: " + e.getMessage());
        }

        return cursor;
    }

    private static Cursor getAllCheckedStoresCursor(Context context, String sortOrder) {
        Cursor cursor = null;

        Uri uri = CONTENT_URI;
        String[] projection = {COL_ID};
        String selection = COL_CHECKED + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(1)};
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("StoresTable", "getAllCheckedStoresCursor: Exception: " + e.getMessage());
        }

        return cursor;
    }

    public static CursorLoader getAllStores(Context context, String sortOrder) {
        CursorLoader cursorLoader = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = null;
        String selectionArgs[] = null;
        try {
            cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);

        } catch (Exception e) {
            MyLog.e("StoresTable", "getAllStores: Exception: " + e.getMessage());
        }

        return cursorLoader;
    }

    public static Cursor getAllStoresWithChainNames(Context context, String sortOrder) {
        Cursor cursor = null;

        Uri uri = JoinedTables.CONTENT_URI_STORES_WITH_CHAIN_NAMES;
        String[] projection = JoinedTables.PROJECTION_STORES_WITH_CHAIN_NAMES;
        String selection = null;
        String selectionArgs[] = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("StoresTable", "getAllStoresWithChainNames: Exception: " + e.getMessage());
        }

        return cursor;
    }

    public static CursorLoader getAllStoresWithChainNames(Context context) {
        CursorLoader cursorLoader = null;

        Uri uri = JoinedTables.CONTENT_URI_STORES_WITH_CHAIN_NAMES;
        String[] projection = JoinedTables.PROJECTION_STORES_WITH_CHAIN_NAMES;
        String selection = null;
        String selectionArgs[] = null;
        String sortOrder = JoinedTables.SORT_ORDER_CHAIN_NAME_THEN_STORE_NAME;
        try {
            cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("StoresTable", "getAllStoresWithChainNames: Exception: " + e.getMessage());
        }

        return cursorLoader;
    }


    public static String getStoreRegionalName(Context context, long storeID) {
        String storeName = "";
        Cursor cursor = getStoreCursor(context, storeID);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            storeName = cursor.getString(cursor.getColumnIndexOrThrow(COL_STORE_REGIONAL_NAME));
        }
        if (cursor != null) {
            cursor.close();
        }
        return storeName;
    }


    public static int getStoreItemsSortingOrder(Context context, long storeID) {
        // TODO: Move getStoreItemsSortingOrder to a new stores preferences SQLite table
        int itemsSortingOrder = -1;
/*        if (storeID > 0) {
            Cursor cursor = getStoreCursor(context, storeID);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                itemsSortingOrder = cursor.getInt(cursor.getColumnIndexOrThrow(COL_STORE_ITEMS_SORTING_ORDER));
            }
            if (cursor != null) {
                cursor.close();
            }
        }*/
        return itemsSortingOrder;
    }


/*    public static String getStoreMapParseName(Context context, long storeID) {
        String parseObjectName = "";
        Cursor cursor = getStoreCursor(context, storeID);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            parseObjectName = cursor.getString(cursor.getColumnIndex(COL_PARSE_STORE_MAP_NAME));
        }
        if (cursor != null) {
            cursor.close();
        }
        return parseObjectName;
    }*/

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Update Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int updateStoreFieldValues(Context context, long storeID, ContentValues newFieldValues) {
        int numberOfUpdatedRecords = -1;
        if (storeID > 0) {
            ContentResolver cr = context.getContentResolver();
            Uri itemUri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(storeID));
            String selection = null;
            String[] selectionArgs = null;
            numberOfUpdatedRecords = cr.update(itemUri, newFieldValues, selection, selectionArgs);
        } else {
            MyLog.e("StoresTable", "updateStoreFieldValues: Invalid itemID.");
        }
        return numberOfUpdatedRecords;
    }

    public static int updateStoreItemsSortOrder(Context context, long storeID, int storeItemsSortingOrder) {
        // TODO: Move updateStoreItemsSortOrder to a new stores preferences SQLite table
        int numberOfUpdatedRecords = -1;
/*        String selection = null;
        String[] selectionArgs = null;
        if (storeID > 0) {
            selection = COL_ID + " = ?";
            selectionArgs = new String[]{String.valueOf(storeID)};
        }

        ContentValues cv = new ContentValues();
        cv.put(COL_STORE_ITEMS_SORTING_ORDER, storeItemsSortingOrder);

        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        numberOfUpdatedRecords = cr.update(uri, cv, selection, selectionArgs);*/

        return numberOfUpdatedRecords;
    }


    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Delete Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int deleteStore(Context context, long storeID) {
        int numberOfDeletedRecords;

        // delete the store
        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String where = COL_ID + " = ?";
        String[] selectionArgs = {String.valueOf(storeID)};
        numberOfDeletedRecords = cr.delete(uri, where, selectionArgs);

        return numberOfDeletedRecords;
    }

    public static int deleteStore(Context context, String parseStoreID) {
        int numberOfDeletedRecords;

        // delete the store
        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String where = COL_STORE_ID + " = ?";
        String[] selectionArgs = {parseStoreID};
        numberOfDeletedRecords = cr.delete(uri, where, selectionArgs);

        return numberOfDeletedRecords;
    }

    public static int deleteAllStoresWithChainID(Context context, String storeChainID) {
        int numberOfDeletedRecords = 0;
        Cursor cursor = getAllStoresWithChainID(context, storeChainID, null);
        long storeID;
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                storeID = cursor.getLong(cursor.getColumnIndex(COL_ID));
                numberOfDeletedRecords += deleteStore(context, storeID);
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        return numberOfDeletedRecords;
    }

    public static int deleteCheckedStores(Context context) {
        int numberOfDeletedRecords = 0;
        Cursor cursor = getAllCheckedStoresCursor(context, null);
        long storeID;
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                storeID = cursor.getLong(cursor.getColumnIndex(COL_ID));
                numberOfDeletedRecords += deleteStore(context, storeID);
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        return numberOfDeletedRecords;
    }


    public static int clear(Context context) {
        int numberOfDeletedRecords = 0;

        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String selection = null;
        String[] selectionArgs = null;
        numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);

        return numberOfDeletedRecords;
    }
}
