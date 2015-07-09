package com.lbconsulting.agrocerylist.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.adapters.DrawerArrayAdapter;
import com.lbconsulting.agrocerylist.adapters.StoreListPagerAdapter;
import com.lbconsulting.agrocerylist.classes.MyEvents;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;
import com.lbconsulting.agrocerylist.classes_parse.clsParseGroup;
import com.lbconsulting.agrocerylist.classes_parse.clsParseLocation;
import com.lbconsulting.agrocerylist.classes_parse.clsParseStore;
import com.lbconsulting.agrocerylist.classes_parse.clsParseStoreChain;
import com.lbconsulting.agrocerylist.classes_parse.clsParseUtils;
import com.lbconsulting.agrocerylist.database.GroupsTable;
import com.lbconsulting.agrocerylist.database.ItemsTable;
import com.lbconsulting.agrocerylist.database.LocationsTable;
import com.lbconsulting.agrocerylist.database.StoreChainsTable;
import com.lbconsulting.agrocerylist.database.StoresTable;
import com.lbconsulting.agrocerylist.database.aGroceryListDatabaseHelper;
import com.lbconsulting.agrocerylist.dialogs.dialog_SelectLocation;
import com.lbconsulting.agrocerylist.dialogs.dialog_edit_item;
import com.lbconsulting.agrocerylist.dialogs.sortListDialog;
import com.lbconsulting.agrocerylist.fragments.fragItemsByGroup;
import com.lbconsulting.agrocerylist.fragments.fragMasterList;
import com.lbconsulting.agrocerylist.fragments.fragProductsList;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;


public class MainActivity extends Activity implements DrawerLayout.DrawerListener {

    private ActionBar mActionBar;

    public static final String NOT_AVAILABLE = "Name N/A: ";

    private LinearLayout mProgressBar;
    private TextView tvProgressMessage;
    private FrameLayout mFragmentContainer;
    private ViewPager mStoreListPager;
    private StoreListPagerAdapter mStoreListPagerAdapter;

    private String[] mDrawerItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    private static long mActiveStoreID;
    private boolean mInitializingData;

    public static long getActiveStoreID() {
        return mActiveStoreID;
    }

    private int mActiveFragmentID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("MainActivity", "onCreate");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        mInitializingData = false;
        mDrawerItemTitles = getResources().getStringArray(R.array.navigation_drawer_titles);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view

        DrawerArrayAdapter adapter = new DrawerArrayAdapter(this, mDrawerItemTitles);
        mDrawerList.setAdapter(adapter);
       /* mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mDrawerItemTitles));*/
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mProgressBar = (LinearLayout) findViewById(R.id.llProgressBar);
        tvProgressMessage = (TextView) findViewById(R.id.tvProgressMessage);
        mFragmentContainer = (FrameLayout) findViewById(R.id.fragment_container);
        mStoreListPager = (ViewPager) findViewById(R.id.storeListPager);

        MySettings.setContext(this);
        EventBus.getDefault().register(this);
        mActionBar = getActionBar();
        //MySettings.setActiveFragmentID(MySettings.FRAG_STORE_LISTS);
        mActiveFragmentID = MySettings.getActiveFragmentID();

        if (savedInstanceState != null) {
            // set activity variables
            mActiveStoreID = savedInstanceState.getLong(MySettings.SETTING_ACTIVE_STORE_ID);

        } else {
            // set default activity variables
        }


/*        //region Parse setup
        // Initialize Crash Reporting.
        ParseCrashReporting.enable(this);

        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Add your initialization code here
        ParseObject.registerSubclass(StoreMaps.class);
        ParseObject.registerSubclass(PublicTablesData.class);
        Parse.initialize(this, "Z1uTyZFcvSsV74AdrqbfWPe44WhqtTvwmJupITew", "ZuBh1PV8oBebw2xgpURpdF5XDms5zS11QpYW9Kpn");
        MyLog.i("AGroceryListApplication", "onCreate: initialized");

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        //user's data is only accessible by the user itself unless explicit permission is given
        ParseACL.setDefaultACL(defaultACL, true);
        ParseACL.setDefaultACL(new ParseACL(), true);
        //endregion*/


        if (!aGroceryListDatabaseHelper.databaseExists()) {
            new LoadInitialDataAsync().execute();
        }
    }


    private void runParseTest() {
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
        MyLog.i("MainActivity", "runParseTest: put(\"foo\", \"bar\")");
    }

    private void loadInitialData() {

        // initialize the grocery groups
        String[] groceryGroups = getResources().getStringArray(R.array.grocery_groups);
        for (String groceryGroup : groceryGroups) {
            GroupsTable.createNewGroup(this, groceryGroup);
        }
        // create Parse groups table
        createParseGroupsTable();


        // initialize the store locations
        LocationsTable.createInitialLocationsIfNeeded(this);
        // create Parse locations table
        createParseLocationsTable();


        // create store chains
        String[] storeChains = getResources().getStringArray(R.array.grocery_store_chains);
        for (String store : storeChains) {
            StoreChainsTable.createNewStoreChain(this, store);
        }
        // create Parse store chain table
        createParseStoreChainsTable();


        // create stores ... NOTE: assumes store chains and groups are already created.
        // also assumes store chain IDs are in the order in R.array.grocery_store_chains

        // Albertsons
        StoresTable.createNewStore(this, 1, "Eastgate", "15100 SE 38TH ST", "STE 103", "BELLEVUE", "WA", "98006-1763");

        // Fred Myer
        StoresTable.createNewStore(this, 2, "Bellevue", "2041 148TH AVE NE", "", "BELLEVUE", "WA", "98007-3788");
        StoresTable.createNewStore(this, 2, "Redmond", "17667 NE 76TH ST", "", "REDMOND", "WA", "98052-4994");

        // PCC
        StoresTable.createNewStore(this, 4, "Issaquah", "1810 12TH AVE NW", "STE A", "ISSAQUAH", "WA", "98027-8110");
        StoresTable.createNewStore(this, 4, "Kirkland", "10718 NE 68TH ST", "", "KIRKLAND", "WA", "98033-7030");

        // QFC
        StoresTable.createNewStore(this, 5, "Bel-East", "1510 145TH PL SE", "STE A", "BELLEVUE", "WA", "98007-5593");
        StoresTable.createNewStore(this, 5, "Issaquah", "1540 NW GILMAN BLVD", "", "ISSAQUAH", "WA", "98027-5309");
        StoresTable.createNewStore(this, 5, "Klahanie Dr", "4570 KLAHANIE DR SE", "", "ISSAQUAH", "WA", "98029-5812");
        StoresTable.createNewStore(this, 5, "Factoria", "3500 FACTORIA BLVD SE", "", "BELLEVUE", "WA", "98006-5276");
        StoresTable.createNewStore(this, 5, "Newcastle", "6940 COAL CREEK PKWY SE", "", "NEWCASTLE", "WA", "98059-3137");

        // Safeway
        StoresTable.createNewStore(this, 6, "Factoria", "3903 FACTORIA BLVD SE", "", "BELLEVUE", "WA", "98006-6148");
        StoresTable.createNewStore(this, 6, "Evergreen Village", "1645 140TH AVE NE", "STE A5", "BELLEVUE", "WA", "98005-2320");
        StoresTable.createNewStore(this, 6, "Issaquah", "735 NW GILMAN BLVD", "STE B", "ISSAQUAH", "WA", "98027-8996");
        StoresTable.createNewStore(this, 6, "Highlands", "1451 HIGHLANDS DR NE", "", "ISSAQUAH", "WA", "98029-6240");

        //Trader Joe’s
        StoresTable.createNewStore(this, 8, "Issaquah", "975 NW GILMAN BLVD", "STE A", "ISSAQUAH", "WA", "98027-5377");
        StoresTable.createNewStore(this, 8, "Redmond", "15932 REDMOND WAY", "STE 101", "REDMOND", "WA", " 98052-4060");
        StoresTable.createNewStore(this, 8, "Bellevue", "15563 NE 24TH ST", "", "BELLEVUE", "WA", "98007-3836");

        // Whole Foods
        StoresTable.createNewStore(this, 9, "Bellevue", "888 116TH AVE NE", "", "BELLEVUE", "WA", "98004-4607");
        StoresTable.createNewStore(this, 9, "Redmond", "17991 REDMOND WAY", "", "REDMOND", "WA", " 98052-4907");

        // create Parse stores table
        createParseStoresTable();

        // create initial items.
        String[] groceryItems = getResources().getStringArray(R.array.grocery_items);
        for (String groceryItem : groceryItems) {
            String[] item = groceryItem.split(", ");
            String itemName = item[0];
            String groupID = item[1];
            ItemsTable.createNewItem(this, itemName, groupID);
        }


        //region LoadInitialData Backup
/*    private void loadInitialData() {

        // initialize the grocery groups
        String[] groceryGroups = getResources().getStringArray(R.array.grocery_groups);
        for (String groceryGroup : groceryGroups) {
            GroupsTable.createNewGroup(this, groceryGroup);
        }

        // initialize the store locations
        LocationsTable.createInitialLocationsIfNeeded(this);

        // create store chains
        String[] storeChains = getResources().getStringArray(R.array.grocery_store_chains);
        for (String store : storeChains) {
            StoreChainsTable.createNewStoreChain(this, store);
        }

        // create stores ... NOTE: assumes store chains and groups are already created.
        // also assumes store chain IDs are in the order in R.array.grocery_store_chains
        StoresTable.createNewStore(this, 1, "Eastgate");

        StoresTable.createNewStore(this, 2, "Bellevue");
        StoresTable.createNewStore(this, 2, "Redmond");

        StoresTable.createNewStore(this, 4, "Issaquah");
        StoresTable.createNewStore(this, 4, "Kirkland");
        StoresTable.createNewStore(this, 4, "Seattle");

        StoresTable.createNewStore(this, 5, "Issaquah");
        StoresTable.createNewStore(this, 5, "Factoria");
        StoresTable.createNewStore(this, 5, "Newcastle");

        StoresTable.createNewStore(this, 6, "Factoria");
        StoresTable.createNewStore(this, 6, "Evergreen Village");
        StoresTable.createNewStore(this, 6, "Issaquah");
        StoresTable.createNewStore(this, 6, "Highlands");

        StoresTable.createNewStore(this, 8, "Bellevue");
        StoresTable.createNewStore(this, 8, "Redmond");
        StoresTable.createNewStore(this, 8, "Issaquah");

        StoresTable.createNewStore(this, 9, "Bellevue");
        StoresTable.createNewStore(this, 9, "Redmond");


        // create initial items.
        String[] groceryItems = getResources().getStringArray(R.array.grocery_items);
        for (String groceryItem : groceryItems) {
            String[] item = groceryItem.split(", ");
            String itemName = item[0];
            String groupID = item[1];
            ItemsTable.createNewItem(this, itemName, groupID);
        }*/
        //endregion




/*        long groupID = 0;
        long locationID = 2;
        long numberOfLocations = LocationsTable.getNumberOfLocations(this);
        Cursor storeCursor = StoresTable.getAllDisplayedStoresCursor(this, StoresTable.SORT_ORDER_MANUAL);
        if (storeCursor != null && storeCursor.getCount() > 0) {
            aGroceryListContentProvider.setSuppressChangeNotification(true);
            long storeID;
            while (storeCursor.moveToNext()) {
                storeID = storeCursor.getLong(storeCursor.getColumnIndex(StoresTable.COL_STORE_ID));
                for (String groceryGroup : groceryGroups) {
                    groupID++;
                    if (groupID > 1) {
                        StoreMapsTable.createNewStoreMapEntry(this, -1, groupID, storeID, locationID);

                        locationID++;
                        if (locationID > numberOfLocations) {
                            locationID = 2;
                        }
                    }
                }
                groupID=0;
            }
            aGroceryListContentProvider.setSuppressChangeNotification(false);
        }*/


    }


    private void createParseGroupsTable() {
        MyLog.i("MainActivity", "createParseGroupsTable");
        Cursor groupsCursor = GroupsTable.getAllGroupsCursor(this, GroupsTable.SORT_ORDER_GROUP_NAME);
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
                clsParseUtils.createPublicTable(groupsList, null, null, null, clsParseUtils.saveThisThread);
            }
        }
    }

    private void createParseLocationsTable() {
        MyLog.i("MainActivity", "createParseLocationsTable");
        Cursor locationsCursor = LocationsTable.getAllLocationsCursor(this, LocationsTable.SORT_ORDER_LOCATION_ID);
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
                clsParseUtils.createPublicTable(null, locationsList, null, null, clsParseUtils.saveThisThread);
            }
        }
    }

    private void createParseStoreChainsTable() {
        MyLog.i("MainActivity", "createParseStoreChainsTable");
        Cursor storeChainsCursor = StoreChainsTable.getAllStoreChainsCursor(this, StoreChainsTable.SORT_ORDER_STORE_CHAIN_NAME);
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
                clsParseUtils.createPublicTable(null, null, storeChainsList, null, clsParseUtils.saveThisThread);
            }
        }
    }

    private void createParseStoresTable() {
        MyLog.i("MainActivity", "createParseStoresTable");
        Cursor storesCursor = StoresTable.getAllStoresCursor(this, StoresTable.SORT_ORDER_CHAIN_ID_BY_REGIONAL_NAME);
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

                storeList.add(store);
            }
            if (storeList.size() > 0) {
                // use saveThisThread because this method is being run in an AsyncTask
                clsParseUtils.createPublicTable(null, null, null, storeList, clsParseUtils.saveThisThread);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("MainActivity", "onSaveInstanceState: ActiveStoreID = " + mActiveStoreID);
        // save activity variables
        outState.putLong(MySettings.SETTING_ACTIVE_STORE_ID, mActiveStoreID);
    }

    public void onEvent(MyEvents.toggleItemStrikeOut event) {
        ItemsTable.toggleStrikeOut(this, event.getItemID());
        //EventBus.getDefault().post(new MyEvents.restartLoader(MySettings.ITEMS_LOADER));
    }

    public void onEvent(MyEvents.showEditItemDialog event) {
        showEditItemDialog(event.getItemID());
    }

    private void showEditItemDialog(long itemID) {
        FragmentManager fm = getFragmentManager();
        dialog_edit_item dialog = dialog_edit_item.newInstance(itemID, getString(R.string.edit_item_dialog_title));
        dialog.show(fm, "dialog_edit_item");
    }

    public void onEvent(MyEvents.showSelectGroupLocationDialog event) {
        showSelectGroupLocationDialog(event.getItemID(), event.getGroupID(), event.getLocationID(), event.getStoreID());
    }

    private void showSelectGroupLocationDialog(long itemID, long groupID, long locationID, long storeID) {
        FragmentManager fm = getFragmentManager();
        dialog_SelectLocation dialog = dialog_SelectLocation.newInstance(itemID, groupID, locationID, storeID);
        dialog.show(fm, "dialog_SelectLocation");
    }

    //region onEvents
    public void onEvent(MyEvents.setActionBarTitle event) {
        setActionBarTitle(event.getTitle());
    }

    public void onEvent(MyEvents.showOkDialog event) {
        showOkDialog(this, event.getTitle(), event.getMessage());
    }

    public void onEvent(MyEvents.showToast event) {
        Toast.makeText(this, event.getMessage(), Toast.LENGTH_SHORT).show();
    }
    //endregion


    private void showFragment(int fragmentID) {

        mActiveFragmentID = fragmentID;
        FragmentManager fm = getFragmentManager();

        switch (fragmentID) {
            case MySettings.FRAG_STORE_LISTS:
                MySettings.setActiveFragmentID(MySettings.FRAG_STORE_LISTS);
                displayListPagerAdapter();

                mStoreListPagerAdapter = new StoreListPagerAdapter(getFragmentManager(), this);
                mStoreListPager.setAdapter(mStoreListPagerAdapter);
                mStoreListPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                    @Override
                    public void onPageScrollStateChanged(int state) {
                    }

                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    }

                    @Override
                    public void onPageSelected(int position) {
                        // A list page has been selected
                        MyLog.i("MainActivity", "onPageSelected: position=" + position);
                        mActiveStoreID = StoreListPagerAdapter.getStoreID(position);
                    }
                });

                int pagerPosition = StoreListPagerAdapter.findStoreIDPosition(mActiveStoreID);
                mStoreListPager.setCurrentItem(pagerPosition);

                break;
            case MySettings.FRAG_MASTER_LIST:
                displayFragmentContainer();
                fm.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container,
                                fragMasterList.newInstance(),
                                MySettings.getFragmentTag(MySettings.FRAG_MASTER_LIST))
                        .commit();
                MyLog.i("MainActivity", "showFragment: " + MySettings.getFragmentTag(MySettings.FRAG_MASTER_LIST));
                break;

            case MySettings.FRAG_PRODUCTS_LIST:
                displayFragmentContainer();
                fm.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container,
                                fragProductsList.newInstance(),
                                MySettings.getFragmentTag(MySettings.FRAG_PRODUCTS_LIST))
                        .commit();
                MyLog.i("MainActivity", "showFragment: " + MySettings.getFragmentTag(MySettings.FRAG_PRODUCTS_LIST));
                break;

            case MySettings.FRAG_ITEMS_BY_GROUP:
                displayFragmentContainer();
                fm.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container,
                                fragItemsByGroup.newInstance(),
                                MySettings.getFragmentTag(MySettings.FRAG_ITEMS_BY_GROUP))
                        .commit();
                MyLog.i("MainActivity", "showFragment: " + MySettings.getFragmentTag(MySettings.FRAG_ITEMS_BY_GROUP));
                break;

            case MySettings.FRAG_CULL_ITEMS:
                displayFragmentContainer();
                break;

            case MySettings.FRAG_SET_GROUPS:
                displayFragmentContainer();
                break;

            default:


        }
    }

    private void displayProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);
        mStoreListPager.setVisibility(View.GONE);
        mFragmentContainer.setVisibility(View.GONE);
        MyLog.d("MainActivity", "displayProgressBar");
    }

    private void displayListPagerAdapter() {
        mStoreListPager.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mFragmentContainer.setVisibility(View.GONE);
        // remove the visible fragment from mFragmentContainer so
        // that the fragment transition is smooth the next time a fragment is added
        removeFragmentFromContainer();
        MyLog.d("MainActivity", "displayListPagerAdapter");
    }

    private void removeFragmentFromContainer() {
        FragmentManager fm = getFragmentManager();
        Fragment visibleFragment;
        // TODO: Verify the starting and ending fragment tags
        String fragmentTag;
        for (int i = 2; i < 8; i++) {
            fragmentTag = MySettings.getFragmentTag(i);
            visibleFragment = fm.findFragmentByTag(fragmentTag);
            if (visibleFragment != null) {
                fm.beginTransaction().remove(visibleFragment).commit();
                // we've found and removed the visible fragment that is in mFragmentContainer
                // so break out of the for loop
                break;
            }
        }

    }

    private void displayFragmentContainer() {
        mFragmentContainer.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.GONE);
        mStoreListPager.setVisibility(View.GONE);
        MyLog.d("MainActivity", "displayFragmentContainer");
    }

    private void showSortDialog() {
        FragmentManager fm = getFragmentManager();
        sortListDialog dialog = sortListDialog.newInstance(MySettings.FRAG_STORE_LISTS);
        dialog.show(fm, "dialog_sort_list");
    }


    @Override
    public void onBackPressed() {

        switch (MySettings.getActiveFragmentID()) {
            case MySettings.FRAG_STORE_LISTS:
                super.onBackPressed();
                break;

            default:
                showFragment(MySettings.FRAG_STORE_LISTS);
                break;
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        MyLog.i("MainActivity", "onPause: ActiveStoreID = " + mActiveStoreID);
        MySettings.setActiveStoreID(mActiveStoreID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mActiveStoreID = MySettings.getActiveStoreID();
        MyLog.i("MainActivity", "onResume: ActiveStoreID = " + mActiveStoreID);

        // show the appropriate fragment
        if (!mInitializingData) {
            showFragment(MySettings.getActiveFragmentID());
        }
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mActiveStoreID = savedInstanceState.getLong(MySettings.SETTING_ACTIVE_STORE_ID);
        }
        MyLog.i("MainActivity", "onRestoreInstanceState: ActiveStoreID = " + mActiveStoreID);
    }


/*    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MyLog.i("MainActivity", "onPrepareOptionsMenu");



        return super.onPrepareOptionsMenu(menu);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MyLog.i("MainActivity", "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);

        MenuItem action_removeStruckOffItems = menu.findItem(R.id.action_removeStruckOffItems);
        MenuItem action_addItem = menu.findItem(R.id.action_addItem);
        MenuItem action_remove_all_items = menu.findItem(R.id.action_remove_all_items);
        MenuItem action_show_sort_dialog = menu.findItem(R.id.action_show_sort_store_items_dialog);
        MenuItem action_new_store = menu.findItem(R.id.action_new_store);
        MenuItem action_edit_store = menu.findItem(R.id.action_edit_store);

        switch (mActiveFragmentID) {
            case MySettings.FRAG_STORE_LISTS:
                action_removeStruckOffItems.setVisible(true);
                action_addItem.setVisible(true);
                action_remove_all_items.setVisible(true);
                action_show_sort_dialog.setVisible(true);
                action_new_store.setVisible(true);
                action_edit_store.setVisible(true);
                getActionBar().setDisplayHomeAsUpEnabled(false);
                break;

            default:
                action_removeStruckOffItems.setVisible(false);
                action_addItem.setVisible(false);
                action_remove_all_items.setVisible(false);
                action_show_sort_dialog.setVisible(false);
                action_new_store.setVisible(false);
                action_edit_store.setVisible(false);
                getActionBar().setDisplayHomeAsUpEnabled(true);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            // TODO: Remove run parse test
            case R.id.action_run_parse_test:
                runParseTest();
                return true;


            case R.id.action_removeStruckOffItems:
                ItemsTable.removeStruckOffItems(this);
                return true;

            case R.id.action_addItem:
                showFragment(MySettings.FRAG_MASTER_LIST);
                return true;

            case R.id.action_remove_all_items:
                ItemsTable.removeAllItems(this);
                return true;

            case R.id.action_show_sort_store_items_dialog:
                showSortDialog();
                return true;

            case R.id.action_new_store:
                Toast.makeText(this, "action_new_store", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_edit_store:
                Toast.makeText(this, "action_edit_store", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_settings:
                Toast.makeText(this, "action_settings", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_about:
                Toast.makeText(this, "action_about", Toast.LENGTH_SHORT).show();
                return true;

            case android.R.id.home:
                showFragment(MySettings.FRAG_STORE_LISTS);
                invalidateOptionsMenu();
                return true;

            case R.id.action_log_out:
                ParseUser.logOut();

                // FLAG_ACTIVITY_CLEAR_TASK only works on API 11, so if the user
                // logs out on older devices, we'll just exit.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    Intent intent = new Intent(MainActivity.this, SampleDispatchActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                            | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    finish();
                }
                return true;

            default:
                return false;

        }


    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i("MainActivity", "onDestroy");
        EventBus.getDefault().unregister(this);

    }

    private static void showOkDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set dialog title and message
        alertDialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void setActionBarTitle(String title) {
        mActionBar.setTitle(title);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        MyLog.i("MainActivity", "onDrawerSlide");
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        MyLog.i("MainActivity", "onDrawerOpened");
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        MyLog.i("MainActivity", "onDrawerClosed");
    }

    @Override
    public void onDrawerStateChanged(int newState) {
        MyLog.i("MainActivity", "onDrawerStateChanged");
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            MyLog.i("DrawerItemClickListener", "onItemClick: position = " + position);
            selectItem(position);
        }
    }

    private void selectItem(int position) {

        switch (position) {
            case 0:
                // Store Lists
                showFragment(MySettings.FRAG_STORE_LISTS);
                break;

            case 1:
                // Master Items List
                showFragment(MySettings.FRAG_MASTER_LIST);
                break;

            case 2:
                // Store Item PublicTablesData
                showFragment(MySettings.FRAG_ITEMS_BY_GROUP);
                break;
        }

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        //setTitle(mPlanetTitles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    public class LoadInitialDataAsync extends AsyncTask<Void, Void, Void> {
        public LoadInitialDataAsync() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mInitializingData = true;
            tvProgressMessage.setText("Please wait while loading initial data...");
            displayProgressBar();
            String temp = "";
        }

        @Override
        protected Void doInBackground(Void... params) {
            loadInitialData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mInitializingData = false;
            showFragment(MySettings.FRAG_STORE_LISTS);
        }
    }
}