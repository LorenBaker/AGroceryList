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
import com.lbconsulting.agrocerylist.classes_parse.clsParseUtils;
import com.lbconsulting.agrocerylist.database.ItemsTable;
import com.lbconsulting.agrocerylist.database.aGroceryListDatabaseHelper;
import com.lbconsulting.agrocerylist.dialogs.dialog_SelectLocation;
import com.lbconsulting.agrocerylist.dialogs.dialog_edit_item;
import com.lbconsulting.agrocerylist.dialogs.sortListDialog;
import com.lbconsulting.agrocerylist.fragments.fragItemsByGroup;
import com.lbconsulting.agrocerylist.fragments.fragMasterList;
import com.lbconsulting.agrocerylist.fragments.fragProductsList;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.HashMap;

import de.greenrobot.event.EventBus;


public class MainActivity extends Activity implements DrawerLayout.DrawerListener {

    // TODO: Remove mLoadInitialDataToParse
    private boolean mLoadInitialDataToParse = true;

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
        ParseObject.registerSubclass(ParseStoreMap.class);
        ParseObject.registerSubclass(PublicTablesData.class);
        Parse.initialize(this, "Z1uTyZFcvSsV74AdrqbfWPe44WhqtTvwmJupITew", "ZuBh1PV8oBebw2xgpURpdF5XDms5zS11QpYW9Kpn");
        MyLog.i("AGroceryListApplication", "onCreate: initialized");

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();
        //user's data is only accessible by the user itself unless explicit permission is given
        ParseACL.setDefaultACL(defaultACL, true);
        ParseACL.setDefaultACL(new ParseACL(), true);
        //endregion*/

        if (ParseUser.getCurrentUser().isNew()) {
           new LoadInitialDataAsync(this).execute();
        } else if (!aGroceryListDatabaseHelper.databaseExists()) {
            // TODO: remove databaseExists if statement
           new LoadInitialDataAsync(this).execute();
        }
    }

    private void downLoadInitialData() {

    }

/*    private void downLoadInitialData() {
        try {
            // get all the tables from Parse
            ParseQuery<PublicTablesData> publicTablesQuery = ParseQuery.getQuery(PublicTablesData.class);
            List<PublicTablesData> publicTablesResult = publicTablesQuery.find();
            if (publicTablesResult != null) {
                MyLog.i("MainActivity", "downLoadInitialData: Number of tables found = " + publicTablesResult.size());
                if (publicTablesResult.size() > 0) {
                    for (PublicTablesData table : publicTablesResult) {
                        if (table.getTableName().equals(GroupsTable.TABLE_GROUPS)) {
                            fillGroupsTable(this, table.getJsonContent());
                        } else if (table.getTableName().equals(LocationsTable.TABLE_LOCATIONS)) {
                            fillLocationsTable(this, table.getJsonContent());
                        } else if (table.getTableName().equals(StoreChainsTable.TABLE_STORE_CHAINS)) {
                            fillStoreChainsTable(this, table.getJsonContent());
                        } else if (table.getTableName().equals(StoresTable.TABLE_STORES)) {
                            fillStoresTable(this, table.getJsonContent());
                        } else if (table.getTableName().equals(ItemsTable.TABLE_ITEMS)) {
                            fillItemsTable(this, table.getJsonContent());
                        }
                    }
                }
            }

            ParseQuery<ParseStoreMap> storesMapQuery = ParseQuery.getQuery(ParseStoreMap.class);
            List<ParseStoreMap> storesMapResults = storesMapQuery.find();
            if (storesMapResults != null) {
                MyLog.i("MainActivity", "downLoadInitialData: Number of store maps found = " + storesMapResults.size());
                if (storesMapResults.size() > 0) {
                    for (ParseStoreMap storeMap : storesMapResults) {
                        StoreMapsTable.updateStoreMap(this, storeMap);
                    }
                }
            }

        } catch (ParseException e) {
            MyLog.e("MainActivity", "downLoadInitialData: ParseException: " + e.getMessage());
        }

    }*/


/*    private void fillGroupsTable(Context context, String jsonContent) {
        MyLog.i("MainActivity", "fillGroupsTable");
        Gson gson = new Gson();
        GroupsTable.clear(context);
        clsParseGroupArray content = gson.fromJson(jsonContent, clsParseGroupArray.class);
        for (clsParseGroup group : content.getGroups()) {
            GroupsTable.createNewGroup(context, group);
        }
    }

    private void fillLocationsTable(Context context, String jsonContent) {
        MyLog.i("MainActivity", "fillLocationsTable");
        Gson gson = new Gson();
        LocationsTable.clear(context);
        clsParseLocationArray content = gson.fromJson(jsonContent, clsParseLocationArray.class);
        for (clsParseLocation location : content.getLocations()) {
            LocationsTable.createNewLocation(context, location);
        }
    }

    private void fillStoreChainsTable(Context context, String jsonContent) {
        MyLog.i("MainActivity", "fillStoreChainsTable");
        Gson gson = new Gson();
        StoreChainsTable.clear(context);
        clsParseStoreChainArray content = gson.fromJson(jsonContent, clsParseStoreChainArray.class);
        for (clsParseStoreChain storeChain : content.getStoreChains()) {
            StoreChainsTable.createNewStoreChain(context, storeChain);
        }
    }

    private void fillStoresTable(Context context, String jsonContent) {
        MyLog.i("MainActivity", "fillStoresTable");
        Gson gson = new Gson();
        StoresTable.clear(context);
        clsParseStoreArray content = gson.fromJson(jsonContent, clsParseStoreArray.class);
        for (clsParseStore store : content.getStores()) {
            StoresTable.createNewStore(context, store);
        }
    }

    private void fillItemsTable(Context context, String jsonContent) {
        MyLog.i("MainActivity", "fillItemsTable");
        Gson gson = new Gson();
        ItemsTable.clear(context);
        clsParseItemArray content = gson.fromJson(jsonContent, clsParseItemArray.class);
        for (clsParseInitialItem item : content.getInitialItems()) {
            ItemsTable.createNewItem(context, item);
        }
    }*/

    private void runParseTest() {
        // TODO: remove runParseTest
        ParseCloud.callFunctionInBackground("initializeNewUser", new HashMap<String, Object>(), new FunctionCallback<Object>() {
            public void done(Object result, ParseException e) {
                if (e == null) {
                    int length = (int)result;
                    showOkDialog(MainActivity.this,"Parse Test", "Number of initial items = " + length);

                } else{
                    showOkDialog(MainActivity.this, "ERROR: Parse Test", "Error: " + e.getLocalizedMessage());
                }
            }
        });
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
        Context mContext;

        public LoadInitialDataAsync(Context context) {
            super();
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mInitializingData = true;
            tvProgressMessage.setText("Please wait while loading initial data from the cloud ...");
            displayProgressBar();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO: remove cls
            if (mLoadInitialDataToParse) {
                // load initial data TO Parse
                clsParseUtils.loadInitialDataToParse(mContext);
               // clsParseUtils.uploadInitialItemsToParse(mContext,clsParseUtils.SAVE_THIS_THREAD);
            } else {
                // load initial data FROM Parse
                //aGroceryListDatabaseHelper database = new aGroceryListDatabaseHelper(mContext);
                aGroceryListDatabaseHelper.resetDatabase();
                downLoadInitialData();
                //clsParseUtils.uploadNewItemsToParse(mContext, clsParseUtils.SAVE_THIS_THREAD);
            }


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