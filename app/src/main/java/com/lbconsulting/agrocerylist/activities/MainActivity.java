package com.lbconsulting.agrocerylist.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.barcodescanner.ScannerFragmentActivity;
import com.lbconsulting.agrocerylist.classes.MyEvents;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;
import com.lbconsulting.agrocerylist.database.ItemsTable;
import com.lbconsulting.agrocerylist.database.SelectedItemsTable;
import com.lbconsulting.agrocerylist.database.StoreChainsTable;
import com.lbconsulting.agrocerylist.database.StoresTable;
import com.lbconsulting.agrocerylist.database.aGroceryListDatabaseHelper;
import com.lbconsulting.agrocerylist.fragments.fragHome;
import com.lbconsulting.agrocerylist.fragments.fragProductsList;

import de.greenrobot.event.EventBus;


public class MainActivity extends Activity {

    private ActionBar mActionBar;

    public static final String NOT_AVAILABLE = "Name N/A: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("MainActivity", "onCreate");
        setContentView(R.layout.activity_main);

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
        StoresTable.createNewStore(this, 6, "Sammamish Highlands");

        StoresTable.createNewStore(this, 8, "Bellevue");
        StoresTable.createNewStore(this, 8, "Redmond");
        StoresTable.createNewStore(this, 8, "Issaquah");

        StoresTable.createNewStore(this, 9, "Bellevue");
        StoresTable.createNewStore(this, 9, "Redmond");

        String[] groceryItems = getResources().getStringArray(R.array.grocery_items);
        for (String groceryItem : groceryItems) {
            ItemsTable.createNewItem(this, groceryItem);
        }

        long storeIndex = 1;
        long itemIndex = 1;
        for (String item : groceryItems) {
            SelectedItemsTable.newSelectedItem(this, storeIndex, itemIndex);
            itemIndex++;
            storeIndex++;
            if (storeIndex > 18) {
                storeIndex = 1;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("MainActivity", "onSaveInstanceState");
        // save activity variables
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

        switch (fragmentID) {
            case MySettings.FRAG_HOME:
                fm.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container,
                                fragHome.newInstance(), "FRAG_HOME")
                        .commit();
                MyLog.i("MainActivity", "showFragment: FRAG_HOME");
                break;

            case MySettings.FRAG_PRODUCTS_LIST:
                fm.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container,
                                fragProductsList.newInstance(), "FRAG_PRODUCTS_LIST")
                        .commit();
                MyLog.i("MainActivity", "showFragment: FRAG_PRODUCTS_LIST");
                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog.i("MainActivity", "onResume");

        // show the appropriate fragment
        showFragment(MySettings.getActiveFragmentID());
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        MyLog.i("MainActivity", "onRestoreInstanceState");
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MyLog.i("MainActivity", "onPrepareOptionsMenu");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MyLog.i("MainActivity", "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_scan_barcodes) {
            launchScannerActivity();
            return true;
        } else if (id == R.id.action_show_products) {
            showFragment(MySettings.FRAG_PRODUCTS_LIST);
            return true;
        }

        return super.onOptionsItemSelected(item);
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
}
