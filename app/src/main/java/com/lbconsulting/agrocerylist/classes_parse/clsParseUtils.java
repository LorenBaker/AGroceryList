package com.lbconsulting.agrocerylist.classes_parse;

import android.content.Context;
import android.database.Cursor;

import com.google.gson.Gson;
import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;
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

    public static final int SAVE_THIS_THREAD = 1;
    public static final int SAVE_IN_BACKGROUND = 2;
    public static final int SAVE_EVENTUALLY = 3;


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

                case SAVE_THIS_THREAD:
                    // use SAVE_THIS_THREAD if this method is being called in an AsyncTask or background thread.
                    storeMap.save();
                    MyLog.i("clsParseUtils", "createParseStoreMap: save(): storeMapName = " + storeMapName);
                    break;

                case SAVE_IN_BACKGROUND:
                    storeMap.saveInBackground();
                    MyLog.i("clsParseUtils", "createParseStoreMap: SAVE_IN_BACKGROUND(): storeMapName = " + storeMapName);
                    break;

                case SAVE_EVENTUALLY:
                    storeMap.saveEventually();
                    MyLog.i("clsParseUtils", "createParseStoreMap: SAVE_EVENTUALLY(): storeMapName = " + storeMapName);
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
                case SAVE_THIS_THREAD:
                    // use SAVE_THIS_THREAD if this method is being called in an AsyncTask or background thread.
                    tableData.save();
                    MyLog.i("clsParseUtils", "createPublicTable: save(): tableName = " + tableName);
                    break;

                case SAVE_IN_BACKGROUND:
                    tableData.saveInBackground();
                    MyLog.i("clsParseUtils", "createPublicTable: SAVE_IN_BACKGROUND(): tableName = " + tableName);
                    break;

                case SAVE_EVENTUALLY:
                    tableData.saveEventually();
                    MyLog.i("clsParseUtils", "createPublicTable: SAVE_EVENTUALLY(): tableName = " + tableName);
                    break;
            }

        } catch (ParseException e) {
            MyLog.e("clsParseUtils", "createPublicTable: ParseException: " + e.getMessage());
        }

    }

    //region Initial Data SAVED to Parse


    public static void loadInitialDataToParse(Context context) {

        // upload initial grocery groups to Parse
        String[] groceryGroups = context.getResources().getStringArray(R.array.grocery_groups);
        long id = 1;
        Groups group;
        for (String groceryGroup : groceryGroups) {
            group = new Groups();
            group.setGroup(id, groceryGroup);
            saveGroupToParse(group, clsParseUtils.SAVE_THIS_THREAD);
            id++;
        }

        // upload initial store locations to Parse
        String[] initialLocations = context.getResources().getStringArray(R.array.initial_location_list);
        ArrayList<String> groupLocations = new ArrayList<>();
        for (String initialLocation : initialLocations) {
            groupLocations.add(initialLocation);
        }
        groupLocations = createInitialAisles(groupLocations);
        id = 1;
        Locations location;
        for (String groupLocation : groupLocations) {
            location = new Locations();
            location.setLocation(id, groupLocation);
            saveLocationToParse(location, clsParseUtils.SAVE_THIS_THREAD);
            id++;
        }


        // upload initial store chains to Parse
        String[] storeChains = context.getResources().getStringArray(R.array.grocery_store_chains);
        id = 1;
        StoreChains storeChain;
        for (String storeChainName : storeChains) {
            storeChain = new StoreChains();
            storeChain.setStoreChain(id, storeChainName);
            saveStoreChainToParse(storeChain, clsParseUtils.SAVE_THIS_THREAD);
            id++;
        }


        // create stores ... NOTE: assumes store chains and groups are already created.
        // also assumes store chain IDs are in the order in R.array.grocery_store_chains

        id = 1;
        // Albertsons
        Stores store = new Stores();
        store.setStore(id, 1, "Eastgate", "15100 SE 38TH ST", "STE 103", "BELLEVUE", "WA", "98006-1763");
        saveStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
        id++;

        // Fred Myer
        store = new Stores();
        store.setStore(id, 2, "Bellevue", "2041 148TH AVE NE", "", "BELLEVUE", "WA", "98007-3788");
        saveStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
        id++;
        store = new Stores();
        store.setStore(id, 2, "Redmond", "17667 NE 76TH ST", "", "REDMOND", "WA", "98052-4994");
        saveStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
        id++;

        // PCC
        store = new Stores();
        store.setStore(id, 4, "Issaquah", "1810 12TH AVE NW", "STE A", "ISSAQUAH", "WA", "98027-8110");
        saveStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
        id++;
        store = new Stores();
        store.setStore(id, 4, "Kirkland", "10718 NE 68TH ST", "", "KIRKLAND", "WA", "98033-7030");
        saveStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
        id++;

        // QFC
        store = new Stores();
        store.setStore(id, 5, "Bel-East", "1510 145TH PL SE", "STE A", "BELLEVUE", "WA", "98007-5593");
        saveStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
        id++;
        store = new Stores();
        store.setStore(id, 5, "Issaquah", "1540 NW GILMAN BLVD", "", "ISSAQUAH", "WA", "98027-5309");
        saveStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
        id++;
        store = new Stores();
        store.setStore(id, 5, "Klahanie Dr", "4570 KLAHANIE DR SE", "", "ISSAQUAH", "WA", "98029-5812");
        saveStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
        id++;
        store = new Stores();
        store.setStore(id, 5, "Factoria", "3500 FACTORIA BLVD SE", "", "BELLEVUE", "WA", "98006-5276");
        saveStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
        id++;
        store = new Stores();
        store.setStore(id, 5, "Newcastle", "6940 COAL CREEK PKWY SE", "", "NEWCASTLE", "WA", "98059-3137");
        saveStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
        id++;

        // Safeway
        store = new Stores();
        store.setStore(id, 6, "Factoria", "3903 FACTORIA BLVD SE", "", "BELLEVUE", "WA", "98006-6148");
        saveStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
        id++;
        store = new Stores();
        store.setStore(id, 6, "Evergreen Village", "1645 140TH AVE NE", "STE A5", "BELLEVUE", "WA", "98005-2320");
        saveStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
        id++;
        store = new Stores();
        store.setStore(id, 6, "Issaquah", "735 NW GILMAN BLVD", "STE B", "ISSAQUAH", "WA", "98027-8996");
        saveStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
        id++;
        store = new Stores();
        store.setStore(id, 6, "Highlands", "1451 HIGHLANDS DR NE", "", "ISSAQUAH", "WA", "98029-6240");
        saveStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
        id++;

        //Trader Joe’s
        store = new Stores();
        store.setStore(id, 8, "Issaquah", "975 NW GILMAN BLVD", "STE A", "ISSAQUAH", "WA", "98027-5377");
        saveStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
        id++;
        store = new Stores();
        store.setStore(id, 8, "Redmond", "15932 REDMOND WAY", "STE 101", "REDMOND", "WA", " 98052-4060");
        saveStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
        id++;
        store = new Stores();
        store.setStore(id, 8, "Bellevue", "15563 NE 24TH ST", "", "BELLEVUE", "WA", "98007-3836");
        saveStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
        id++;

        // Whole Foods
        store = new Stores();
        store.setStore(id, 9, "Bellevue", "888 116TH AVE NE", "", "BELLEVUE", "WA", "98004-4607");
        saveStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
        id++;
        store = new Stores();
        store.setStore(id, 9, "Redmond", "17991 REDMOND WAY", "", "REDMOND", "WA", " 98052-4907");
        saveStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
        id++;


        // upload initial items to Parse
        id = 1;
        String[] groceryItems = context.getResources().getStringArray(R.array.grocery_items);
        Initial_Items initialItem;
        for (String groceryItem : groceryItems) {
            initialItem = new Initial_Items();
            String[] item = groceryItem.split(", ");
            String itemName = item[0];
            String groupID = item[1];
            initialItem.setInitialItem(id, itemName, groupID, id);
            saveInitialItemToParse(initialItem, clsParseUtils.SAVE_THIS_THREAD);
            id++;
        }

    }


    private static void saveGroupToParse(Groups group, int saveType) {
        ParseACL groupACL = new ParseACL(ParseUser.getCurrentUser());
        groupACL.setPublicReadAccess(true);
        group.setACL(groupACL);
        try {
            switch (saveType) {
                case clsParseUtils.SAVE_THIS_THREAD:
                    // use SAVE_THIS_THREAD if this method is being called in an AsyncTask or background thread.
                    group.save();
                    MyLog.i("clsParseUtils", "saveGroupToParse: SAVE(): name = " + group.getGroupName());
                    break;

                case clsParseUtils.SAVE_IN_BACKGROUND:
                    group.saveInBackground();
                    MyLog.i("clsParseUtils", "saveGroupToParse: SAVE_IN_BACKGROUND(): name = " + group.getGroupName());
                    break;

                case clsParseUtils.SAVE_EVENTUALLY:
                    group.saveEventually();
                    MyLog.i("clsParseUtils", "saveGroupToParse: SAVE_EVENTUALLY(): name = " + group.getGroupName());
                    break;
            }

        } catch (ParseException e) {
            MyLog.e("clsParseUtils", "saveGroupToParse: ParseException: " + e.getMessage());
        }
    }


    private static void saveLocationToParse(Locations location, int saveType) {
        ParseACL locationACL = new ParseACL(ParseUser.getCurrentUser());
        locationACL.setPublicReadAccess(true);
        locationACL.setPublicWriteAccess(true);
        location.setACL(locationACL);
        try {
            switch (saveType) {
                case clsParseUtils.SAVE_THIS_THREAD:
                    // use SAVE_THIS_THREAD if this method is being called in an AsyncTask or background thread.
                    location.save();
                    MyLog.i("clsParseUtils", "saveLocationToParse: SAVE(): name = " + location.getLocationName());
                    break;

                case clsParseUtils.SAVE_IN_BACKGROUND:
                    location.saveInBackground();
                    MyLog.i("clsParseUtils", "saveLocationToParse: SAVE_IN_BACKGROUND(): name = " + location.getLocationName());
                    break;

                case clsParseUtils.SAVE_EVENTUALLY:
                    location.saveEventually();
                    MyLog.i("clsParseUtils", "saveLocationToParse: SAVE_EVENTUALLY(): name = " + location.getLocationName());
                    break;
            }

        } catch (ParseException e) {
            MyLog.e("clsParseUtils", "saveLocationToParse: ParseException: " + e.getMessage());
        }
    }

    private static void saveStoreChainToParse(StoreChains storeChain, int saveType) {
        ParseACL storeChainACL = new ParseACL(ParseUser.getCurrentUser());
        storeChainACL.setPublicReadAccess(true);
        storeChainACL.setPublicWriteAccess(true);
        storeChain.setACL(storeChainACL);
        try {
            switch (saveType) {
                case clsParseUtils.SAVE_THIS_THREAD:
                    // use SAVE_THIS_THREAD if this method is being called in an AsyncTask or background thread.
                    storeChain.save();
                    MyLog.i("clsParseUtils", "saveStoreChainToParse: SAVE(): name = " + storeChain.getStoreChainName());
                    break;

                case clsParseUtils.SAVE_IN_BACKGROUND:
                    storeChain.saveInBackground();
                    MyLog.i("clsParseUtils", "saveStoreChainToParse: SAVE_IN_BACKGROUND(): name = " + storeChain.getStoreChainName());
                    break;

                case clsParseUtils.SAVE_EVENTUALLY:
                    storeChain.saveEventually();
                    MyLog.i("clsParseUtils", "saveStoreChainToParse: SAVE_EVENTUALLY(): name = " + storeChain.getStoreChainName());
                    break;
            }

        } catch (ParseException e) {
            MyLog.e("clsParseUtils", "saveStoreChainToParse: ParseException: " + e.getMessage());
        }
    }

    private static void saveStoreToParse(Stores store, int saveType) {
        ParseACL storeACL = new ParseACL(ParseUser.getCurrentUser());
        storeACL.setPublicReadAccess(true);
        storeACL.setPublicWriteAccess(true);
        store.setACL(storeACL);
        try {
            switch (saveType) {
                case clsParseUtils.SAVE_THIS_THREAD:
                    // use SAVE_THIS_THREAD if this method is being called in an AsyncTask or background thread.
                    store.save();
                    MyLog.i("clsParseUtils", "saveStoreToParse: SAVE(): regional name = " + store.getStoreRegionalName());
                    break;

                case clsParseUtils.SAVE_IN_BACKGROUND:
                    store.saveInBackground();
                    MyLog.i("clsParseUtils", "saveStoreToParse: SAVE_IN_BACKGROUND(): regional name = " + store.getStoreRegionalName());
                    break;

                case clsParseUtils.SAVE_EVENTUALLY:
                    store.saveEventually();
                    MyLog.i("clsParseUtils", "saveStoreToParse: SAVE_EVENTUALLY(): regional name = " + store.getStoreRegionalName());
                    break;
            }

        } catch (ParseException e) {
            MyLog.e("clsParseUtils", "saveStoreToParse: ParseException: " + e.getMessage());
        }
    }

    private static void saveInitialItemToParse(Initial_Items initialItem, int saveType) {
        ParseACL initialItemsACL = new ParseACL(ParseUser.getCurrentUser());
        initialItemsACL.setPublicReadAccess(true);
        initialItem.setACL(initialItemsACL);
        try {
            switch (saveType) {
                case clsParseUtils.SAVE_THIS_THREAD:
                    // use SAVE_THIS_THREAD if this method is being called in an AsyncTask or background thread.
                    initialItem.save();
                    MyLog.i("clsParseUtils", "saveInitialItemToParse: SAVE(): name = " + initialItem.getItemName());
                    break;

                case clsParseUtils.SAVE_IN_BACKGROUND:
                    initialItem.saveInBackground();
                    MyLog.i("clsParseUtils", "saveInitialItemToParse: SAVE_IN_BACKGROUND(): name = " + initialItem.getItemName());
                    break;

                case clsParseUtils.SAVE_EVENTUALLY:
                    initialItem.saveEventually();
                    MyLog.i("clsParseUtils", "saveInitialItemToParse: SAVE_EVENTUALLY(): name = " + initialItem.getItemName());
                    break;
            }

        } catch (ParseException e) {
            MyLog.e("clsParseUtils", "saveInitialItemToParse: ParseException: " + e.getMessage());
        }
    }

    private static ArrayList<String> createInitialAisles(ArrayList<String> locations) {
        String aisleName;
        for (int i = 1; i < MySettings.INITIAL_NUMBER_OF_AISLES + 1; i++) {
            aisleName = "Aisle " + i;
            locations.add(aisleName);
        }
        return locations;
    }
    // ------------------------------------------------------------------------------------------------------

/*
    private static void createParseGroupsTable(Context context) {
        //MyLog.i("clsParseUtils", "createParseGroupsTable");
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
                // use SAVE_THIS_THREAD because this method is being run in an AsyncTask
                clsParseUtils.createPublicTable(groupsList, null, null, null, null, clsParseUtils.SAVE_THIS_THREAD);
            }
        }
    }

    private static void createParseLocationsTable(Context context) {
        //MyLog.i("clsParseUtils", "createParseLocationsTable");
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
                // use SAVE_THIS_THREAD because this method is being run in an AsyncTask
                clsParseUtils.createPublicTable(null, locationsList, null, null, null, clsParseUtils.SAVE_THIS_THREAD);
            }
        }
    }

    private static void createParseStoreChainsTable(Context context) {
        // MyLog.i("clsParseUtils", "createParseStoreChainsTable");
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
                // use SAVE_THIS_THREAD because this method is being run in an AsyncTask
                clsParseUtils.createPublicTable(null, null, storeChainsList, null, null, clsParseUtils.SAVE_THIS_THREAD);
            }
        }
    }

    private static void createParseStoresTable(Context context) {
        // MyLog.i("clsParseUtils", "createParseStoresTable");
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
                // use SAVE_THIS_THREAD because this method is being run in an AsyncTask
                clsParseUtils.createPublicTable(null, null, null, storeList, null, clsParseUtils.SAVE_THIS_THREAD);
            }
        }
    }

    private static void createParseItemsTable(Context context) {
        // MyLog.i("clsParseUtils", "createParseItemsTable");
        Cursor itemsCursor = ItemsTable.getAllItemsCursor(context, ItemsTable.SORT_ORDER_ITEM_NAME_DESC);
        if (itemsCursor != null && itemsCursor.getCount() > 0) {
            ArrayList<clsParseInitialItem> itemsList = new ArrayList<>();
            String itemName;
            long itemID, groupID, timestamp;
            int manualSortOrder;
            clsParseInitialItem item;
            while (itemsCursor.moveToNext()) {
                itemID = itemsCursor.getLong(itemsCursor.getColumnIndex(ItemsTable.COL_ITEM_ID));
                groupID = itemsCursor.getLong(itemsCursor.getColumnIndex(ItemsTable.COL_GROUP_ID));
                itemName = itemsCursor.getString(itemsCursor.getColumnIndex(ItemsTable.COL_ITEM_NAME));
                timestamp = itemsCursor.getLong(itemsCursor.getColumnIndex(ItemsTable.COL_ITEM_TIMESTAMP));
                manualSortOrder = itemsCursor.getInt(itemsCursor.getColumnIndex(ItemsTable.COL_MANUAL_SORT_ORDER));
                item = new clsParseInitialItem(itemID, itemName, groupID, manualSortOrder, timestamp);
                itemsList.add(item);
            }
            if (itemsList.size() > 0) {
                // use SAVE_THIS_THREAD because this method is being run in an AsyncTask
                clsParseUtils.createPublicTable(null, null, null, null, itemsList, clsParseUtils.SAVE_THIS_THREAD);
            }
        }
    }

    public static void uploadNewItemsToParse(Context context, int saveType) {
        // get all dirty items
        Cursor itemCursor = ItemsTable.getAllDirtyItemsCursor(context);
        if (itemCursor != null && itemCursor.getCount() > 0) {
            Items item;
            String itemName = "";
            while (itemCursor.moveToNext()) {
                try {
                    item = new Items();
                    item.setItemCursor(itemCursor);
                    itemName = item.getItemName();
                    switch (saveType) {

                        case clsParseUtils.SAVE_THIS_THREAD:
                            // use SAVE_THIS_THREAD if this method is being called in an AsyncTask or background thread.
                            item.save();
                            MyLog.i("clsParseUtils", "uploadNewItemsToParse: SAVE(): itemName = " + itemName);
                            break;

                        case clsParseUtils.SAVE_IN_BACKGROUND:
                            item.saveInBackground();
                            MyLog.i("clsParseUtils", "uploadNewItemsToParse: SAVE_IN_BACKGROUND(): itemName = " + itemName);
                            break;

                        case clsParseUtils.SAVE_EVENTUALLY:
                            item.saveEventually();
                            MyLog.i("clsParseUtils", "uploadNewItemsToParse: SAVE_EVENTUALLY(): itemName = " + itemName);
                            break;
                    }
                } catch (ParseException e) {
                    MyLog.e("clsParseUtils", "uploadNewItemsToParse: : itemName = " + itemName + ": ParseException: " + e.getMessage());

                }
            }
            ItemsTable.resetAllDirtyItems(context);

        }
    }


    public static void uploadInitialItemsToParse(Context context, int saveType) {
        // get all dirty items
        Cursor itemCursor = ItemsTable.getAllItemsCursor(context, ItemsTable.SORT_ORDER_ITEM_NAME_DESC);
        if (itemCursor != null && itemCursor.getCount() > 0) {
            Initial_Items item;
            String itemName = "";
            // Set ACL for public read access
            ParseACL initialItemsACL = new ParseACL(ParseUser.getCurrentUser());
            initialItemsACL.setPublicReadAccess(true);
            while (itemCursor.moveToNext()) {
                try {
                    item = new Initial_Items();
                    item.setInitialItemCursor(itemCursor);
                    item.setACL(initialItemsACL);
                    itemName = item.getItemName();
                    switch (saveType) {

                        case clsParseUtils.SAVE_THIS_THREAD:
                            // use SAVE_THIS_THREAD if this method is being called in an AsyncTask or background thread.
                            item.save();
                            MyLog.i("clsParseUtils", "uploadInitialItemsToParse: SAVE(): itemName = " + itemName);
                            break;

                        case clsParseUtils.SAVE_IN_BACKGROUND:
                            item.saveInBackground();
                            MyLog.i("clsParseUtils", "uploadInitialItemsToParse: SAVE_IN_BACKGROUND(): itemName = " + itemName);
                            break;

                        case clsParseUtils.SAVE_EVENTUALLY:
                            item.saveEventually();
                            MyLog.i("clsParseUtils", "uploadInitialItemsToParse: SAVE_EVENTUALLY(): itemName = " + itemName);
                            break;
                    }
                } catch (ParseException e) {
                    MyLog.e("clsParseUtils", "uploadInitialItemsToParse: : itemName = " + itemName + ": ParseException: " + e.getMessage());

                }
            }
            //ItemsTable.resetAllDirtyItems(context);

        }
    }*/


//endregion

}
