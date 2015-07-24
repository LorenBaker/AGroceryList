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
import com.parse.ParseObject;

import java.util.ArrayList;

public class GroupsTable {

    // Version 1
    public static final String TABLE_GROUPS = "tblGroups";
    public static final String COL_ID = "_id";
    // Parse fields
    public static final String COL_GROUP_ID = "groupID";
    public static final String COL_GROUP_NAME = "groupName";
    public static final String COL_SORT_KEY = "sortKey";
    // SQLite only fields
    public static final String COL_DIRTY = "dirty";
    public static final String COL_CHECKED = "checked";

    public static final String[] PROJECTION_ALL = {COL_ID, COL_GROUP_ID, COL_GROUP_NAME,
            COL_SORT_KEY, COL_DIRTY, COL_CHECKED};

    public static final String CONTENT_PATH = TABLE_GROUPS;


    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting."
            + TABLE_GROUPS;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting."
            + TABLE_GROUPS;
    public static final Uri CONTENT_URI = Uri.parse("content://" + aGroceryListContentProvider.AUTHORITY + "/" + CONTENT_PATH);

    public static final String SORT_ORDER_GROUP_NAME = COL_GROUP_NAME + " ASC";
    public static final String SORT_ORDER_SORT_KEY = COL_SORT_KEY + " ASC";

    private static long mExistingGroupID = -1;

    // Database creation SQL statements
    private static final String CREATE_TABLE = "create table "
            + TABLE_GROUPS
            + " ("
            + COL_ID + " integer primary key, "
            + COL_GROUP_ID + " text default '', "
            + COL_GROUP_NAME + " text collate nocase default '', "
            + COL_SORT_KEY + " integer default 0, "
            + COL_DIRTY + " integer default 0, "
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


    public static void resetTable(SQLiteDatabase database) {
        MyLog.i(TABLE_GROUPS, "Resetting table");
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

    public static void createNewGroup(Context context, ParseObject group) {
        String groupID = group.getObjectId();
        String groupName = group.getString(COL_GROUP_NAME);
        long sortKey = group.getLong(COL_SORT_KEY);
        if (!groupName.isEmpty() && !groupID.isEmpty()) {

            try {
                ContentResolver cr = context.getContentResolver();
                Uri uri = CONTENT_URI;
                ContentValues values = new ContentValues();
                values.put(COL_GROUP_ID, groupID);
                values.put(COL_GROUP_NAME, groupName);
                values.put(COL_SORT_KEY, sortKey);
                cr.insert(uri, values);

            } catch (Exception e) {
                MyLog.e("GroupsTable", "createNewGroup: Exception: " + e.getMessage());
            }
        }else{
            MyLog.e("GroupsTable", "createNewGroup: Either groupName or groupID is empty.");
        }
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Read Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Cursor getGroupCursor(Context context, long ID) {
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
                MyLog.e("GroupsTable", "getGroupCursor: Exception: " + e.getMessage());
            }
        } else {
            MyLog.e("GroupsTable", "getGroupCursor: Invalid groupID");
        }
        return cursor;
    }

    private static Cursor getGroupCursor(Context context, String groupFieldValue, boolean isParseObjectID) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection;

        if(isParseObjectID){
             selection = COL_GROUP_ID + " = ?";
        }else{
            selection = COL_GROUP_NAME + " = ?";
        }
        String selectionArgs[]  = new String[]{groupFieldValue};
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
        Cursor cursor = getGroupCursor(context, groupName,false);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                mExistingGroupID = cursor.getLong(cursor.getColumnIndex(COL_ID));
                result = true;
            }
            cursor.close();
        }
        return result;
    }

    public static boolean groupExists(Context context, long groupID) {
        boolean result = false;
        Cursor cursor = getGroupCursor(context, groupID);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                result = true;
            }
            cursor.close();
        }
        return result;
    }

    public static boolean isGroupDirty(Context context, long groupID) {
        boolean result = false;
        Cursor cursor = getGroupCursor(context, groupID);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                int groupDirty = cursor.getInt(cursor.getColumnIndex(COL_DIRTY));
                result = groupDirty > 0;
            }
            cursor.close();
        }
        return result;
    }

    public static Cursor getAllDirtyGroupsCursor(Context context) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = COL_DIRTY + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(1)};
        String sortOrder = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("ItemsTable", "getAllDirtyGroupsCursor: Exception: " + e.getMessage());
        }
        return cursor;
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
                groupID = cursor.getLong(cursor.getColumnIndex(COL_ID));
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

    public static void updateGroup(Context context, ParseObject group) {
        long groupID = group.getLong("groupID");
        String groupName = group.getString(COL_GROUP_NAME);
        if (!groupName.isEmpty() && groupID > 0) {
            ContentValues cv = new ContentValues();
            cv.put(COL_GROUP_NAME, groupName);
            updateGroupFieldValues(context, groupID, cv);
        }
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
            String where = COL_ID + " = ?";
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
        String selection = COL_CHECKED + " = ?";
        String selectionArgs[] = new String[]{String.valueOf(1)};
        ContentResolver cr = context.getContentResolver();
        numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);

        return numberOfDeletedRecords;
    }


    public static int clear(Context context) {
        // deletes all group records
        int numberOfDeletedRecords = 0;

        Uri uri = CONTENT_URI;
        String selection = null;
        String selectionArgs[] = null;
        ContentResolver cr = context.getContentResolver();
        numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);

        return numberOfDeletedRecords;
    }


}
