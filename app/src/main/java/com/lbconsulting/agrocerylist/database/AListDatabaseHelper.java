package com.lbconsulting.agrocerylist.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lbconsulting.agrocerylist.classes.MyLog;

public class AListDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "AList.db";
	private static final int DATABASE_VERSION = 4;

	private static SQLiteDatabase dBase;

	public AListDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		AListDatabaseHelper.dBase = database;

		MyLog.i("AListDatabaseHelper", "onCreate");
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
		MyLog.i("AListDatabaseHelper", "onUpgrade");
		GroupsTable.onUpgrade(database, oldVersion, newVersion);
		ItemsTable.onUpgrade(database, oldVersion, newVersion);
		ProductsTable.onUpgrade(database,oldVersion,newVersion);
		StoreChainsTable.onUpgrade(database,oldVersion,newVersion);
		StoresTable.onUpgrade(database, oldVersion, newVersion);
		LocationsTable.onUpgrade(database, oldVersion, newVersion);
		StoreMapsTable.onUpgrade(database, oldVersion, newVersion);
	}

	public static SQLiteDatabase getDatabase() {
		return dBase;
	}

}
