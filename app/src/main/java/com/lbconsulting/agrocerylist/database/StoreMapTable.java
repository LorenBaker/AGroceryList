package com.lbconsulting.agrocerylist.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lbconsulting.agrocerylist.classes.MyLog;

public class StoreMapTable {

    public static final String TABLE_LOCATIONS_BRIDGE = "tblStoreMaps";
    public static final String COL_MAP_ENTRY_ID = "_id";
    public static final String COL_ITEM_ID = "itemID";
    public static final String COL_GROUP_ID = "groupID";
    public static final String COL_STORE_ID = "storeID";
    public static final String COL_LOCATION_ID = "locationID";

    public static final String[] PROJECTION_ALL = {COL_MAP_ENTRY_ID, COL_ITEM_ID, COL_GROUP_ID, COL_STORE_ID, COL_LOCATION_ID};

    public static final String CONTENT_PATH = TABLE_LOCATIONS_BRIDGE;

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting." + TABLE_LOCATIONS_BRIDGE;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting." + TABLE_LOCATIONS_BRIDGE;
    public static final Uri CONTENT_URI = Uri.parse("content://" + aGroceryListContentProvider.AUTHORITY + "/" + CONTENT_PATH);


    private static long mExistingMapEntryID;
    private static long DEFAULT_LOCATION = 1;

    // Database creation SQL statements
    private static final String CREATE_TABLE = "create table " + TABLE_LOCATIONS_BRIDGE + " ("
            + COL_MAP_ENTRY_ID + " integer primary key autoincrement, "
            + COL_ITEM_ID + " integer not null references " + ItemsTable.TABLE_ITEMS + " (" + ItemsTable.COL_ITEM_ID + ") default -1, "
            + COL_GROUP_ID + " integer not null references " + GroupsTable.TABLE_GROUPS + " (" + GroupsTable.COL_GROUP_ID + ") default 1, "
            + COL_STORE_ID + " integer not null references " + StoresTable.TABLE_STORES + " (" + StoresTable.COL_STORE_ID + ") default -1, "
            + COL_LOCATION_ID + " integer not null references " + LocationsTable.TABLE_LOCATIONS + " (" + LocationsTable.COL_LOCATION_ID + ") default 1 "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
        MyLog.i("StoreMapTable", "onCreate: " + TABLE_LOCATIONS_BRIDGE + " created.");
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


    public static long createNewStoreMapEntry(Context context, long itemID, long groupID, long storeID, long locationID) {
        long newMapEntryID = -1;
        // crate a new bridge row if the inputs are valid
        if ((itemID > 0 || groupID > 0) && storeID > 0 && locationID > 0) {
            if (storeMapEntryExists(context, itemID, groupID, storeID)) {
                // the bridge row exists in the table ... so return its id
                newMapEntryID = mExistingMapEntryID;
            } else {
                // the group does not exist in the table ... so add it
                ContentResolver cr = context.getContentResolver();
                Uri uri = CONTENT_URI;
                ContentValues values = new ContentValues();
                values.put(COL_ITEM_ID, itemID);
                values.put(COL_GROUP_ID, groupID);
                values.put(COL_STORE_ID, storeID);
                values.put(COL_LOCATION_ID, locationID);
                Uri newMapEntryUri = cr.insert(uri, values);
                if (newMapEntryUri != null) {
                    newMapEntryID = Long.parseLong(newMapEntryUri.getLastPathSegment());
                }
            }
        }
        return newMapEntryID;
    }

    public static void initializeStoreMap(Context context, long storeID) {
        // delete all map entries associated with the store
        deleteAllStoreMapEntriesInStore(context, storeID);

        // create a map entry for each group with the default location
        Cursor groupsCursor = GroupsTable.getAllGroupsCursor(context,GroupsTable.SORT_ORDER_GROUP_NAME);
        if (groupsCursor != null && groupsCursor.getCount() > 0) {
            long groupID;
            while (groupsCursor.moveToNext()) {
                groupID = groupsCursor.getLong(groupsCursor.getColumnIndex(GroupsTable.COL_GROUP_ID));
                createNewStoreMapEntry(context, -1, groupID, storeID, DEFAULT_LOCATION);
            }
        }
        if (groupsCursor != null) {
            groupsCursor.close();
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Read Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////

    private static Cursor getMapEntryCursor(Context context, long mapEntryID) {
        Cursor cursor = null;
        if (mapEntryID > 0) {
            Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(mapEntryID));
            String[] projection = PROJECTION_ALL;
            String selection = null;
            String selectionArgs[] = null;
            String sortOrder = null;
            ContentResolver cr = context.getContentResolver();
            try {
                cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
            } catch (Exception e) {
                MyLog.e("StoreMapTable", "getMapEntryCursor: Exception: " + e.getMessage());
            }
        } else {
            MyLog.e("StoreMapTable", "getMapEntryCursor: Invalid mapEntryID");
        }
        return cursor;
    }

    private static boolean storeMapEntryExists(Context context, long itemID, long groupID, long storeID) {
        // the bridge row exists if either of the following is true:
        // 1) the itemID and storeID pair exists in the table, or
        // 2) the groupID and storeID pair exists in the table.

        mExistingMapEntryID = -1;
        boolean mapEntryExists = false;

        // check existence if the groupID is greater than the default groupID
        if (groupID > 1) {
            Cursor groupStoreCursor = getGroupStoreCursor(context, groupID, storeID);
            if (groupStoreCursor != null) {
                if (groupStoreCursor.getCount() > 0) {
                    groupStoreCursor.moveToFirst();
                    mExistingMapEntryID = groupStoreCursor.getLong(groupStoreCursor.getColumnIndex(COL_MAP_ENTRY_ID));
                    mapEntryExists = true;
                }
                groupStoreCursor.close();
            }
        }

        if (!mapEntryExists && itemID > 0) {
            Cursor itemStoreCursor = getItemStoreCursor(context, itemID, storeID);
            if (itemStoreCursor != null) {
                if (itemStoreCursor.getCount() > 0) {
                    itemStoreCursor.moveToFirst();
                    mExistingMapEntryID = itemStoreCursor.getLong(itemStoreCursor.getColumnIndex(COL_MAP_ENTRY_ID));
                    mapEntryExists = true;
                }
                itemStoreCursor.close();
            }
        }
        return mapEntryExists;
    }

    private static Cursor getItemStoreCursor(Context context, long itemID, long storeID) {
        Cursor cursor;

        Uri uri = CONTENT_URI;
        String[] projection = new String[]{COL_MAP_ENTRY_ID};
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
        String[] projection = new String[]{COL_MAP_ENTRY_ID};
        String selection = COL_GROUP_ID + " = ? AND " + COL_STORE_ID + " = ?";
        String selectionArgs[] = {String.valueOf(groupID), String.valueOf(storeID)};
        String sortOrder = null;
        ContentResolver cr = context.getContentResolver();
        cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);

        return cursor;
    }

    private static long getStoreMapEntryID(Context context, long itemID, long groupID, long storeID) {
        long mapEntryID = -1;
        if (storeMapEntryExists(context, itemID, groupID, storeID)) {
            mapEntryID = mExistingMapEntryID;
        }
        return mapEntryID;
    }

    public static long getLocationID(Context context, long itemID, long groupID, long storeID) {
        long locationID = DEFAULT_LOCATION;

        long mapEntryID = getStoreMapEntryID(context, itemID, groupID, storeID);
        Cursor mapEntryCursor = getMapEntryCursor(context, mapEntryID);
        if (mapEntryCursor != null && mapEntryCursor.getCount() > 0) {
            mapEntryCursor.moveToFirst();
            locationID = mapEntryCursor.getLong(mapEntryCursor.getColumnIndex(COL_LOCATION_ID));
        }
        if (mapEntryCursor != null) {
            mapEntryCursor.close();
        }
        return locationID;
    }


    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Update Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int updateStoreMapEntryValues(Context context, long mapEntryID, ContentValues newFieldValues) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(mapEntryID));
        String selection = null;
        String[] selectionArgs = null;
        return cr.update(uri, newFieldValues, selection, selectionArgs);
    }

    public static void setLocation(Context context, long mapEntryID, long locationID) {
        if (mapEntryID > 0) {
            ContentValues cv = new ContentValues();
            cv.put(COL_LOCATION_ID, locationID);
            updateStoreMapEntryValues(context, mapEntryID, cv);
        }
    }

    public static void setLocation(Context context, long itemID, long groupID, long storeID, long locationID) {
        long mapEntryID;
        if (storeMapEntryExists(context, itemID, groupID, storeID)) {
            mapEntryID = mExistingMapEntryID;
            ContentValues values = new ContentValues();
            values.put(COL_LOCATION_ID, locationID);
            updateStoreMapEntryValues(context, mapEntryID, values);
        } else {
            // create a new bridge row
            createNewStoreMapEntry(context, itemID, groupID, storeID, locationID);
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
    public static int deleteStoreMapEntry(Context context, long mapEntryID) {
        int numberOfDeletedRecords = -1;
        if (mapEntryID > 0) {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(mapEntryID));
            String selection = null;
            String[] selectionArgs = null;
            cr.delete(uri, selection, selectionArgs);
        }
        return numberOfDeletedRecords;
    }

    public static int deleteAllStoreMapEntriesInStore(Context context, long storeID) {
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

/*    public static int deleteAllStoreMapEntriesInGroup(Context context, long groupID) {
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
    }*/

    public static int deleteAllStoreMapEntriesWithItemID(Context context, long itemID) {
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




}


