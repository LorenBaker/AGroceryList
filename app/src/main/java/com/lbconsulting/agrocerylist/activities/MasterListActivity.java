package com.lbconsulting.agrocerylist.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.adapters.StoresSpinnerCursorAdapter;
import com.lbconsulting.agrocerylist.classes.MyEvents;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;
import com.lbconsulting.agrocerylist.database.StoresTable;
import com.lbconsulting.agrocerylist.fragments.fragMasterList;
import com.lbconsulting.agrocerylist.fragments.fragProductsList;

import de.greenrobot.event.EventBus;


public class MasterListActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    private ActionBar mActionBar;

    public static final String NOT_AVAILABLE = "Name N/A: ";
    public static final int STORE_LOADER = 2;

    private long mActiveStoreID;

    public long getActiveStoreID() {
        return mActiveStoreID;
    }

    private LoaderManager mLoaderManager = null;
    private LoaderManager.LoaderCallbacks<Cursor> mStoreCallbacks;
    private StoresSpinnerCursorAdapter mStoresSpinnerCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("MasterListActivity", "onCreate");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_master_list);

        MySettings.setContext(this);
        EventBus.getDefault().register(this);

        if (savedInstanceState != null) {
            // set activity variables
            if (savedInstanceState.containsKey(MySettings.SETTING_ACTIVE_STORE_ID)) {
                mActiveStoreID = savedInstanceState.getLong(MySettings.SETTING_ACTIVE_STORE_ID);
            }
        } else {
            // set default activity variables
            mActiveStoreID = MySettings.getActiveStoreID();
        }
        mActionBar = getActionBar();
        mActionBar.setDisplayShowTitleEnabled(false);
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        mStoresSpinnerCursorAdapter = new StoresSpinnerCursorAdapter(this, null, 0);
        mActionBar.setListNavigationCallbacks(mStoresSpinnerCursorAdapter, new ActionBar.OnNavigationListener() {

            @Override
            public boolean onNavigationItemSelected(int position, long storeID) {
                mActiveStoreID = storeID;
                EventBus.getDefault().post(new MyEvents.onActiveStoreChange(storeID));
                return true;
            }
        });

        mStoreCallbacks = this;
        mLoaderManager = getLoaderManager();
        mLoaderManager.initLoader(STORE_LOADER, null, mStoreCallbacks);

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("MasterListActivity", "onSaveInstanceState");
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


    private void showFragment(int fragmentID) {

        FragmentManager fm = getFragmentManager();

        switch (fragmentID) {


            case MySettings.FRAG_PRODUCTS_LIST:
                fm.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container,
                                fragProductsList.newInstance(), "FRAG_PRODUCTS_LIST")
                        .commit();
                MyLog.i("MasterListActivity", "showFragment: FRAG_PRODUCTS_LIST");
                break;

            case MySettings.FRAG_CULL_ITEMS:
                break;

            case MySettings.FRAG_SET_GROUPS:
                break;

            default:
            case MySettings.FRAG_MASTER_LIST:
                fm.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container,
                                fragMasterList.newInstance(mActiveStoreID), "FRAG_MASTER_LIST")
                        .commit();
                MyLog.i("MasterListActivity", "showFragment: FRAG_MASTER_LIST");
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
        MySettings.setActiveStoreID(mActiveStoreID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog.i("MasterListActivity", "onResume");
        mActiveStoreID = MySettings.getActiveStoreID();
        // show the appropriate fragment
        showFragment(MySettings.getActiveFragmentID());
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        MyLog.i("MasterListActivity", "onRestoreInstanceState");
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MyLog.i("MasterListActivity", "onPrepareOptionsMenu");
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MyLog.i("MasterListActivity", "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_master_list_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {

            case R.id.action_settings:
                Toast.makeText(this, "action_settings", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_about:
                Toast.makeText(this, "action_about", Toast.LENGTH_SHORT).show();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i("MasterListActivity", "onDestroy");
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        switch (id) {
            case STORE_LOADER:
                MyLog.i("MasterListActivity", "onCreateLoader: STORE_LOADER");
                cursorLoader = StoresTable.getAllStoresWithChainNames(this);
                break;


        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
        int id = loader.getId();
        // The asynchronous load is complete and the newCursor is now available for use.
        // Update the mStoresSpinnerCursorAdapter to show the changed data.


        switch (loader.getId()) {
            case STORE_LOADER:
                MyLog.i("MasterListActivity", "onLoadFinished: STORE_LOADER");
                int position = findActiveStorePosition(newCursor, mActiveStoreID);
                mStoresSpinnerCursorAdapter.swapCursor(newCursor);
                mActionBar.setSelectedNavigationItem(position);
                break;

            default:
                break;
        }
    }

    private int findActiveStorePosition(Cursor cursor, long activeStoreID) {

        int storePosition = -1;
        if (cursor != null && cursor.getCount() > 0) {
            if (activeStoreID < 1) {
                return 0;
            }
            long storeID;
            cursor.moveToPosition(-1);
            while (cursor.moveToNext()) {
                storePosition++;
                storeID = cursor.getLong(cursor.getColumnIndex(StoresTable.COL_STORE_ID));
                if (storeID == activeStoreID) {
                    return storePosition;
                }
            }
            // store not found!
            storePosition = -1;
        }

        return storePosition;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case STORE_LOADER:
                MyLog.i("MasterListActivity", "onLoaderReset: STORE_LOADER");
                mStoresSpinnerCursorAdapter.swapCursor(null);
                break;

            default:
                break;
        }
    }
}
