package com.lbconsulting.agrocerylist.database;


/**
 * This class is a SQLite database table
 * that holds Parse update information on the following 4 tables:
 * GroupsTable, LocationsTable, StoreChainsTable, StoresTable.
 * NOTE: Parse update information for each Item is held in its respective ItemsTable row,
 *       and ParseStoreMap information is held in each respective StoreTable row.
 */

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lbconsulting.agrocerylist.classes.MyLog;


public class ParseObjectsTable {

    public static final String TABLE_PARSE_OBJECTS = "tblParseObjects";
    public static final String COL_PARSE_OBJECT_LOCAL_ID = "_id";
    public static final String COL_PARSE_OBJECT_NAME = "parseObjectName";
    public static final String COL_PARSE_OBJECT_ID = "parseObjectID";
    public static final String COL_PARSE_OBJECT_TIMESTAMP = "parseObjectTimestamp";
    public static final String COL_PARSE_OBJECT_IS_DIRTY = "parseObjectIsDirty";

    public static final String[] PROJECTION_ALL = {COL_PARSE_OBJECT_LOCAL_ID, COL_PARSE_OBJECT_NAME,
            COL_PARSE_OBJECT_ID, COL_PARSE_OBJECT_TIMESTAMP, COL_PARSE_OBJECT_IS_DIRTY};

    public static final String CONTENT_PATH = TABLE_PARSE_OBJECTS;


    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting."
            + TABLE_PARSE_OBJECTS;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting."
            + TABLE_PARSE_OBJECTS;
    public static final Uri CONTENT_URI = Uri.parse("content://" + aGroceryListContentProvider.AUTHORITY + "/" + CONTENT_PATH);

    public static final String SORT_ORDER_TABLE_NAME = COL_PARSE_OBJECT_NAME + " ASC";

    private static long mExistingObjectLocalID = -1;

    // Database creation SQL statements
    private static final String CREATE_TABLE = "create table "
            + TABLE_PARSE_OBJECTS
            + " ("
            + COL_PARSE_OBJECT_LOCAL_ID + " integer primary key, "
            + COL_PARSE_OBJECT_NAME + " text collate nocase default '', "
            + COL_PARSE_OBJECT_ID + " text default '', "
            + COL_PARSE_OBJECT_TIMESTAMP + " integer default 0, "
            + COL_PARSE_OBJECT_IS_DIRTY + " integer default 0 "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE);
        MyLog.i("ParseObjectsTable", "onCreate: " + TABLE_PARSE_OBJECTS + " created.");
    }


    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        MyLog.i(TABLE_PARSE_OBJECTS, "Upgrading database from version " + oldVersion + " to version " + newVersion);
        // This wipes out all of the user data and create an empty table
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PARSE_OBJECTS);
        onCreate(database);
    }


    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Create Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static long createParseObjectItem(Context context, String parseObjectName) {
        long newID = -1;
        parseObjectName = parseObjectName.trim();
        if (!parseObjectName.isEmpty()) {
            if (parseObjectItemExists(context, parseObjectName)) {
                // the parse update table exists in the table ... so return its id
                newID = mExistingObjectLocalID;
            } else {
                // the parse update object does not exist in the table ... so add it
                try {
                    ContentResolver cr = context.getContentResolver();
                    Uri uri = CONTENT_URI;
                    ContentValues values = new ContentValues();
                    values.put(COL_PARSE_OBJECT_NAME, parseObjectName);
                    Uri newGroupUri = cr.insert(uri, values);
                    if (newGroupUri != null) {
                        newID = Long.parseLong(newGroupUri.getLastPathSegment());
                    }
                } catch (Exception e) {
                    MyLog.e("ParseObjectsTable", "createParseObjectItem: Exception: " + e.getMessage());
                }
            }
        }
        return newID;
    }


    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Read Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static Cursor getParseObjectItemCursor(Context context, long objectLocalID) {
        Cursor cursor = null;
        if (objectLocalID > 0) {
            Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(objectLocalID));
            String[] projection = PROJECTION_ALL;
            String selection = null;
            String selectionArgs[] = null;
            String sortOrder = null;
            ContentResolver cr = context.getContentResolver();
            try {
                cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
            } catch (Exception e) {
                MyLog.e("ParseObjectsTable", "getParseObjectItemCursor: Exception: " + e.getMessage());
            }
        } else {
            MyLog.e("ParseObjectsTable", "getParseObjectItemCursor: Invalid objectLocalID");
        }
        return cursor;
    }

    private static Cursor getParseObjectItemCursor(Context context, String parseObjectName) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = COL_PARSE_OBJECT_NAME + " = ?";
        String selectionArgs[] = new String[]{parseObjectName};
        String sortOrder = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("ParseObjectsTable", "getParseObjectItemCursor: Exception: " + e.getMessage());
        }
        return cursor;
    }

    public static Cursor getAllParseObjectItemsCursor(Context context, String sortOrder) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = null;
        String selectionArgs[] = null;
        if (sortOrder == null) {
            sortOrder = SORT_ORDER_TABLE_NAME;
        }
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("ParseObjectsTable", "getAllParseObjectItemsCursor: Exception: " + e.getMessage());
        }
        return cursor;
    }

    private static boolean parseObjectItemExists(Context context, String parseObjectName) {
        mExistingObjectLocalID = -1;
        boolean result = false;
        Cursor cursor = getParseObjectItemCursor(context, parseObjectName);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                mExistingObjectLocalID = cursor.getLong(cursor.getColumnIndex(COL_PARSE_OBJECT_LOCAL_ID));
                result = true;
            }
            cursor.close();
        }
        return result;
    }


    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Update Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static int updateParseObjectFieldValues(Context context, long parseObjectLocalID, ContentValues newFieldValues) {
        int numberOfUpdatedRecords = -1;
        if (parseObjectLocalID > 0) {
            ContentResolver cr = context.getContentResolver();
            Uri GroupUri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(parseObjectLocalID));
            String selection = null;
            String[] selectionArgs = null;
            numberOfUpdatedRecords = cr.update(GroupUri, newFieldValues, selection, selectionArgs);
        } else {
            MyLog.e("GroupTable", "updateParseObjectFieldValues: Invalid parseObjectLocalID.");
        }
        return numberOfUpdatedRecords;
    }

    public static long getParseObjectLocalID(Context context, String parseObjectName) {
        long parseObjectLocalID = -1;
        Cursor cursor = getParseObjectItemCursor(context, parseObjectName);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            parseObjectLocalID = cursor.getLong(cursor.getColumnIndex(COL_PARSE_OBJECT_LOCAL_ID));
        }
        if (cursor != null) {
            cursor.close();
        }
        return parseObjectLocalID;
    }

    public static void setTimestamp(Context context, long parseObjectLocalID, long timestamp) {
        ContentValues cv = new ContentValues();
        cv.put(COL_PARSE_OBJECT_TIMESTAMP, timestamp);
        updateParseObjectFieldValues(context, parseObjectLocalID, cv);
    }

    public static void setIsDirty(Context context, long parseObjectLocalID, boolean isDirty) {
        ContentValues cv = new ContentValues();
        int isDirtyValue = 0;
        if (isDirty) {
            isDirtyValue = 1;
        }
        cv.put(COL_PARSE_OBJECT_IS_DIRTY, isDirtyValue);
        updateParseObjectFieldValues(context, parseObjectLocalID, cv);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Delete Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int deleteParseObject(Context context, long parseObjectLocalID) {
        int numberOfDeletedRecords = 0;
        if (parseObjectLocalID > 0) {
            ContentResolver cr = context.getContentResolver();
            Uri uri = CONTENT_URI;
            String selection = COL_PARSE_OBJECT_LOCAL_ID + " = ?";
            String[] selectionArgs = {String.valueOf(parseObjectLocalID)};
            numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);
        }
        return numberOfDeletedRecords;
    }


}
