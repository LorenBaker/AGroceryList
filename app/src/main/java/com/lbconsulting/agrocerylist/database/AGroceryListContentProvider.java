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

    private static final int STORE_CHAINS_MULTI_ROWS = 40;
    private static final int STORE_CHAINS_SINGLE_ROW = 41;

    private static final int STORES_MULTI_ROWS = 50;
    private static final int STORES_SINGLE_ROW = 51;

    private static final int GROUPS_MULTI_ROWS = 60;
    private static final int GROUPS_SINGLE_ROW = 61;

    private static final int LOCATIONS_MULTI_ROWS = 70;
    private static final int LOCATIONS_SINGLE_ROW = 71;

    private static final int LOCATIONS_BRIDGE_MULTI_ROWS = 80;
    private static final int LOCATIONS_BRIDGE_SINGLE_ROW = 81;


    private static final int STORES_WITH_CHAIN_NAMES = 2000;
    private static final int ITEMS_BY_GROUPS = 2001;
    private static final int ITEMS_BY_LOCATIONS_AND_GROUPS = 2002;

    //endregion

    //region UriMatcher
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sURIMatcher.addURI(AUTHORITY, ProductsTable.CONTENT_PATH, PRODUCTS_MULTI_ROWS);
        sURIMatcher.addURI(AUTHORITY, ProductsTable.CONTENT_PATH + "/#", PRODUCTS_SINGLE_ROW);


        sURIMatcher.addURI(AUTHORITY, ItemsTable.CONTENT_PATH, ITEMS_MULTI_ROWS);
        sURIMatcher.addURI(AUTHORITY, ItemsTable.CONTENT_PATH + "/#", ITEMS_SINGLE_ROW);

        sURIMatcher.addURI(AUTHORITY, StoreChainsTable.CONTENT_PATH, STORE_CHAINS_MULTI_ROWS);
        sURIMatcher.addURI(AUTHORITY, StoreChainsTable.CONTENT_PATH + "/#", STORE_CHAINS_SINGLE_ROW);

        sURIMatcher.addURI(AUTHORITY, StoresTable.CONTENT_PATH, STORES_MULTI_ROWS);
        sURIMatcher.addURI(AUTHORITY, StoresTable.CONTENT_PATH + "/#", STORES_SINGLE_ROW);

        sURIMatcher.addURI(AUTHORITY, GroupsTable.CONTENT_PATH, GROUPS_MULTI_ROWS);
        sURIMatcher.addURI(AUTHORITY, GroupsTable.CONTENT_PATH + "/#", GROUPS_SINGLE_ROW);

        sURIMatcher.addURI(AUTHORITY, LocationsTable.CONTENT_PATH, LOCATIONS_MULTI_ROWS);
        sURIMatcher.addURI(AUTHORITY, LocationsTable.CONTENT_PATH + "/#", LOCATIONS_SINGLE_ROW);

        sURIMatcher.addURI(AUTHORITY, StoreMapsTable.CONTENT_PATH, LOCATIONS_BRIDGE_MULTI_ROWS);
        sURIMatcher.addURI(AUTHORITY, StoreMapsTable.CONTENT_PATH + "/#", LOCATIONS_BRIDGE_SINGLE_ROW);

        sURIMatcher.addURI(AUTHORITY, JoinedTables.CONTENT_PATH_STORES_WITH_CHAIN_NAMES, STORES_WITH_CHAIN_NAMES);
        //sURIMatcher.addURI(AUTHORITY, JoinedTables.CONTENT_PATH_ITEMS_BY_GROUPS, ITEMS_BY_GROUPS);
        sURIMatcher.addURI(AUTHORITY, JoinedTables.CONTENT_PATH_ITEMS_BY_LOCATIONS_AND_GROUPS, ITEMS_BY_LOCATIONS_AND_GROUPS);

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
        String groupBy = null;

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

            case GROUPS_MULTI_ROWS:
                queryBuilder.setTables(GroupsTable.TABLE_GROUPS);
                checkColumnNames(projection, GROUPS_MULTI_ROWS);
                break;

            case GROUPS_SINGLE_ROW:
                queryBuilder.setTables(GroupsTable.TABLE_GROUPS);
                checkColumnNames(projection, GROUPS_SINGLE_ROW);
                queryBuilder.appendWhere(GroupsTable.COL_GROUP_ID + "=" + uri.getLastPathSegment());
                break;


            case LOCATIONS_MULTI_ROWS:
                queryBuilder.setTables(LocationsTable.TABLE_LOCATIONS);
                checkColumnNames(projection, LOCATIONS_MULTI_ROWS);
                break;

            case LOCATIONS_SINGLE_ROW:
                queryBuilder.setTables(LocationsTable.TABLE_LOCATIONS);
                checkColumnNames(projection, LOCATIONS_SINGLE_ROW);
                queryBuilder.appendWhere(LocationsTable.COL_LOCATION_ID + "=" + uri.getLastPathSegment());
                break;

            case LOCATIONS_BRIDGE_MULTI_ROWS:
                queryBuilder.setTables(StoreMapsTable.TABLE_LOCATIONS_BRIDGE);
                checkColumnNames(projection, LOCATIONS_BRIDGE_MULTI_ROWS);
                break;

            case LOCATIONS_BRIDGE_SINGLE_ROW:
                queryBuilder.setTables(StoreMapsTable.TABLE_LOCATIONS_BRIDGE);
                checkColumnNames(projection, LOCATIONS_BRIDGE_SINGLE_ROW);
                queryBuilder.appendWhere(StoreMapsTable.COL_MAP_ENTRY_ID + "=" + uri.getLastPathSegment());
                break;

            case STORES_WITH_CHAIN_NAMES:
/*          SELECT tblStores._id, tblStores.storeRegionalName, tblStoreChains.storeChainName
            FROM tblStores
            JOIN tblStoreChains
            ON tblStores.storeChainID = tblStoreChains._id
            ORDER BY storeChainName ASC, storeRegionalName ASC  */

                String tables = StoresTable.TABLE_STORES +
                        " JOIN " + StoreChainsTable.TABLE_STORE_CHAINS
                        + " ON "
                        + StoresTable.TABLE_STORES + "." + StoresTable.COL_STORE_CHAIN_ID + " = "
                        + StoreChainsTable.TABLE_STORE_CHAINS + "." + StoreChainsTable.COL_STORE_CHAIN_ID;
                queryBuilder.setTables(tables);
                break;

            case ITEMS_BY_GROUPS:
    /*      SELECT tblItems._id, groupName, itemName, itemNote, groupID, itemSelected, itemStruckOut, itemChecked
            FROM tblItems JOIN tblGroups
            ON (tblItems.groupID = tblGroups._id)
            ORDER BY groupName, itemName*/
                tables = ItemsTable.TABLE_ITEMS + " JOIN " + GroupsTable.TABLE_GROUPS
                        + " ON "
                        + ItemsTable.TABLE_ITEMS + "." + ItemsTable.COL_GROUP_ID + " = "
                        + GroupsTable.TABLE_GROUPS + "." + GroupsTable.COL_GROUP_ID;
                queryBuilder.setTables(tables);
                break;

            case ITEMS_BY_LOCATIONS_AND_GROUPS:
/*          SELECT tblItems._id,tblItems.itemName, tblItems.itemNote, tblItems.groupID, tblGroups.groupName, tblLocationsBridge.locationID, tblLocations.locationName
            FROM tblItems
            JOIN tblLocationsBridge ON tblItems.groupID = tblLocationsBridge.groupID

            JOIN tblLocations ON tblLocationsBridge.locationID = tblLocations._id

            JOIN tblGroups ON tblLocationsBridge.groupID = tblGroups._id

            WHERE   tblItems.itemSelected=1 AND tblLocationsBridge.storeID =6
            ORDER BY   tblGroups.groupName, tblItems.itemName*/
                tables = ItemsTable.TABLE_ITEMS
                        + " JOIN " + StoreMapsTable.TABLE_LOCATIONS_BRIDGE + " ON "
                        + ItemsTable.TABLE_ITEMS + "." + ItemsTable.COL_GROUP_ID + " = "
                        + StoreMapsTable.TABLE_LOCATIONS_BRIDGE + "." + StoreMapsTable.COL_GROUP_ID

                        + " JOIN " + LocationsTable.TABLE_LOCATIONS + " ON "
                        + StoreMapsTable.TABLE_LOCATIONS_BRIDGE + "." + StoreMapsTable.COL_LOCATION_ID + " = "
                        + LocationsTable.TABLE_LOCATIONS + "." + LocationsTable.COL_LOCATION_ID

                        + " JOIN " + GroupsTable.TABLE_GROUPS + " ON "
                        + StoreMapsTable.TABLE_LOCATIONS_BRIDGE + "." + StoreMapsTable.COL_GROUP_ID + " = "
                        + GroupsTable.TABLE_GROUPS + "." + GroupsTable.COL_GROUP_ID;

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
            //String groupBy = null;
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
                        getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_STORES_WITH_CHAIN_NAMES, null);
                        //getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_ITEMS_BY_GROUPS, null);
                        getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_ITEMS_BY_LOCATIONS_AND_GROUPS, null);
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
                        getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_STORES_WITH_CHAIN_NAMES, null);
                        //getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_ITEMS_BY_GROUPS, null);
                        getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_ITEMS_BY_LOCATIONS_AND_GROUPS, null);
                    }
                    return newRowUri;
                }

            case ITEMS_SINGLE_ROW:
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
                        getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_STORES_WITH_CHAIN_NAMES, null);
                        //getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_ITEMS_BY_GROUPS, null);
                        getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_ITEMS_BY_LOCATIONS_AND_GROUPS, null);
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
                        getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_STORES_WITH_CHAIN_NAMES, null);
                        //getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_ITEMS_BY_GROUPS, null);
                        getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_ITEMS_BY_LOCATIONS_AND_GROUPS, null);

                    }
                    return newRowUri;
                }

            case STORES_SINGLE_ROW:
                throw new IllegalArgumentException(
                        "Cannot insert a new row with a single row URI. Illegal URI: " + uri);


            case GROUPS_MULTI_ROWS:
                newRowId = db.insertOrThrow(GroupsTable.TABLE_GROUPS, nullColumnHack, values);
                if (newRowId > 0) {
                    // Construct and return the URI of the newly inserted row.
                    Uri newRowUri = ContentUris.withAppendedId(GroupsTable.CONTENT_URI, newRowId);

                    if (!mSuppressChangeNotification) {
                        // Notify and observers of the change in the database.
                        getContext().getContentResolver().notifyChange(GroupsTable.CONTENT_URI, null);
                        getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_STORES_WITH_CHAIN_NAMES, null);
                        //getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_ITEMS_BY_GROUPS, null);
                        getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_ITEMS_BY_LOCATIONS_AND_GROUPS, null);
                    }
                    return newRowUri;
                }

            case GROUPS_SINGLE_ROW:
                throw new IllegalArgumentException(
                        "Cannot insert a new row with a single row URI. Illegal URI: " + uri);

            case LOCATIONS_MULTI_ROWS:
                newRowId = db.insertOrThrow(LocationsTable.TABLE_LOCATIONS, nullColumnHack, values);
                if (newRowId > 0) {
                    Uri newRowUri = ContentUris.withAppendedId(LocationsTable.CONTENT_URI, newRowId);
                    if (!mSuppressChangeNotification) {
                        getContext().getContentResolver().notifyChange(LocationsTable.CONTENT_URI, null);
                        getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_STORES_WITH_CHAIN_NAMES, null);
                        //getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_ITEMS_BY_GROUPS, null);
                        getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_ITEMS_BY_LOCATIONS_AND_GROUPS, null);
                    }
                    return newRowUri;
                }

            case LOCATIONS_SINGLE_ROW:
                throw new IllegalArgumentException(
                        "Cannot insert a new row with a single row URI. Illegal URI: " + uri);


            case LOCATIONS_BRIDGE_MULTI_ROWS:
                newRowId = db.insertOrThrow(StoreMapsTable.TABLE_LOCATIONS_BRIDGE, nullColumnHack, values);
                if (newRowId > 0) {
                    Uri newRowUri = ContentUris.withAppendedId(StoreMapsTable.CONTENT_URI, newRowId);
                    if (!mSuppressChangeNotification) {
                        getContext().getContentResolver().notifyChange(StoreMapsTable.CONTENT_URI, null);
                        getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_STORES_WITH_CHAIN_NAMES, null);
                        //getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_ITEMS_BY_GROUPS, null);
                        getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_ITEMS_BY_LOCATIONS_AND_GROUPS, null);
                    }
                    return newRowUri;
                }

            case LOCATIONS_BRIDGE_SINGLE_ROW:
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

            case GROUPS_MULTI_ROWS:
                // To return the number of deleted items you must specify a where clause.
                // To delete all rows and return a value pass in "1".
                if (selection == null) {
                    selection = "1";
                }
                // Perform the deletion
                deleteCount = db.delete(GroupsTable.TABLE_GROUPS, selection, selectionArgs);
                break;

            case GROUPS_SINGLE_ROW:
                // Limit deletion to a single row
                rowID = uri.getLastPathSegment();
                selection = GroupsTable.COL_GROUP_ID + "=" + rowID
                        + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                // Perform the deletion
                deleteCount = db.delete(GroupsTable.TABLE_GROUPS, selection, selectionArgs);
                break;


            case LOCATIONS_MULTI_ROWS:
                // To return the number of deleted items you must specify a where clause.
                // To delete all rows and return a value pass in "1".
                if (selection == null) {
                    selection = "1";
                }
                // Perform the deletion
                deleteCount = db.delete(LocationsTable.TABLE_LOCATIONS, selection, selectionArgs);
                break;

            case LOCATIONS_SINGLE_ROW:
                // Limit deletion to a single row
                rowID = uri.getLastPathSegment();
                selection = LocationsTable.COL_LOCATION_ID + "=" + rowID
                        + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                // Perform the deletion
                deleteCount = db.delete(LocationsTable.TABLE_LOCATIONS, selection, selectionArgs);
                break;

            case LOCATIONS_BRIDGE_MULTI_ROWS:
                // To return the number of deleted items you must specify a where clause.
                // To delete all rows and return a value pass in "1".
                if (selection == null) {
                    selection = "1";
                }
                // Perform the deletion
                deleteCount = db.delete(StoreMapsTable.TABLE_LOCATIONS_BRIDGE, selection, selectionArgs);
                break;

            case LOCATIONS_BRIDGE_SINGLE_ROW:
                // Limit deletion to a single row
                rowID = uri.getLastPathSegment();
                selection = StoreMapsTable.COL_MAP_ENTRY_ID + "=" + rowID
                        + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                // Perform the deletion
                deleteCount = db.delete(StoreMapsTable.TABLE_LOCATIONS_BRIDGE, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Method delete: Unknown URI: " + uri);

        }

        if (!mSuppressChangeNotification) {
            // Notify and observers of the change in the database.
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_STORES_WITH_CHAIN_NAMES, null);
           // getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_ITEMS_BY_GROUPS, null);
            getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_ITEMS_BY_LOCATIONS_AND_GROUPS, null);
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
                // remove the COL_DATE_TIME_LAST_USED key if it exists, then set the current time
                if(values.containsKey(ItemsTable.COL_DATE_TIME_LAST_USED)){
                    values.remove(ItemsTable.COL_DATE_TIME_LAST_USED);
                }
                values.put(ItemsTable.COL_DATE_TIME_LAST_USED,System.currentTimeMillis());
                updateCount = db.update(ItemsTable.TABLE_ITEMS, values, selection, selectionArgs);
                break;

            case ITEMS_SINGLE_ROW:
                // remove the COL_DATE_TIME_LAST_USED key if it exists, then set the current time
                if(values.containsKey(ItemsTable.COL_DATE_TIME_LAST_USED)){
                    values.remove(ItemsTable.COL_DATE_TIME_LAST_USED);
                }
                values.put(ItemsTable.COL_DATE_TIME_LAST_USED,System.currentTimeMillis());
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

            case GROUPS_MULTI_ROWS:
                updateCount = db.update(GroupsTable.TABLE_GROUPS, values, selection, selectionArgs);

                break;

            case GROUPS_SINGLE_ROW:
                // Limit update to a single row
                rowID = uri.getLastPathSegment();
                if (selection == null) {
                    selection = GroupsTable.COL_GROUP_ID + "=" + rowID;
                } else {
                    selection = GroupsTable.COL_GROUP_ID + "=" + rowID
                            + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                }
                // Perform the update
                updateCount = db.update(GroupsTable.TABLE_GROUPS, values, selection, selectionArgs);
                break;


            case LOCATIONS_MULTI_ROWS:
                updateCount = db.update(LocationsTable.TABLE_LOCATIONS, values, selection, selectionArgs);
                break;

            case LOCATIONS_SINGLE_ROW:
                // Limit update to a single row
                rowID = uri.getLastPathSegment();
                if (selection == null) {
                    selection = LocationsTable.COL_LOCATION_ID + "=" + rowID;
                } else {
                    selection = LocationsTable.COL_LOCATION_ID + "=" + rowID
                            + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                }
                // Perform the update
                updateCount = db.update(LocationsTable.TABLE_LOCATIONS, values, selection, selectionArgs);
                break;

            case LOCATIONS_BRIDGE_MULTI_ROWS:
                updateCount = db.update(StoreMapsTable.TABLE_LOCATIONS_BRIDGE, values, selection, selectionArgs);

                break;

            case LOCATIONS_BRIDGE_SINGLE_ROW:
                // Limit update to a single row
                rowID = uri.getLastPathSegment();
                if (selection == null) {
                    selection = StoreMapsTable.COL_MAP_ENTRY_ID + "=" + rowID;
                } else {
                    selection = StoreMapsTable.COL_MAP_ENTRY_ID + "=" + rowID
                            + (!selection.isEmpty() ? " AND (" + selection + ")" : "");
                }
                // Perform the update
                updateCount = db.update(StoreMapsTable.TABLE_LOCATIONS_BRIDGE, values, selection, selectionArgs);
                break;

            default:
                throw new IllegalArgumentException("Method update: Unknown URI: " + uri);
        }

        if (!mSuppressChangeNotification) {
            // Notify any observers of the change in the database.
            getContext().getContentResolver().notifyChange(uri, null);
            getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_STORES_WITH_CHAIN_NAMES, null);
            //getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_ITEMS_BY_GROUPS, null);
            getContext().getContentResolver().notifyChange(JoinedTables.CONTENT_URI_ITEMS_BY_LOCATIONS_AND_GROUPS, null);
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

            case STORE_CHAINS_MULTI_ROWS:
                return StoreChainsTable.CONTENT_TYPE;
            case STORE_CHAINS_SINGLE_ROW:
                return StoreChainsTable.CONTENT_ITEM_TYPE;

            case STORES_MULTI_ROWS:
                return StoresTable.CONTENT_TYPE;
            case STORES_SINGLE_ROW:
                return StoresTable.CONTENT_ITEM_TYPE;

            case GROUPS_MULTI_ROWS:
                return GroupsTable.CONTENT_TYPE;
            case GROUPS_SINGLE_ROW:
                return GroupsTable.CONTENT_ITEM_TYPE;

            case LOCATIONS_MULTI_ROWS:
                return LocationsTable.CONTENT_TYPE;
            case LOCATIONS_SINGLE_ROW:
                return LocationsTable.CONTENT_ITEM_TYPE;

            case LOCATIONS_BRIDGE_MULTI_ROWS:
                return StoreMapsTable.CONTENT_TYPE;
            case LOCATIONS_BRIDGE_SINGLE_ROW:
                return StoreMapsTable.CONTENT_ITEM_TYPE;

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

            case STORE_CHAINS_MULTI_ROWS:
            case STORE_CHAINS_SINGLE_ROW:
                availableColumns = new HashSet<>(Arrays.asList(StoreChainsTable.PROJECTION_ALL));
                break;


            case STORES_MULTI_ROWS:
            case STORES_SINGLE_ROW:
                availableColumns = new HashSet<>(Arrays.asList(StoresTable.PROJECTION_ALL));
                break;

            case GROUPS_MULTI_ROWS:
            case GROUPS_SINGLE_ROW:
                availableColumns = new HashSet<>(Arrays.asList(GroupsTable.PROJECTION_ALL));
                break;


            case LOCATIONS_MULTI_ROWS:
            case LOCATIONS_SINGLE_ROW:
                availableColumns = new HashSet<>(Arrays.asList(LocationsTable.PROJECTION_ALL));
                break;

            case LOCATIONS_BRIDGE_MULTI_ROWS:
            case LOCATIONS_BRIDGE_SINGLE_ROW:
                availableColumns = new HashSet<>(Arrays.asList(StoreMapsTable.PROJECTION_ALL));
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
