package com.lbconsulting.agrocerylist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.lbconsulting.agrocerylist.classes.MyLog;


/**
 * This class is the database helper for the Passwords database
 */
public class aGroceryListDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_PATH = "/data/data/com.lbconsulting.agrocerylist/databases/";
    private static final String DATABASE_NAME = "aGroceryList.db";
    private static final int DATABASE_VERSION = 1;

    private static SQLiteDatabase dBase;
    private static Context mContext;

    public aGroceryListDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        aGroceryListDatabaseHelper.dBase = database;
        MyLog.i("aGroceryListDatabaseHelper", "onCreate");
        GroupsTable.onCreate(database);
        ItemsTable.onCreate(database);
        ProductsTable.onCreate(database);
        StoreChainsTable.onCreate(database);
        StoresTable.onCreate(database);
        LocationsTable.onCreate(database);
        StoreMapsTable.onCreate(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        MyLog.i("aGroceryListDatabaseHelper", "onUpgrade");
        GroupsTable.onUpgrade(database, oldVersion, newVersion);
        ItemsTable.onUpgrade(database, oldVersion, newVersion);
        ProductsTable.onUpgrade(database, oldVersion, newVersion);
        StoreChainsTable.onUpgrade(database, oldVersion, newVersion);
        StoresTable.onUpgrade(database, oldVersion, newVersion);
        LocationsTable.onUpgrade(database,oldVersion,newVersion);
        StoreMapsTable.onUpgrade(database, oldVersion, newVersion);
    }

    public static boolean databaseExists() {
        SQLiteDatabase checkDB = null;
        try {

            String DB_FULL_PATH = DB_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(DB_FULL_PATH, null, SQLiteDatabase.OPEN_READONLY);
            if (checkDB != null) {

                MyLog.i("aGroceryListDatabaseHelper", "Database exists. Max size = " + checkDB.getMaximumSize());
                checkDB.close();
            }
        } catch (SQLiteException e) {
            // database doesn't exist.
            MyLog.i("aGroceryListDatabaseHelper", "Database does not exists. " + e.getMessage());
        }
        return checkDB != null;
    }


    public static SQLiteDatabase getDatabase() {
        return dBase;
    }

    public static boolean deleteDatabase() {
        return mContext.deleteDatabase(DATABASE_NAME); // true if deleted
    }
}
