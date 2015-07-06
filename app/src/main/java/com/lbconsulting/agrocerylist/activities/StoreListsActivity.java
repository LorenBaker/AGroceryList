package com.lbconsulting.agrocerylist.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.adapters.StoreListPagerAdapter;
import com.lbconsulting.agrocerylist.classes.MyEvents;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;
import com.lbconsulting.agrocerylist.database.GroupsTable;
import com.lbconsulting.agrocerylist.database.ItemsTable;
import com.lbconsulting.agrocerylist.database.LocationsTable;
import com.lbconsulting.agrocerylist.database.StoreChainsTable;
import com.lbconsulting.agrocerylist.database.StoresTable;
import com.lbconsulting.agrocerylist.database.aGroceryListDatabaseHelper;
import com.lbconsulting.agrocerylist.dialogs.dialog_SelectLocation;
import com.lbconsulting.agrocerylist.dialogs.dialog_edit_item;
import com.lbconsulting.agrocerylist.dialogs.sortListDialog;

import de.greenrobot.event.EventBus;


public class StoreListsActivity extends Activity {

    private ActionBar mActionBar;

    public static final String NOT_AVAILABLE = "Name N/A: ";

    private StoreListPagerAdapter mListsPagerAdapter;
    private ViewPager mStoreListPager;
    private LinearLayout mProgressBar;
    private TextView tvProgressMessage;

    private static long mActiveStoreID;

    public static long getActiveStoreID() {
        return mActiveStoreID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("StoreListsActivity", "onCreate");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_store_lists);
        mStoreListPager = (ViewPager) findViewById(R.id.storeListPager);
        mProgressBar = (LinearLayout) findViewById(R.id.llProgressBar);
        tvProgressMessage = (TextView) findViewById(R.id.tvProgressMessage);

        MySettings.setContext(this);
        EventBus.getDefault().register(this);
        mActionBar = getActionBar();
        MySettings.setActiveFragmentID(MySettings.HOME_FRAG_STORE_LIST);

        if (savedInstanceState != null) {
            // set activity variables
        } else {
            // set default activity variables
        }

        if (!aGroceryListDatabaseHelper.databaseExists()) {
            new LoadInitialDataAsync().execute();
        }
    }

    private void loadInitialData() {

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
        }




/*        long groupID = 0;
        long locationID = 2;
        long numberOfLocations = LocationsTable.getNumberOfLocations(this);
        Cursor storeCursor = StoresTable.getAllStoresCursor(this, StoresTable.SORT_ORDER_MANUAL);
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("StoreListsActivity", "onSaveInstanceState: ActiveStoreID = " + mActiveStoreID);
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

        FragmentManager fm = getFragmentManager();

/*        switch (fragmentID) {
            case MySettings.HOME_FRAG_STORE_LIST:*/
/*                fm.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container,
                                fragStoreList.newInstance(), "HOME_FRAG_STORE_LIST")
                        .commit();
                MyLog.i("StoreListsActivity", "showFragment: HOME_FRAG_STORE_LIST");*/

        mListsPagerAdapter = new StoreListPagerAdapter(getFragmentManager(), this);

        mStoreListPager.setAdapter(mListsPagerAdapter);
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
                MyLog.i("StoreListsActivity", "onPageSelected: position=" + position);
                mActiveStoreID = StoreListPagerAdapter.getStoreID(position);
            }
        });

        int pagerPosition = StoreListPagerAdapter.findStoreIDPosition(mActiveStoreID);
        mStoreListPager.setCurrentItem(pagerPosition);

/*                break;

            case MySettings.FRAG_PRODUCTS_LIST:
                fm.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container,
                                fragProductsList.newInstance(), "FRAG_PRODUCTS_LIST")
                        .commit();
                MyLog.i("StoreListsActivity", "showFragment: FRAG_PRODUCTS_LIST");
                break;

        }*/
    }

    private void showSortDialog() {
        FragmentManager fm = getFragmentManager();
        sortListDialog dialog = sortListDialog.newInstance(MySettings.HOME_FRAG_STORE_LIST);
        dialog.show(fm, "dialog_sort_list");
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        super.onPause();
        MyLog.i("StoreListsActivity", "onPause: ActiveStoreID = " + mActiveStoreID);
        MySettings.setActiveStoreID(mActiveStoreID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog.i("StoreListsActivity", "onResume: ActiveStoreID = " + mActiveStoreID);

        mActiveStoreID = MySettings.getActiveStoreID();
        // show the appropriate fragment
        showFragment(MySettings.getActiveFragmentID());
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mActiveStoreID = savedInstanceState.getLong(MySettings.SETTING_ACTIVE_STORE_ID);
        }
        MyLog.i("StoreListsActivity", "onRestoreInstanceState: ActiveStoreID = " + mActiveStoreID);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MyLog.i("StoreListsActivity", "onPrepareOptionsMenu");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MyLog.i("StoreListsActivity", "onCreateOptionsMenu");
        getMenuInflater().inflate(R.menu.menu_store_lists_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_removeStruckOffItems:
                //Toast.makeText(this, "action_removeStruckOffItems", Toast.LENGTH_SHORT).show();
                ItemsTable.removeStruckOffItems(this);
                //EventBus.getDefault().post(new MyEvents.restartLoader(MySettings.ITEMS_LOADER));
                break;

            case R.id.action_addItem:
                Intent intent = new Intent(this, MasterListActivity.class);
                startActivity(intent);
                break;

            case R.id.action_remove_all_items:
                //Toast.makeText(this, "action_remove_all_items", Toast.LENGTH_SHORT).show();
                ItemsTable.removeAllItems(this);
                //EventBus.getDefault().post(new MyEvents.restartLoader(MySettings.ITEMS_LOADER));
                break;

            case R.id.action_show_sort_dialog:
                showSortDialog();
                break;

            case R.id.action_new_store:
                Toast.makeText(this, "action_new_store", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_edit_store:
                Toast.makeText(this, "action_edit_store", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_settings:
                Toast.makeText(this, "action_settings", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_about:
                Toast.makeText(this, "action_about", Toast.LENGTH_SHORT).show();
                break;

        }
        return true;

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i("StoreListsActivity", "onDestroy");
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


    public class LoadInitialDataAsync extends AsyncTask<Void, Void, Void> {
        public LoadInitialDataAsync() {
            super();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvProgressMessage.setText("Please wait while loading initial data...");
            mProgressBar.setVisibility(View.VISIBLE);
            mStoreListPager.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            loadInitialData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            showFragment(MySettings.HOME_FRAG_STORE_LIST);
            mStoreListPager.setVisibility(View.VISIBLE);
            mProgressBar.setVisibility(View.GONE);
        }
    }
}