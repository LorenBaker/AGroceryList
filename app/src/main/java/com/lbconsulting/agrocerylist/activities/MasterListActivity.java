package com.lbconsulting.agrocerylist.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.classes.MyEvents;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;
import com.lbconsulting.agrocerylist.fragments.fragItemsByGroup;
import com.lbconsulting.agrocerylist.fragments.fragMasterList;
import com.lbconsulting.agrocerylist.fragments.fragProductsList;

import de.greenrobot.event.EventBus;


public class MasterListActivity extends Activity {

    private ActionBar mActionBar;

    private long mActiveStoreID;

    public long getActiveStoreID() {
        return mActiveStoreID;
    }

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

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("MasterListActivity", "onSaveInstanceState");
        // save activity variables
        outState.putLong(MySettings.SETTING_ACTIVE_STORE_ID, mActiveStoreID);
    }

    //region onEvents
    public void onEvent(MyEvents.setActionBarTitle event) {
        setActionBarTitle(event.getTitle());
    }

    public void onEvent(MyEvents.showFragment event){
        showFragment(event.getFragmentID());
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

            case MySettings.FRAG_ITEMS_BY_GROUP:
                fm.beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .replace(R.id.fragment_container,
                                fragItemsByGroup.newInstance(), "FRAG_ITEMS_BY_GROUP")
                        .commit();
                MyLog.i("MasterListActivity", "showFragment: FRAG_ITEMS_BY_GROUP");
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
                                fragMasterList.newInstance(), "FRAG_MASTER_LIST")
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
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        MyLog.i("MasterListActivity", "onRestoreInstanceState");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MyLog.i("MasterListActivity", "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_master_list_activity, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MyLog.i("MasterListActivity", "onPrepareOptionsMenu");
        return super.onPrepareOptionsMenu(menu);
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


}
