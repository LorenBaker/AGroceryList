package com.lbconsulting.agrocerylist.classes_parse;

import android.content.Context;
import android.location.Location;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;
import com.lbconsulting.agrocerylist.database.GroupsTable;
import com.lbconsulting.agrocerylist.database.StoreChainsTable;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class contains helper methods used with Parse
 */
public class clsParseUtils {

    public static final int SAVE_THIS_THREAD = 1;
    public static final int SAVE_IN_BACKGROUND = 2;
    public static final int SAVE_EVENTUALLY = 3;


/*    public static void createParseStoreMap(ArrayList<clsParseStoreMapEntry> storeMapEntryList,
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
    }*/

/*    private static void createPublicTable(ArrayList<clsParseGroup> groupsList, ArrayList<clsParseLocation> locationsList,
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

    }*/

    //region Load Initial Data To Parse
    public static void loadInitialDataToParse(Context context) {

        // upload initial grocery groups to Parse
        String[] groceryGroups = context.getResources().getStringArray(R.array.grocery_groups);
        long sortKey = 1;
        Groups group;
        for (String groceryGroup : groceryGroups) {
            group = new Groups();
            group.setGroup(groceryGroup, sortKey);
            Groups.saveGroupToParse(group, clsParseUtils.SAVE_THIS_THREAD);
            sortKey++;
        }

        // upload initial store locations to Parse
        String[] initialLocations = context.getResources().getStringArray(R.array.initial_location_list);
        ArrayList<String> groupLocations = new ArrayList<>();
        for (String initialLocation : initialLocations) {
            groupLocations.add(initialLocation);
        }
        groupLocations = createInitialAisles(groupLocations);
        sortKey = 1;
        Locations location;
        for (String groupLocation : groupLocations) {
            location = new Locations();
            location.setLocation(groupLocation, sortKey);
            Locations.saveLocationToParse(location, clsParseUtils.SAVE_THIS_THREAD);
            sortKey++;
        }


        // upload initial store chains to Parse
        String[] storeChains = context.getResources().getStringArray(R.array.grocery_store_chains);
        sortKey = 1;
        StoreChains storeChain;
        for (String storeChainName : storeChains) {
            storeChain = new StoreChains();
            storeChain.setStoreChain(storeChainName, sortKey);
            StoreChains.saveStoreChainToParse(storeChain, clsParseUtils.SAVE_THIS_THREAD);
            sortKey++;
        }


        // create stores ...
        // NOTE: requires store groups, locations, and chains be already created.
        // also assumes store chain IDs are in the order in R.array.grocery_store_chains

        ParseQuery groupsQuery = Groups.getQuery();
        groupsQuery.orderByAscending(GroupsTable.COL_SORT_KEY);
        List<ParseObject> groups;
        try {
            groups = groupsQuery.find();
        } catch (ParseException e) {
            MyLog.e("clsParseUtils", "loadInitialDataToParse: ParseException: " + e.getMessage());
        }

        ParseQuery storeChainsQuery = StoreChains.getQuery();
        storeChainsQuery.orderByAscending(StoreChainsTable.COL_SORT_KEY);
        List<ParseObject> storeChainsList = null;
        try {
            storeChainsList = storeChainsQuery.find();
            if(storeChainsList!=null){
                MyLog.i("clsParseUtils", "loadInitialDataToParse: Found " +storeChainsList.size() + " store chains.");
            }else{
                MyLog.e("clsParseUtils", "loadInitialDataToParse: Did not find any store chains.");
            }
        } catch (ParseException e) {
            MyLog.e("clsParseUtils", "loadInitialDataToParse: ParseException: " + e.getMessage());
        }

        if (storeChainsList != null && storeChainsList.size() > 0) {
            //id = 1;
            // Albertsons
            Stores store = new Stores();
            ParseObject storeChainObject = storeChainsList.get(0);
            MyLog.i("clsParseUtils", "loadInitialDataToParse: Creating stores with chainID = " +storeChainObject.getObjectId());
            store.setStore(storeChainObject, "Eastgate", "15100 SE 38TH ST", "STE 103", "BELLEVUE", "WA", "98006-1763");
            saveNewStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
            //id++;

            // Fred Myer
            store = new Stores();
            storeChainObject = storeChainsList.get(1);
            MyLog.i("clsParseUtils", "loadInitialDataToParse: Creating stores with chainID = " +storeChainObject.getObjectId());
            store.setStore(storeChainObject, "Bellevue", "2041 148TH AVE NE", "", "BELLEVUE", "WA", "98007-3788");
            saveNewStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
            // id++;
            store = new Stores();
            store.setStore(storeChainObject, "Redmond", "17667 NE 76TH ST", "", "REDMOND", "WA", "98052-4994");
            saveNewStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
            // id++;

            // PCC
            store = new Stores();
            storeChainObject = storeChainsList.get(3);
            MyLog.i("clsParseUtils", "loadInitialDataToParse: Creating stores with chainID = " +storeChainObject.getObjectId());
            store.setStore(storeChainObject, "Issaquah", "1810 12TH AVE NW", "STE A", "ISSAQUAH", "WA", "98027-8110");
            saveNewStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
            //  id++;
            store = new Stores();
            store.setStore(storeChainObject, "Kirkland", "10718 NE 68TH ST", "", "KIRKLAND", "WA", "98033-7030");
            saveNewStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
            // id++;

            // QFC
            store = new Stores();
            storeChainObject = storeChainsList.get(4);
            MyLog.i("clsParseUtils", "loadInitialDataToParse: Creating stores with chainID = " +storeChainObject.getObjectId());
            store.setStore(storeChainObject, "Bel-East", "1510 145TH PL SE", "STE A", "BELLEVUE", "WA", "98007-5593");
            saveNewStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
            //  id++;
            store = new Stores();
            store.setStore(storeChainObject, "Issaquah", "1540 NW GILMAN BLVD", "", "ISSAQUAH", "WA", "98027-5309");
            saveNewStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
            //  id++;
            store = new Stores();
            store.setStore(storeChainObject, "Klahanie Dr", "4570 KLAHANIE DR SE", "", "ISSAQUAH", "WA", "98029-5812");
            saveNewStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
            // id++;
            store = new Stores();
            store.setStore(storeChainObject, "Factoria", "3500 FACTORIA BLVD SE", "", "BELLEVUE", "WA", "98006-5276");
            saveNewStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
            // id++;
            store = new Stores();
            store.setStore(storeChainObject, "Newcastle", "6940 COAL CREEK PKWY SE", "", "NEWCASTLE", "WA", "98059-3137");
            saveNewStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
            //  id++;

            // Safeway
            store = new Stores();
            storeChainObject = storeChainsList.get(5);
            MyLog.i("clsParseUtils", "loadInitialDataToParse: Creating stores with chainID = " +storeChainObject.getObjectId());
            store.setStore(storeChainObject, "Factoria", "3903 FACTORIA BLVD SE", "", "BELLEVUE", "WA", "98006-6148");
            saveNewStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
            //   id++;
            store = new Stores();
            store.setStore(storeChainObject, "Evergreen Village", "1645 140TH AVE NE", "STE A5", "BELLEVUE", "WA", "98005-2320");
            saveNewStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
            //  id++;
            store = new Stores();
            store.setStore(storeChainObject, "Issaquah", "735 NW GILMAN BLVD", "STE B", "ISSAQUAH", "WA", "98027-8996");
            saveNewStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
            //  id++;
            store = new Stores();
            store.setStore(storeChainObject, "Highlands", "1451 HIGHLANDS DR NE", "", "ISSAQUAH", "WA", "98029-6240");
            saveNewStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
            //   id++;

            //Trader Joe’s
            store = new Stores();
            storeChainObject = storeChainsList.get(7);
            MyLog.i("clsParseUtils", "loadInitialDataToParse: Creating stores with chainID = " +storeChainObject.getObjectId());
            store.setStore(storeChainObject, "Issaquah", "975 NW GILMAN BLVD", "STE A", "ISSAQUAH", "WA", "98027-5377");
            saveNewStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
            // id++;
            store = new Stores();
            store.setStore(storeChainObject, "Redmond", "15932 REDMOND WAY", "STE 101", "REDMOND", "WA", " 98052-4060");
            saveNewStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
            //  id++;
            store = new Stores();
            store.setStore(storeChainObject, "Bellevue", "15563 NE 24TH ST", "", "BELLEVUE", "WA", "98007-3836");
            saveNewStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
            //  id++;

            // Whole Foods
            store = new Stores();
            storeChainObject = storeChainsList.get(8);
            MyLog.i("clsParseUtils", "loadInitialDataToParse: Creating stores with chainID = " +storeChainObject.getObjectId());
            store.setStore(storeChainObject, "Bellevue", "888 116TH AVE NE", "", "BELLEVUE", "WA", "98004-4607");
            saveNewStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
            //  id++;
            store = new Stores();
            store.setStore(storeChainObject, "Redmond", "17991 REDMOND WAY", "", "REDMOND", "WA", " 98052-4907");
            saveNewStoreToParse(store, clsParseUtils.SAVE_THIS_THREAD);
            // id++;
        }

        // upload initial items to Parse
        List<Groups> groupsList=null;
        try {
            ParseQuery<Groups> groupsParseQuery = Groups.getQuery();
            groupsParseQuery.addAscendingOrder(GroupsTable.COL_SORT_KEY);
            groupsList = groupsParseQuery.find();
            MyLog.i("clsParseUtils", "loadInitialDataToParse: Found " + groupsList.size() + " groups.");
        } catch (ParseException e) {
            MyLog.e("clsParseUtils", "loadInitialDataToParse: ParseException: " + e.getMessage());
            return;
        }

        sortKey = 1;
        String[] groceryItems = context.getResources().getStringArray(R.array.grocery_items);
        Initial_Items initialItem;
        for (String groceryItem : groceryItems) {
            initialItem = new Initial_Items();
            String[] item = groceryItem.split(", ");
            String itemName = item[0];
            String groupIdStringNumber = item[1];
            int groupIdInt = Integer.parseInt(groupIdStringNumber);
            groupIdInt--;
            ParseObject groupObject = groupsList.get(groupIdInt);
            initialItem.setInitialItem(itemName, groupObject, sortKey);
            saveInitialItemToParse(initialItem, clsParseUtils.SAVE_THIS_THREAD);
            sortKey++;
        }

    }


    public static void saveNewStoreToParse(final Stores store, int saveType) {
        ParseACL storeACL = new ParseACL(ParseUser.getCurrentUser());
        storeACL.setPublicReadAccess(true);
        storeACL.setPublicWriteAccess(true);
        store.setACL(storeACL);

/*        final HashMap<String, Long> params = new HashMap<String, Long>();
        params.put("storeID", store.getStoreID());*/

        try {
            switch (saveType) {
                case clsParseUtils.SAVE_THIS_THREAD:
                    // use SAVE_THIS_THREAD if this method is being called in an AsyncTask or background thread.
                    store.save();
                    MyLog.i("clsParseUtils", "saveNewStoreToParse: SAVE(): regional name = " + store.getStoreRegionalName());
                    break;

                case clsParseUtils.SAVE_IN_BACKGROUND:
                    store.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                MyLog.i("clsParseUtils", "saveNewStoreToParse: SAVE_IN_BACKGROUND(): regional name = " + store.getStoreRegionalName());

                            } else {
                                String errMessSimple = e.getMessage();
                                MyLog.i("clsParseUtils", "saveNewStoreToParse: SAVE_IN_BACKGROUND() FAILED: regional name = " + store.getStoreRegionalName());
                            }
                        }
                    });
                    break;

                case clsParseUtils.SAVE_EVENTUALLY:
                    store.saveEventually();
                    MyLog.i("clsParseUtils", "saveNewStoreToParse: SAVE_EVENTUALLY(): regional name = " + store.getStoreRegionalName());
                    break;
            }


        } catch (ParseException e) {
            MyLog.e("clsParseUtils", "saveNewStoreToParse: ParseException: " + e.getMessage());
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

//endregion

/*    public static void syncWithParse(Context context, Location location) {
        // Call this from a background thread
*//*  Over view of sync process:
        1) get data from Parse that is updated after last sync
        2) update the SQLite database if the database row is not dirty
        3) update Parse with dirty database rows
*//*
        // sync the data tables
        // Note: groups may not be updated the user

*//*        syncGroups(context);
        syncItems(context);
        syncLocations(context);
        syncStoreChains(context);
        syncStoreMaps(context);*//*
        syncStores(context, location);
    }*/

/*    private static void syncGroups(Context context) {
        //get data from Parse that has been updated since the last sync
        try {
            Date dateLastUpdated = MySettings.getUpdatedAt(MySettings.UPDATED_AT_GROUPS);
            Date newLastUpdateDate = dateLastUpdated;
            ParseQuery query = Groups.getQuery();
            query.whereGreaterThan("updatedAt", dateLastUpdated);
            List<ParseObject> results = query.find();
            if (results != null && results.size() > 0) {
                for (ParseObject obj : results) {
                    if (obj.getUpdatedAt().compareTo(newLastUpdateDate) > 0) {
                        newLastUpdateDate = obj.getUpdatedAt();
                        if (GroupsTable.groupExists(context, obj.getLong("groupID"))) {
                            // update the SQLite database if the database row is not dirty
                            if (!GroupsTable.isGroupDirty(context, obj.getLong("groupID"))) {
                                // the group is not dirty... so update the existing group
                                GroupsTable.updateGroup(context, obj);
                            }

                        } else {
                            // the group does not exist ... so create it.
                            GroupsTable.createNewGroup(context, obj);
                        }
                    }
                    MySettings.setUpdatedAt(MySettings.UPDATED_AT_GROUPS, newLastUpdateDate);
                }
            }
        } catch (ParseException e) {
            MyLog.e("clsParseUtils", "syncGroups: ParseException: " + e.getMessage());
            e.printStackTrace();
        }

        // Note: Parse groups may not be updated the user

    }*/



    private static void syncStores(Context context, Location location) {
        //get nearby Stores from Parse
        try {
            if (location == null) {
                try {
                    MyLog.i("clsParseUtils", "syncStores: Sleeping.");
                    ;
                    Thread.sleep(5000); // 5 seconds
                    location = MySettings.getLastLocation();
                } catch (InterruptedException e) {
                    MyLog.e("clsParseUtils", "syncStores: InterruptedException: " + e.getMessage());
                    return;
                }
            }

            if (location == null) {
                MyLog.e("clsParseUtils", "syncStores: location == null");
                return;
            }
            MyLog.i("clsParseUtils", "syncStores: start nearby store query.");
            ParseGeoPoint userLocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());

            ParseQuery nearbyStoresQuery = Stores.getQuery();
            nearbyStoresQuery.whereNear("location", userLocation);
            nearbyStoresQuery.setLimit(MySettings.getClosestStoreNumber());
            List<ParseObject> nearbyStores = nearbyStoresQuery.find();
            MyLog.i("clsParseUtils", "syncStores. Found " + nearbyStores.size() + " nearby stores.");

        } catch (ParseException e) {
            MyLog.e("clsParseUtils", "syncStores: ParseException" + e.getMessage());
        }

        //get data from Parse that has been updated since the last sync

    }


}
