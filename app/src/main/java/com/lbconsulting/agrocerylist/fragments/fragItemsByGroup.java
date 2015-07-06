package com.lbconsulting.agrocerylist.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.activities.MainActivity;
import com.lbconsulting.agrocerylist.adapters.ItemsByGroupCursorAdapter;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;
import com.lbconsulting.agrocerylist.database.ItemsTable;
import com.lbconsulting.agrocerylist.database.aGroceryListContentProvider;
import com.lbconsulting.agrocerylist.dialogs.dialog_edit_item;


/**
 * A fragment to show items by group
 */
public class fragItemsByGroup extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private Spinner spnGroups;
    private Button btnApply;
    private ListView lvItemsByGroup;


    // The callbacks through which we will interact with the LoaderManager.
    private LoaderManager.LoaderCallbacks<Cursor> mFragItemsByGroupCallbacks;
    private ItemsByGroupCursorAdapter mItemsByGroupCursorAdapter;
    private LoaderManager mLoaderManager = null;

    public fragItemsByGroup() {
    }

    public static fragItemsByGroup newInstance() {
        return new fragItemsByGroup();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        MyLog.i("fragItemsByGroup", "onCreate");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MyLog.i("fragItemsByGroup", "onCreateView");
        View rootView = inflater.inflate(R.layout.frag_items_by_group, container, false);

        spnGroups = (Spinner) rootView.findViewById(R.id.spnGroups);
        btnApply = (Button) rootView.findViewById(R.id.btnApply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "btnApply", Toast.LENGTH_SHORT).show();
            }
        });
        lvItemsByGroup = (ListView) rootView.findViewById(R.id.lvItemsByGroup);
        lvItemsByGroup.setItemsCanFocus(true);

        lvItemsByGroup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long itemID) {
                ItemsTable.toggleCheckBox(getActivity(), itemID);
            }
        });

        lvItemsByGroup.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showEditItemDialog(id);
                return true;
            }
        });
        mFragItemsByGroupCallbacks = this;

        return rootView;
    }

    private void showEditItemDialog(long itemID) {
        FragmentManager fm = getFragmentManager();
        dialog_edit_item dialog = dialog_edit_item.newInstance(itemID, getActivity().getString(R.string.edit_item_dialog_title));
        dialog.show(fm, "dialog_edit_item");
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("fragItemsByGroup", "onActivityCreated");

        MySettings.setActiveFragmentID(MySettings.FRAG_ITEMS_BY_GROUP);
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mItemsByGroupCursorAdapter = new ItemsByGroupCursorAdapter(getActivity(), null, 0, 1);
        lvItemsByGroup.setAdapter(mItemsByGroupCursorAdapter);
        mLoaderManager = getLoaderManager();
        mLoaderManager.initLoader(MySettings.GROUPS_LOADER, null, mFragItemsByGroupCallbacks);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        MyLog.i("fragItemsByGroup", "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("fragItemsByGroup", "onPause");
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        }
        aGroceryListContentProvider.setSuppressChangeNotification(true);
        ItemsTable.unCheckAllItems(getActivity());
        aGroceryListContentProvider.setSuppressChangeNotification(false);
    }

    @Override
    public void onResume() {
        MyLog.i("fragItemsByGroup", "onResume");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        MyLog.i("fragItemsByGroup", "onDestroy");
        super.onDestroy();
        // EventBus.getDefault().unregister(this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_master_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                launchHomeActivity();
                return true;

            default:
                // Not implemented here
                return false;
        }
    }

    private void launchHomeActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        switch (id) {
            case MySettings.GROUPS_LOADER:
                MyLog.i("fragItemsByGroup", "onCreateLoader: GROUPS_LOADER");

                try {
                    //cursorLoader = ItemsTable.getAllItemsByGroups(getActivity());

                } catch (SQLiteException e) {
                    MyLog.e("fragItemsByGroup", "onCreateLoader: SQLiteException: " + e.getMessage());
                    return null;

                } catch (IllegalArgumentException e) {
                    MyLog.e("fragItemsByGroup", "onCreateLoader: IllegalArgumentException: " + e.getMessage());
                    return null;
                }
                break;
        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
        // The asynchronous load is complete and the newCursor is now available for use.
        switch (loader.getId()) {
            case MySettings.GROUPS_LOADER:
                MyLog.i("fragItemsByGroup", "onLoadFinished: GROUPS_LOADER");
                mItemsByGroupCursorAdapter.swapCursor(newCursor);
                break;

            default:
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case MySettings.GROUPS_LOADER:
                MyLog.i("fragItemsByGroup", "onLoaderReset: GROUPS_LOADER");
                mItemsByGroupCursorAdapter.swapCursor(null);
                break;

            default:
                break;
        }

    }

}
