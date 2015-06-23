package com.lbconsulting.agrocerylist.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lbconsulting.agrocerylist.classes.MyLog;

import java.util.Calendar;

public class ItemsTable {

    // Items data table
    // Version 1
    public static final String TABLE_ITEMS = "tblItems";
    public static final String COL_ITEM_ID = "_id";
    public static final String COL_ITEM_NAME = "itemName";
    public static final String COL_ITEM_NOTE = "itemNote";
    public static final String COL_GROUP_ID = "groupID";
    public static final String COL_STRUCK_OUT = "itemStruckOut";
    public static final String COL_CHECKED = "itemChecked";
    public static final String COL_MANUAL_SORT_ORDER = "manualSortOrder";
    public static final String COL_MANUAL_SORT_SWITCH = "manualSortSwitch";
    public static final String COL_DATE_TIME_LAST_USED = "dateTimeLastUsed";

    public static final String[] PROJECTION_ALL = {COL_ITEM_ID, COL_ITEM_NAME, COL_ITEM_NOTE,
            COL_GROUP_ID, COL_STRUCK_OUT, COL_CHECKED,
            COL_MANUAL_SORT_ORDER, COL_MANUAL_SORT_SWITCH, COL_DATE_TIME_LAST_USED};

    public static final String[] PROJECTION_WITH_GROUP_NAME = {
            TABLE_ITEMS + "." + COL_ITEM_ID,
            TABLE_ITEMS + "." + COL_ITEM_NAME,
            TABLE_ITEMS + "." + COL_ITEM_NOTE,
            TABLE_ITEMS + "." + COL_GROUP_ID,
            TABLE_ITEMS + "." + COL_STRUCK_OUT,
            TABLE_ITEMS + "." + COL_CHECKED,
            TABLE_ITEMS + "." + COL_MANUAL_SORT_ORDER,
            TABLE_ITEMS + "." + COL_MANUAL_SORT_SWITCH,
            GroupsTable.TABLE_GROUPS + "." + GroupsTable.COL_GROUP_NAME};

    public static final String[] PROJECTION_WITH_ITEM_NAME_AND_GROUP_NAME = {
            TABLE_ITEMS + "." + COL_ITEM_ID,
            TABLE_ITEMS + "." + COL_ITEM_NAME,
            GroupsTable.TABLE_GROUPS + "." + GroupsTable.COL_GROUP_NAME};

    public static final String[] PROJECTION_WITH_LOCATION_NAME = {
            TABLE_ITEMS + "." + COL_ITEM_ID,
            TABLE_ITEMS + "." + COL_ITEM_NAME,
            TABLE_ITEMS + "." + COL_ITEM_NOTE,
            TABLE_ITEMS + "." + COL_GROUP_ID,
            TABLE_ITEMS + "." + COL_STRUCK_OUT,
            TABLE_ITEMS + "." + COL_CHECKED,
            TABLE_ITEMS + "." + COL_MANUAL_SORT_ORDER,
            TABLE_ITEMS + "." + COL_MANUAL_SORT_SWITCH,
            BridgeTable.TABLE_BRIDGE + "." + BridgeTable.COL_LOCATION_ID,
            LocationsTable.TABLE_LOCATIONS + "." + LocationsTable.COL_LOCATION_NAME};

    public static final String CONTENT_PATH = TABLE_ITEMS;
    public static final String CONTENT_PATH_ITEMS_WITH_GROUPS = "itemsWithGroups";
    public static final String CONTENT_PATH_ITEMS_WITH_LOCATIONS = "itemsWithLocations";

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting."
            + TABLE_ITEMS;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting."
            + TABLE_ITEMS;
    public static final Uri CONTENT_URI = Uri.parse("content://" + aGroceryListContentProvider.AUTHORITY + "/" + CONTENT_PATH);

    public static final Uri CONTENT_URI_ITEMS_WITH_GROUPS = Uri.parse("content://" + aGroceryListContentProvider.AUTHORITY
            + "/" + CONTENT_PATH_ITEMS_WITH_GROUPS);

    public static final Uri CONTENT_URI_ITEMS_WITH_LOCATIONS = Uri.parse("content://" + aGroceryListContentProvider.AUTHORITY
            + "/" + CONTENT_PATH_ITEMS_WITH_LOCATIONS);

    public static final String SORT_ORDER_ITEM_NAME = COL_ITEM_NAME + " ASC";
    public static final String SORT_ORDER_LAST_USED = COL_DATE_TIME_LAST_USED + " DESC, " + SORT_ORDER_ITEM_NAME;
    public static final String SORT_ORDER_MANUAL = COL_MANUAL_SORT_ORDER + " ASC";

    // TODO: SORT by group name not id!
    // public static final String SORT_ORDER_BY_GROUP = COL_GROUP_ID + " ASC, "
    // + SORT_ORDER_ITEM_NAME;

    public static final int FALSE = 0;
    public static final int TRUE = 1;

    public static final int MANUAL_SORT_SWITCH_INVISIBLE = 0;
    public static final int MANUAL_SORT_SWITCH_VISIBLE = 1;
    public static final int MANUAL_SORT_SWITCH_ITEM_SWITCHED = 2;

    private static final long milliSecondsPerDay = 1000 * 60 * 60 * 24;
    // Database creation SQL statements
    private static final String CREATE_TABLE =
            "create table " + TABLE_ITEMS
                    + " ("
                    + COL_ITEM_ID + " integer primary key autoincrement, "
                    + COL_ITEM_NAME + " text collate nocase, "
                    + COL_ITEM_NOTE + " text collate nocase, "
                    + COL_GROUP_ID + " integer not null references " + GroupsTable.TABLE_GROUPS + " (" + GroupsTable.COL_GROUP_ID + ") default -1, "
                    + COL_STRUCK_OUT + " integer default 0, "
                    + COL_CHECKED + " integer default 0, "
                    + COL_MANUAL_SORT_ORDER + " integer default -1, "
                    + COL_MANUAL_SORT_SWITCH + " integer default 1, "
                    + COL_DATE_TIME_LAST_USED + " integer default 0"
                    + ");";

    private static long mExistingItemID;

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
        MyLog.i("ItemsTable", "onCreate: " + TABLE_ITEMS + " created.");
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        MyLog.i(TABLE_ITEMS, "Upgrading database from version " + oldVersion + " to version " + newVersion);
        // This wipes out all of the user data and create an empty table
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(database);
    }


// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Create Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static long createNewItem(Context context, String itemName) {
        long newItemID = -1;
        itemName = itemName.trim();

        if (!itemExists(context, itemName)) {
            // item does not exist in the table ... so add it
            try {
                ContentResolver cr = context.getContentResolver();
                Uri uri = CONTENT_URI;
                ContentValues values = new ContentValues();
                values.put(COL_ITEM_NAME, itemName);
                values.put(COL_DATE_TIME_LAST_USED, System.currentTimeMillis());

                Uri newListUri = cr.insert(uri, values);
                if (newListUri != null) {
                    newItemID = Long.parseLong(newListUri.getLastPathSegment());
                    values = new ContentValues();
                    values.put(COL_MANUAL_SORT_ORDER, newItemID);
                    updateItemFieldValues(context, newItemID, values);
                }
            } catch (Exception e) {
                MyLog.e("Exception error in CreateNewItem. ", e.toString());
            }

        } else {
            // the item exists in the table ... so return its ID
            newItemID = mExistingItemID; // mExistingItemID is set in itemExists method
        }
        return newItemID;
    }

    private static boolean itemExists(Context context, String itemName) {
        mExistingItemID = -1;
        boolean result = false;
        Cursor cursor = getItemCursor(context, itemName);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                mExistingItemID = cursor.getLong(cursor.getColumnIndex(COL_ITEM_ID));
                result = true;
            }
            cursor.close();
        }
        return result;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Read Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static Cursor getItemCursor(Context context, long itemID) {
        Cursor cursor = null;
        if (itemID > 0) {
            Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(itemID));
            String[] projection = PROJECTION_ALL;
            String selection = null;
            String selectionArgs[] = null;
            String sortOrder = null;
            ContentResolver cr = context.getContentResolver();
            try {
                cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
            } catch (Exception e) {
                MyLog.e("ItemsTable", "getItemCursor: Exception: " + e.getMessage());
            }
        } else {
            MyLog.e("ItemsTable", "getItemCursor: Invalid itemID");
        }
        return cursor;
    }

    public static Cursor getItemCursor(Context context, String itemName) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = COL_ITEM_NAME + " = ?";
        String selectionArgs[] = new String[]{itemName};
        String sortOrder = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("ItemsTable", "getItemCursor: Exception: " + e.getMessage());
        }
        return cursor;
    }

    public static Cursor getAllCheckedItemsCursor(Context context) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = {COL_ITEM_ID};
        String selection = COL_CHECKED + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(TRUE)};
        String sortOrder = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("ItemsTable", "getAllCheckedItemsCursor: Exception: " + e.getMessage());
        }
        return cursor;
    }

    public static CursorLoader getAllItems(Context context, String sortOrder) {
        CursorLoader cursorLoader = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;

        String selection = null;
        String selectionArgs[] = null;
        try {
            cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("ItemsTable", "getAllItems: Exception: " + e.getMessage());
        }
        return cursorLoader;
    }

/*    public static boolean isItemSwitched(Context context, long itemID) {
        boolean result = false;
        Cursor itemCursor = getItemCursor(context, itemID);
        if (itemCursor != null) {
            itemCursor.moveToFirst();
            int switchValue = itemCursor.getInt(itemCursor.getColumnIndexOrThrow(COL_MANUAL_SORT_SWITCH));
            result = switchValue > 1;
            itemCursor.close();
        }
        return result;
    }

    public static boolean isItemVisible(Context context, long itemID) {
        boolean result = false;
        Cursor itemCursor = getItemCursor(context, itemID);
        if (itemCursor != null) {
            itemCursor.moveToFirst();
            int switchValue = itemCursor.getInt(itemCursor.getColumnIndexOrThrow(COL_MANUAL_SORT_SWITCH));
            result = switchValue > 0;
            itemCursor.close();
        }
        return result;
    }*/

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Update Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int updateItemFieldValues(Context context, long itemID, ContentValues newFieldValues) {
        int numberOfUpdatedRecords = -1;
        if (itemID > 0) {
            ContentResolver cr = context.getContentResolver();
            Uri itemUri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(itemID));
            String selection = null;
            String[] selectionArgs = null;
            numberOfUpdatedRecords = cr.update(itemUri, newFieldValues, selection, selectionArgs);
        } else {
            MyLog.e("ItemsTable", "updateItemFieldValues: Invalid itemID.");
        }
        return numberOfUpdatedRecords;
    }

/*    public static void setItemInvisible(Context context, long itemID) {
        ContentValues newFieldValues = new ContentValues();
        newFieldValues.put(COL_MANUAL_SORT_SWITCH, MANUAL_SORT_SWITCH_INVISIBLE);
        updateItemFieldValues(context, itemID, newFieldValues);
    }

    public static void setItemVisible(Context context, long itemID) {
        ContentValues newFieldValues = new ContentValues();
        newFieldValues.put(COL_MANUAL_SORT_SWITCH, MANUAL_SORT_SWITCH_VISIBLE);
        updateItemFieldValues(context, itemID, newFieldValues);
    }*/

    public static void toggleStrikeOut(Context context, long itemID) {
        Cursor cursor = getItemCursor(context, itemID);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int strikeOutValue = cursor.getInt(cursor.getColumnIndex(COL_STRUCK_OUT));
            int newStrikeOutValue = FALSE;
            if (strikeOutValue == FALSE) {
                newStrikeOutValue = TRUE;
            }
            ContentValues cv = new ContentValues();
            cv.put(COL_STRUCK_OUT, newStrikeOutValue);
            updateItemFieldValues(context, itemID, cv);

        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static void removeStrikeOut(Context context, long itemID) {

        Cursor cursor = getItemCursor(context, itemID);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int strikeOutValue = cursor.getInt(cursor.getColumnIndex(COL_STRUCK_OUT));
            int newStrikeOutValue = FALSE;
            ContentValues cv = new ContentValues();
            cv.put(COL_STRUCK_OUT, newStrikeOutValue);
            updateItemFieldValues(context, itemID, cv);
        }
        if (cursor != null) {
            cursor.close();
        }
    }


    public static void toggleCheckBox(Context context, long itemID) {
        Cursor cursor = getItemCursor(context, itemID);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int checkedValue = cursor.getInt(cursor.getColumnIndex(COL_CHECKED));
            int newCheckedValue = FALSE;
            if (checkedValue == FALSE) {
                newCheckedValue = TRUE;
            }
            ContentValues cv = new ContentValues();
            cv.put(COL_CHECKED, newCheckedValue);
            updateItemFieldValues(context, itemID, cv);
        }
        if (cursor != null) {
            cursor.close();
        }
    }


    public static int unCheckAllItems(Context context) {
        int numberOfUpdatedRecords = -1;

        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = CONTENT_URI;
            String selection = null;
            String selectionArgs[] = null;
            ContentValues newFieldValues = new ContentValues();
            newFieldValues.put(COL_CHECKED, FALSE);
            numberOfUpdatedRecords = cr.update(uri, newFieldValues, selection, selectionArgs);

        } catch (Exception e) {
            MyLog.e("ItemsTable", "unCheckAllItems: Exception: " + e.getMessage());
        }

        return numberOfUpdatedRecords;
    }


    public static int checkUnusedItems(Context context, long numberOfDays) {
        int numberOfCheckedItems = -1;

        long numberOfMilliSeconds = numberOfDays * milliSecondsPerDay;
        Calendar now = Calendar.getInstance();
        long dateTimeCutOff = now.getTimeInMillis() - numberOfMilliSeconds;

        ContentResolver cr = context.getContentResolver();
        Uri itemUri = CONTENT_URI;
        String selection = COL_DATE_TIME_LAST_USED + " < ?";
        String[] selectionArgs = {String.valueOf(dateTimeCutOff)};

        ContentValues values = new ContentValues();
        values.put(COL_CHECKED, TRUE);

        numberOfCheckedItems = cr.update(itemUri, values, selection, selectionArgs);

        return numberOfCheckedItems;
    }


/*
    public static void swapManualSortOrder(Context context, long mobileItemID, long switchItemID, long previousSwitchItemID) {
        int numberOfUpdatedRecords = -1;
        if (mobileItemID > 0 && switchItemID > 0) {
            try {
                Cursor mobileItemCursor = getItemCursor(context, mobileItemID);
                Cursor switchItemCursor = getItemCursor(context, switchItemID);

                mobileItemCursor.moveToFirst();
                switchItemCursor.moveToFirst();

                int mobileItemManualSortOrder = mobileItemCursor.getInt(mobileItemCursor.getColumnIndexOrThrow(COL_MANUAL_SORT_ORDER));
                int switchItemManualSortOrder = switchItemCursor.getInt(switchItemCursor.getColumnIndexOrThrow(COL_MANUAL_SORT_ORDER));

                // TODO remove strings names
                String mobileItemName = mobileItemCursor.getString(mobileItemCursor.getColumnIndexOrThrow(COL_ITEM_NAME));
                String switchItemName = switchItemCursor.getString(switchItemCursor.getColumnIndexOrThrow(COL_ITEM_NAME));

                ContentResolver cr = context.getContentResolver();
                Uri uri = CONTENT_URI;
                String where = COL_ITEM_ID + " = ?";
                String[] whereArgsMobileItemCursor = {String.valueOf(mobileItemID)};
                ContentValues values = new ContentValues();
                values.put(COL_MANUAL_SORT_ORDER, switchItemManualSortOrder);
                numberOfUpdatedRecords = cr.update(uri, values, where, whereArgsMobileItemCursor);

                String[] whereArgsSwitchItemCursor = {String.valueOf(switchItemID)};
                values = new ContentValues();
                values.put(COL_MANUAL_SORT_ORDER, mobileItemManualSortOrder);
                values.put(COL_MANUAL_SORT_SWITCH, MANUAL_SORT_SWITCH_ITEM_SWITCHED);
                numberOfUpdatedRecords += cr.update(uri, values, where, whereArgsSwitchItemCursor);

                if (numberOfUpdatedRecords != 2) {
                    MyLog.e("ItemsTable", "SwapManualSortOrder: Incorrect number of records updated.");
                }

                if (previousSwitchItemID > 0) {
                    String[] whereArgsPreviousSwitchedItem = {String.valueOf(previousSwitchItemID)};
                    values = new ContentValues();
                    values.put(COL_MANUAL_SORT_SWITCH, MANUAL_SORT_SWITCH_VISIBLE);
                    numberOfUpdatedRecords += cr.update(uri, values, where, whereArgsPreviousSwitchedItem);
                }

                mobileItemCursor.close();
                switchItemCursor.close();
                MyLog.i("ItemsTable",
                        "SwapManualSortOrder: mobileItem:"
                                + mobileItemName + "(" + mobileItemManualSortOrder + ")"
                                + " MANUAL_SORT_ORDER swapped with switchItem:"
                                + switchItemName + "(" + switchItemManualSortOrder + ")");

            } catch (Exception e) {
                MyLog.e("ItemsTable", "SwapManualSortOrder: Exception: " + e.getMessage());
            }
        }
    }

*/

/*    public static int setManualSortOrder(Context context, long itemID, int manualSortOrder) {
        int numberOfUpdatedRecords = -1;
        if (itemID > 0) {
            try {
                ContentResolver cr = context.getContentResolver();
                Uri uri = CONTENT_URI;
                String where = COL_ITEM_ID + " = ?";
                String[] whereArgs = {String.valueOf(itemID)};

                ContentValues values = new ContentValues();
                values.put(COL_MANUAL_SORT_ORDER, manualSortOrder);
                numberOfUpdatedRecords = cr.update(uri, values, where, whereArgs);
            } catch (Exception e) {
                MyLog.e("ItemsTable", "setManualSortOrder: Exception: " + e.getMessage());
            }
        } else {
            MyLog.e("ItemsTable", "setManualSortOrder: Invalid itemID");
        }
        return numberOfUpdatedRecords;
    }

    public static int getManualSortOrder(Context context, long itemID) {
        int manualSortOrder = -1;
        if (itemID > 0) {
            Cursor cursor = getItemCursor(context, itemID);
            if (cursor != null) {
                cursor.moveToFirst();
                manualSortOrder = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MANUAL_SORT_ORDER));
                cursor.close();
            }
        }
        return manualSortOrder;
    }*/

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Delete Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int deleteItem(Context context, long itemID) {
        int numberOfDeletedRecords = 0;
        if (itemID > 0) {
            numberOfDeletedRecords += SelectedItemsTable.removeItem(context, itemID);

            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(itemID));
            String selection = null;
            String[] selectionArgs = null;
            numberOfDeletedRecords += cr.delete(uri, selection, selectionArgs);
        } else {
            MyLog.e("ItemsTable", "deleteItem: Invalid itemID");
        }
        return numberOfDeletedRecords;
    }


    public static int deleteAllCheckedItems(Context context) {
        int numberOfDeletedRecords = 0;

        Cursor cursor = getAllCheckedItemsCursor(context);
        long itemID;
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                itemID = cursor.getLong(cursor.getColumnIndex(COL_ITEM_ID));
                numberOfDeletedRecords += deleteItem(context, itemID);
            }
        }
        if (cursor != null){
            cursor.close();
        }
        return numberOfDeletedRecords;
    }


}
