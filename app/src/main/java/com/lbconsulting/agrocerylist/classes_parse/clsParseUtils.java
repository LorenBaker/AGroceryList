package com.lbconsulting.agrocerylist.classes_parse;

import android.content.Context;
import android.database.Cursor;

import com.google.gson.Gson;
import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.database.GroupsTable;
import com.lbconsulting.agrocerylist.database.ItemsTable;
import com.lbconsulting.agrocerylist.database.LocationsTable;
import com.lbconsulting.agrocerylist.database.StoreChainsTable;
import com.lbconsulting.agrocerylist.database.StoresTable;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * This class contains helper methods used with Parse
 */
public class clsParseUtils {

    public static final int saveThisThread = 1;
    private static final int saveInBackground = 2;
    private static final int saveEventually = 3;


    public static void createParseStoreMap(ArrayList<clsParseStoreMapEntry> storeMapEntryList,
                                           String storeMapName, long storeID, int saveType) {
        if (storeMapEntryList.size() == 0 || storeMapName.isEmpty() || storeID < 1) {
            MyLog.e("clsParseUtils", "createParseStoreMap: Unable to update store map. " +
                    "storeMapEntryList.size = 0, or storeMapName is empty, or storeID < 1.");
            return;
        }

        // make JSON string from the store map list
        Gson gson = new Gson();
        clsParseStoreMapEntryArray array = new clsParseStoreMapEntryArray(storeMapEntryList);
        String storeMapJson = gson.toJson(array);

        final ParseStoreMap storeMap = new ParseStoreMap();
        storeMap.setStoreID(storeID);
        storeMap.setJsonContent(storeMapJson);
        storeMap.setMapName(storeMapName);
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
                    MyLog.i("clsParseUtils", "createParseStoreMap: save(): storeMapName = " + storeMapName);
                    break;

                case saveInBackground:
                    storeMap.saveInBackground();
                    MyLog.i("clsParseUtils", "createParseStoreMap: saveInBackground(): storeMapName = " + storeMapName);
                    break;

                case saveEventually:
                    storeMap.saveEventually();
                    MyLog.i("clsParseUtils", "createParseStoreMap: saveEventually(): storeMapName = " + storeMapName);
                    break;
            }

        } catch (ParseException e) {
            MyLog.e("clsParseUtils", "createParseStoreMap: ParseException: " + e.getMessage());
        }
    }

    private static void createPublicTable(ArrayList<clsParseGroup> groupsList, ArrayList<clsParseLocation> locationsList,
                                          ArrayList<clsParseStoreChain> storeChainsList, ArrayList<clsParseStore> storesList,
                                          ArrayList<clsParseInitialItem> itemsList, int saveType) {
        //GroupsTable, LocationsTable, StoreChainsTable, StoresTable, initial Items.

        // make JSON string from the list
        Gson gson = new Gson();
        String jasonString = null;
        String tableName = "";
        if (groupsList != null) {
            clsParseGroupArray array = new clsParseGroupArray(groupsList);
            jasonString = gson.toJson(array);
            tableName = GroupsTable.TABLE_GROUPS;

        } else if (locationsList != null) {
            clsParseLocationArray array = new clsParseLocationArray(locationsList);
            jasonString = gson.toJson(array);
            tableName = LocationsTable.TABLE_LOCATIONS;

        } else if (storeChainsList != null) {
            clsParseStoreChainArray array = new clsParseStoreChainArray(storeChainsList);
            jasonString = gson.toJson(array);
            tableName = StoreChainsTable.TABLE_STORE_CHAINS;

        } else if (storesList != null) {
            clsParseStoreArray array = new clsParseStoreArray(storesList);
            jasonString = gson.toJson(array);
            tableName = StoresTable.TABLE_STORES;

        } else if (itemsList != null) {
            clsParseItemArray array = new clsParseItemArray(itemsList);
            jasonString = gson.toJson(array);
            tableName = ItemsTable.TABLE_ITEMS;
        }
        if (jasonString == null) {
            return;
        }

        final PublicTablesData tableData = new PublicTablesData();
        tableData.setJsonContent(jasonString);
        tableData.setTableName(tableName);
        tableData.setTimestamp(System.currentTimeMillis());

        if (groupsList != null) {
            // set the Access Control List (ACL) to allow all users read only access.
            ParseACL tableDataACL = new ParseACL(ParseUser.getCurrentUser());
            tableDataACL.setPublicReadAccess(true);
            tableData.setACL(tableDataACL);

        } else if (locationsList != null) {
            // set the Access Control List (ACL) to allow all users read and write access.
            // TODO: limit the number of aisles that can be added to ... 50?
            ParseACL tableDataACL = new ParseACL(ParseUser.getCurrentUser());
            tableDataACL.setPublicReadAccess(true);
            tableDataACL.setPublicWriteAccess(true);
            tableData.setACL(tableDataACL);

        } else if (storeChainsList != null) {
            // set the Access Control List (ACL) to allow all users read and write access.
            ParseACL tableDataACL = new ParseACL(ParseUser.getCurrentUser());
            tableDataACL.setPublicReadAccess(true);
            tableDataACL.setPublicWriteAccess(true);
            tableData.setACL(tableDataACL);

        } else if (storesList != null) {
            // set the Access Control List (ACL) to allow all users read and write access.
            ParseACL tableDataACL = new ParseACL(ParseUser.getCurrentUser());
            tableDataACL.setPublicReadAccess(true);
            tableDataACL.setPublicWriteAccess(true);
            tableData.setACL(tableDataACL);

        } else if (itemsList != null) {
            // set the Access Control List (ACL) to allow all users read only access.
            ParseACL tableDataACL = new ParseACL(ParseUser.getCurrentUser());
            tableDataACL.setPublicReadAccess(true);
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

    //region Initial Data SAVED to Parse


    public static void loadInitialData(Context context) {

        // initialize the grocery groups
        String[] groceryGroups = context.getResources().getStringArray(R.array.grocery_groups);
        for (String groceryGroup : groceryGroups) {
            GroupsTable.createNewGroup(context, groceryGroup);
        }
        // create Parse groups table
        createParseGroupsTable(context);


        // initialize the store locations
        LocationsTable.createInitialLocationsIfNeeded(context);
        // create Parse locations table
        createParseLocationsTable(context);


        // create store chains
        String[] storeChains = context.getResources().getStringArray(R.array.grocery_store_chains);
        for (String store : storeChains) {
            StoreChainsTable.createNewStoreChain(context, store);
        }
        // create Parse store chain table
        createParseStoreChainsTable(context);


        // create stores ... NOTE: assumes store chains and groups are already created.
        // also assumes store chain IDs are in the order in R.array.grocery_store_chains

        // Albertsons
        StoresTable.createNewStore(context, 1, "Eastgate", "15100 SE 38TH ST", "STE 103", "BELLEVUE", "WA", "98006-1763");

        // Fred Myer
        StoresTable.createNewStore(context, 2, "Bellevue", "2041 148TH AVE NE", "", "BELLEVUE", "WA", "98007-3788");
        StoresTable.createNewStore(context, 2, "Redmond", "17667 NE 76TH ST", "", "REDMOND", "WA", "98052-4994");

        // PCC
        StoresTable.createNewStore(context, 4, "Issaquah", "1810 12TH AVE NW", "STE A", "ISSAQUAH", "WA", "98027-8110");
        StoresTable.createNewStore(context, 4, "Kirkland", "10718 NE 68TH ST", "", "KIRKLAND", "WA", "98033-7030");

        // QFC
        StoresTable.createNewStore(context, 5, "Bel-East", "1510 145TH PL SE", "STE A", "BELLEVUE", "WA", "98007-5593");
        StoresTable.createNewStore(context, 5, "Issaquah", "1540 NW GILMAN BLVD", "", "ISSAQUAH", "WA", "98027-5309");
        StoresTable.createNewStore(context, 5, "Klahanie Dr", "4570 KLAHANIE DR SE", "", "ISSAQUAH", "WA", "98029-5812");
        StoresTable.createNewStore(context, 5, "Factoria", "3500 FACTORIA BLVD SE", "", "BELLEVUE", "WA", "98006-5276");
        StoresTable.createNewStore(context, 5, "Newcastle", "6940 COAL CREEK PKWY SE", "", "NEWCASTLE", "WA", "98059-3137");

        // Safeway
        StoresTable.createNewStore(context, 6, "Factoria", "3903 FACTORIA BLVD SE", "", "BELLEVUE", "WA", "98006-6148");
        StoresTable.createNewStore(context, 6, "Evergreen Village", "1645 140TH AVE NE", "STE A5", "BELLEVUE", "WA", "98005-2320");
        StoresTable.createNewStore(context, 6, "Issaquah", "735 NW GILMAN BLVD", "STE B", "ISSAQUAH", "WA", "98027-8996");
        StoresTable.createNewStore(context, 6, "Highlands", "1451 HIGHLANDS DR NE", "", "ISSAQUAH", "WA", "98029-6240");

        //Trader Joe’s
        StoresTable.createNewStore(context, 8, "Issaquah", "975 NW GILMAN BLVD", "STE A", "ISSAQUAH", "WA", "98027-5377");
        StoresTable.createNewStore(context, 8, "Redmond", "15932 REDMOND WAY", "STE 101", "REDMOND", "WA", " 98052-4060");
        StoresTable.createNewStore(context, 8, "Bellevue", "15563 NE 24TH ST", "", "BELLEVUE", "WA", "98007-3836");

        // Whole Foods
        StoresTable.createNewStore(context, 9, "Bellevue", "888 116TH AVE NE", "", "BELLEVUE", "WA", "98004-4607");
        StoresTable.createNewStore(context, 9, "Redmond", "17991 REDMOND WAY", "", "REDMOND", "WA", " 98052-4907");

        // create Parse stores table
        createParseStoresTable(context);

        // create initial items.
        String[] groceryItems = context.getResources().getStringArray(R.array.grocery_items);
        for (String groceryItem : groceryItems) {
            String[] item = groceryItem.split(", ");
            String itemName = item[0];
            String groupID = item[1];
            ItemsTable.createNewItem(context, itemName, groupID);
        }

        // create Items table
        createParseItemsTable(context);
    }


    private static void createParseGroupsTable(Context context) {
        //MyLog.i("MainActivity", "createParseGroupsTable");
        Cursor groupsCursor = GroupsTable.getAllGroupsCursor(context, GroupsTable.SORT_ORDER_GROUP_NAME);
        if (groupsCursor != null && groupsCursor.getCount() > 0) {
            ArrayList<clsParseGroup> groupsList = new ArrayList<>();
            long groupID;
            String groupName;
            clsParseGroup group;
            while (groupsCursor.moveToNext()) {
                groupID = groupsCursor.getLong(groupsCursor.getColumnIndex(GroupsTable.COL_GROUP_ID));
                groupName = groupsCursor.getString(groupsCursor.getColumnIndex(GroupsTable.COL_GROUP_NAME));
                group = new clsParseGroup(groupID, groupName);
                groupsList.add(group);
            }
            if (groupsList.size() > 0) {
                // use saveThisThread because this method is being run in an AsyncTask
                clsParseUtils.createPublicTable(groupsList, null, null, null, null, clsParseUtils.saveThisThread);
            }
        }
    }

    private static void createParseLocationsTable(Context context) {
        //MyLog.i("MainActivity", "createParseLocationsTable");
        Cursor locationsCursor = LocationsTable.getAllLocationsCursor(context, LocationsTable.SORT_ORDER_LOCATION_ID);
        if (locationsCursor != null && locationsCursor.getCount() > 0) {
            ArrayList<clsParseLocation> locationsList = new ArrayList<>();
            long locationID;
            String locationName;
            clsParseLocation location;
            while (locationsCursor.moveToNext()) {
                locationID = locationsCursor.getLong(locationsCursor.getColumnIndex(LocationsTable.COL_LOCATION_ID));
                locationName = locationsCursor.getString(locationsCursor.getColumnIndex(LocationsTable.COL_LOCATION_NAME));
                location = new clsParseLocation(locationID, locationName);
                locationsList.add(location);
            }
            if (locationsList.size() > 0) {
                // use saveThisThread because this method is being run in an AsyncTask
                clsParseUtils.createPublicTable(null, locationsList, null, null, null, clsParseUtils.saveThisThread);
            }
        }
    }

    private static void createParseStoreChainsTable(Context context) {
        // MyLog.i("MainActivity", "createParseStoreChainsTable");
        Cursor storeChainsCursor = StoreChainsTable.getAllStoreChainsCursor(context, StoreChainsTable.SORT_ORDER_STORE_CHAIN_NAME);
        if (storeChainsCursor != null && storeChainsCursor.getCount() > 0) {
            ArrayList<clsParseStoreChain> storeChainsList = new ArrayList<>();
            long storeChainID;
            String storeChainName;
            clsParseStoreChain storeChain;
            while (storeChainsCursor.moveToNext()) {
                storeChainID = storeChainsCursor.getLong(storeChainsCursor.getColumnIndex(StoreChainsTable.COL_STORE_CHAIN_ID));
                storeChainName = storeChainsCursor.getString(storeChainsCursor.getColumnIndex(StoreChainsTable.COL_STORE_CHAIN_NAME));
                storeChain = new clsParseStoreChain(storeChainID, storeChainName);
                storeChainsList.add(storeChain);
            }
            if (storeChainsList.size() > 0) {
                // use saveThisThread because this method is being run in an AsyncTask
                clsParseUtils.createPublicTable(null, null, storeChainsList, null, null, clsParseUtils.saveThisThread);
            }
        }
    }

    private static void createParseStoresTable(Context context) {
        // MyLog.i("MainActivity", "createParseStoresTable");
        Cursor storesCursor = StoresTable.getAllStoresCursor(context, StoresTable.SORT_ORDER_CHAIN_ID_BY_REGIONAL_NAME);
        if (storesCursor != null && storesCursor.getCount() > 0) {
            ArrayList<clsParseStore> storeList = new ArrayList<>();
            long storeID;
            long storeChainID;
            String storeRegionalName;
            String parseStoreMapName;

            String address1;
            String address2;
            String city;
            String state;
            String zip;
            String gpsLatitude;
            String gpsLongitude;
            String websiteURL;
            String phoneNumber;
            int manualSortKey;

            clsParseStore store;
            while (storesCursor.moveToNext()) {

                storeID = storesCursor.getLong(storesCursor.getColumnIndex(StoresTable.COL_STORE_ID));
                storeChainID = storesCursor.getLong(storesCursor.getColumnIndex(StoresTable.COL_STORE_CHAIN_ID));
                storeRegionalName = storesCursor.getString(storesCursor.getColumnIndex(StoresTable.COL_STORE_REGIONAL_NAME));
                parseStoreMapName = storesCursor.getString(storesCursor.getColumnIndex(StoresTable.COL_PARSE_STORE_MAP_NAME));

                address1 = storesCursor.getString(storesCursor.getColumnIndex(StoresTable.COL_ADDRESS1));
                address2 = storesCursor.getString(storesCursor.getColumnIndex(StoresTable.COL_ADDRESS2));
                city = storesCursor.getString(storesCursor.getColumnIndex(StoresTable.COL_CITY));
                state = storesCursor.getString(storesCursor.getColumnIndex(StoresTable.COL_STATE));
                zip = storesCursor.getString(storesCursor.getColumnIndex(StoresTable.COL_ZIP));
                gpsLatitude = storesCursor.getString(storesCursor.getColumnIndex(StoresTable.COL_GPS_LATITUDE));
                gpsLongitude = storesCursor.getString(storesCursor.getColumnIndex(StoresTable.COL_GPS_LONGITUDE));
                websiteURL = storesCursor.getString(storesCursor.getColumnIndex(StoresTable.COL_WEBSITE_URL));
                phoneNumber = storesCursor.getString(storesCursor.getColumnIndex(StoresTable.COL_PHONE_NUMBER));
                manualSortKey = storesCursor.getInt(storesCursor.getColumnIndex(StoresTable.COL_MANUAL_SORT_KEY));


                store = new clsParseStore();
                store.setStoreID(storeID);
                store.setStoreChainID(storeChainID);
                store.setStoreRegionalName(storeRegionalName);
                store.setParseStoreMapName(parseStoreMapName);

                store.setAddress1(address1);
                store.setAddress2(address2);
                store.setCity(city);
                store.setState(state);
                store.setZip(zip);
                store.setGpsLatitude(gpsLatitude);
                store.setGpsLongitude(gpsLongitude);
                store.setWebsiteURL(websiteURL);
                store.setPhoneNumber(phoneNumber);
                store.setManualSortKey(manualSortKey);

                storeList.add(store);
            }
            if (storeList.size() > 0) {
                // use saveThisThread because this method is being run in an AsyncTask
                clsParseUtils.createPublicTable(null, null, null, storeList, null, clsParseUtils.saveThisThread);
            }
        }
    }

    private static void createParseItemsTable(Context context) {
        // MyLog.i("MainActivity", "createParseItemsTable");
        Cursor itemsCursor = ItemsTable.getAllItemsCursor(context, ItemsTable.SORT_ORDER_ITEM_NAME_DESC);
        if (itemsCursor != null && itemsCursor.getCount() > 0) {
            ArrayList<clsParseInitialItem> itemsList = new ArrayList<>();
            String itemName;
            long itemID, groupID, lastTimeUsed;
            int manualSortOrder;
            clsParseInitialItem item;
            while (itemsCursor.moveToNext()) {
                itemID = itemsCursor.getLong(itemsCursor.getColumnIndex(ItemsTable.COL_ITEM_ID));
                groupID = itemsCursor.getLong(itemsCursor.getColumnIndex(ItemsTable.COL_GROUP_ID));
                itemName = itemsCursor.getString(itemsCursor.getColumnIndex(ItemsTable.COL_ITEM_NAME));
                lastTimeUsed = itemsCursor.getLong(itemsCursor.getColumnIndex(ItemsTable.COL_LAST_TIME_USED));
                manualSortOrder = itemsCursor.getInt(itemsCursor.getColumnIndex(ItemsTable.COL_MANUAL_SORT_ORDER));
                item = new clsParseInitialItem(itemID, itemName, groupID, manualSortOrder, lastTimeUsed);
                itemsList.add(item);
            }
            if (itemsList.size() > 0) {
                // use saveThisThread because this method is being run in an AsyncTask
                clsParseUtils.createPublicTable(null, null, null, null, itemsList, clsParseUtils.saveThisThread);
            }
        }
    }

//endregion

}
