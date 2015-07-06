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
    public static final String COL_PRODUCT_ID = "productID";
    public static final String COL_SELECTED = "itemSelected";
    public static final String COL_STRUCK_OUT = "itemStruckOut";
    public static final String COL_CHECKED = "itemChecked";
    public static final String COL_FAVORITE = "itemIsFavorite";
    public static final String COL_MANUAL_SORT_ORDER = "manualSortOrder";
    public static final String COL_DATE_TIME_LAST_USED = "dateTimeLastUsed";

    public static final String[] PROJECTION_ALL = {COL_ITEM_ID, COL_ITEM_NAME, COL_ITEM_NOTE,
            COL_GROUP_ID, COL_PRODUCT_ID, COL_SELECTED, COL_STRUCK_OUT, COL_CHECKED, COL_FAVORITE,
            COL_MANUAL_SORT_ORDER, COL_DATE_TIME_LAST_USED};

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

    public static final int FALSE = 0;
    public static final int TRUE = 1;

    private static final long milliSecondsPerDay = 1000 * 60 * 60 * 24;

    // Database creation SQL statements
    private static final String CREATE_TABLE =
            "create table " + TABLE_ITEMS
                    + " ("
                    + COL_ITEM_ID + " integer primary key autoincrement, "
                    + COL_ITEM_NAME + " text collate nocase default '', "
                    + COL_ITEM_NOTE + " text collate nocase default '', "
                    + COL_GROUP_ID + " integer not null references " + GroupsTable.TABLE_GROUPS + " (" + GroupsTable.COL_GROUP_ID + ") default 1, "
                    + COL_PRODUCT_ID + " integer default -1, "
                    + COL_SELECTED + " integer default 0, "
                    + COL_STRUCK_OUT + " integer default 0, "
                    + COL_CHECKED + " integer default 0, "
                    + COL_FAVORITE + " integer default 0, "
                    + COL_MANUAL_SORT_ORDER + " integer default -1, "
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
                ContentValues cv = new ContentValues();
                cv.put(COL_ITEM_NAME, itemName);
                cv.put(COL_DATE_TIME_LAST_USED, System.currentTimeMillis());

                Uri newListUri = cr.insert(uri, cv);
                if (newListUri != null) {
                    newItemID = Long.parseLong(newListUri.getLastPathSegment());
                    cv = new ContentValues();
                    cv.put(COL_MANUAL_SORT_ORDER, newItemID);
                    updateItemFieldValues(context, newItemID, cv);
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

    public static void createNewItem(Context context, String itemName, String strGroupID) {

        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        ContentValues cv = new ContentValues();
        cv.put(COL_ITEM_NAME, itemName);
        int groupID = Integer.parseInt(strGroupID);

        // make sure there is a valid groupID
        if (groupID < 1) {
            groupID = 1;
        }
        cv.put(COL_DATE_TIME_LAST_USED, System.currentTimeMillis());
        cv.put(COL_GROUP_ID, groupID);

        Uri newListUri = cr.insert(uri, cv);
        if (newListUri != null) {
            long newItemID = Long.parseLong(newListUri.getLastPathSegment());
            cv = new ContentValues();
            cv.put(COL_MANUAL_SORT_ORDER, newItemID);
            updateItemFieldValues(context, newItemID, cv);
        }

    }

    public static boolean itemExists(Context context, String itemName) {
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

    public static Cursor getAllSelectedItemsCursor(Context context, String sortOrder) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = COL_SELECTED + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(TRUE)};
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("ItemsTable", "getAllSelectedItemsCursor: Exception: " + e.getMessage());
        }
        return cursor;
    }

    public static Cursor getAllStruckOutItemsCursor(Context context, String sortOrder) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = COL_STRUCK_OUT + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(TRUE)};
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("ItemsTable", "getAllSelectedItemsCursor: Exception: " + e.getMessage());
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

    public static CursorLoader getAllItems(Context context, String selection, String sortOrder) {
        CursorLoader cursorLoader = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selectionArgs[] = null;
        try {
            cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("ItemsTable", "getAllItems: Exception: " + e.getMessage());
        }
        return cursorLoader;
    }

    public static CursorLoader getAllSelectedItems(Context context, String sortOrder) {
        CursorLoader cursorLoader = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = COL_SELECTED + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(TRUE)};
        try {
            cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("ItemsTable", "getAllSelectedItems: Exception: " + e.getMessage());
        }
        return cursorLoader;
    }

/*    public static Cursor getAllItemsByGroupsCursor(Context context) {
        Cursor cursor = null;

        Uri uri = JoinedTables.CONTENT_URI_ITEMS_BY_GROUPS;
        String[] projection = JoinedTables.PROJECTION_ITEMS_BY_GROUPS;
        String selection = null;
        String selectionArgs[] = null;
        String sortOrder = JoinedTables.SORT_ORDER_ITEMS_BY_GROUP;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("StoresTable", "getAllItemsByGroupsCursor: Exception: " + e.getMessage());
        }
        return cursor;
    }

    public static CursorLoader getAllItemsByGroups(Context context) {
        CursorLoader cursorLoader = null;

        Uri uri = JoinedTables.CONTENT_URI_ITEMS_BY_GROUPS;
        String[] projection = JoinedTables.PROJECTION_ITEMS_BY_GROUPS;
        String selection = null;
        String selectionArgs[] = null;
        String sortOrder = JoinedTables.SORT_ORDER_ITEMS_BY_GROUP;
        try {
            cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("StoresTable", "getAllItemsByGroups: Exception: " + e.getMessage());
        }

        return cursorLoader;
    }*/

    public static CursorLoader getAllSelectedItemsByGroups(Context context, long storeID) {
        CursorLoader cursorLoader = null;

        Uri uri = JoinedTables.CONTENT_URI_ITEMS_BY_LOCATIONS_AND_GROUPS;
        String[] projection = JoinedTables.PROJECTION_ITEMS_BY_LOCATIONS_AND_GROUPS;
        String selection = ItemsTable.TABLE_ITEMS + "." + COL_SELECTED + " = ? AND "
                + StoreMapsTable.TABLE_LOCATIONS_BRIDGE + "." + StoreMapsTable.COL_STORE_ID + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(TRUE), String.valueOf(storeID)};
        String sortOrder = JoinedTables.SORT_ORDER_BY_GROUPS;
        try {
            cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("StoresTable", "getAllSelectedItemsByGroups: Exception: " + e.getMessage());
        }

        return cursorLoader;
    }

    public static CursorLoader getAllSelectedItemsByLocations(Context context, long storeID) {
        CursorLoader cursorLoader = null;

        Uri uri = JoinedTables.CONTENT_URI_ITEMS_BY_LOCATIONS_AND_GROUPS;
        String[] projection = JoinedTables.PROJECTION_ITEMS_BY_LOCATIONS_AND_GROUPS;
        String selection = ItemsTable.TABLE_ITEMS + "." + COL_SELECTED + " = ? AND "
                + StoreMapsTable.TABLE_LOCATIONS_BRIDGE + "." + StoreMapsTable.COL_STORE_ID + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(TRUE), String.valueOf(storeID)};
        String sortOrder = JoinedTables.SORT_ORDER_BY_LOCATIONS;
        try {
            cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("StoresTable", "getAllSelectedItemsByLocations: Exception: " + e.getMessage());
        }

        return cursorLoader;
    }

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
            MyLog.e("ItemsTable", "updateStoreMapEntryValues: Invalid itemID.");
        }
        return numberOfUpdatedRecords;
    }

    public static void setItemSelected(Context context, long itemID, boolean isSelected) {
        int value = FALSE;
        if (isSelected) {
            value = TRUE;
        }
        ContentValues cv = new ContentValues();
        cv.put(COL_SELECTED, value);
        updateItemFieldValues(context, itemID, cv);
    }

    public static int deselectAllItems(Context context) {
        int numberOfUpdatedRecords = -1;

        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = CONTENT_URI;
            String selection = null;
            String selectionArgs[] = null;
            ContentValues newFieldValues = new ContentValues();
            newFieldValues.put(COL_SELECTED, FALSE);
            newFieldValues.put(COL_CHECKED, FALSE);
            numberOfUpdatedRecords = cr.update(uri, newFieldValues, selection, selectionArgs);

        } catch (Exception e) {
            MyLog.e("ItemsTable", "deselectAllItems: Exception: " + e.getMessage());
        }

        return numberOfUpdatedRecords;
    }

    public static void toggleItemSelection(Context context, long itemID) {
        Cursor cursor = getItemCursor(context, itemID);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int selectedValue = cursor.getInt(cursor.getColumnIndex(COL_SELECTED));
            int newSelectedValue = FALSE;
            if (selectedValue == FALSE) {
                newSelectedValue = TRUE;
            }
            ContentValues cv = new ContentValues();
            cv.put(COL_SELECTED, newSelectedValue);
            cv.put(COL_CHECKED, newSelectedValue);
            updateItemFieldValues(context, itemID, cv);

        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static void setItemStruckOut(Context context, long itemID, boolean isStruckOut) {
        int selectedValue = FALSE;
        if (isStruckOut) {
            selectedValue = TRUE;
        }
        ContentValues cv = new ContentValues();
        cv.put(COL_STRUCK_OUT, selectedValue);
        updateItemFieldValues(context, itemID, cv);
    }

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

    public static void setItemFavorite(Context context, long itemID, boolean isFavorite) {
        int value = FALSE;
        if (isFavorite) {
            value = TRUE;
        }
        ContentValues cv = new ContentValues();
        cv.put(COL_FAVORITE, value);
        updateItemFieldValues(context, itemID, cv);
    }

    public static void toggleFavorite(Context context, long itemID) {
        Cursor cursor = getItemCursor(context, itemID);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int isFavoriteValue = cursor.getInt(cursor.getColumnIndex(COL_FAVORITE));
            int newIsFavoriteValue = FALSE;
            if (isFavoriteValue == FALSE) {
                newIsFavoriteValue = TRUE;
            }
            ContentValues cv = new ContentValues();
            cv.put(COL_FAVORITE, newIsFavoriteValue);
            updateItemFieldValues(context, itemID, cv);

        }
        if (cursor != null) {
            cursor.close();
        }
    }

    public static void setItemChecked(Context context, long itemID, boolean isChecked) {
        int value = FALSE;
        if (isChecked) {
            value = TRUE;
        }
        ContentValues cv = new ContentValues();
        cv.put(COL_CHECKED, value);
        updateItemFieldValues(context, itemID, cv);
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

    public static void putItemInGroup(Context context, long itemID, long groupID) {
        if (groupID < 1) {
            groupID = 1;
        }
        ContentValues cv = new ContentValues();
        cv.put(COL_GROUP_ID, groupID);
        updateItemFieldValues(context, itemID, cv);
    }

    public static int removeStruckOffItems(Context context) {
        int numberOfUpdatedRecords = -1;

        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String selection = COL_STRUCK_OUT + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(TRUE)};

        ContentValues newFieldValues = new ContentValues();
        newFieldValues.put(COL_STRUCK_OUT, FALSE);
        newFieldValues.put(COL_SELECTED, FALSE);
        numberOfUpdatedRecords = cr.update(uri, newFieldValues, selection, selectionArgs);

        return numberOfUpdatedRecords;
    }

    public static int removeAllItems(Context context) {
        int numberOfUpdatedRecords = -1;

        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String selection = COL_SELECTED + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(TRUE)};

        ContentValues newFieldValues = new ContentValues();
        newFieldValues.put(COL_STRUCK_OUT, FALSE);
        newFieldValues.put(COL_SELECTED, FALSE);
        numberOfUpdatedRecords = cr.update(uri, newFieldValues, selection, selectionArgs);

        return numberOfUpdatedRecords;
    }

    public static int addAllItems(Context context) {
        int numberOfUpdatedRecords = -1;

        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String selection = null;
        String selectionArgs[] = null;

        ContentValues newFieldValues = new ContentValues();
        newFieldValues.put(COL_SELECTED, TRUE);
        newFieldValues.put(COL_CHECKED, TRUE);
        numberOfUpdatedRecords = cr.update(uri, newFieldValues, selection, selectionArgs);

        return numberOfUpdatedRecords;
    }

    public static int addAllFavoritesItems(Context context) {
        int numberOfUpdatedRecords = -1;

        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String selection = COL_FAVORITE + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(TRUE)};

        ContentValues newFieldValues = new ContentValues();
        newFieldValues.put(COL_SELECTED, TRUE);
        newFieldValues.put(COL_CHECKED, TRUE);
        numberOfUpdatedRecords = cr.update(uri, newFieldValues, selection, selectionArgs);

        return numberOfUpdatedRecords;
    }

    public static void setProductID(Context context, long itemID, long productID) {
        ContentValues cv = new ContentValues();
        cv.put(COL_PRODUCT_ID, productID);
        updateItemFieldValues(context, itemID, cv);
    }


    public static int resetAllItemsWithGroupID(Context context, long groupID) {
        int numberOfUpdatedRecords = -1;

        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String selection = COL_GROUP_ID + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(groupID)};

        ContentValues newFieldValues = new ContentValues();
        newFieldValues.put(COL_GROUP_ID, 1);
        numberOfUpdatedRecords = cr.update(uri, newFieldValues, selection, selectionArgs);

        return numberOfUpdatedRecords;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Delete Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int deleteItem(Context context, long itemID) {
        int numberOfDeletedRecords = 0;
        if (itemID > 0) {
            ContentResolver cr = context.getContentResolver();
            Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(itemID));
            String selection = null;
            String[] selectionArgs = null;
            numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);
        } else {
            MyLog.e("ItemsTable", "deleteItem: Invalid itemID");
        }
        return numberOfDeletedRecords;
    }


    public static int deleteAllCheckedItems(Context context) {
        int numberOfDeletedRecords = 0;

        Uri uri = CONTENT_URI;
        String selection = COL_CHECKED + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(TRUE)};
        ContentResolver cr = context.getContentResolver();
        numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);

        return numberOfDeletedRecords;
    }


}
