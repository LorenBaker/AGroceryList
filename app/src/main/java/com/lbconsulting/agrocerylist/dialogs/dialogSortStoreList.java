package com.lbconsulting.agrocerylist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.classes.MyEvents;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;
import com.lbconsulting.agrocerylist.database.StoresTable;

import de.greenrobot.event.EventBus;

/**
 * A dialog where the user can:
 * 1) select how the store list is sorted
 * 2) select to show all selected items regardless of which store the item was selected
 * 3) limit these choices to the active store, or apply to all stores
 */
public class dialogSortStoreList extends DialogFragment {

    private static final String ARG_STORE_ID = "argStoreID";
    private static final String ARG_STORE_ITEMS_SORT_ORDER = "argStoreItemsSortOrder";

    private RadioButton rbAlphabetical;
    private RadioButton rbByAisle;
    private RadioButton rbByGroup;
    private RadioButton rbManual;
    private CheckBox ckShowAllSelectedItemsInStoreList;
    private CheckBox ckApplyToAllStores;

    private long mStoreID = -1;
    private int mStoreItemsSortOrder = -1;


    public dialogSortStoreList() {
        // Empty constructor required for DialogFragment
    }


    public static dialogSortStoreList newInstance(long storeID, int storeItemsSortOrder) {
        MyLog.i("fragStoreList", "newInstance: storeID = " + storeID);
        dialogSortStoreList fragment = new dialogSortStoreList();
        Bundle args = new Bundle();
        args.putLong(ARG_STORE_ID, storeID);
        args.putInt(ARG_STORE_ITEMS_SORT_ORDER, storeItemsSortOrder);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("dialogSortStoreList", "onCreate");
        Bundle args = getArguments();
        if (args.containsKey(ARG_STORE_ID)) {
            mStoreID = args.getLong(ARG_STORE_ID);
            mStoreItemsSortOrder = args.getInt(ARG_STORE_ITEMS_SORT_ORDER);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("dialogSortStoreList", "onActivityCreated");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MyLog.i("dialogSortStoreList", "onCreateDialog");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_sort_store_list, null, false);

        // find the dialog's views
        rbAlphabetical = (RadioButton) view.findViewById(R.id.rbAlphabetical);
        rbByAisle = (RadioButton) view.findViewById(R.id.rbByAisle);
        rbByGroup = (RadioButton) view.findViewById(R.id.rbByGroup);
        rbManual = (RadioButton) view.findViewById(R.id.rbManual);
        ckShowAllSelectedItemsInStoreList = (CheckBox) view.findViewById(R.id.ckShowAllSelectedItemsInStoreList);
        ckApplyToAllStores = (CheckBox) view.findViewById(R.id.ckApplyToAllStores);

        // set the starting radio button and checkbox checked values
        switch (mStoreItemsSortOrder) {
            case MySettings.SORT_ALPHABETICAL:
                rbAlphabetical.setChecked(true);
                break;

            case MySettings.SORT_BY_AISLE:
                rbByAisle.setChecked(true);
                break;

            case MySettings.SORT_BY_GROUP:
                rbByGroup.setChecked(true);
                break;

            case MySettings.SORT_MANUALLY:
                rbManual.setChecked(true);
                break;
        }

        ckShowAllSelectedItemsInStoreList.setChecked(MySettings.showAllSelectedItemsInStoreList());

        // build the dialog
        return new AlertDialog.Builder(getActivity())
                .setTitle("Sort Store List")
                .setView(view)
                .setPositiveButton(getActivity().getString(R.string.btnOk_text),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                if (rbAlphabetical.isChecked()) {
                                    if (ckApplyToAllStores.isChecked()) {
                                        StoresTable.updateStoreItemsSortOrder(getActivity(), -1, MySettings.SORT_ALPHABETICAL);
                                    } else {
                                        StoresTable.updateStoreItemsSortOrder(getActivity(), mStoreID, MySettings.SORT_ALPHABETICAL);
                                    }

                                } else if (rbByAisle.isChecked()) {
                                    // TODO: SORT_BY_AISLE
                                   /* if (ckApplyToAllStores.isChecked()) {
                                        StoresTable.updateStoreItemsSortOrder(getActivity(), -1, MySettings.SORT_BY_AISLE);
                                    } else {
                                        StoresTable.updateStoreItemsSortOrder(getActivity(), mStoreID, MySettings.SORT_BY_AISLE);
                                    }*/

                                } else if (rbByGroup.isChecked()) {
                                    // TODO: SORT_BY_GROUP
                                  /*  if (ckApplyToAllStores.isChecked()) {
                                        StoresTable.updateStoreItemsSortOrder(getActivity(), -1, MySettings.SORT_BY_GROUP);
                                    } else {
                                        StoresTable.updateStoreItemsSortOrder(getActivity(), mStoreID, MySettings.SORT_BY_GROUP);
                                    }*/

                                } else if (rbManual.isChecked()) {
                                    // TODO: SORT_MANUALLY
                                   /* if (ckApplyToAllStores.isChecked()) {
                                        StoresTable.updateStoreItemsSortOrder(getActivity(), -1, MySettings.SORT_MANUALLY);
                                    } else {
                                        StoresTable.updateStoreItemsSortOrder(getActivity(), mStoreID, MySettings.SORT_MANUALLY);
                                    }*/
                                }

                                // if showAllSelectedItemsInStoreList has changed, save it and restart the items loader
                                boolean showAllSelectedItemsInStoreList = MySettings.showAllSelectedItemsInStoreList();
                                if (ckShowAllSelectedItemsInStoreList.isChecked() != showAllSelectedItemsInStoreList) {
                                    MySettings.setShowAllSelectedItemsInStoreList(ckShowAllSelectedItemsInStoreList.isChecked());
                                    EventBus.getDefault().post(new MyEvents.restartLoader(MySettings.ITEMS_LOADER));
                                }

                                dismiss();
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
}
