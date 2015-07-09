package com.lbconsulting.agrocerylist.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lbconsulting.agrocerylist.classes.MyLog;

public class StoresTable {
    // Lists data table
    public static final String TABLE_STORES = "tblStores";
    public static final String COL_STORE_ID = "_id";
    public static final String COL_STORE_CHAIN_ID = "storeChainID";

    //public static final String COL_PARSE_STORE_MAP_ID = "parseStoreMapID";
    public static final String COL_PARSE_STORE_MAP_NAME = "parseStoreMapName";
    public static final String COL_PARSE_STORE_MAP_IS_DIRTY = "parseStoreMapIsDirty";
    public static final String COL_PARSE_STORE_MAP_TIMESTAMP = "parseStoreMapTimestamp";

    public static final String COL_STORE_REGIONAL_NAME = "storeRegionalName";
    public static final String COL_CHECKED = "storeChecked";
    public static final String COL_DISPLAYED = "storeDisplayed";
    public static final String COL_MANUAL_SORT_KEY = "manualSortKey";
    public static final String COL_STORE_ITEMS_SORTING_ORDER = "storeItemsSortOrder";
    public static final String COL_COLOR_THEME_ID = "colorThemeID";

    public static final String COL_ADDRESS1 = "address1";   // number and street
    public static final String COL_ADDRESS2 = "address2";   // ste, etc.
    public static final String COL_CITY = "city";
    public static final String COL_STATE = "state";
    public static final String COL_ZIP = "zip";
    public static final String COL_GPS_LATITUDE = "gpsLatitude";
    public static final String COL_GPS_LONGITUDE = "gpsLongitude";
    public static final String COL_WEBSITE_URL = "websiteURL";
    public static final String COL_PHONE_NUMBER = "phoneNumber";

    public static final String[] PROJECTION_ALL = {COL_STORE_ID, COL_STORE_CHAIN_ID,
            COL_PARSE_STORE_MAP_NAME, COL_PARSE_STORE_MAP_IS_DIRTY, COL_PARSE_STORE_MAP_TIMESTAMP,
            COL_STORE_REGIONAL_NAME, COL_CHECKED, COL_DISPLAYED,
            COL_MANUAL_SORT_KEY, COL_STORE_ITEMS_SORTING_ORDER, COL_COLOR_THEME_ID,
            COL_ADDRESS1, COL_ADDRESS2, COL_CITY, COL_STATE, COL_ZIP,
            COL_GPS_LATITUDE, COL_GPS_LONGITUDE, COL_WEBSITE_URL, COL_PHONE_NUMBER
    };

    public static final String CONTENT_PATH = TABLE_STORES;

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
            + "vnd.lbconsulting." + TABLE_STORES;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
            + "vnd.lbconsulting." + TABLE_STORES;

    public static final Uri CONTENT_URI = Uri.parse("content://" + aGroceryListContentProvider.AUTHORITY + "/" + CONTENT_PATH);

    public static final String SORT_ORDER_CHAIN_ID_BY_REGIONAL_NAME = COL_STORE_CHAIN_ID + " ASC, " + COL_STORE_REGIONAL_NAME + " ASC";
    public static final String SORT_ORDER_MANUAL = COL_MANUAL_SORT_KEY + " ASC";
    public static final String SORT_ORDER_CITY = COL_CITY + " ASC";
    public static final String SORT_ORDER_STATE = COL_STATE + " ASC";
    public static final String SORT_ORDER_ZIP = COL_ZIP + " ASC";

    // TODO: add references to Theme Table
    // TODO: upon updating the store zip code, revise the Parse Store Map Object Name
    // Database creation SQL statements
    private static final String CREATE_TABLE =
            "create table " + TABLE_STORES
                    + " ("
                    + COL_STORE_ID + " integer primary key autoincrement, "
                    //+ COL_STORE_CHAIN_ID + " integer default -1, "
                    + COL_STORE_CHAIN_ID + " integer not null references " + StoreChainsTable.TABLE_STORE_CHAINS + "  default -1, "

                    + COL_PARSE_STORE_MAP_NAME + " text collate nocase default '', "
                    + COL_PARSE_STORE_MAP_IS_DIRTY + " integer default 0, "
                    + COL_PARSE_STORE_MAP_TIMESTAMP + " integer default 0, "

                    + COL_STORE_REGIONAL_NAME + " text collate nocase default '', "
                    + COL_CHECKED + " integer default 0, "
                    + COL_DISPLAYED + " integer default 1, "
                    + COL_MANUAL_SORT_KEY + " integer default 0, "
                    + COL_STORE_ITEMS_SORTING_ORDER + " integer default 0, "
                    + COL_COLOR_THEME_ID + " integer default 1, "
                    + COL_ADDRESS1 + " text collate nocase default '', "
                    + COL_ADDRESS2 + " text collate nocase default '', "
                    + COL_CITY + " text collate nocase default '', "
                    + COL_STATE + " text collate nocase default '', "
                    + COL_ZIP + " text collate nocase default '', "
                    + COL_GPS_LATITUDE + " text default '', "
                    + COL_GPS_LONGITUDE + " text default '', "
                    + COL_WEBSITE_URL + " text default '', "
                    + COL_PHONE_NUMBER + " text default '' "
                    + ");";

    private static String defaultStoreValue = "[No Store]";
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

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Create Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static long createNewStore(Context context, long storeChainID,
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
                    cv.put(COL_STORE_CHAIN_ID, storeChainID);
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
                        cv.put(COL_MANUAL_SORT_KEY, newStoreID);
                        cv.put(COL_COLOR_THEME_ID, getColorThemeID(newStoreID));
                        updateStoreFieldValues(context, newStoreID, cv);
                        StoreMapsTable.initializeStoreMap(context, newStoreID);
                    }
                } catch (Exception e) {
                    MyLog.e("Exception error in CreateNewStore. ", e.toString());
                }

            }
        }
        return newStoreID;
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

    public static Cursor getStoreCursor(Context context, long storeChainID, String storeName) {
        Cursor cursor = null;

        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = COL_STORE_CHAIN_ID + " = ? AND " + COL_STORE_REGIONAL_NAME + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(storeChainID), storeName};
        String sortOrder = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("StoresTable", "getStoreChainCursor: Exception: " + e.getMessage());
        }

        return cursor;
    }

    private static boolean storeExists(Context context, long storeChainID, String storeName) {
        mExistingStoreID = -1;
        boolean result = false;
        Cursor cursor = getStoreCursor(context, storeChainID, storeName);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                mExistingStoreID = cursor.getLong(cursor.getColumnIndex(COL_STORE_ID));
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

    public static Cursor getAllDisplayedStoresCursor(Context context, String sortOrder) {
        Cursor cursor = null;

        Uri uri = CONTENT_URI;
        String[] projection = {COL_STORE_ID};
        String selection = COL_DISPLAYED + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(1)};

        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("StoresTable", "getAllDisplayedStoresCursor: Exception: " + e.getMessage());
        }

        return cursor;
    }
    public static Cursor getAllStoresWithChainID(Context context, long storeChainID, String sortOrder) {
        Cursor cursor = null;

        Uri uri = CONTENT_URI;
        String[] projection = {COL_STORE_ID};
        String selection = COL_DISPLAYED + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(1)};
        if (storeChainID > 0) {
            selection = COL_STORE_CHAIN_ID + " = ?";
            selectionArgs = new String[]{String.valueOf(storeChainID)};
        }

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
        String[] projection = {COL_STORE_ID};
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
        int itemsSortingOrder = -1;
        if (storeID > 0) {
            Cursor cursor = getStoreCursor(context, storeID);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                itemsSortingOrder = cursor.getInt(cursor.getColumnIndexOrThrow(COL_STORE_ITEMS_SORTING_ORDER));
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        return itemsSortingOrder;
    }


    public static String getStoreMapParseObjectName(Context context, long storeID) {
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
    }

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
        int numberOfUpdatedRecords = -1;
        String selection = null;
        String[] selectionArgs = null;
        if (storeID > 0) {
            selection = COL_STORE_ID + " = ?";
            selectionArgs = new String[]{String.valueOf(storeID)};
        }

        ContentValues cv = new ContentValues();
        cv.put(COL_STORE_ITEMS_SORTING_ORDER, storeItemsSortingOrder);

        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        numberOfUpdatedRecords = cr.update(uri, cv, selection, selectionArgs);

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
        String where = COL_STORE_ID + " = ?";
        String[] selectionArgs = {String.valueOf(storeID)};
        numberOfDeletedRecords = cr.delete(uri, where, selectionArgs);

        return numberOfDeletedRecords;
    }

    public static int deleteAllStoresWithChainID(Context context, long storeChainID) {
        int numberOfDeletedRecords = 0;
        Cursor cursor = getAllStoresWithChainID(context, storeChainID, null);
        long storeID;
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                storeID = cursor.getLong(cursor.getColumnIndex(COL_STORE_ID));
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
                storeID = cursor.getLong(cursor.getColumnIndex(COL_STORE_ID));
                numberOfDeletedRecords += deleteStore(context, storeID);
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        return numberOfDeletedRecords;
    }


}
