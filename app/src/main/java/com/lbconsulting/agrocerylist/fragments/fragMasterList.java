package com.lbconsulting.agrocerylist.fragments;


import android.app.Fragment;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.activities.StoreListsActivity;
import com.lbconsulting.agrocerylist.adapters.MasterListCursorAdapter;
import com.lbconsulting.agrocerylist.classes.MyEvents;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;
import com.lbconsulting.agrocerylist.database.ItemsTable;
import com.lbconsulting.agrocerylist.database.SelectedItemsTable;
import com.lbconsulting.agrocerylist.database.aGroceryListContentProvider;

import de.greenrobot.event.EventBus;


/**
 * A placeholder fragment containing a simple view.
 */
public class fragMasterList extends Fragment implements View.OnClickListener,
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_STORE_ID = "argStoreID";

    private EditText txtItemName;
    private EditText txtItemNote;
    private Button btnAddToMasterList;
    private Button btnClearEditText;
    private ListView lvItemsListView;


    // The callbacks through which we will interact with the LoaderManager.
    private LoaderManager.LoaderCallbacks<Cursor> mMasterListFragmentCallbacks;
    private MasterListCursorAdapter mMasterListCursorAdapter;
    private LoaderManager mLoaderManager = null;

    private final int ITEMS_LOADER = 1;
    private long mActiveStoreID = -1;

    private boolean okToRestartItemsLoader = true;

    public fragMasterList() {
    }

    public static fragMasterList newInstance(long storeID) {
        fragMasterList fragment = new fragMasterList();
        Bundle args = new Bundle();
        args.putLong(ARG_STORE_ID, storeID);
        fragment.setArguments(args);
        return fragment;
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
        btnAddToMasterList = (Button) rootView.findViewById(R.id.btnAddToMasterList);
        btnClearEditText = (Button) rootView.findViewById(R.id.btnClearEditText);

        btnAddToMasterList.setOnClickListener(this);
        btnClearEditText.setOnClickListener(this);

        lvItemsListView = (ListView) rootView.findViewById(R.id.lvItemsListView);
        lvItemsListView.setItemsCanFocus(true);

        lvItemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long itemID) {
                SelectedItemsTable.toggleSelection(getActivity(), mActiveStoreID, itemID);
                txtItemName.setText("");
                txtItemNote.setText("");
            }
        });

        lvItemsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(), "TO COME: Edit Item.", Toast.LENGTH_SHORT).show();

                return true;
            }
        });
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
                    addItemToActiveList();
                    result = true;
                }
                return result;
            }
        });

        txtItemName.addTextChangedListener(new TextWatcher() {
            // filter master list as the user inputs text
            @Override
            public void afterTextChanged(Editable s) {
/*                MyLog.i("fragMasterList", "afterTextChanged; txtItemName.afterTextChanged -- "
                        + txtItemName.getText().toString());*/
                if (okToRestartItemsLoader) {
                    mLoaderManager.restartLoader(ITEMS_LOADER, null, mMasterListFragmentCallbacks);
                } else {
                    okToRestartItemsLoader = true;
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
/*                MyLog.i("MasterListFragment", "onActivityCreated; txtItemName.beforeTextChanged -- "
                        + txtItemName.getText().toString());*/
                // Do nothing

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
/*                MyLog.i("MasterListFragment", "onActivityCreated; txtItemName.onTextChanged -- "
                        + txtItemName.getText().toString());*/
                // Do nothing

            }

        });

        return rootView;
    }

    private void addItemToActiveList() {
        String newItemName = txtItemName.getText().toString().trim();
        if (!newItemName.isEmpty()) {
            long newItemID = ItemsTable.createNewItem(getActivity(), newItemName);
            SelectedItemsTable.addItemToStore(getActivity(), mActiveStoreID, newItemID);

            String newItemNote = txtItemNote.getText().toString().trim();
            ContentValues newFieldValues = new ContentValues();
            newFieldValues.put(ItemsTable.COL_ITEM_NOTE, newItemNote);
            ItemsTable.updateItemFieldValues(getActivity(), newItemID, newFieldValues);
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

        if (savedInstanceState == null) {
            Bundle args = getArguments();
            if (args.containsKey(ARG_STORE_ID)) {
                mActiveStoreID = args.getLong(ARG_STORE_ID);
            }

        } else {
            if (savedInstanceState.containsKey(ARG_STORE_ID)) {
                mActiveStoreID = savedInstanceState.getLong(ARG_STORE_ID);
            }
        }

        MySettings.setActiveFragmentID(MySettings.FRAG_MASTER_LIST);
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mMasterListCursorAdapter = new MasterListCursorAdapter(getActivity(), null, 0);
        lvItemsListView.setAdapter(mMasterListCursorAdapter);
        mLoaderManager = getLoaderManager();
        mLoaderManager.initLoader(ITEMS_LOADER, null, mMasterListFragmentCallbacks);

    }

    private void checkStoreSelectedItems() {
        aGroceryListContentProvider.setSuppressChangeNotification(true);
        ItemsTable.unCheckAllItems(getActivity());
        Cursor cursor = SelectedItemsTable.getAllItemsSelectedInStoreCursor(getActivity(), mActiveStoreID);
       /* String cursorContent = DatabaseUtils.dumpCursorToString(cursor);
        MyLog.i("clsItemValues: \n", cursorContent);*/
        long itemID;
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                itemID = cursor.getLong(cursor.getColumnIndex(SelectedItemsTable.COL_ITEM_ID));
                ItemsTable.setCheckMark(getActivity(), itemID, true);
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        aGroceryListContentProvider.setSuppressChangeNotification(false);
        mLoaderManager.restartLoader(ITEMS_LOADER, null, mMasterListFragmentCallbacks);
    }

    public void onEvent(MyEvents.onActiveStoreChange event) {
        mActiveStoreID = event.getActiveStoreID();
        checkStoreSelectedItems();
    }

    public void onEvent(MyEvents.onClick_masterListItem event) {
        SelectedItemsTable.toggleSelection(getActivity(), mActiveStoreID, event.getItemID());
        txtItemName.setText("");
        txtItemNote.setText("");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        MyLog.i("fragMasterList", "onSaveInstanceState");
        outState.putLong(ARG_STORE_ID, mActiveStoreID);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("fragMasterList", "onPause");
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        }
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
        inflater.inflate(R.menu.menu_master_list_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_addNewStore:
                Toast.makeText(getActivity(), "action_addNewStore", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_show_favorites:
                Toast.makeText(getActivity(), "action_show_favorites", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_show_master_list:
                Toast.makeText(getActivity(), "action_show_master_list", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_show_sort_dialog:
                Toast.makeText(getActivity(), "action_show_sort_dialog", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_manual_sort_order:
                Toast.makeText(getActivity(), "action_manual_sort_order", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_clear_all:
                Toast.makeText(getActivity(), "action_clear_all", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_cull_items:
                Toast.makeText(getActivity(), "action_cull_items", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_set_groups:
                Toast.makeText(getActivity(), "action_set_groups", Toast.LENGTH_SHORT).show();
                return true;

            case android.R.id.home:
                launchHomeActivity();
                return true;

            default:
                // Not implemented here
                return false;
        }
    }

    public void launchHomeActivity() {
        Intent intent = new Intent(getActivity(), StoreListsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddToMasterList:
                //Toast.makeText(getActivity(), "btnAddToMasterList.click", Toast.LENGTH_SHORT).show();
                addItemToActiveList();
                // hide the soft input keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                        Service.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtItemName.getWindowToken(), 0);
                break;

            case R.id.btnClearEditText:
                // Toast.makeText(getActivity(), "btnClearEditText.click", Toast.LENGTH_SHORT).show();
                ClearEditText();
                break;
        }
    }

    private void ClearEditText() {
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
            case ITEMS_LOADER:
                MyLog.i("fragMasterList", "onCreateLoader: ITEMS_LOADER");
                // filter the cursor based on user typed text in txtListItem and the activeListTypeID
                String selection = null;
                String itemNameText = txtItemName.getText().toString().trim();
                if (!itemNameText.isEmpty()) {
                    selection = ItemsTable.COL_ITEM_NAME + " Like '%" + itemNameText + "%'";
                }

                int masterListSortOrder = MySettings.getMasterListSortOrder();
                String sortOrder = "";
                try {
                    switch (masterListSortOrder) {
                        case MySettings.MASTER_LIST_SORT_ALPHABETICAL:
                            sortOrder = ItemsTable.SORT_ORDER_ITEM_NAME;
                            cursorLoader = ItemsTable.getAllItems(getActivity(), selection, sortOrder);
                            break;

                        case MySettings.MASTER_LIST_SORT_BY_AISLE:
                            // TODO: join query master list by aisle
                            //cursorLoader = ItemsTable.getAllItemsInListWithGroups(getActivity(), mActiveListID, selection);
                            break;

                        case MySettings.MASTER_LIST_SORT_BY_GROUP:
                            //TODO: join query master list by group
                            //cursorLoader = ItemsTable.getAllItemsInListWithGroups(getActivity(), mActiveListID, selection);
                            break;

                        case MySettings.MASTER_LIST_SORT_MANUAL:
                            //TODO: join query master list manual sort order
                            sortOrder = ItemsTable.SORT_ORDER_LAST_USED;
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
        int id = loader.getId();
        // The asynchronous load is complete and the newCursor is now available for use.
        // Update the masterListAdapter to show the changed data.
        switch (loader.getId()) {
            case ITEMS_LOADER:
                MyLog.i("fragMasterList", "onLoadFinished: ITEMS_LOADER");
                mMasterListCursorAdapter.swapCursor(newCursor);
                // TODO: set list view position if first time loading data

                if (newCursor != null) {
                    if (newCursor.getCount() == 1) {
                        // there is only one possible selection
                        // complete the item's name and show its note
                        completeItemNameAndNote(newCursor);

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
            case ITEMS_LOADER:
                MyLog.i("fragMasterList", "onLoaderReset: ITEMS_LOADER");
                mMasterListCursorAdapter.swapCursor(null);
                break;

            default:
                break;
        }

    }

    private void completeItemNameAndNote(Cursor cursor) {
        // set okToRestartItemsLoader flag to prevent an unneeded cursor loader restart
        okToRestartItemsLoader = false;
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
        if (!itemName.equals(txtItemName.getText().toString())) {
            txtItemName.setText(itemName);
            // move edit text cursor to the end of the word
            txtItemName.setSelection(itemName.length());
        }
    }
}
