package com.lbconsulting.agrocerylist.database;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lbconsulting.agrocerylist.classes.MyLog;


/**
 * SQLite table to hold user data
 * Created by Loren on 5/13/2015.
 */
public class ProductsTable {


    // Users data table
    // Version 1
    public static final String TABLE_PRODUCTS = "tblProducts";
    public static final String COL_PRODUCT_ID = "_id";
    public static final String COL_ITEM_ID = "itemID";
    public static final String COL_BAR_CODE_FORMAT = "barCodeFormat";
    public static final String COL_BAR_CODE_NUMBER = "barCodeNumber"; // UPC_A, EAN, or ISBN codes
    public static final String COL_PRODUCT_TITLE = "productTitle";
    public static final String COL_TIME_STAMP = "timeStamp";

    public static final String UPC_A = "UPC_A";
    public static final String UPC_E = "UPC_E";
    public static final String ISBN = "ISBN";

    public static final String[] PROJECTION_ALL = {COL_PRODUCT_ID, COL_ITEM_ID, COL_BAR_CODE_FORMAT,
            COL_BAR_CODE_NUMBER, COL_PRODUCT_TITLE, COL_TIME_STAMP};

    public static final String CONTENT_PATH = TABLE_PRODUCTS;

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + "vnd.lbconsulting."
            + TABLE_PRODUCTS;
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + "vnd.lbconsulting."
            + TABLE_PRODUCTS;
    public static final Uri CONTENT_URI = Uri.parse("content://" + aGroceryListContentProvider.AUTHORITY + "/" + CONTENT_PATH);

    public static final String SORT_ORDER_BAR_CODE_NUMBER = COL_BAR_CODE_NUMBER + " ASC ";
    public static final String SORT_ORDER_PRODUCT_TITLE = COL_PRODUCT_TITLE + " ASC ";
    public static final String SORT_ORDER_TIME_STAMP = COL_TIME_STAMP + " ASC ";

    private static final int ITEM_ID_DEFAULT = -1;

    // Database creation SQL statements
    private static final String CREATE_DATA_TABLE = "create table "
            + TABLE_PRODUCTS
            + " ("
            + COL_PRODUCT_ID + " integer primary key autoincrement, "
            + COL_ITEM_ID + " integer DEFAULT " + ITEM_ID_DEFAULT + ", "
            + COL_BAR_CODE_FORMAT + " text  DEFAULT '', "
            + COL_BAR_CODE_NUMBER + " text  DEFAULT '', "
            + COL_PRODUCT_TITLE + " text  DEFAULT '', "
            + COL_TIME_STAMP + " integer DEFAULT 0 "
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_DATA_TABLE);
        MyLog.i("ProductsTable", "onCreate: " + TABLE_PRODUCTS + " created.");
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        // This wipes out all of the user data and create an empty table
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCTS);
        onCreate(database);
    }

    // /////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Create Methods
    // /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static long createNewProduct(Context context, String barCodeNumber, String barCodeFormat, long timeStamp) {

        long newUserID = -1;
        if (productExists(context, barCodeNumber)) {
            newUserID = getProductID(context, barCodeNumber);
            return newUserID;
        }
        try {
            ContentResolver cr = context.getContentResolver();
            Uri uri = CONTENT_URI;
            ContentValues values = new ContentValues();
            values.put(COL_BAR_CODE_NUMBER, barCodeNumber);
            values.put(COL_BAR_CODE_FORMAT, barCodeFormat);
            values.put(COL_TIME_STAMP, timeStamp);
            Uri newUserUri = cr.insert(uri, values);
            if (newUserUri != null) {
                newUserID = Long.parseLong(newUserUri.getLastPathSegment());
            }
        } catch (Exception e) {
            MyLog.e("ProductsTable", "createNewProduct: Exception" + e.getMessage());
        }
        return newUserID;
    }

    private static boolean productExists(Context context, String barCodeNumber) {
        boolean results = false;
        Cursor cursor = getProductCursor(context, barCodeNumber, SORT_ORDER_TIME_STAMP);
        if (cursor != null && cursor.getCount() > 0) {
            results = true;
            cursor.close();
        }
        return results;
    }

    private static long getProductID(Context context, String barCodeNumber) {
        long results = -1;
        Cursor cursor = getProductCursor(context, barCodeNumber, SORT_ORDER_TIME_STAMP);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            results = cursor.getLong(cursor.getColumnIndex(COL_PRODUCT_ID));
            cursor.close();
        }
        return results;
    }


// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Read Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////


    public static Cursor getProductCursor(Context context, long productID) {
        Cursor cursor = null;

        Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(productID));
        String[] projection = PROJECTION_ALL;
        String selection = null;
        String selectionArgs[] = null;
        String sortOrder = null;
        ContentResolver cr = context.getContentResolver();
        try {
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("ProductsTable", "getProductCursor: Exception; " + e.getMessage());
        }

        return cursor;
    }

    public static Cursor getProductCursor(Context context, String barCodeNumber, String sortOrder) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = COL_BAR_CODE_NUMBER + " = ?";
        String[] selectionArgs = new String[]{barCodeNumber.trim()};
        try {
            ContentResolver cr = context.getContentResolver();
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("ProductsTable", "getProductCursor: Exception; " + e.getMessage());
        }
        return cursor;
    }

    public static Cursor getAllProductsCursor(Context context, String sortOrder) {
        Cursor cursor = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = null;
        String selectionArgs[] = null;
        try {
            ContentResolver cr = context.getContentResolver();
            cursor = cr.query(uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("ProductsTable", "getAllProductsCursor: Exception; " + e.getMessage());
        }
        return cursor;
    }


    public static CursorLoader getAllProductsCursorLoader(Context context, String sortOrder) {
        CursorLoader cursorLoader = null;
        Uri uri = CONTENT_URI;
        String[] projection = PROJECTION_ALL;
        String selection = null;
        String selectionArgs[] = null;
        try {
            cursorLoader = new CursorLoader(context, uri, projection, selection, selectionArgs, sortOrder);
        } catch (Exception e) {
            MyLog.e("ProductsTable", "getAllProductsCursorLoader: Exception; " + e.getMessage());
        }
        return cursorLoader;
    }


// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Update Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int updateProductFields(Context context, long productID, ContentValues newFieldValues) {

        // Update the product fields
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(CONTENT_URI, String.valueOf(productID));
        String selection = null;
        String[] selectionArgs = null;
        return cr.update(uri, newFieldValues, selection, selectionArgs);
    }

    public static int updateProductFields(Context context, String barCodeNumber, ContentValues newFieldValues) {

        // Update the product fields
        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String selection = COL_BAR_CODE_NUMBER + " = ?";
        String[] selectionArgs = new String[]{barCodeNumber.trim()};

        int numberOfRecordsUpdated = cr.update(uri, newFieldValues, selection, selectionArgs);
        return numberOfRecordsUpdated;
    }

    public static void setItemID(Context context, long productID, long itemID) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ITEM_ID, itemID);
        updateProductFields(context, productID, cv);
    }

// /////////////////////////////////////////////////////////////////////////////////////////////////////////
// Delete Methods
// /////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int deleteAllProducts(Context context) {
        int numberOfDeletedRecords = 0;
        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String selection = null;
        String[] selectionArgs = null;
        numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);

        return numberOfDeletedRecords;
    }

    public static int deleteAllUnlinkedProducts(Context context) {
        int numberOfDeletedRecords = 0;
        ContentResolver cr = context.getContentResolver();
        Uri uri = CONTENT_URI;
        String selection = COL_ITEM_ID;
        String[] selectionArgs = new String[]{String.valueOf(ITEM_ID_DEFAULT)};
        numberOfDeletedRecords = cr.delete(uri, selection, selectionArgs);

        return numberOfDeletedRecords;
    }

}
