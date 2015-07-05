package com.lbconsulting.agrocerylist.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.adapters.LocationsArrayAdapter;
import com.lbconsulting.agrocerylist.classes.MyEvents;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;
import com.lbconsulting.agrocerylist.classes.clsLocation;
import com.lbconsulting.agrocerylist.database.LocationsTable;
import com.lbconsulting.agrocerylist.database.StoreMapTable;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * A dialog where the user can edit the item's name, note, and group
 */
public class dialog_SelectLocation extends DialogFragment {

    private static final String ARG_ITEM_ID = "argItemID";
    private static final String ARG_GROUP_ID = "argGroupID";
    private static final String ARG_LOCATION_ID = "argLocationID";
    private static final String ARG_STORE_ID = "argStoreID";

    private ListView lvLocations;

    private long mItemID = -1;
    private long mGroupID = -1;
    private long mLocationID = -1;
    private long mStoreID = -1;

    private Activity mActivity;
    private ArrayList<clsLocation> locations;

    public dialog_SelectLocation() {
        // Empty constructor required for DialogFragment
    }


    public static dialog_SelectLocation newInstance(long itemID, long groupID, long locationID, long storeID) {
        MyLog.i("dialog_SelectLocation", "newInstance");
        dialog_SelectLocation fragment = new dialog_SelectLocation();
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, itemID);
        args.putLong(ARG_GROUP_ID, groupID);
        args.putLong(ARG_LOCATION_ID, locationID);
        args.putLong(ARG_STORE_ID, storeID);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("dialog_SelectLocation", "onCreate");
        Bundle args = getArguments();
        if (args.containsKey(ARG_ITEM_ID)) {
            mItemID = args.getLong(ARG_ITEM_ID);
            mGroupID = args.getLong(ARG_GROUP_ID);
            mLocationID = args.getLong(ARG_LOCATION_ID);
            mStoreID = args.getLong(ARG_STORE_ID);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("dialog_SelectLocation", "onActivityCreated");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MyLog.i("dialog_SelectLocation", "onAttach");
        mActivity = activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MyLog.i("dialog_SelectLocation", "onCreateDialog");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_select_location, null, false);

        // find the dialog's views
        lvLocations = (ListView) view.findViewById(R.id.lvLocations);
        locations = setupLocationsListView();

        int startingPosition = findStartingPosition(locations, mLocationID);
        lvLocations.setSelection(startingPosition);

        lvLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clsLocation location = locations.get(position);
                StoreMapTable.setLocation(getActivity(), mItemID, mGroupID, mStoreID, location.getLocationID());
                //EventBus.getDefault().post(new MyEvents.restartLoader(MySettings.ITEMS_LOADER));
                dismiss();
            }
        });


        // build the dialog
        return new AlertDialog.Builder(getActivity())
                .setTitle("Select Location")
                .setView(view)

                .setPositiveButton("Add Aisles",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                showAddAisleDialog();
                                //locations = setupLocationsListView();
                            }
                        }
                )

                .setNegativeButton(getActivity().getString(R.string.btnCancel_text),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();
                            }
                        }
                )
                .create();
    }

    private ArrayList<clsLocation> setupLocationsListView() {
        final ArrayList<clsLocation> locations = LocationsTable.getLocationsArray(getActivity());
        LocationsArrayAdapter adapter = new LocationsArrayAdapter(getActivity(), locations);
        lvLocations.setAdapter(adapter);
        return locations;
    }

    private void showAddAisleDialog() {
        // Creating and Building the Dialog
        Dialog itemLocationDialog;
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View layout = inflater.inflate(R.layout.dialog_number_picker, (RelativeLayout) getActivity().findViewById(R.id.numberPickerRelativeLayout));
        final NumberPicker numberPicker_10s = (NumberPicker) layout.findViewById(R.id.numberPicker_10s);
        final NumberPicker numberPicker_1s = (NumberPicker) layout.findViewById(R.id.numberPicker_1s);
        numberPicker_10s.setMaxValue(9);
        numberPicker_10s.setMinValue(0);
        numberPicker_1s.setMaxValue(9);
        numberPicker_1s.setMinValue(0);

        int numberOfAisles = LocationsTable.getNumberOfAisles(getActivity());
        int tens = numberOfAisles / 10;
        int ones = numberOfAisles % 10;
        numberPicker_10s.setValue(tens);
        numberPicker_1s.setValue(ones);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select the number of aisles in this store.");
        builder.setPositiveButton(getString(R.string.btnOk_text), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int tens = numberPicker_10s.getValue();
                int ones = numberPicker_1s.getValue();
                int numberOfAisles = tens * 10 + ones;
                LocationsTable.createNewAisles(mActivity, numberOfAisles);
                dialog.dismiss();
                EventBus.getDefault().post(new MyEvents.showSelectGroupLocationDialog(mItemID, mGroupID, mLocationID, mStoreID));

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
    }

    private int findStartingPosition(ArrayList<clsLocation> locations, long locationID) {
        int position = 0;
        boolean found = false;
        for (clsLocation location : locations) {
            if (location.getLocationID() == locationID) {
                found = true;
                break;
            } else {
                position++;
            }
        }

        if (!found) {
            position = -1;
        }
        return position;
    }
}
