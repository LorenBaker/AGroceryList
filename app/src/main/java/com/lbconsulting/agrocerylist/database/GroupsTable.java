package com.lbconsulting.agrocerylist.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.clsGroup;

import java.util.ArrayList;

public class GroupsTable {

    // PublicTablesData data table
    // Version 1
    public static final String TABLE_GROUPS = "tblGroups";
    public static final String COL_GROUP_ID = "_id";
    public static final String COL_GROUP_NAME = "groupName";
    public static final String COL_CHECKED = "checked";

    public static final String[] PROJECTION_ALL = {COL_GROUP_ID, COL_GROUP_NAME, COL_CHECKED};

    public static final String CONTENT_PATH = TABLE_GROUPS;


    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting."
            + TABLE_GROUPS;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting."
            + TABLE_GROUPS;
    public static final Uri CONTENT_URI = Uri.parse("content://" + aGroceryListContentProvider.AUTHORITY + "/" + CONTENT_PATH);

    public static final String SORT_ORDER_GROUP_NAME = COL_GROUP_NAME + " ASC";

    private static long mExistingGroupID = -1;

    // Database creation SQL statements
    private static final String CREATE_TABLE = "create table "
            + TABLE_GROUPS
            + " ("
            + COL_GROUP_ID + " integer primary key autoincrement, "
            + COL_GROUP_NAME + " text collate nocase default '', "
            + COL_CHECKED + " integer default 0 "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
        MyLog.i("GroupsTable", "onCreate: " + TABLE_GROUPS + " created.");
    }


    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        MyLog.i(TABLE_GROUPS, "Upgrading database from version " + oldVersion + " to version " + newVersion);
        // This wipes out all of the user data and create an empty table
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_GROUPS);
        onCreate(database);
    }


    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Create Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static long createNewGroup(Context context, String groupName) {
        long newGroupID = -1;
        groupName = groupName.trim();
        if (!groupName.isEmpty()) {
            if (groupExists(context, groupName)) {
                // the group exists in the table ... so return its id
                newGroupID = mExistingGroupID;
            } else {
                // the group does not exist in the table ... so add it
                try {
                    ContentResolver cr = context.getContentResolver();
                    Uri uri = CONTENT_URI;
                    ContentValues values = new ContentValues();
                    values.put(COL_GROUP_NAME, groupName);
                    Uri newGroupUri = cr.insert(uri, values);
                    if (newGroupUri != null) {
                        newGroupID = Long.parseLong(newGroupUri.getLastPathSegment());
                    }
                } catch (Exception e) {
                    MyLog.e("GroupsTable", "createNewGroup: Exception: " + e.getMessage());
                }
            }
        }
        return newGroupID;
    }


    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Read Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Cursor getGroupCursor(Context context, long groupID) {
        Cursor cursor = null;
        if (groupID > 0) {
            Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(groupID));
            String[] projection = PROJECTION_ALL;
            String selection = null;
            String selectionArgs[] = null;
            String sortOrder = null;
            ContentResolver cr = context.getContentResolver();
            try {
                cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
            } catch (Exception e) {
                MyLog.e("GroupsTable", "getGroupCursor: Exception: " + e.getMessage());
            }
        } else {
            MyLog.e("GroupsTable", "getGroupCursor: Invalid groupID");
        }
        return cursor;
    }

    private static Cursor getGroupCursor(Context context, String groupName) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = COL_GROUP_NAME + " = ?";
        String selectionArgs[] = new String[]{groupName};
        String sortOrder = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("GroupsTable", "getGroupCursor: Exception: " + e.getMessage());
        }
        return cursor;
    }

    public static Cursor getAllGroupsCursor(Context context, String sortOrder) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = null;
        String selectionArgs[] = null;
        if (sortOrder == null) {
            sortOrder = SORT_ORDER_GROUP_NAME;
        }
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("GroupsTable", "getAllGroupsCursor: Exception: " + e.getMessage());
        }
        return cursor;
    }

    private static boolean groupExists(Context context, String groupName) {
        mExistingGroupID = -1;
        boolean result = false;
        Cursor cursor = getGroupCursor(context, groupName);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                mExistingGroupID = cursor.getLong(cursor.getColumnIndex(COL_GROUP_ID));
                result = true;
            }
            cursor.close();
        }
        return result;
    }


    public static CursorLoader getAllGroupNames(Context context, String sortOrder) {
        CursorLoader cursorLoader = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = null;
        String selectionArgs[] = null;
        try {
            cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);

        } catch (Exception e) {
            MyLog.e("GroupsTable", "getAllGroupNames: Exception: " + e.getMessage());
        }
        return cursorLoader;
    }


    public static ArrayList<clsGroup> getAllGroupsArray(Context context) {
        ArrayList<clsGroup> groups = new ArrayList<>();
        Cursor cursor = getAllGroupsCursor(context, SORT_ORDER_GROUP_NAME);
        if (cursor != null && cursor.getCount() > 0) {
            long groupID;
            String groupName;
            boolean isChecked;
            clsGroup group;
            while (cursor.moveToNext()) {
                groupID = cursor.getLong(cursor.getColumnIndex(COL_GROUP_ID));
                groupName = cursor.getString(cursor.getColumnIndex(COL_GROUP_NAME));
                isChecked = cursor.getInt(cursor.getColumnIndex(COL_CHECKED)) > 0;
                group = new clsGroup(groupID, groupName, isChecked);
                groups.add(group);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        return groups;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Update Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int updateGroupFieldValues(Context context, long groupID, ContentValues newFieldValues) {
        int numberOfUpdatedRecords = -1;
        if (groupID > 0) {
            ContentResolver cr = context.getContentResolver();
            Uri GroupUri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(groupID));
            String selection = null;
            String[] selectionArgs = null;
            numberOfUpdatedRecords = cr.update(GroupUri, newFieldValues, selection, selectionArgs);
        } else {
            MyLog.e("GroupTable", "updateGroupFieldValues: Invalid groupID.");
        }
        return numberOfUpdatedRecords;
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Delete Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int deleteGroup(Context context, long groupID) {
        int numberOfDeletedRecords = 0;
        // don't delete the default group
        if (groupID > 1) {
            ContentResolver cr = context.getContentResolver();
            Uri uri = CONTENT_URI;
            String where = COL_GROUP_ID + " = ?";
            String[] selectionArgs = {String.valueOf(groupID)};
            numberOfDeletedRecords = cr.delete(uri, where, selectionArgs);
            StoreMapsTable.resetGroupID(context, groupID);
            ItemsTable.resetAllItemsWithGroupID(context, groupID);
        }
        return numberOfDeletedRecords;
    }

    public static int deleteCheckedGroups(Context context) {
        int numberOfDeletedRecords = -1;

        Uri uri = CONTENT_URI;
        String where = COL_CHECKED + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(1)};
        ContentResolver cr = context.getContentResolver();
        numberOfDeletedRecords = cr.delete(uri, where, selectionArgs);

        return numberOfDeletedRecords;
    }


}
