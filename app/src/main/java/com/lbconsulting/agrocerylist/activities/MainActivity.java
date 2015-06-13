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
import com.lbconsulting.agrocerylist.fragments.fragHome;

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

        if(savedInstanceState != null) {
            // set activity variables
        }else{
            // set default activity variables
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
        MyLog.i("MainActivity", "onResume");
        super.onResume();
        showFragment(MySettings.FRAG_HOME);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        MyLog.i("MainActivity", "onRestoreInstanceState");
        super.onRestoreInstanceState(savedInstanceState);
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
        } else if(id == R.id.action_scan_barcodes) {
            launchScannerActivity();
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
