package com.lbconsulting.agrocerylist.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lbconsulting.agrocerylist.classes.MyLog;

public class LocationsBridgeTable {

    public static final String TABLE_LOCATIONS_BRIDGE = "tblLocationsBridge";
    public static final String COL_BRIDGE_ROW_ID = "_id";
    public static final String COL_ITEM_ID = "itemID";
    public static final String COL_GROUP_ID = "groupID";
    public static final String COL_STORE_ID = "storeID";
    public static final String COL_LOCATION_ID = "locationID";

    public static final String[] PROJECTION_ALL = {COL_BRIDGE_ROW_ID, COL_ITEM_ID, COL_GROUP_ID, COL_STORE_ID, COL_LOCATION_ID};

    public static final String CONTENT_PATH = TABLE_LOCATIONS_BRIDGE;

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting." + TABLE_LOCATIONS_BRIDGE;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting." + TABLE_LOCATIONS_BRIDGE;
    public static final Uri CONTENT_URI = Uri.parse("content://" + aGroceryListContentProvider.AUTHORITY + "/" + CONTENT_PATH);


    private static long mExistingBridgeRowID;

    // Database creation SQL statements
    private static final String CREATE_TABLE = "create table " + TABLE_LOCATIONS_BRIDGE + " ("
            + COL_BRIDGE_ROW_ID + " integer primary key autoincrement, "
            + COL_ITEM_ID + " integer not null references " + ItemsTable.TABLE_ITEMS + " (" + ItemsTable.COL_ITEM_ID + ") default -1, "
            + COL_GROUP_ID + " integer not null references " + GroupsTable.TABLE_GROUPS + " (" + GroupsTable.COL_GROUP_ID + ") default 1, "
            + COL_STORE_ID + " integer not null references " + StoresTable.TABLE_STORES + " (" + StoresTable.COL_STORE_ID + ") default -1, "
            + COL_LOCATION_ID + " integer not null references " + LocationsTable.TABLE_LOCATIONS + " (" + LocationsTable.COL_LOCATION_ID + ") default 1 "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
        MyLog.i("LocationsBridgeTable", "onCreate: " + TABLE_LOCATIONS_BRIDGE + " created.");
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        MyLog.i(TABLE_LOCATIONS_BRIDGE, "Upgrading database from version " + oldVersion + " to version " + newVersion);
        // This wipes out all of the user data and create an empty table
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS_BRIDGE);
        onCreate(database);

    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Create Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static long createNewBridgeRow(Context context, long itemID, long groupID, long storeID, long locationID) {
        long newBridgeRowID = -1;
        // crate a new bridge row if the inputs are valid
        if ((itemID > 0 || groupID > 1) && storeID > 0 && locationID > 0) {
            if (bridgeRowExists(context, itemID, groupID, storeID)) {
                // the bridge row exists in the table ... so return its id
                newBridgeRowID = mExistingBridgeRowID;
            } else {
                // the group does not exist in the table ... so add it
                ContentResolver cr = context.getContentResolver();
                Uri uri = CONTENT_URI;
                ContentValues values = new ContentValues();
                values.put(COL_ITEM_ID, itemID);
                values.put(COL_GROUP_ID, groupID);
                values.put(COL_STORE_ID, storeID);
                values.put(COL_LOCATION_ID, locationID);
                Uri newBridgeRowUri = cr.insert(uri, values);
                if (newBridgeRowUri != null) {
                    newBridgeRowID = Long.parseLong(newBridgeRowUri.getLastPathSegment());
                }
            }
        }
        return newBridgeRowID;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Read Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static boolean bridgeRowExists(Context context, long itemID, long groupID, long storeID) {
        // the bridge row exists if either of the following is true:
        // 1) the itemID and storeID pair exists in the table, or
        // 2) the groupID and storeID pair exists in the table.

        mExistingBridgeRowID = -1;
        boolean bridgeRowExists = false;

        // check existence if the groupID is greater than the default groupID
        if (groupID > 1) {
            Cursor groupStoreCursor = getGroupStoreCursor(context, groupID, storeID);
            if (groupStoreCursor != null) {
                if (groupStoreCursor.getCount() > 0) {
                    groupStoreCursor.moveToFirst();
                    mExistingBridgeRowID = groupStoreCursor.getLong(groupStoreCursor.getColumnIndex(COL_BRIDGE_ROW_ID));
                    bridgeRowExists = true;
                }
                groupStoreCursor.close();
            }
        }

        if (!bridgeRowExists && itemID > 0) {
            Cursor itemStoreCursor = getItemStoreCursor(context, itemID, storeID);
            if (itemStoreCursor != null) {
                if (itemStoreCursor.getCount() > 0) {
                    itemStoreCursor.moveToFirst();
                    mExistingBridgeRowID = itemStoreCursor.getLong(itemStoreCursor.getColumnIndex(COL_BRIDGE_ROW_ID));
                    bridgeRowExists = true;
                }
                itemStoreCursor.close();
            }
        }
        return bridgeRowExists;
    }

    private static Cursor getItemStoreCursor(Context context, long itemID, long storeID) {
        Cursor cursor;

        Uri uri = CONTENT_URI;
        String[] projection = new String[]{COL_BRIDGE_ROW_ID};
        String selection = COL_ITEM_ID + " = ? AND " + COL_STORE_ID + " = ?";
        String selectionArgs[] = {String.valueOf(itemID), String.valueOf(storeID)};
        String sortOrder = null;
        ContentResolver cr = context.getContentResolver();
        cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);

        return cursor;
    }

    private static Cursor getGroupStoreCursor(Context context, long groupID, long storeID) {
        Cursor cursor;

        Uri uri = CONTENT_URI;
        String[] projection = new String[]{COL_BRIDGE_ROW_ID};
        String selection = COL_GROUP_ID + " = ? AND " + COL_STORE_ID + " = ?";
        String selectionArgs[] = {String.valueOf(groupID), String.valueOf(storeID)};
        String sortOrder = null;
        ContentResolver cr = context.getContentResolver();
        cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);

        return cursor;
    }

    private static long getBridgeRowID(Context context, long itemID, long groupID, long storeID) {
        long bridgeRowID = -1;
        if (bridgeRowExists(context, itemID, groupID, storeID)) {
            bridgeRowID = mExistingBridgeRowID;
        }
        return bridgeRowID;
    }
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Update Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int updateBridgeRowValues(Context context, long bridgeRowID, ContentValues newFieldValues) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(bridgeRowID));
        String selection = null;
        String[] selectionArgs = null;
        return cr.update(uri, newFieldValues, selection, selectionArgs);
    }

    public static void setLocation(Context context, long bridgeRowID, long locationID) {
        if (bridgeRowID > 0) {
            ContentValues cv = new ContentValues();
            cv.put(COL_LOCATION_ID, locationID);
            updateBridgeRowValues(context, bridgeRowID, cv);
        }
    }

    public static void setLocation(Context context, long itemID, long groupID, long storeID, long locationID) {
        long bridgeRowID;
        if (bridgeRowExists(context, itemID, groupID, storeID)) {
            bridgeRowID = mExistingBridgeRowID;
            ContentValues values = new ContentValues();
            values.put(COL_LOCATION_ID, locationID);
            updateBridgeRowValues(context, bridgeRowID, values);
        } else {
            // create a new bridge row
            createNewBridgeRow(context, itemID, groupID, storeID, locationID);
        }
    }

    public static int setGroupID(Context context, long locationID, long groupID) {
        int numberOfUpdatedRecords = -1;
        // cannot set the default group
        if (groupID > 1) {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(locationID));
            String selection = null;
            String[] selectionArgs = null;

            ContentValues values = new ContentValues();
            values.put(COL_GROUP_ID, groupID);
            numberOfUpdatedRecords = cr.update(uri, values, selection, selectionArgs);
        }
        return numberOfUpdatedRecords;
    }

    public static int setItemID(Context context, long locationID, long itemID) {
        int numberOfUpdatedRecords = -1;
        if (itemID > 0) {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(locationID));
            String selection = null;
            String[] selectionArgs = null;

            ContentValues values = new ContentValues();
            values.put(COL_ITEM_ID, itemID);
            numberOfUpdatedRecords = cr.update(uri, values, selection, selectionArgs);
        }
        return numberOfUpdatedRecords;
    }

    public static int resetGroupID(Context context, long groupID) {
        int numberOfUpdatedRecords = -1;
        // cannot set the default group
        if (groupID > 1) {
            ContentResolver cr = context.getContentResolver();
            Uri uri = CONTENT_URI;
            String selection = COL_GROUP_ID + " = ?";
            String[] selectionArgs = {String.valueOf(groupID)};

            ContentValues values = new ContentValues();
            values.put(COL_GROUP_ID, 1); // groupID = 1 is the default groupID
            numberOfUpdatedRecords = cr.update(uri, values, selection, selectionArgs);
        }
        return numberOfUpdatedRecords;
    }

    public static int resetLocationID(Context context, long locationID) {
        int numberOfUpdatedRecords = -1;
        if (locationID > 1) {
            ContentResolver cr = context.getContentResolver();
            Uri uri = CONTENT_URI;
            String selection = COL_LOCATION_ID + " = ?";
            String[] selectionArgs = {String.valueOf(locationID)};

            ContentValues values = new ContentValues();
            values.put(COL_LOCATION_ID, 1); // locationID = 1 is the default locationID
            numberOfUpdatedRecords = cr.update(uri, values, selection, selectionArgs);
        }
        return numberOfUpdatedRecords;
    }


    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Delete Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int deleteBridgeRow(Context context, long bridgeRowID) {
        int numberOfDeletedRecords = -1;
        if (bridgeRowID > 0) {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(bridgeRowID));
            String selection = null;
            String[] selectionArgs = null;
            cr.delete(uri, selection, selectionArgs);
        }
        return numberOfDeletedRecords;
    }

    public static int deleteAllBridgeRowsInStore(Context context, long storeID) {
        int numberOfDeletedRecords = -1;
        if (storeID > 0) {
            Uri uri = CONTENT_URI;
            String selection = COL_STORE_ID + " = ?";
            String selectionArgs[] = new String[]{String.valueOf(storeID)};
            ContentResolver cr = context.getContentResolver();
            numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);
        }
        return numberOfDeletedRecords;
    }

    public static int deleteAllBridgeRowsInGroup(Context context, long groupID) {
        int numberOfDeletedRecords = -1;
        // cannot delete the default group
        if (groupID > 1) {
            Uri uri = CONTENT_URI;
            String selection = COL_GROUP_ID + " = ?";
            String selectionArgs[] = new String[]{String.valueOf(groupID)};
            ContentResolver cr = context.getContentResolver();
            numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);
        }
        return numberOfDeletedRecords;
    }

    public static int deleteAllBridgeRowsWithItemID(Context context, long itemID) {
        int numberOfDeletedRecords = -1;
        if (itemID > 0) {
            Uri uri = CONTENT_URI;
            String where = COL_ITEM_ID + " = ?";
            String selectionArgs[] = new String[]{String.valueOf(itemID)};
            ContentResolver cr = context.getContentResolver();
            numberOfDeletedRecords = cr.delete(uri, where, selectionArgs);
        }
        return numberOfDeletedRecords;
    }

    public static int deleteAllBridgeRowsWithLocation(Context context, long locationID) {
        int numberOfDeletedRecords = -1;
        // cannot delete the default location
        if (locationID > 1) {
            Uri uri = CONTENT_URI;
            String selection = COL_LOCATION_ID + " = ?";
            String selectionArgs[] = new String[]{String.valueOf(locationID)};
            ContentResolver cr = context.getContentResolver();
            numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);
        }
        return numberOfDeletedRecords;
    }

}
