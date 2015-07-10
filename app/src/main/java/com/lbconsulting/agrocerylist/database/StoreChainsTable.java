package com.lbconsulting.agrocerylist.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lbconsulting.agrocerylist.activities.MainActivity;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes_parse.clsParseStoreChain;

public class StoreChainsTable {
    // Lists data table
    public static final String TABLE_STORE_CHAINS = "tblStoreChains";
    public static final String COL_STORE_CHAIN_ID = "_id";
    public static final String COL_STORE_CHAIN_NAME = "storeChainName";
    public static final String COL_CHECKED = "checked";


    public static final String[] PROJECTION_ALL = {COL_STORE_CHAIN_ID, COL_STORE_CHAIN_NAME, COL_CHECKED};

    public static final String CONTENT_PATH = TABLE_STORE_CHAINS;

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
            + "vnd.lbconsulting." + TABLE_STORE_CHAINS;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
            + "vnd.lbconsulting." + TABLE_STORE_CHAINS;
    public static final Uri CONTENT_URI = Uri.parse("content://" + aGroceryListContentProvider.AUTHORITY + "/" + CONTENT_PATH);

    // Version 1
    public static final String SORT_ORDER_STORE_CHAIN_NAME = COL_STORE_CHAIN_NAME + " ASC";

    // Database creation SQL statements
    private static final String CREATE_TABLE =
            "create table " + TABLE_STORE_CHAINS
                    + " ("
                    + COL_STORE_CHAIN_ID + " integer primary key, "
                    + COL_STORE_CHAIN_NAME + " text collate nocase default '', "
                    + COL_CHECKED + " integer default 0 "
                    + ");";

    public final static String defaultStoreChainName = "[No Store Chain]";
    private static long mExistingStoreChainID = -1;

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
        MyLog.i("StoreChainsTable", "onCreate: " + TABLE_STORE_CHAINS + " created.");
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        MyLog.i(TABLE_STORE_CHAINS, ": Upgrading database from version " + oldVersion + " to version " + newVersion
                + ". NO CHANGES REQUIRED.");
        // This wipes out all of the user data and create an empty table
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_STORE_CHAINS);
        onCreate(database);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Create Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static long createNewStoreChain(Context context, String storeChainName) {
        long newStoreChainID = -1;
        storeChainName = storeChainName.trim();
        if (!storeChainName.isEmpty()) {
            if (storeChainExists(context, storeChainName)) {
                // the store chain exists in the table ... so return its id
                newStoreChainID = mExistingStoreChainID;
            } else {
                // the store chain does not exist in the table ... so add it
                try {
                    ContentResolver cr = context.getContentResolver();
                    Uri uri = CONTENT_URI;
                    ContentValues values = new ContentValues();
                    values.put(COL_STORE_CHAIN_NAME, storeChainName);
                    Uri newStoreChainUri = cr.insert(uri, values);
                    if (newStoreChainUri != null) {
                        newStoreChainID = Long.parseLong(newStoreChainUri.getLastPathSegment());
                    }
                } catch (Exception e) {
                    MyLog.e("StoreChainsTable", "createNewStoreChain: Exception: " + e.getMessage());
                }

            }
        }
        return newStoreChainID;
    }

    public static void createNewStoreChain(Context context, clsParseStoreChain storeChain) {
        ContentResolver cr = context.getContentResolver();

        try {
            Uri uri = CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put(COL_STORE_CHAIN_ID, storeChain.getStoreChainID());
            values.put(COL_STORE_CHAIN_NAME, storeChain.getStoreChainName());
            cr.insert(uri, values);
        } catch (Exception e) {
            MyLog.e("LocationsTable", "createNewStoreChain: Exception: " + e.getMessage());
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Read Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static Cursor getStoreChainCursor(Context context, long storeChainID) {
        Cursor cursor = null;
        if (storeChainID > 0) {
            Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(storeChainID));
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
            MyLog.e("StoresTable", "getStoreChainCursor: Invalid storeChainID");
        }
        return cursor;
    }

    private static Cursor getStoreChainCursor(Context context, String storeChainName) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = COL_STORE_CHAIN_NAME + " = ?";
        String selectionArgs[] = new String[]{storeChainName};
        String sortOrder = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("StoresTable", "getStoreChainCursor: Exception: " + e.getMessage());
        }
        return cursor;
    }

    private static boolean storeChainExists(Context context, String storeName) {
        mExistingStoreChainID = -1;
        boolean result = false;
        Cursor cursor = getStoreChainCursor(context, storeName);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                mExistingStoreChainID = cursor.getLong(cursor.getColumnIndex(COL_STORE_CHAIN_ID));
                result = true;
            }
            cursor.close();
        }
        return result;

    }
    public static Cursor getAllStoreChainsCursor(Context context, String sortOrder) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = null;
        String selectionArgs[] = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("StoresTable", "getAllStoreChainsCursor: Exception: " + e.getMessage());
        }
        return cursor;
    }

    public static CursorLoader getAllChainNames(Context context, String sortOrder) {
        CursorLoader cursorLoader = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = null;
        String selectionArgs[] = null;
        try {
            cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);

        } catch (Exception e) {
            MyLog.e("StoreChainsTable", "getAllChainNames: Exception: " + e.getMessage());
        }
        return cursorLoader;
    }


    public static String getStoreChainName(Context context, long storeChainID) {
        String storeChainName = "";
        Cursor cursor = getStoreChainCursor(context, storeChainID);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            storeChainName = cursor.getString(cursor.getColumnIndex(COL_STORE_CHAIN_NAME));
        }
        if (cursor != null) {
            cursor.close();
        }
        return storeChainName;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Update Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int updateStoreChainFieldValues(Context context, long storeChainID, ContentValues newFieldValues) {
        int numberOfUpdatedRecords = -1;
        if (storeChainID > 0) {
            ContentResolver cr = context.getContentResolver();
            Uri storeChainUri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(storeChainID));
            String selection = null;
            String[] selectionArgs = null;
            numberOfUpdatedRecords = cr.update(storeChainUri, newFieldValues, selection, selectionArgs);
        } else {
            MyLog.e("StoresTable", "updateStoreFieldValues: Invalid itemID.");
        }
        return numberOfUpdatedRecords;
    }


    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Delete Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int deleteStoreChain(Context context, long storeChainID) {
        int numberOfDeletedRecords = -1;
        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String selection = COL_STORE_CHAIN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(storeChainID)};
        numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);
        return numberOfDeletedRecords;
    }

    public static int deleteAllCheckedStoreChains(Context context) {
        int numberOfDeletedRecords = -1;

        Uri uri = CONTENT_URI;
        String selection = COL_CHECKED + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(1)};
        ContentResolver cr = context.getContentResolver();
        numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);

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
