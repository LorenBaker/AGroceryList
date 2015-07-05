package com.lbconsulting.agrocerylist.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.adapters.GroupsSpinnerArrayAdapter;
import com.lbconsulting.agrocerylist.classes.MyEvents;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;
import com.lbconsulting.agrocerylist.classes.clsGroup;
import com.lbconsulting.agrocerylist.database.GroupsTable;
import com.lbconsulting.agrocerylist.database.ItemsTable;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * A dialog where the user can edit the item's name, note, and group
 */
public class dialog_edit_item extends DialogFragment {

    private static final String ARG_ITEM_ID = "argItemID";
    private static final String ARG_DIALOG_TITLE = "argDialogTitle";

    private EditText txtItemName;
    private EditText txtItemNote;
    private Spinner spnGroup;

    private String mInitialItemName;
    private long mItemID = -1;
    private String mDialogTitle = "";

    private AlertDialog mAlertDialog;

    public dialog_edit_item() {
        // Empty constructor required for DialogFragment
    }


    public static dialog_edit_item newInstance(long itemID, String dialogTitle) {
        MyLog.i("dialog_edit_item", "newInstance: itemID = " + itemID);
        dialog_edit_item fragment = new dialog_edit_item();
        Bundle args = new Bundle();
        args.putLong(ARG_ITEM_ID, itemID);
        args.putString(ARG_DIALOG_TITLE, dialogTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("dialog_edit_item", "onCreate");
        Bundle args = getArguments();
        if (args.containsKey(ARG_ITEM_ID)) {
            mItemID = args.getLong(ARG_ITEM_ID);
            mDialogTitle = args.getString(ARG_DIALOG_TITLE);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("dialog_edit_item", "onActivityCreated");
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button btnPositive = mAlertDialog.getButton(Dialog.BUTTON_POSITIVE);
                btnPositive.setTextSize(18);

                Button btnNegative = mAlertDialog.getButton(Dialog.BUTTON_NEGATIVE);
                btnNegative.setTextSize(18);
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MyLog.i("dialog_edit_item", "onCreateDialog");

        // inflate the xml layout
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_item, null, false);

        // find the dialog's views
        txtItemName = (EditText) view.findViewById(R.id.txtItemName);
        txtItemNote = (EditText) view.findViewById(R.id.txtItemNote);
        spnGroup = (Spinner) view.findViewById(R.id.spnGroup);

        // get the item's cursor, then set the item name and text
        Cursor itemCursor = ItemsTable.getItemCursor(getActivity(), mItemID);
        if (itemCursor != null && itemCursor.getCount() > 0) {
            itemCursor.moveToFirst();
            String itemName = itemCursor.getString(itemCursor.getColumnIndex(ItemsTable.COL_ITEM_NAME));
            String itemNote = itemCursor.getString(itemCursor.getColumnIndex(ItemsTable.COL_ITEM_NOTE));
            long itemGroupID = itemCursor.getLong(itemCursor.getColumnIndex(ItemsTable.COL_GROUP_ID));
            txtItemName.setText(itemName);
            mInitialItemName = txtItemName.toString();
            txtItemNote.setText(itemNote);

            ArrayList<clsGroup> groups = GroupsTable.getAllGroupsArray(getActivity());
            GroupsSpinnerArrayAdapter adapter = new GroupsSpinnerArrayAdapter(getActivity(), groups);
            int startingPosition = getGroupStartingPosition(itemGroupID, groups);
            spnGroup.setAdapter(adapter);
            spnGroup.setSelection(startingPosition);
        }

        if (itemCursor != null) {
            itemCursor.close();
        }


        // build the dialog
        mAlertDialog = new AlertDialog.Builder(getActivity())
                .setTitle(mDialogTitle)
                .setView(view)
                .setPositiveButton(getActivity().getString(R.string.btnOk_text),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ContentValues cv = new ContentValues();
                                String itemName = txtItemName.getText().toString().trim();

                                if (!itemName.isEmpty()) {
                                    // verify that there is no other item with the same proposed item name
                                    boolean itemExists = ItemsTable.itemExists(getActivity(), itemName);
                                    boolean sameItem = false;
                                    if (itemExists) {
                                        // the item exists, but it might have a different case
                                        sameItem = mInitialItemName.equalsIgnoreCase(itemName);
                                    }

                                    if (!itemExists || sameItem) {
                                        // either the item does not exist in the table
                                        // OR, the item exists but in a different case
                                        // so update the item's name
                                        cv.put(ItemsTable.COL_ITEM_NAME, itemName);
                                    }
                                }
                                // update the item's note
                                cv.put(ItemsTable.COL_ITEM_NOTE, txtItemNote.getText().toString().trim());

                                // update the item's group
                                clsGroup selectedGroup = (clsGroup) spnGroup.getSelectedItem();
                                cv.put(ItemsTable.COL_GROUP_ID, selectedGroup.getGroupID());

                                // update the item's values
                                ItemsTable.updateItemFieldValues(getActivity(), mItemID, cv);
                                //EventBus.getDefault().post(new MyEvents.restartLoader(MySettings.ITEMS_LOADER));
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

        return mAlertDialog;
    }


    private int getGroupStartingPosition(long itemGroupID, ArrayList<clsGroup> groups) {
        int position = 0;
        boolean found = false;
        for (clsGroup group : groups) {
            if (group.getGroupID() == itemGroupID) {
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
