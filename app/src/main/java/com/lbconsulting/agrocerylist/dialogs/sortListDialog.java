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
import com.lbconsulting.agrocerylist.activities.StoreListsActivity;
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
public class sortListDialog extends DialogFragment {

    private static final String ARG_SOURCE_FRAGMENT_ID = "argSourceFragmentID";
/*    private static final String ARG_STORE_ID = "argStoreID";
    private static final String ARG_STORE_ITEMS_SORT_ORDER = "argStoreItemsSortOrder";*/

    private RadioButton rbAlphabetical;
    private RadioButton rbByAisle;
    private RadioButton rbByGroup;
    private RadioButton rbFavoritesFirst;
    private RadioButton rbLastUsed;
    private RadioButton rbManual;
    private RadioButton rbSelectedFirst;


    private CheckBox ckApplyToAllStores;

    private int mSourceFragmentID = -1;
    private long mStoreID = -1;
    private int mStoreItemsSortingOrder = -1;


    public sortListDialog() {
        // Empty constructor required for DialogFragment
    }


    public static sortListDialog newInstance(int sourceFragmentID) {
        MyLog.i("sortListDialog", "newInstance: sourceFragmentID = " + sourceFragmentID);
        sortListDialog fragment = new sortListDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_SOURCE_FRAGMENT_ID, sourceFragmentID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("sortListDialog", "onCreate");
        Bundle args = getArguments();
        if (args.containsKey(ARG_SOURCE_FRAGMENT_ID)) {
            mSourceFragmentID = args.getInt(ARG_SOURCE_FRAGMENT_ID);
        }

        switch (mSourceFragmentID) {
            case MySettings.HOME_FRAG_STORE_LIST:
                mStoreID = StoreListsActivity.getActiveStoreID();
                mStoreItemsSortingOrder = StoresTable.getStoreItemsSortingOrder(getActivity(), mStoreID);
                break;

            case MySettings.FRAG_MASTER_LIST:

                break;

            case MySettings.FRAG_ITEMS_BY_GROUP:

                break;

            case MySettings.FRAG_CULL_ITEMS:

                break;

            default:
                MyLog.e("sortListDialog", "onCreate: Invalid SourceFragmentID = " + mSourceFragmentID);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("sortListDialog", "onActivityCreated");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MyLog.i("sortListDialog", "onCreateDialog");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_sort_list, null, false);

        // find the dialog's views
        rbAlphabetical = (RadioButton) view.findViewById(R.id.rbAlphabetical);
        rbByAisle = (RadioButton) view.findViewById(R.id.rbByAisle);
        rbByGroup = (RadioButton) view.findViewById(R.id.rbByGroup);
        rbFavoritesFirst = (RadioButton) view.findViewById(R.id.rbFavoritesFirst);
        rbLastUsed = (RadioButton) view.findViewById(R.id.rbLastUsed);
        rbManual = (RadioButton) view.findViewById(R.id.rbManual);
        rbSelectedFirst = (RadioButton) view.findViewById(R.id.rbSelectedFirst);
        ckApplyToAllStores = (CheckBox) view.findViewById(R.id.ckApplyToAllStores);
        ckApplyToAllStores.setVisibility(View.GONE);

        switch (mSourceFragmentID) {
            case MySettings.HOME_FRAG_STORE_LIST:
                rbAlphabetical.setVisibility(View.VISIBLE);

                rbByAisle.setVisibility(View.VISIBLE);
                // TODO: HOME_FRAG_STORE_LIST: implement sort by aisle
                rbByAisle.setEnabled(false);

                rbByGroup.setVisibility(View.VISIBLE);
                rbFavoritesFirst.setVisibility(View.GONE);
                rbLastUsed.setVisibility(View.GONE);

                rbManual.setVisibility(View.VISIBLE);
                // TODO: HOME_FRAG_STORE_LIST: implement sort manually
                rbManual.setEnabled(false);

                rbSelectedFirst.setVisibility(View.GONE);

                // set the starting radio button and checkbox checked values
                switch (mStoreItemsSortingOrder) {
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
                ckApplyToAllStores.setVisibility(View.VISIBLE);
                ckApplyToAllStores.setChecked(true);

                break;

            case MySettings.FRAG_MASTER_LIST:
                rbAlphabetical.setVisibility(View.VISIBLE);
                rbByAisle.setVisibility(View.GONE);
                rbByGroup.setVisibility(View.VISIBLE);
                rbFavoritesFirst.setVisibility(View.GONE);
                rbLastUsed.setVisibility(View.GONE);
                rbManual.setVisibility(View.VISIBLE);
                // TODO: FRAG_MASTER_LIST: implement sort manually
                rbManual.setEnabled(false);
                rbSelectedFirst.setVisibility(View.GONE);

                // set the starting radio button and checkbox checked values
                switch (MySettings.getMasterListSortOrder()) {
                    case MySettings.SORT_ALPHABETICAL:
                        rbAlphabetical.setChecked(true);
                        break;

                    case MySettings.SORT_BY_GROUP:
                        rbByGroup.setChecked(true);
                        break;

                    case MySettings.SORT_MANUALLY:
                        rbManual.setChecked(true);
                        break;
                }
                break;

            case MySettings.FRAG_ITEMS_BY_GROUP:
                rbAlphabetical.setVisibility(View.VISIBLE);
                rbByAisle.setVisibility(View.GONE);
                rbByGroup.setVisibility(View.VISIBLE);
                rbFavoritesFirst.setVisibility(View.GONE);
                rbLastUsed.setVisibility(View.GONE);
                rbManual.setVisibility(View.GONE);
                rbSelectedFirst.setVisibility(View.GONE);
                break;

            case MySettings.FRAG_CULL_ITEMS:
                rbAlphabetical.setVisibility(View.VISIBLE);
                rbByAisle.setVisibility(View.GONE);
                rbByGroup.setVisibility(View.VISIBLE);
                rbFavoritesFirst.setVisibility(View.GONE);
                rbLastUsed.setVisibility(View.VISIBLE);
                rbManual.setVisibility(View.GONE);
                rbSelectedFirst.setVisibility(View.GONE);
                break;

            default:
                MyLog.e("sortListDialog", "onCreate: Invalid SourceFragmentID = " + mSourceFragmentID);
        }


        // build the dialog
        return new AlertDialog.Builder(getActivity())
                .setTitle(getActivity().getString(R.string.sortListDialog_title))
                .setView(view)
                .setPositiveButton(getActivity().getString(R.string.btnOk_text),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                switch (mSourceFragmentID) {
                                    case MySettings.HOME_FRAG_STORE_LIST:
                                        if (rbAlphabetical.isChecked()) {
                                            if (ckApplyToAllStores.isChecked()) {
                                                StoresTable.updateStoreItemsSortOrder(getActivity(), -1, MySettings.SORT_ALPHABETICAL);
                                            } else {
                                                StoresTable.updateStoreItemsSortOrder(getActivity(), mStoreID, MySettings.SORT_ALPHABETICAL);
                                            }

                                        } else if (rbByAisle.isChecked()) {
                                            // TODO: SORT_BY_AISLE

                                        } else if (rbByGroup.isChecked()) {
                                            // TODO: SORT_BY_GROUP
                                            if (ckApplyToAllStores.isChecked()) {
                                                StoresTable.updateStoreItemsSortOrder(getActivity(), -1, MySettings.SORT_BY_GROUP);
                                            } else {
                                                StoresTable.updateStoreItemsSortOrder(getActivity(), mStoreID, MySettings.SORT_BY_GROUP);
                                            }

                                        } else if (rbManual.isChecked()) {
                                            // TODO: SORT_MANUALLY

                                        }
                                        EventBus.getDefault().post(new MyEvents.restartLoader(MySettings.ITEMS_LOADER));
                                        break;

                                    case MySettings.FRAG_MASTER_LIST:
                                        if (rbAlphabetical.isChecked()) {
                                            if (ckApplyToAllStores.isChecked()) {
                                                MySettings.setMasterListSortOrder(MySettings.SORT_ALPHABETICAL);
                                            }

                                        } else if (rbByGroup.isChecked()) {
                                            // TODO: SORT_BY_GROUP
                                            if (ckApplyToAllStores.isChecked()) {
                                                MySettings.setMasterListSortOrder(MySettings.SORT_BY_GROUP);
                                            }

                                        } else if (rbManual.isChecked()) {
                                            // TODO: SORT_MANUALLY

                                        }
                                        EventBus.getDefault().post(new MyEvents.restartLoader(MySettings.ITEMS_LOADER));
                                        break;

                                    case MySettings.FRAG_ITEMS_BY_GROUP:

                                        break;

                                    case MySettings.FRAG_CULL_ITEMS:

                                        break;

                                    default:
                                        MyLog.e("sortListDialog", "onCreate: Invalid SourceFragmentID = " + mSourceFragmentID);
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

    // set the starting radio button and checkbox checked values
/*    switch (mStoreItemsSortingOrder) {
        case MySettings.SORT_ALPHABETICAL:
            rbAlphabetical.setChecked(true);
            break;

        case MySettings.SORT_BY_AISLE:
            rbByAisle.setChecked(true);
            break;

        case MySettings.SORT_BY_GROUP:
            rbByGroup.setChecked(true);
            break;

        case MySettings.SORT_FAVORITES_FIRST:
            rbFavoritesFirst.setChecked(true);
            break;

        case MySettings.SORT_LAST_USED:
            rbLastUsed.setChecked(true);
            break;

        case MySettings.SORT_MANUALLY:
            rbManual.setChecked(true);
            break;

        case MySettings.SORT_SELECTED_FIRST:
            rbSelectedFirst.setChecked(true);
            break;

    }*/
}
