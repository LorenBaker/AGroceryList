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
public class aGroceryListContentProvider extends ContentProvider {

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

    private static final int ITEMS_MULTI_ROWS = 20;
    private static final int ITEMS_SINGLE_ROW = 21;

    private static final int SELECTED_ITEMS_MULTI_ROWS = 30;
    private static final int SELECTED_ITEMS_SINGLE_ROW = 31;

    private static final int STORE_CHAINS_MULTI_ROWS = 40;
    private static final int STORE_CHAINS_SINGLE_ROW = 41;

    private static final int STORES_MULTI_ROWS = 50;
    private static final int STORES_SINGLE_ROW = 51;


    private static final int ITEM_LOCATIONS_MULTI_ROWS = 1000;
    private static final int ITEM_LOCATIONS_SINGLE_ROW = 1001;


    private static final int STORES_WITH_CHAIN_NAMES = 2000;
    //endregion

    //region UriMatcher
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sURIMatcher.addURI(AUTHORITY, ProductsTable.CONTENT_PATH, PRODUCTS_MULTI_ROWS);
        sURIMatcher.addURI(AUTHORITY, ProductsTable.CONTENT_PATH + "/#", PRODUCTS_SINGLE_ROW);


        sURIMatcher.addURI(AUTHORITY, ItemsTable.CONTENT_PATH, ITEMS_MULTI_ROWS);
        sURIMatcher.addURI(AUTHORITY, ItemsTable.CONTENT_PATH + "/#", ITEMS_SINGLE_ROW);

        sURIMatcher.addURI(AUTHORITY, SelectedItemsTable.CONTENT_PATH, SELECTED_ITEMS_MULTI_ROWS);
        sURIMatcher.addURI(AUTHORITY, SelectedItemsTable.CONTENT_PATH + "/#", SELECTED_ITEMS_SINGLE_ROW);

        sURIMatcher.addURI(AUTHORITY, StoreChainsTable.CONTENT_PATH, STORE_CHAINS_MULTI_ROWS);
        sURIMatcher.addURI(AUTHORITY, StoreChainsTable.CONTENT_PATH + "/#", STORE_CHAINS_SINGLE_ROW);

        sURIMatcher.addURI(AUTHORITY, StoresTable.CONTENT_PATH, STORES_MULTI_ROWS);
        sURIMatcher.addURI(AUTHORITY, StoresTable.CONTENT_PATH + "/#", STORES_SINGLE_ROW);


        sURIMatcher.addURI(AUTHORITY, StoreItemLocationsTable.CONTENT_PATH, ITEM_LOCATIONS_MULTI_ROWS);
        sURIMatcher.addURI(AUTHORITY, StoreItemLocationsTable.CONTENT_PATH + "/#", ITEM_LOCATIONS_SINGLE_ROW);

        sURIMatcher.addURI(AUTHORITY, StoresTable.CONTENT_PATH_STORES_WITH_CHAIN_NAMES, STORES_WITH_CHAIN_NAMES);

    }
    //endregion

    @Override
    public boolean onCreate() {
        MyLog.i("aGroceryListContentProvider", "onCreate");
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
                checkColumnNames(projection, PRODUCTS_MULTI_ROWS);
                break;

            case PRODUCTS_SINGLE_ROW:
                queryBuilder.setTables(ProductsTable.TABLE_PRODUCTS);
                checkColumnNames(projection, PRODUCTS_SINGLE_ROW);
                queryBuilder.appendWhere(ProductsTable.COL_PRODUCT_ID + "=" + uri.getLastPathSegment());
                break;


            case ITEMS_MULTI_ROWS:
                queryBuilder.setTables(ItemsTable.TABLE_ITEMS);
                checkColumnNames(projection, ITEMS_MULTI_ROWS);
                break;

            case ITEMS_SINGLE_ROW:
                queryBuilder.setTables(ItemsTable.TABLE_ITEMS);
                checkColumnNames(projection, ITEMS_SINGLE_ROW);
                queryBuilder.appendWhere(ItemsTable.COL_ITEM_ID + "=" + uri.getLastPathSegment());
                break;


            case SELECTED_ITEMS_MULTI_ROWS:
                queryBuilder.setTables(SelectedItemsTable.TABLE_SELECTED_ITEMS);
                checkColumnNames(projection, SELECTED_ITEMS_MULTI_ROWS);
                break;

            case SELECTED_ITEMS_SINGLE_ROW:
                queryBuilder.setTables(SelectedItemsTable.TABLE_SELECTED_ITEMS);
                checkColumnNames(projection, SELECTED_ITEMS_SINGLE_ROW);
                queryBuilder.appendWhere(SelectedItemsTable.COL_SELECTED_ITEMS_ID + "=" + uri.getLastPathSegment());
                break;


            case STORE_CHAINS_MULTI_ROWS:
                queryBuilder.setTables(StoreChainsTable.TABLE_STORE_CHAINS);
                checkColumnNames(projection, STORE_CHAINS_MULTI_ROWS);
                break;

            case STORE_CHAINS_SINGLE_ROW:
                queryBuilder.setTables(StoreChainsTable.TABLE_STORE_CHAINS);
                checkColumnNames(projection, STORE_CHAINS_SINGLE_ROW);
                queryBuilder.appendWhere(StoreChainsTable.COL_STORE_CHAIN_ID + "=" + uri.getLastPathSegment());
                break;


            case STORES_MULTI_ROWS:
                queryBuilder.setTables(StoresTable.TABLE_STORES);
                checkColumnNames(projection, STORES_MULTI_ROWS);
                break;

            case STORES_SINGLE_ROW:
                queryBuilder.setTables(StoresTable.TABLE_STORES);
                checkColumnNames(projection, STORES_SINGLE_ROW);
                queryBuilder.appendWhere(StoresTable.COL_STORE_ID + "=" + uri.getLastPathSegment());
                break;


            case ITEM_LOCATIONS_MULTI_ROWS:
                queryBuilder.setTables(StoreItemLocationsTable.TABLE_STORE_ITEM_LOCATIONS);
                checkColumnNames(projection, ITEM_LOCATIONS_MULTI_ROWS);
                break;

            case ITEM_LOCATIONS_SINGLE_ROW:
                queryBuilder.setTables(StoreItemLocationsTable.TABLE_STORE_ITEM_LOCATIONS);
                checkColumnNames(projection, ITEM_LOCATIONS_SINGLE_ROW);
                queryBuilder.appendWhere(StoreItemLocationsTable.COL_STORE_ITEM_LOCATION_ID + "=" + uri.getLastPathSegment());
                break;

            case STORES_WITH_CHAIN_NAMES:
/*              SELECT tblStores._id, tblStores.storeRegionalName, tblStoreChains.storeChainName
                FROM tblStores
                JOIN tblStoreChains
                ON tblStores.storeChainID = tblStoreChains._id
                ORDER BY storeChainName ASC, storeRegionalName ASC  */

                String tables =  StoresTable.TABLE_STORES +
                        " JOIN " + StoreChainsTable.TABLE_STORE_CHAINS
                        + " ON "
                        + StoresTable.TABLE_STORES + "." + StoresTable.COL_STORE_CHAIN_ID + " = "
                        + StoreChainsTable.TABLE_STORE_CHAINS + "." + StoreChainsTable.COL_STORE_CHAIN_ID;
                queryBuilder.setTables(tables);
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
                MyLog.e("aGroceryListContentProvider", "query Exception: " + e.getMessage());
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
                        getContext().getContentResolver().notifyChange(StoresTable.CONTENT_URI_STORES_WITH_CHAIN_NAMES, null);
                    }
                    return newRowUri;
                }

            case PRODUCTS_SINGLE_ROW:
                throw new IllegalArgumentException(
                        "Cannot insert a new row with a single row URI. Illegal URI: " + uri);




            case ITEMS_MULTI_ROWS:
                newRowId = db.insertOrThrow(ItemsTable.TABLE_ITEMS, nullColumnHack, values);
                if (newRowId > 0) {
                    // Construct and return the URI of the newly inserted row.
                    Uri newRowUri = ContentUris.withAppendedId(ItemsTable.CONTENT_URI, newRowId);

                    if (!mSuppressChangeNotification) {
                        // Notify and observers of the change in the database.
                        getContext().getContentResolver().notifyChange(ItemsTable.CONTENT_URI, null);
                        getContext().getContentResolver().notifyChange(StoresTable.CONTENT_URI_STORES_WITH_CHAIN_NAMES, null);
                    }
                    return newRowUri;
                }

            case ITEMS_SINGLE_ROW:
                throw new IllegalArgumentException(
                        "Cannot insert a new row with a single row URI. Illegal URI: " + uri);


            case SELECTED_ITEMS_MULTI_ROWS:
                newRowId = db.insertOrThrow(SelectedItemsTable.TABLE_SELECTED_ITEMS, nullColumnHack, values);
                if (newRowId > 0) {
                    // Construct and return the URI of the newly inserted row.
                    Uri newRowUri = ContentUris.withAppendedId(SelectedItemsTable.CONTENT_URI, newRowId);

                    if (!mSuppressChangeNotification) {
                        // Notify and observers of the change in the database.
                        getContext().getContentResolver().notifyChange(SelectedItemsTable.CONTENT_URI, null);
                        getContext().getContentResolver().notifyChange(StoresTable.CONTENT_URI_STORES_WITH_CHAIN_NAMES, null);
                    }
                    return newRowUri;
                }

            case SELECTED_ITEMS_SINGLE_ROW:
                throw new IllegalArgumentException(
                        "Cannot insert a new row with a single row URI. Illegal URI: " + uri);


            case STORE_CHAINS_MULTI_ROWS:
                newRowId = db.insertOrThrow(StoreChainsTable.TABLE_STORE_CHAINS, nullColumnHack, values);
                if (newRowId > 0) {
                    // Construct and return the URI of the newly inserted row.
                    Uri newRowUri = ContentUris.withAppendedId(StoreChainsTable.CONTENT_URI, newRowId);

                    if (!mSuppressChangeNotification) {
                        // Notify and observers of the change in the database.
                        getContext().getContentResolver().notifyChange(StoreChainsTable.CONTENT_URI, null);
                        getContext().getContentResolver().notifyChange(StoresTable.CONTENT_URI_STORES_WITH_CHAIN_NAMES, null);
                    }
                    return newRowUri;
                }

            case STORE_CHAINS_SINGLE_ROW:
                throw new IllegalArgumentException(
                        "Cannot insert a new row with a single row URI. Illegal URI: " + uri);



            case STORES_MULTI_ROWS:
                newRowId = db.insertOrThrow(StoresTable.TABLE_STORES, nullColumnHack, values);
                if (newRowId > 0) {
                    // Construct and return the URI of the newly inserted row.
                    Uri newRowUri = ContentUris.withAppendedId(StoresTable.CONTENT_URI, newRowId);

                    if (!mSuppressChangeNotification) {
                        // Notify and observers of the change in the database.
                        getContext().getContentResolver().notifyChange(StoresTable.CONTENT_URI, null);
                        getContext().getContentResolver().notifyChange(StoresTable.CONTENT_URI_STORES_WITH_CHAIN_NAMES, null);
                    }
                    return newRowUri;
                }

            case STORES_SINGLE_ROW:
                throw new IllegalArgumentException(
                        "Cannot insert a new row with a single row URI. Illegal URI: " + uri);


            case ITEM_LOCATIONS_MULTI_ROWS:
                newRowId = db.insertOrThrow(StoreItemLocationsTable.TABLE_STORE_ITEM_LOCATIONS, nullColumnHack, values);
                if (newRowId > 0) {
                    Uri newRowUri = ContentUris.withAppendedId(StoreItemLocationsTable.CONTENT_URI, newRowId);
                    if (!mSuppressChangeNotification) {
                        getContext().getContentResolver().notifyChange(StoreItemLocationsTable.CONTENT_URI, null);
                        getContext().getContentResolver().notifyChange(StoresTable.CONTENT_URI_STORES_WITH_CHAIN_NAMES, null);
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


            case ITEMS_MULTI_ROWS:
                // To return the number of deleted items you must specify a where clause.
                // To delete all rows and return a value pass in "1".
                if (selection == null) {
                    selection = "1";
                }
                // Perform the deletion
                deleteCount = db.delete(ItemsTable.TABLE_ITEMS, selection, selectionArgs);
                break;

            case ITEMS_SINGLE_ROW:
                // Limit deletion to a single row
                rowID = uri.getLastPathSegment();
                selection = ItemsTable.COL_ITEM_ID + "=" + rowID
                        + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                // Perform the deletion
                deleteCount = db.delete(ItemsTable.TABLE_ITEMS, selection, selectionArgs);
                break;

            case SELECTED_ITEMS_MULTI_ROWS:
                // To return the number of deleted items you must specify a where clause.
                // To delete all rows and return a value pass in "1".
                if (selection == null) {
                    selection = "1";
                }
                // Perform the deletion
                deleteCount = db.delete(SelectedItemsTable.TABLE_SELECTED_ITEMS, selection, selectionArgs);
                break;

            case SELECTED_ITEMS_SINGLE_ROW:
                // Limit deletion to a single row
                rowID = uri.getLastPathSegment();
                selection = SelectedItemsTable.COL_SELECTED_ITEMS_ID + "=" + rowID
                        + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                // Perform the deletion
                deleteCount = db.delete(SelectedItemsTable.TABLE_SELECTED_ITEMS, selection, selectionArgs);
                break;

            case STORE_CHAINS_MULTI_ROWS:
                // To return the number of deleted items you must specify a where clause.
                // To delete all rows and return a value pass in "1".
                if (selection == null) {
                    selection = "1";
                }
                // Perform the deletion
                deleteCount = db.delete(StoreChainsTable.TABLE_STORE_CHAINS, selection, selectionArgs);
                break;

            case STORE_CHAINS_SINGLE_ROW:
                // Limit deletion to a single row
                rowID = uri.getLastPathSegment();
                selection = StoreChainsTable.COL_STORE_CHAIN_ID + "=" + rowID
                        + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                // Perform the deletion
                deleteCount = db.delete(StoreChainsTable.TABLE_STORE_CHAINS, selection, selectionArgs);
                break;

            case STORES_MULTI_ROWS:
                // To return the number of deleted items you must specify a where clause.
                // To delete all rows and return a value pass in "1".
                if (selection == null) {
                    selection = "1";
                }
                // Perform the deletion
                deleteCount = db.delete(StoresTable.TABLE_STORES, selection, selectionArgs);
                break;

            case STORES_SINGLE_ROW:
                // Limit deletion to a single row
                rowID = uri.getLastPathSegment();
                selection = StoresTable.COL_STORE_ID + "=" + rowID
                        + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                // Perform the deletion
                deleteCount = db.delete(StoresTable.TABLE_STORES, selection, selectionArgs);
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
            getContext().getContentResolver().notifyChange(StoresTable.CONTENT_URI_STORES_WITH_CHAIN_NAMES, null);
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




            case ITEMS_MULTI_ROWS:
                updateCount = db.update(ItemsTable.TABLE_ITEMS, values, selection, selectionArgs);
                break;

            case ITEMS_SINGLE_ROW:
                // Limit update to a single row
                rowID = uri.getLastPathSegment();
                if (selection == null) {
                    selection = ItemsTable.COL_ITEM_ID + "=" + rowID;
                } else {
                    selection = ItemsTable.COL_ITEM_ID + "=" + rowID
                            + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                }
                // Perform the update
                updateCount = db.update(ItemsTable.TABLE_ITEMS, values, selection, selectionArgs);
                break;

            case SELECTED_ITEMS_MULTI_ROWS:
                updateCount = db.update(SelectedItemsTable.TABLE_SELECTED_ITEMS, values, selection, selectionArgs);
                break;

            case SELECTED_ITEMS_SINGLE_ROW:
                // Limit update to a single row
                rowID = uri.getLastPathSegment();
                if (selection == null) {
                    selection = SelectedItemsTable.COL_SELECTED_ITEMS_ID + "=" + rowID;
                } else {
                    selection = SelectedItemsTable.COL_SELECTED_ITEMS_ID + "=" + rowID
                            + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                }
                // Perform the update
                updateCount = db.update(SelectedItemsTable.TABLE_SELECTED_ITEMS, values, selection, selectionArgs);
                break;

            case STORE_CHAINS_MULTI_ROWS:
                updateCount = db.update(StoreChainsTable.TABLE_STORE_CHAINS, values, selection, selectionArgs);
                break;

            case STORE_CHAINS_SINGLE_ROW:
                // Limit update to a single row
                rowID = uri.getLastPathSegment();
                if (selection == null) {
                    selection = StoreChainsTable.COL_STORE_CHAIN_ID + "=" + rowID;
                } else {
                    selection = StoreChainsTable.COL_STORE_CHAIN_ID + "=" + rowID
                            + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                }
                // Perform the update
                updateCount = db.update(StoreChainsTable.TABLE_STORE_CHAINS, values, selection, selectionArgs);
                break;

            case STORES_MULTI_ROWS:
                updateCount = db.update(StoresTable.TABLE_STORES, values, selection, selectionArgs);
                break;

            case STORES_SINGLE_ROW:
                // Limit update to a single row
                rowID = uri.getLastPathSegment();
                if (selection == null) {
                    selection = StoresTable.COL_STORE_ID + "=" + rowID;
                } else {
                    selection = StoresTable.COL_STORE_ID + "=" + rowID
                            + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                }
                // Perform the update
                updateCount = db.update(StoresTable.TABLE_STORES, values, selection, selectionArgs);
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
            getContext().getContentResolver().notifyChange(StoresTable.CONTENT_URI_STORES_WITH_CHAIN_NAMES, null);
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

            case ITEMS_MULTI_ROWS:
                return ItemsTable.CONTENT_TYPE;
            case ITEMS_SINGLE_ROW:
                return ItemsTable.CONTENT_ITEM_TYPE;

            case SELECTED_ITEMS_MULTI_ROWS:
                return SelectedItemsTable.CONTENT_TYPE;
            case SELECTED_ITEMS_SINGLE_ROW:
                return SelectedItemsTable.CONTENT_ITEM_TYPE;

            case STORE_CHAINS_MULTI_ROWS:
                return StoreChainsTable.CONTENT_TYPE;
            case STORE_CHAINS_SINGLE_ROW:
                return StoreChainsTable.CONTENT_ITEM_TYPE;

            case STORES_MULTI_ROWS:
                return StoresTable.CONTENT_TYPE;
            case STORES_SINGLE_ROW:
                return StoresTable.CONTENT_ITEM_TYPE;

            case ITEM_LOCATIONS_MULTI_ROWS:
                return StoreItemLocationsTable.CONTENT_TYPE;
            case ITEM_LOCATIONS_SINGLE_ROW:
                return StoreItemLocationsTable.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Method getType. Unknown URI: " + uri);
        }
    }


    private void checkColumnNames(String[] projection, int uriMatcher) {
        // Check if the caller has requested a column that does not exist
        HashSet<String> availableColumns = null;
        switch (uriMatcher) {
            case PRODUCTS_MULTI_ROWS:
            case PRODUCTS_SINGLE_ROW:
                availableColumns = new HashSet<>(Arrays.asList(ProductsTable.PROJECTION_ALL));
                break;

            case ITEMS_MULTI_ROWS:
            case ITEMS_SINGLE_ROW:
                availableColumns = new HashSet<>(Arrays.asList(ItemsTable.PROJECTION_ALL));
                break;

            case SELECTED_ITEMS_MULTI_ROWS:
            case SELECTED_ITEMS_SINGLE_ROW:
                availableColumns = new HashSet<>(Arrays.asList(SelectedItemsTable.PROJECTION_ALL));
                break;


            case STORE_CHAINS_MULTI_ROWS:
            case STORE_CHAINS_SINGLE_ROW:
                availableColumns = new HashSet<>(Arrays.asList(StoreChainsTable.PROJECTION_ALL));
                break;


            case STORES_MULTI_ROWS:
            case STORES_SINGLE_ROW:
                availableColumns = new HashSet<>(Arrays.asList(StoresTable.PROJECTION_ALL));
                break;


            case ITEM_LOCATIONS_MULTI_ROWS:
            case ITEM_LOCATIONS_SINGLE_ROW:
                availableColumns = new HashSet<>(Arrays.asList(StoreItemLocationsTable.PROJECTION_ALL));
                break;


        }
        if (projection != null && availableColumns != null) {
            HashSet<String> requestedColumns = new HashSet<>(Arrays.asList(projection));
            // Check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException();
            }
        }
    }




}
