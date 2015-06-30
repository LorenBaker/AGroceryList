package com.lbconsulting.agrocerylist.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.adapters.StoreListPagerAdapter;
import com.lbconsulting.agrocerylist.barcodescanner.ScannerFragmentActivity;
import com.lbconsulting.agrocerylist.classes.MyEvents;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;
import com.lbconsulting.agrocerylist.database.GroupsTable;
import com.lbconsulting.agrocerylist.database.ItemsTable;
import com.lbconsulting.agrocerylist.database.SelectedItemsTable;
import com.lbconsulting.agrocerylist.database.StoreChainsTable;
import com.lbconsulting.agrocerylist.database.StoresTable;
import com.lbconsulting.agrocerylist.database.aGroceryListDatabaseHelper;
import com.lbconsulting.agrocerylist.dialogs.dialogSortStoreList;

import de.greenrobot.event.EventBus;


public class StoreListsActivity extends Activity {

    private ActionBar mActionBar;

    public static final String NOT_AVAILABLE = "Name N/A: ";

    private StoreListPagerAdapter mListsPagerAdapter;
    private ViewPager mPager;


    private long mActiveStoreID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("StoreListsActivity", "onCreate");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_store_lists);

        MySettings.setContext(this);
        EventBus.getDefault().register(this);
        mActionBar = getActionBar();

        if (savedInstanceState != null) {
            // set activity variables
        } else {
            // set default activity variables
        }

        if (!aGroceryListDatabaseHelper.databaseExists()) {
            loadInitialData();
        }
    }

    private void loadInitialData() {
        String[] storeChains = getResources().getStringArray(R.array.grocery_store_chains);
        for (String store : storeChains) {
            StoreChainsTable.createNewStoreChain(this, store);
        }

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

        String[] groceryItems = getResources().getStringArray(R.array.grocery_items);
        for (String groceryItem : groceryItems) {
            ItemsTable.createNewItem(this, groceryItem);
        }

        String[] groceryGroups = getResources().getStringArray(R.array.grocery_groups);
        for (String groceryGroup : groceryGroups) {
            GroupsTable.createNewGroup(this, groceryGroup);
        }


        long groupIndex = 1;
        long storeIndex = 1;
        long itemIndex = 1;
        for (String item : groceryItems) {
            SelectedItemsTable.addItemToStore(this, storeIndex, itemIndex);
            ItemsTable.putItemInGroup(this, itemIndex, groupIndex);
            itemIndex++;
            storeIndex++;
            if (storeIndex > 18) {
                storeIndex = 1;
            }
            groupIndex++;
            if (groupIndex > groceryGroups.length) {
                groupIndex = 1;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("StoreListsActivity", "onSaveInstanceState: ActiveStoreID = " + mActiveStoreID);
        // save activity variables
        outState.putLong(MySettings.SETTING_ACTIVE_STORE_ID, mActiveStoreID);
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

    public void launchScannerActivity() {
        Intent intent = new Intent(this, ScannerFragmentActivity.class);
        startActivity(intent);
    }

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
        mPager = (ViewPager) findViewById(R.id.storeListPager);
        mPager.setAdapter(mListsPagerAdapter);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

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
        mPager.setCurrentItem(pagerPosition);

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
        dialogSortStoreList dialog = dialogSortStoreList.newInstance(mActiveStoreID,
                StoresTable.getStoreItemsSortingOrder(this, mActiveStoreID));
        dialog.show(fm, "dialog_sort_store_list");
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
                ItemsTable.removeStruckOffItems(this, mActiveStoreID);
                EventBus.getDefault().post(new MyEvents.restartLoader(MySettings.ITEMS_LOADER));
                break;

            case R.id.action_addItem:
                Intent intent = new Intent(this, MasterListActivity.class);
                startActivity(intent);
                break;

            case R.id.action_remove_all_items:
                //Toast.makeText(this, "action_remove_all_items", Toast.LENGTH_SHORT).show();
                SelectedItemsTable.removeAllStoreItems(this, mActiveStoreID);
                EventBus.getDefault().post(new MyEvents.restartLoader(MySettings.ITEMS_LOADER));
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

            case R.id.action_set_store_favorites:
                Toast.makeText(this, "action_set_store_favorites", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_scan_barcodes:
                launchScannerActivity();
                break;

            case R.id.action_show_products:
                showFragment(MySettings.FRAG_PRODUCTS_LIST);
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
}
