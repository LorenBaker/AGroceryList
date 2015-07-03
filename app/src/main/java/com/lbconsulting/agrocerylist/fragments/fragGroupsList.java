package com.lbconsulting.agrocerylist.fragments;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.activities.StoreListsActivity;
import com.lbconsulting.agrocerylist.adapters.GroupsCursorAdapter;
import com.lbconsulting.agrocerylist.adapters.ProductsCursorAdapter;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;
import com.lbconsulting.agrocerylist.database.GroupsTable;
import com.lbconsulting.agrocerylist.database.ProductsTable;


/**
 * A fragment that shows the apps Groups.
 */
public class fragGroupsList extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private ListView lvGroups;
    private GroupsCursorAdapter mGroupsCursorAdapter;

    public fragGroupsList() {
    }

    public static fragGroupsList newInstance() {

        return new fragGroupsList();
/*        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_TWO_PANE, isTwoPane);
        fragment.setArguments(args);
        return fragment;*/
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MyLog.i("fragGroupsList", "onCreateView");
        View rootView = inflater.inflate(R.layout.frag_groups_list, container, false);
        lvGroups = (ListView) rootView.findViewById(R.id.lvGroups);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("fragGroupsList", "onActivityCreated");
        MySettings.setActiveFragmentID(MySettings.FRAG_GROUPS_LIST);

        LoaderManager.LoaderCallbacks<Cursor> mGroupsCallbacks = this;
        LoaderManager mLoaderManager = getLoaderManager();

        mGroupsCursorAdapter = new GroupsCursorAdapter(getActivity(), null, 0);
        lvGroups.setAdapter(mGroupsCursorAdapter);
        mLoaderManager.initLoader(MySettings.GROUPS_LOADER, null, mGroupsCallbacks);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        MyLog.i("fragGroupsList", "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        MyLog.i("fragGroupsList", "onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        MyLog.i("fragGroupsList", "onResume");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        MyLog.i("fragGroupsList", "onDestroy");
        super.onDestroy();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_group_list_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_deleteCheckedGroups:
                Toast.makeText(getActivity(), "action_deleteCheckedGroups", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_addNewGroup:
                Toast.makeText(getActivity(), "action_addNewGroup", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_clear_all_check_marks:
                Toast.makeText(getActivity(), "action_clear_all_check_marks", Toast.LENGTH_SHORT).show();
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        String sortOrder = GroupsTable.SORT_ORDER_GROUP_NAME;
        switch (id) {
            case MySettings.GROUPS_LOADER:
                MyLog.i("fragGroupsList", "onCreateLoader. Loading GROUPS_LOADER");
                cursorLoader = GroupsTable.getAllGroupNames(getActivity(), sortOrder);
                break;
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {

        switch (loader.getId()) {
            case MySettings.GROUPS_LOADER:
                MyLog.i("fragGroupsList", "onLoadFinished MySettings.PRODUCTS_LOADER");
                mGroupsCursorAdapter.swapCursor(newCursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        switch (loader.getId()) {
            case MySettings.GROUPS_LOADER:
                MyLog.i("fragGroupsList", "onLoaderReset MySettings.PRODUCTS_LOADER");
                mGroupsCursorAdapter.swapCursor(null);
                break;

        }
    }
}
