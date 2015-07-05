package com.lbconsulting.agrocerylist.barcodescanner;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;
import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.OutpanRequest;
import com.lbconsulting.agrocerylist.classes.clsProductValues;
import com.lbconsulting.agrocerylist.classes.clsUtils;
import com.lbconsulting.agrocerylist.database.ProductsTable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

/*public class ScannerFragment extends Fragment implements MessageDialogFragment.MessageDialogListener,
        ZXingScannerView.ResultHandler, FormatSelectorDialogFragment.FormatSelectorDialogListener,
        CameraSelectorDialogFragment.CameraSelectorDialogListener {*/

public class ScannerFragment extends Fragment implements MessageDialogFragment.MessageDialogListener,
        ZXingScannerView.ResultHandler {

    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private ZXingScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;
    private RequestQueue mQueue;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {

        MyLog.i("ScannerFragment", "onCreateView()");

        mScannerView = new ZXingScannerView(getActivity());
        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
            mCameraId = state.getInt(CAMERA_ID, -1);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
            mCameraId = -1;
        }

        // setupFormats();
        return mScannerView;
    }


    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_scanner_fragment, menu);
        //Spinner spinner = (Spinner) menu.findItem(R.id.spinner).getActionView(); // find the spinner
/*        Spinner spinner = (Spinner) menu.findItem(R.id.spinner); // find the spinner
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, itemLocationsList);
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter); // set the adapter*/

        //s.setOnItemSelectedListener(myChangeListener); // (optional) reference to a OnItemSelectedListener, that you can use to perform actions based on user selection

/*        MenuItem menuItem;

        if(mFlash) {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_flash, 0, R.string.flash_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);


        if(mAutoFocus) {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_on);
        } else {
            menuItem = menu.add(Menu.NONE, R.id.menu_auto_focus, 0, R.string.auto_focus_off);
        }
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);

        menuItem = menu.add(Menu.NONE, R.id.menu_formats, 0, R.string.formats);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);

        menuItem = menu.add(Menu.NONE, R.id.menu_camera_selector, 0, R.string.select_camera);
        MenuItemCompat.setShowAsAction(menuItem, MenuItem.SHOW_AS_ACTION_ALWAYS);*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {

            case R.id.menu_flash:

                if (mFlash) {
                    item.setIcon(R.drawable.ic_action_flash_off);
                    item.setTitle(R.string.flash_off);
                } else {
                    item.setIcon(R.drawable.ic_action_flash_on);
                    item.setTitle(R.string.flash_on);
                }
                mFlash = !mFlash;
                mScannerView.setFlash(mFlash);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showSelectItemLocationDialog() {
// Creating and Building the Dialog
        Dialog itemLocationDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select your store location");
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
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
        mQueue = Volley.newRequestQueue(getActivity());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
    }

    @Override
    public void handleResult(Result rawResult) {
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getActivity().getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            MyLog.e("ScannerFragment", "handleResult: Exception: " + e.getMessage());
        }

        String gtin = rawResult.getText();
        String format = rawResult.getBarcodeFormat().toString();
        if (format.equals(ProductsTable.UPC_E)) {
            switch (gtin.length()){
                case 6:
                    // do nothing
                    break;
                case 8:
                    // truncate first and last digit
                    //  assume that the first digit is the number system digit
                    //  and the last digit is the UPCE check digit
                    gtin = gtin.substring(1, 7);
                    break;
                default:
                    gtin = clsUtils.UpcE2A(gtin);
                    gtin = clsUtils.UpcA2E(gtin);
            }

        }

        // save UPC, EAN, and ISBN products to the SQLite database
        if (format.startsWith("UPC") || format.startsWith("EAN") || format.startsWith("ISBN")) {
            ProductsTable.createNewProduct(getActivity(), gtin, format, rawResult.getTimestamp());
        }
        if (okToUseInternet()) {
            JsonObjectRequest jsObjRequest = getOutpanRequest(gtin, format);
            mQueue.add(jsObjRequest);
        } else {
            String msg = format + ": " + clsUtils.formatGTIN(gtin);
            showMessageDialog(msg);
        }
    }

    private boolean okToUseInternet() {
        // TODO: write code for okToUseInternet
        return true;
    }

    private JsonObjectRequest getOutpanRequest(final String gtin, final String format) {

        String url = "https://api.outpan.com/v1/products/" + gtin + "/name";

        // Make the request to Outpan
        JsonObjectRequest jsObjRequest = new OutpanRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    String outpanResponse = response.toString();
                    final JSONObject obj = new JSONObject(outpanResponse);
                    String responseName = obj.getString("name");
                    if (responseName.equals("null")) {
                        responseName = getString(R.string.product_name_not_available);
                    }
                    updateDatabaseAndShowMessage(format, gtin, responseName);

                } catch (JSONException e) {
                    MyLog.e("ScannerFragment", "onResponse: JSONException: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                int statusCode = error.networkResponse.statusCode;
                MyLog.e("ScannerFragment", "onErrorResponse: statusCode = " + statusCode);

                String responseName = getString(R.string.product_name_not_available) + getActivity().getString(R.string.invalid_gtin);
                updateDatabaseAndShowMessage(format, gtin, responseName);

            }
        });

        return jsObjRequest;
    }

    private void updateDatabaseAndShowMessage(String format, String gtin, String responseName) {

        if (responseName.contains(getActivity().getString(R.string.invalid_gtin))) {
            String message = responseName +
                    "\n\n" + format + ": " + clsUtils.formatGTIN(gtin);
            showMessageDialog(message);

        } else {
            ContentValues cv = new ContentValues();
            cv.put(ProductsTable.COL_PRODUCT_TITLE, responseName);
            ProductsTable.updateProductFields(getActivity(), gtin, cv);

            clsProductValues product = new clsProductValues(getActivity(), gtin);
            showMessageDialog(product.displayMessage());
        }
    }

    public void showMessageDialog(String message) {
        DialogFragment fragment = MessageDialogFragment
                .newInstance(getActivity().getString(R.string.scan_resutls_title), message, this);
        fragment.show(getActivity().getFragmentManager(), "scan_results");
    }

    public void closeMessageDialog() {
        closeDialog("scan_results");
    }

    public void closeFormatsDialog() {
        closeDialog("format_selector");
    }

    public void closeDialog(String dialogName) {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        DialogFragment fragment = (DialogFragment) fragmentManager.findFragmentByTag(dialogName);
        if (fragment != null) {
            fragment.dismiss();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        // Resume the camera
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

/*    @Override
    public void onFormatsSaved(ArrayList<Integer> selectedIndices) {
        mSelectedIndices = selectedIndices;
        setupFormats();
    }

    @Override
    public void onCameraSelected(int cameraId) {
        mCameraId = cameraId;
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }*/

/*    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();
        if (mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<Integer>();
            for (int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
                mSelectedIndices.add(i);
            }
        }

        for (int index : mSelectedIndices) {
            formats.add(ZXingScannerView.ALL_FORMATS.get(index));
        }
        if (mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }*/

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
        closeMessageDialog();
        closeFormatsDialog();
    }

}
