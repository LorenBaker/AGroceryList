package com.lbconsulting.agrocerylist.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.adapters.MasterListCursorAdapter;
import com.lbconsulting.agrocerylist.barcodescanner.ScannerFragmentActivity;
import com.lbconsulting.agrocerylist.classes.MyEvents;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;
import com.lbconsulting.agrocerylist.database.ItemsTable;
import com.lbconsulting.agrocerylist.database.aGroceryListContentProvider;
import com.lbconsulting.agrocerylist.dialogs.dialog_edit_item;

import de.greenrobot.event.EventBus;


/**
 * A placeholder fragment containing a simple view.
 */
public class fragMasterList extends Fragment implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private EditText txtItemName;
    private EditText txtItemNote;
    private ListView lvItemsListView;


    // The callbacks through which we will interact with the LoaderManager.
    private LoaderManager.LoaderCallbacks<Cursor> mMasterListFragmentCallbacks;
    private MasterListCursorAdapter mMasterListCursorAdapter;
    private LoaderManager mLoaderManager = null;

    //private boolean okToRestartItemsLoader = true;

    public fragMasterList() {
    }

    public static fragMasterList newInstance() {
        return new fragMasterList();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        MyLog.i("fragMasterList", "onCreate");
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MyLog.i("fragMasterList", "onCreateView");


        View rootView = inflater.inflate(R.layout.frag_master_list, container, false);

        txtItemName = (EditText) rootView.findViewById(R.id.txtItemName);

        txtItemNote = (EditText) rootView.findViewById(R.id.txtItemNote);
        Button btnAddToMasterList = (Button) rootView.findViewById(R.id.btnAddToMasterList);
        Button btnClearEditText = (Button) rootView.findViewById(R.id.btnClearEditText);

        btnAddToMasterList.setOnClickListener(this);
        btnClearEditText.setOnClickListener(this);

        lvItemsListView = (ListView) rootView.findViewById(R.id.lvItemsListView);
        lvItemsListView.setItemsCanFocus(true);

        mMasterListFragmentCallbacks = this;

        // setup txtItemName Listeners
        txtItemName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                boolean result = false;
                if ((event.getAction() == KeyEvent.ACTION_DOWN)
                        && (keyCode == KeyEvent.KEYCODE_ENTER ||
                        keyCode == KeyEvent.FLAG_EDITOR_ACTION ||
                        keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
                    addItemToMasterList();
                    result = true;
                }
                return result;
            }
        });

        txtItemName.addTextChangedListener(new TextWatcher() {
            // filter master list as the user inputs text
            @Override
            public void afterTextChanged(Editable s) {
                // if (okToRestartItemsLoader) {
                mLoaderManager.restartLoader(MySettings.ITEMS_LOADER, null, mMasterListFragmentCallbacks);
                //} else {
                //     okToRestartItemsLoader = true;
                // }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Do nothing
            }
        });

        return rootView;
    }

    public void onEvent(MyEvents.toggleItemSelection event) {
        ItemsTable.toggleItemSelection(getActivity(), event.getItemID());
        txtItemName.setText("");
        txtItemNote.setText("");
    }

    public void onEvent(MyEvents.showEditItemDialog event) {
        showEditItemDialog(event.getItemID());
    }

    private void showEditItemDialog(long itemID) {
        FragmentManager fm = getFragmentManager();
        dialog_edit_item dialog = dialog_edit_item.newInstance(itemID, getActivity().getString(R.string.edit_item_dialog_title));
        dialog.show(fm, "dialog_edit_item");
    }

    private void addItemToMasterList() {
        String newItemName = txtItemName.getText().toString().trim();
        if (!newItemName.isEmpty()) {
            ContentValues newFieldValues = new ContentValues();
            String newItemNote = txtItemNote.getText().toString().trim();
            long itemID;
            if (lvItemsListView.getAdapter().getCount() == 1) {
                // there is only one item in the list view ... no need to add it to the master list
                LinearLayout view = (LinearLayout) lvItemsListView.getChildAt(0);
                itemID = (long) view.getTag();
            } else {
                // add the item to the master list
                itemID = ItemsTable.createNewItem(getActivity(), newItemName);
            }
            newFieldValues.put(ItemsTable.COL_ITEM_NOTE, newItemNote);
            newFieldValues.put(ItemsTable.COL_SELECTED, 1);
            ItemsTable.updateItemFieldValues(getActivity(), itemID, newFieldValues);
        }
        txtItemNote.setText("");
        txtItemName.setText("");

        txtItemName.post(new Runnable() {
            @Override
            public void run() {
                txtItemName.requestFocus();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("fragMasterList", "onActivityCreated");

/*        if (savedInstanceState == null) {
            Bundle args = getArguments();
            if (args.containsKey(ARG_STORE_ID)) {
                mActiveStoreID = args.getLong(ARG_STORE_ID);
            }

        } else {
            if (savedInstanceState.containsKey(ARG_STORE_ID)) {
                mActiveStoreID = savedInstanceState.getLong(ARG_STORE_ID);
            }
        }*/

        MySettings.setActiveFragmentID(MySettings.FRAG_MASTER_LIST);
/*        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }*/

        mMasterListCursorAdapter = new MasterListCursorAdapter(getActivity(), null, 0);
        lvItemsListView.setAdapter(mMasterListCursorAdapter);
        mLoaderManager = getLoaderManager();
        mLoaderManager.initLoader(MySettings.ITEMS_LOADER, null, mMasterListFragmentCallbacks);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        MyLog.i("fragMasterList", "onSaveInstanceState");
        //outState.putLong(ARG_STORE_ID, mActiveStoreID);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("fragMasterList", "onPause");
/*        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        }*/
        aGroceryListContentProvider.setSuppressChangeNotification(true);
        ItemsTable.unCheckAllItems(getActivity());
        aGroceryListContentProvider.setSuppressChangeNotification(false);
    }

    @Override
    public void onResume() {
        MyLog.i("fragMasterList", "onResume");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        MyLog.i("fragMasterList", "onDestroy");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_frag_master_list, menu);
        MyLog.i("fragMasterList", "onCreateOptionsMenu");

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MyLog.i("fragMasterList", "onPrepareOptionsMenu");
        MenuItem showFavorites = menu.findItem(R.id.action_show_favorites);
        MenuItem showAllItems = menu.findItem(R.id.action_show_all_items);
        if (MySettings.showFavorites()) {
            showFavorites.setVisible(false);
            showAllItems.setVisible(true);
        } else {
            showFavorites.setVisible(true);
            showAllItems.setVisible(false);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_show_favorites:
                //Toast.makeText(getActivity(), "action_show_favorites", Toast.LENGTH_SHORT).show();
                MySettings.setShowFavorites(true);
                mLoaderManager.restartLoader(MySettings.ITEMS_LOADER, null, mMasterListFragmentCallbacks);
                getActivity().invalidateOptionsMenu();
                return true;

            case R.id.action_show_all_items:
                //Toast.makeText(getActivity(), "action_show_master_list", Toast.LENGTH_SHORT).show();
                MySettings.setShowFavorites(false);
                mLoaderManager.restartLoader(MySettings.ITEMS_LOADER, null, mMasterListFragmentCallbacks);
                getActivity().invalidateOptionsMenu();
                return true;

            case R.id.action_add_all:
                ItemsTable.addAllItems(getActivity());
                return true;

            case R.id.action_add_all_favorites:
                ItemsTable.addAllFavoritesItems(getActivity());
                return true;

            case R.id.action_clear_all:
                //Toast.makeText(getActivity(), "action_clear_all", Toast.LENGTH_SHORT).show();
                ItemsTable.deselectAllItems(getActivity());
                return true;

            case R.id.action_show_sort_master_list_dialog:
                Toast.makeText(getActivity(), "action_show_sort_master_list_dialog", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_set_manual_sort_order:
                Toast.makeText(getActivity(), "action_set_manual_sort_order", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_cull_items:
                Toast.makeText(getActivity(), "action_cull_items", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_manage_groups:
                //EventBus.getDefault().post(new MyEvents.showFragment(MySettings.FRAG_ITEMS_BY_GROUP));
                Toast.makeText(getActivity(), "action_manage_groups", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_manage_item_location:
                Toast.makeText(getActivity(), "action_manage_item_location", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_scan_barcodes:
                launchScannerActivity();
                return true;

            case R.id.action_show_scanned_items:
                EventBus.getDefault().post(new MyEvents.showFragment(MySettings.FRAG_PRODUCTS_LIST));
                return true;

            default:
                // Not implemented here
                return false;
        }

    }

    public void launchScannerActivity() {
        Intent intent = new Intent(getActivity(), ScannerFragmentActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddToMasterList:
                //Toast.makeText(getActivity(), "btnAddToMasterList.click", Toast.LENGTH_SHORT).show();
                addItemToMasterList();
                // hide the soft input keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Service.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtItemName.getWindowToken(), 0);
                break;

            case R.id.btnClearEditText:
                // Toast.makeText(getActivity(), "btnClearEditText.click", Toast.LENGTH_SHORT).show();
                clearEditText();
                break;
        }
    }

    private void clearEditText() {
        String itemNote = txtItemNote.getText().toString().trim();
        if (!itemNote.isEmpty()) {
            // the item has a note ... so clear it
            txtItemNote.setText("");
        } else {
            // the item has no note ... so clear the item
            txtItemName.setText("");
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        switch (id) {
            case MySettings.ITEMS_LOADER:
                MyLog.i("fragMasterList", "onCreateLoader: ITEMS_LOADER");
                // filter the cursor based on user typed text in txtListItem and the activeListTypeID
                String selection = null;
                String itemNameText = txtItemName.getText().toString().trim();
                if (!itemNameText.isEmpty()) {
                    selection = ItemsTable.COL_ITEM_NAME + " Like '%" + itemNameText + "%'";
                }
                if (MySettings.showFavorites()) {
                    if (selection == null) {
                        selection = ItemsTable.COL_FAVORITE + " = 1";
                    } else {
                        selection = selection + " AND " + ItemsTable.COL_FAVORITE + " = 1";
                    }
                }

                int masterListSortOrder = MySettings.getMasterListSortOrder();
                String sortOrder;
                try {
                    switch (masterListSortOrder) {
                        case MySettings.SORT_ALPHABETICAL:
                            sortOrder = ItemsTable.SORT_ORDER_ITEM_NAME_ASC;
                            cursorLoader = ItemsTable.getAllItems(getActivity(), selection, sortOrder);
                            break;

                        case MySettings.SORT_BY_AISLE:
                            // TODO: join query master list by aisle
                            //cursorLoader = ItemsTable.getAllItemsInListWithGroups(getActivity(), mActiveListID, selection);
                            break;

                        case MySettings.SORT_BY_GROUP:
                            //TODO: join query master list by group
                            //cursorLoader = ItemsTable.getAllItemsInListWithGroups(getActivity(), mActiveListID, selection);
                            break;

                        case MySettings.SORT_MANUALLY:
                            //TODO: join query master list manual sort order
                            sortOrder = ItemsTable.SORT_ORDER_MANUAL;
                            //cursorLoader = ItemsTable.getAllItemsInList(getActivity(), mActiveListID, selection, sortOrder);
                            break;

                    }

                } catch (SQLiteException e) {
                    MyLog.e("fragMasterList", "onCreateLoader: SQLiteException: " + e.getMessage());
                    return null;

                } catch (IllegalArgumentException e) {
                    MyLog.e("fragMasterList", "onCreateLoader: IllegalArgumentException: " + e.getMessage());
                    return null;
                }
                break;


        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
        // The asynchronous load is complete and the newCursor is now available for use.
        // Update the masterListAdapter to show the changed data.
        switch (loader.getId()) {
            case MySettings.ITEMS_LOADER:
                MyLog.i("fragMasterList", "onLoadFinished: ITEMS_LOADER");
                mMasterListCursorAdapter.swapCursor(newCursor);
                // TODO: set list view position if first time loading data

                if (newCursor != null) {
                    if (newCursor.getCount() == 1) {
                        // there is only one possible selection
                        // show item's note
                        showItemNote(newCursor);

                    } else {
                        // there is not only one selection, so clear the item's note
                        txtItemNote.setText("");
                    }
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case MySettings.ITEMS_LOADER:
                MyLog.i("fragMasterList", "onLoaderReset: ITEMS_LOADER");
                mMasterListCursorAdapter.swapCursor(null);
                break;

            default:
                break;
        }

    }

    private void showItemNote(Cursor cursor) {
        // set okToRestartItemsLoader flag to prevent an unneeded cursor loader restart
        //okToRestartItemsLoader = false;
        // get the item's name and note
        cursor.moveToFirst();
        String itemName = cursor.getString(cursor.getColumnIndexOrThrow(ItemsTable.COL_ITEM_NAME));
        String itemNote = cursor.getString(cursor.getColumnIndexOrThrow(ItemsTable.COL_ITEM_NOTE));

        if (!itemNote.isEmpty()) {
            // show the note if not empty
            txtItemNote.setText(itemNote);
            // move edit text cursor to the end of the word
            txtItemNote.setSelection(itemNote.length());
        }
        // if needed, complete the item's name
  /*      if (!itemName.equals(txtItemName.getText().toString())) {
            txtItemName.setText(itemName);
            // move edit text cursor to the end of the word
            txtItemName.setSelection(itemName.length());
        }*/
    }
}
