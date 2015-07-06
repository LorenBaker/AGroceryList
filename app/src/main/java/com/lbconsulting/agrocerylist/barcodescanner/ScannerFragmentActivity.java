package com.lbconsulting.agrocerylist.barcodescanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.database.LocationsTable;
import com.lbconsulting.agrocerylist.database.ProductsTable;

import java.util.ArrayList;

public class ScannerFragmentActivity extends Activity {

    private Spinner spnLocation;
    private ArrayAdapter<String> mSpinnerArrayAdapter;
    private long mItemLocationID = -1;

    public long getItemLocationID() {
        return mItemLocationID;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        MyLog.i("ScannerFragmentActivity", "onCreate");
        setContentView(R.layout.activity_scanner_fragment);

        spnLocation = (Spinner) findViewById(R.id.spnItemLocation);
        ArrayList<String> itemLocationsList = LocationsTable.getAllItemLocations(this,LocationsTable.SORT_ORDER_LOCATION_ID);
        mSpinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, itemLocationsList);
        mSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnLocation.setAdapter(mSpinnerArrayAdapter);
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
        getMenuInflater().inflate(R.menu.menu_activity_scanner, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, @NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_add_aisles) {

            // Creating and Building the Dialog
            Dialog itemLocationDialog;
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.dialog_number_picker, (RelativeLayout) findViewById(R.id.numberPickerRelativeLayout));
            final NumberPicker numberPicker_10s = (NumberPicker) layout.findViewById(R.id.numberPicker_10s);
            final NumberPicker numberPicker_1s = (NumberPicker) layout.findViewById(R.id.numberPicker_1s);
            numberPicker_10s.setMaxValue(9);
            numberPicker_10s.setMinValue(0);
            numberPicker_1s.setMaxValue(9);
            numberPicker_1s.setMinValue(0);

            int numberOfAisles = LocationsTable.getNumberOfAisles(this);
            int tens = numberOfAisles / 10;
            int ones = numberOfAisles % 10;
            numberPicker_10s.setValue(tens);
            numberPicker_1s.setValue(ones);

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select the number of aisles in this store.");
            builder.setPositiveButton(getString(R.string.btnOk_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    int tens = numberPicker_10s.getValue();
                    int ones = numberPicker_1s.getValue();
                    int numberOfAisles = tens * 10 + ones;
                    LocationsTable.createNewAisles(ScannerFragmentActivity.this, numberOfAisles);

                    ArrayList<String> itemLocationsList = LocationsTable
                            .getAllItemLocations(ScannerFragmentActivity.this,LocationsTable.SORT_ORDER_LOCATION_ID);
                    mSpinnerArrayAdapter = new ArrayAdapter<>(ScannerFragmentActivity.this,
                            android.R.layout.simple_spinner_item, itemLocationsList);
                    int position = spnLocation.getSelectedItemPosition();
                    spnLocation.setAdapter(mSpinnerArrayAdapter);
                    spnLocation.setSelection(position);

                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(getString(R.string.btnCancel_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });


            builder.setView(layout);
            itemLocationDialog = builder.create();
            itemLocationDialog.show();
            return true;
        } else if (id == R.id.action_clear_products_table) {
            ProductsTable.deleteAllProducts(this);
        }

        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyLog.i("ScannerFragmentActivity", "onDestroy");
    }
}