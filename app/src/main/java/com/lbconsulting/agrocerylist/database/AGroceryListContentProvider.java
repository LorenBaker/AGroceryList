package com.lbconsulting.agrocerylist.database;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;


import com.lbconsulting.agrocerylist.classes.MyLog;

import java.util.Arrays;
import java.util.HashSet;

/**
 * This class provides access to Password SQLite tables
 * Created by Loren on 5/13/2015.
 */
public class AGroceryListContentProvider extends ContentProvider {

    // AGroceryList database
    private aGroceryListDatabaseHelper database = null;
    private static boolean mSuppressChangeNotification = false;

    public static void setSuppressChangeNotification(boolean suppressChanges) {
        mSuppressChangeNotification = suppressChanges;
    }

    public static final String AUTHORITY = "com.lbconsulting.aGroceryList.contentProvider";

    //region UriMatcher switch constants
    private static final int PRODUCTS_MULTI_ROWS = 10;
    private static final int PRODUCTS_SINGLE_ROW = 11;



    private static final int ITEM_LOCATIONS_MULTI_ROWS = 1000;
    private static final int ITEM_LOCATIONS_SINGLE_ROW = 1001;
    //endregion

    //region UriMatcher
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sURIMatcher.addURI(AUTHORITY, ProductsTable.CONTENT_PATH, PRODUCTS_MULTI_ROWS);
        sURIMatcher.addURI(AUTHORITY, ProductsTable.CONTENT_PATH + "/#", PRODUCTS_SINGLE_ROW);


        sURIMatcher.addURI(AUTHORITY, StoreItemLocationsTable.CONTENT_PATH, ITEM_LOCATIONS_MULTI_ROWS);
        sURIMatcher.addURI(AUTHORITY, StoreItemLocationsTable.CONTENT_PATH + "/#", ITEM_LOCATIONS_SINGLE_ROW);

    }
    //endregion

    @Override
    public boolean onCreate() {
        MyLog.i("AGroceryListContentProvider", "onCreate");
        // Construct the underlying database
        // Defer opening the database until you need to perform
        // a query or other transaction.
        database = new aGroceryListDatabaseHelper(getContext());
        return true;
    }
    /*A content provider is created when its hosting process is created,
     * and remains around for as long as the process does, so there is
	 * no need to close the database -- it will get closed as part of the
	 * kernel cleaning up the process's resources when the process is killed.
	 */

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Using SQLiteQueryBuilder
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {

            case PRODUCTS_MULTI_ROWS:
                queryBuilder.setTables(ProductsTable.TABLE_PRODUCTS);
                checkProductColumnNames(projection);
                break;

            case PRODUCTS_SINGLE_ROW:
                queryBuilder.setTables(ProductsTable.TABLE_PRODUCTS);
                checkProductColumnNames(projection);
                queryBuilder.appendWhere(ProductsTable.COL_PRODUCT_ID + "=" + uri.getLastPathSegment());
                break;


            case ITEM_LOCATIONS_MULTI_ROWS:
                queryBuilder.setTables(StoreItemLocationsTable.TABLE_STORE_ITEM_LOCATIONS);
                checkItemLocationsColumnNames(projection);
                break;

            case ITEM_LOCATIONS_SINGLE_ROW:
                queryBuilder.setTables(StoreItemLocationsTable.TABLE_STORE_ITEM_LOCATIONS);
                checkItemLocationsColumnNames(projection);
                queryBuilder.appendWhere(StoreItemLocationsTable.COL_STORE_ITEM_LOCATION_ID + "=" + uri.getLastPathSegment());
                break;

            default:
                throw new IllegalArgumentException("Method query. Unknown URI: " + uri);
        }

        // Execute the query on the database
        SQLiteDatabase db;
        try {
            db = database.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = database.getReadableDatabase();
        }

        if (db != null) {
            String groupBy = null;
            String having = null;
            Cursor cursor = null;
            try {
                cursor = queryBuilder.query(db, projection, selection, selectionArgs, groupBy, having, sortOrder);
            } catch (Exception e) {
                MyLog.e("AGroceryListContentProvider", "query: " + e.toString());
                e.printStackTrace();
            }

            if (cursor != null) {
                cursor.setNotificationUri(getContext().getContentResolver(), uri);
            }
            return cursor;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        SQLiteDatabase db;
        long newRowId;
        String nullColumnHack = null;

        // Open a WritableDatabase database to support the insert transaction
        db = database.getWritableDatabase();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case PRODUCTS_MULTI_ROWS:
                newRowId = db.insertOrThrow(ProductsTable.TABLE_PRODUCTS, nullColumnHack, values);
                if (newRowId > 0) {
                    // Construct and return the URI of the newly inserted row.
                    Uri newRowUri = ContentUris.withAppendedId(ProductsTable.CONTENT_URI, newRowId);

                    if (!mSuppressChangeNotification) {
                        // Notify and observers of the change in the database.
                        getContext().getContentResolver().notifyChange(ProductsTable.CONTENT_URI, null);
                    }
                    return newRowUri;
                }

            case PRODUCTS_SINGLE_ROW:
                throw new IllegalArgumentException(
                        "Cannot insert a new row with a single row URI. Illegal URI: " + uri);


            case ITEM_LOCATIONS_MULTI_ROWS:
                newRowId = db.insertOrThrow(StoreItemLocationsTable.TABLE_STORE_ITEM_LOCATIONS, nullColumnHack, values);
                if (newRowId > 0) {
                    Uri newRowUri = ContentUris.withAppendedId(StoreItemLocationsTable.CONTENT_URI, newRowId);
                    if (!mSuppressChangeNotification) {
                        getContext().getContentResolver().notifyChange(StoreItemLocationsTable.CONTENT_URI, null);
                    }
                    return newRowUri;
                }

            case ITEM_LOCATIONS_SINGLE_ROW:
                throw new IllegalArgumentException(
                        "Cannot insert a new row with a single row URI. Illegal URI: " + uri);

            default:
                throw new IllegalArgumentException("Method insert: Unknown URI: " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        String rowID;
        int deleteCount;

        // Open a WritableDatabase database to support the delete transaction
        SQLiteDatabase db = database.getWritableDatabase();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case PRODUCTS_MULTI_ROWS:
                // To return the number of deleted items you must specify a where clause.
                // To delete all rows and return a value pass in "1".
                if (selection == null) {
                    selection = "1";
                }
                // Perform the deletion
                deleteCount = db.delete(ProductsTable.TABLE_PRODUCTS, selection, selectionArgs);
                break;

            case PRODUCTS_SINGLE_ROW:
                // Limit deletion to a single row
                rowID = uri.getLastPathSegment();
                selection = ProductsTable.COL_PRODUCT_ID + "=" + rowID
                        + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                // Perform the deletion
                deleteCount = db.delete(ProductsTable.TABLE_PRODUCTS, selection, selectionArgs);
                break;

            case ITEM_LOCATIONS_MULTI_ROWS:
                // To return the number of deleted items you must specify a where clause.
                // To delete all rows and return a value pass in "1".
                if (selection == null) {
                    selection = "1";
                }
                // Perform the deletion
                deleteCount = db.delete(StoreItemLocationsTable.TABLE_STORE_ITEM_LOCATIONS, selection, selectionArgs);
                break;

            case ITEM_LOCATIONS_SINGLE_ROW:
                // Limit deletion to a single row
                rowID = uri.getLastPathSegment();
                selection = ProductsTable.COL_PRODUCT_ID + "=" + rowID
                        + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                // Perform the deletion
                deleteCount = db.delete(StoreItemLocationsTable.TABLE_STORE_ITEM_LOCATIONS, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Method delete: Unknown URI: " + uri);

        }

        if (!mSuppressChangeNotification) {
            // Notify and observers of the change in the database.
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleteCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        String rowID;
        int updateCount;

        // Open a WritableDatabase database to support the update transaction
        SQLiteDatabase db = database.getWritableDatabase();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case PRODUCTS_MULTI_ROWS:
                updateCount = db.update(ProductsTable.TABLE_PRODUCTS, values, selection, selectionArgs);
                break;

            case PRODUCTS_SINGLE_ROW:
                // Limit update to a single row
                rowID = uri.getLastPathSegment();
                if (selection == null) {
                    selection = ProductsTable.COL_PRODUCT_ID + "=" + rowID;
                } else {
                    selection = ProductsTable.COL_PRODUCT_ID + "=" + rowID
                            + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                }
                // Perform the update
                updateCount = db.update(ProductsTable.TABLE_PRODUCTS, values, selection, selectionArgs);
                break;


            case ITEM_LOCATIONS_MULTI_ROWS:
                updateCount = db.update(StoreItemLocationsTable.TABLE_STORE_ITEM_LOCATIONS, values, selection, selectionArgs);
                break;

            case ITEM_LOCATIONS_SINGLE_ROW:
                // Limit update to a single row
                rowID = uri.getLastPathSegment();
                if (selection == null) {
                    selection = StoreItemLocationsTable.COL_STORE_ITEM_LOCATION_ID + "=" + rowID;
                } else {
                    selection = StoreItemLocationsTable.COL_STORE_ITEM_LOCATION_ID + "=" + rowID
                            + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                }
                // Perform the update
                updateCount = db.update(StoreItemLocationsTable.TABLE_STORE_ITEM_LOCATIONS, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Method update: Unknown URI: " + uri);


        }

        if (!mSuppressChangeNotification) {
            // Notify any observers of the change in the database.
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return updateCount;
    }

    public String getType(Uri uri) {
        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case PRODUCTS_MULTI_ROWS:
                return ProductsTable.CONTENT_TYPE;
            case PRODUCTS_SINGLE_ROW:
                return ProductsTable.CONTENT_ITEM_TYPE;

            case ITEM_LOCATIONS_MULTI_ROWS:
                return StoreItemLocationsTable.CONTENT_TYPE;
            case ITEM_LOCATIONS_SINGLE_ROW:
                return StoreItemLocationsTable.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Method getType. Unknown URI: " + uri);
        }
    }

    private void checkProductColumnNames(String[] projection) {
        // Check if the caller has requested a column that does not exist
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(ProductsTable.PROJECTION_ALL));

            // Check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException();
            }
        }
    }

    private void checkItemLocationsColumnNames(String[] projection) {
        // Check if the caller has requested a column that does not exist
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<>(Arrays.asList(StoreItemLocationsTable.PROJECTION_ALL));

            // Check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException();
            }
        }
    }
//
}
