package com.lbconsulting.agrocerylist.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lbconsulting.agrocerylist.classes.MyLog;

public class SelectedItemsTable {
    // Lists data table
    public static final String TABLE_SELECTED_ITEMS = "tblSelectedItems";
    public static final String COL_SELECTED_ITEMS_ID = "_id";
    public static final String COL_STORE_ID = "storeID";
    public static final String COL_ITEM_ID = "itemID";


    public static final String[] PROJECTION_ALL = {COL_SELECTED_ITEMS_ID, COL_STORE_ID, COL_ITEM_ID};

    public static final String CONTENT_PATH = TABLE_SELECTED_ITEMS;

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
            + "vnd.lbconsulting." + TABLE_SELECTED_ITEMS;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
            + "vnd.lbconsulting." + TABLE_SELECTED_ITEMS;
    public static final Uri CONTENT_URI = Uri.parse("content://" + aGroceryListContentProvider.AUTHORITY + "/" + CONTENT_PATH);

private static long mExistingSelectedItemsID;
    // Version 1
    // TODO: Sort by item name, etc

    // Database creation SQL statements
    private static final String CREATE_TABLE =
            "create table " + TABLE_SELECTED_ITEMS
                    + " ("
                    + COL_SELECTED_ITEMS_ID + " integer primary key autoincrement, "
                    + COL_STORE_ID + " integer default -1, "
                    + COL_ITEM_ID + " integer default -1 "
                    + ");";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
        MyLog.i("StoresTable", "onCreate: " + TABLE_SELECTED_ITEMS + " created.");
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        MyLog.i(TABLE_SELECTED_ITEMS, ": Upgrading database from version " + oldVersion + " to version " + newVersion
                + ". NO CHANGES REQUIRED.");
        // This wipes out all of the user data and create an empty table
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_SELECTED_ITEMS);
        onCreate(database);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Create Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static long newSelectedItem(Context context, long storeID, long itemID) {
        long newSelectedItemID = -1;
        if (storeID > 0 && itemID>0) {
            if (selectedItemExists(context, storeID, itemID)) {
                // the item is already selected exists in the table ... so return its id
                newSelectedItemID = mExistingSelectedItemsID;
            } else {
                // the selected item does not exist in the table ... so add it
                try {
                    ContentResolver cr = context.getContentResolver();
                    Uri uri = CONTENT_URI;
                    ContentValues values = new ContentValues();
                    values.put(COL_STORE_ID, storeID);
                    values.put(COL_ITEM_ID, itemID);
                    Uri newListUri = cr.insert(uri, values);
                    if (newListUri != null) {
                        newSelectedItemID = Long.parseLong(newListUri.getLastPathSegment());
                    }
                } catch (Exception e) {
                    MyLog.e("Exception error in newSelectedItem. ", e.toString());
                }
            }
        }
        return newSelectedItemID;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Read Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static Cursor getSelectedItemsCursor(Context context, long storeID, long itemID) {
        Cursor cursor = null;

        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = COL_STORE_ID + " = ? AND " + COL_ITEM_ID + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(storeID), String.valueOf(itemID)};
        String sortOrder = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("StoresTable", "getSelectedItemsCursor: Exception: " + e.getMessage());
        }

        return cursor;
    }

    private static boolean selectedItemExists(Context context, long storeID, long itemID) {
        mExistingSelectedItemsID = -1;
        boolean result = false;
        Cursor cursor = getSelectedItemsCursor(context, storeID, itemID);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                mExistingSelectedItemsID = cursor.getLong(cursor.getColumnIndex(COL_SELECTED_ITEMS_ID));
                result = true;
            }
            cursor.close();
        }
        return result;

    }


    public static CursorLoader getStoreItems(Context context, long storeID) {
        // TODO: Implement a joined table query
        CursorLoader cursorLoader = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = COL_STORE_ID + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(storeID)};
        String sortOrder = null;
        try {
            cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);

        } catch (Exception e) {
            MyLog.e("StoresTable", "getStoreItems: Exception: " + e.getMessage());
        }

        return cursorLoader;
    }



    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Update Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////


    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Delete Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int removeSelectedItem(Context context, long storeID, long itemID) {
        int numberOfDeletedRecords = 0;

        // delete the selected item
        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String selection = COL_STORE_ID + " = ? AND " + COL_ITEM_ID + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(storeID), String.valueOf(itemID)};
        numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);

        return numberOfDeletedRecords;
    }

    public static int removeAllStoreItems(Context context, long storeID) {
        int numberOfDeletedRecords = 0;

        // delete all entries with storeID
        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String selection = COL_STORE_ID + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(storeID)};
        numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);

        return numberOfDeletedRecords;
    }

    public static int removeItem(Context context, long itemID) {
        int numberOfDeletedRecords = 0;

        // delete all entries with itemID
        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String selection = COL_ITEM_ID + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(itemID)};
        numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);

        return numberOfDeletedRecords;
    }

}
