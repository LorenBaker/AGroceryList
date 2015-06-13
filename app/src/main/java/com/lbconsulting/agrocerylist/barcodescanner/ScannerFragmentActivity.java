package com.lbconsulting.agrocerylist.barcodescanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.database.StoreItemLocationsTable;

import java.util.ArrayList;

//public class ScannerFragmentActivity extends ActionBarActivity {
public class ScannerFragmentActivity extends Activity {

    private Spinner spnLocation;
    private FrameLayout scanner_fragment_container;

    private long mItemLocationID=-1;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        MyLog.i("ScannerFragmentActivity", "onCreate");
        setContentView(R.layout.activity_scanner_fragment);

        spnLocation = (Spinner) findViewById(R.id.spnItemLocation);
        scanner_fragment_container = (FrameLayout) findViewById(R.id.scanner_fragment_container);

        ArrayList<String> itemLocationsList = StoreItemLocationsTable.getAllItemLocations(this);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, itemLocationsList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnLocation.setAdapter(spinnerArrayAdapter);
        spnLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mItemLocationID = id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mItemLocationID = -1;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog.i("ScannerFragmentActivity", "onResume");
        //getActionBar().setTitle("");

        FragmentManager fm = getFragmentManager();
        fm.beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .replace(R.id.scanner_fragment_container,
                        new ScannerFragment(), "SCANNER_FRAGMENT")
                .commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyLog.i("ScannerFragmentActivity", "onPause");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scanner_activity, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_add_aisles) {
            //Toast.makeText(this, "TO COME: action_add_aisles", Toast.LENGTH_SHORT).show();

            // Creating and Building the Dialog
            Dialog itemLocationDialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("TO COME: Select your store location");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
/*        builder.s(names, selectedUserPosition, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                // find the new user
                String newUserName = userNames.get(position);
                Cursor newUserCursor = UsersTable.getUser(getActivity(), newUserName);
                mActiveUser = new clsUserValues(getActivity(), newUserCursor);
                selectActiveUser();
                dialog.dismiss();
                newUserCursor.close();
            }
        });*/
            itemLocationDialog = builder.create();
            itemLocationDialog.show();
            return true;
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i("ScannerFragmentActivity", "onDestroy");
    }
}