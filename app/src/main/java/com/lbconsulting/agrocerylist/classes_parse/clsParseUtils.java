package com.lbconsulting.agrocerylist.classes_parse;

import com.google.gson.Gson;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.clsStoreMap;
import com.lbconsulting.agrocerylist.database.GroupsTable;
import com.lbconsulting.agrocerylist.database.LocationsTable;
import com.lbconsulting.agrocerylist.database.StoreChainsTable;
import com.lbconsulting.agrocerylist.database.StoresTable;
import com.parse.ParseACL;
import com.parse.ParseException;

import java.util.ArrayList;

/**
 * This class contains helper methods used with Parse
 */
public class clsParseUtils {

    public static final int saveThisThread = 1;
    public static final int saveInBackground = 2;
    public static final int saveEventually = 3;


    public static void createParseStoreMap(ArrayList<clsStoreMap> storeMapList, String mapName, int saveType) {
        if (storeMapList.size() == 0 || mapName.isEmpty()) {
            MyLog.e("clsParseUtils", "createParseStoreMap: Unable to update store map. Either storeMapList.size = 0, or mapName is empty.");
            return;
        }

        // make JSON string from the store map list
        Gson gson = new Gson();
        String storeMapString = gson.toJson(storeMapList);

        final StoreMaps storeMap = new StoreMaps();
        storeMap.setJsonContent(storeMapString);
        storeMap.setMapName(mapName);
        storeMap.setTimestamp(System.currentTimeMillis());

        // set the Access Control List (ACL) to allow all users read and write access.
        ParseACL storeMapACL = new ParseACL();
        storeMapACL.setPublicReadAccess(true);
        storeMapACL.setPublicWriteAccess(true);
        storeMap.setACL(storeMapACL);

        try {
            switch (saveType) {
                case saveThisThread:
                    // use saveThisThread if this method is being called in an AsyncTask or background thread.
                    storeMap.save();
                    MyLog.i("clsParseUtils", "createParseStoreMap: save(): mapName = " + mapName);
                    break;

                case saveInBackground:
                    storeMap.saveInBackground();
                    MyLog.i("clsParseUtils", "createParseStoreMap: saveInBackground(): mapName = " + mapName);
                    break;

                case saveEventually:
                    storeMap.saveEventually();
                    MyLog.i("clsParseUtils", "createParseStoreMap: saveEventually(): mapName = " + mapName);
                    break;
            }

        } catch (ParseException e) {
            MyLog.e("clsParseUtils", "createParseStoreMap: ParseException: " + e.getMessage());
        }
    }

    public static void createPublicTable(ArrayList<clsParseGroup> groupsList, ArrayList<clsParseLocation> locationsList,
                                         ArrayList<clsParseStoreChain> storeChainsList, ArrayList<clsParseStore> storesList,
                                         int saveType) {
        //GroupsTable, LocationsTable, StoreChainsTable, StoresTable.

        // make JSON string from the list
        Gson gson = new Gson();
        String jasonString = null;
        String tableName = "";
        if (groupsList != null) {
            jasonString = gson.toJson(groupsList);
            tableName = GroupsTable.TABLE_GROUPS;

        } else if (locationsList != null) {
            jasonString = gson.toJson(locationsList);
            tableName = LocationsTable.TABLE_LOCATIONS;

        } else if (storeChainsList != null) {
            jasonString = gson.toJson(storeChainsList);
            tableName = StoreChainsTable.TABLE_STORE_CHAINS;

        } else if (storesList != null) {
            jasonString = gson.toJson(storesList);
            tableName = StoresTable.TABLE_STORES;
        }
        if (jasonString == null) {
            return;
        }

        final PublicTablesData tableData = new PublicTablesData();
        tableData.setJsonContent(jasonString);
        tableData.setTableName(tableName);
        tableData.setTimestamp(System.currentTimeMillis());

        if (groupsList != null) {
            // TODO: set the Access Control List (ACL) to allow all users read only access.
            // set the Access Control List (ACL) to allow all users read only access.
            //ParseACL tableDataACL = new ParseACL(ParseUser.getCurrentUser());
            ParseACL tableDataACL = new ParseACL();
            tableDataACL.setPublicReadAccess(true);
            tableDataACL.setPublicReadAccess(true);
            tableData.setACL(tableDataACL);

        } else if (locationsList != null) {
            // set the Access Control List (ACL) to allow all users read and write access.
            // TODO: limit the number of aisles that can be added to ... 50?
            ParseACL tableDataACL = new ParseACL();
            tableDataACL.setPublicReadAccess(true);
            tableDataACL.setPublicWriteAccess(true);
            tableData.setACL(tableDataACL);

        } else if (storeChainsList != null) {
            // set the Access Control List (ACL) to allow all users read and write access.
            ParseACL tableDataACL = new ParseACL();
            tableDataACL.setPublicReadAccess(true);
            tableDataACL.setPublicWriteAccess(true);
            tableData.setACL(tableDataACL);

        } else if (storesList != null) {
            // set the Access Control List (ACL) to allow all users read and write access.
            ParseACL tableDataACL = new ParseACL();
            tableDataACL.setPublicReadAccess(true);
            tableDataACL.setPublicWriteAccess(true);
            tableData.setACL(tableDataACL);
        }


        try {
            switch (saveType) {
                case saveThisThread:
                    // use saveThisThread if this method is being called in an AsyncTask or background thread.
                    tableData.save();
                    MyLog.i("clsParseUtils", "createPublicTable: save(): tableName = " + tableName);
                    break;

                case saveInBackground:
                    tableData.saveInBackground();
                    MyLog.i("clsParseUtils", "createPublicTable: saveInBackground(): tableName = " + tableName);
                    break;

                case saveEventually:
                    tableData.saveEventually();
                    MyLog.i("clsParseUtils", "createPublicTable: saveEventually(): tableName = " + tableName);
                    break;
            }

        } catch (ParseException e) {
            MyLog.e("clsParseUtils", "createPublicTable: ParseException: " + e.getMessage());
        }
    }
}
