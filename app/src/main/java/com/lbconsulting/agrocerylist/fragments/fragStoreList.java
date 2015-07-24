package com.lbconsulting.agrocerylist.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.adapters.StoreListCursorAdapter;
import com.lbconsulting.agrocerylist.classes.MyEvents;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;
import com.lbconsulting.agrocerylist.database.ItemsTable;
import com.lbconsulting.agrocerylist.database.StoresTable;
import com.lbconsulting.agrocerylist.dialogs.dialog_edit_item;

import de.greenrobot.event.EventBus;


/**
 * A fragment showing the store's selected items.
 */
public class fragStoreList extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_STORE_ID = "argStoreID";
    private static final String ARG_STORE_POSITION = "argStorePosition";
    private static final String ARG_DISPLAY_NAME = "argDisplayName";
    private static final String ARG_COLOR_THEME_ID = "argColorThemeID";
    // private static final String ARG_STORE_ITEMS_SORT_ORDER = "argStoreItemsSortOrder";


    private LoaderManager.LoaderCallbacks<Cursor> mStoreListFragmentCallbacks;
    private StoreListCursorAdapter mStoreListCursorAdapter;
    private LoaderManager mLoaderManager = null;

    private TextView tvStoreTitle;
    private ListView lvStoreItems;

    private int mStorePosition = -1;
    private long mStoreID = -1;
    private String mDisplayName;
    private long mColorThemeID;
    // private int mStoreItemsSortOrder;
    //private clsStoreValues mStoreValues;

    public fragStoreList() {
    }

    public static fragStoreList newInstance(int position, long storeID, String displayName,
                                            long colorThemeID) {
        MyLog.i("fragStoreList", "newInstance: storeID = " + storeID);
        fragStoreList fragment = new fragStoreList();
        Bundle args = new Bundle();
        args.putInt(ARG_STORE_POSITION, position);
        args.putLong(ARG_STORE_ID, storeID);
        args.putString(ARG_DISPLAY_NAME, displayName);
        args.putLong(ARG_COLOR_THEME_ID, colorThemeID);
        //args.putInt(ARG_STORE_ITEMS_SORT_ORDER, storeItemsSortOrder);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        MyLog.i("fragStoreList", "onCreate: storeID = " + mStoreID);
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MyLog.i("fragStoreList", "onCreateView: storeID = " + mStoreID);
        View rootView = inflater.inflate(R.layout.frag_store_list, container, false);

        tvStoreTitle = (TextView) rootView.findViewById(R.id.tvStoreTitle);
        lvStoreItems = (ListView) rootView.findViewById(R.id.lvStoreItems);
/*        lvStoreItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemsTable.toggleStrikeOut(getActivity(), id);
            }
        });

        lvStoreItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showEditItemDialog(id);
                return true;
            }
        });*/

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle args = getArguments();
            if (args.containsKey(ARG_STORE_ID)) {
                mStorePosition = args.getInt(ARG_STORE_POSITION);
                mStoreID = args.getLong(ARG_STORE_ID);
                mDisplayName = args.getString(ARG_DISPLAY_NAME);
                mColorThemeID = args.getLong(ARG_COLOR_THEME_ID);
                //mStoreItemsSortOrder = args.getInt(ARG_STORE_ITEMS_SORT_ORDER);
            }

        } else {
            if (savedInstanceState.containsKey(ARG_STORE_ID)) {
                mStorePosition = savedInstanceState.getInt(ARG_STORE_POSITION);
                mStoreID = savedInstanceState.getLong(ARG_STORE_ID);
                mDisplayName = savedInstanceState.getString(ARG_DISPLAY_NAME);
                mColorThemeID = savedInstanceState.getLong(ARG_COLOR_THEME_ID);
                //mStoreItemsSortOrder = savedInstanceState.getInt(ARG_STORE_ITEMS_SORT_ORDER);
            }
        }
        MyLog.i("fragStoreList", "onActivityCreated: storeID = " + mStoreID);

        mStoreListCursorAdapter = new StoreListCursorAdapter(getActivity(), null, 0, mStoreID);
        lvStoreItems.setAdapter(mStoreListCursorAdapter);
        mLoaderManager = getLoaderManager();
        mStoreListFragmentCallbacks = this;
        mLoaderManager.initLoader(MySettings.ITEMS_LOADER, null, mStoreListFragmentCallbacks);

        tvStoreTitle.setText(mDisplayName + ": color=" + mColorThemeID);
    }

    private void showEditItemDialog(long itemID) {
        FragmentManager fm = getFragmentManager();
        dialog_edit_item dialog = dialog_edit_item.newInstance(itemID, getActivity().getString(R.string.edit_item_dialog_title));
        dialog.show(fm, "dialog_edit_item");
    }

    public void onEvent(MyEvents.restartLoader event) {
        mLoaderManager.restartLoader(event.getLoaderID(), null, mStoreListFragmentCallbacks);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        MyLog.i("fragStoreList", "onSaveInstanceState: storeID = " + mStoreID);
        outState.putInt(ARG_STORE_POSITION, mStorePosition);
        outState.putLong(ARG_STORE_ID, mStoreID);
        outState.putString(ARG_DISPLAY_NAME, mDisplayName);
        outState.putLong(ARG_COLOR_THEME_ID, mColorThemeID);
        //outState.putInt(ARG_STORE_ITEMS_SORT_ORDER, mStoreItemsSortOrder);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        MyLog.i("fragStoreList", "onPause: storeID = " + mStoreID);
        super.onPause();
    }

    @Override
    public void onResume() {
        MyLog.i("fragStoreList", "onResume: storeID = " + mStoreID);
        super.onResume();
    }

    @Override
    public void onDestroy() {
        MyLog.i("fragStoreList", "onDestroy: storeID = " + mStoreID);
       EventBus.getDefault().unregister(this);

        super.onDestroy();
    }


/*    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        switch (id) {
            case MySettings.ITEMS_LOADER:
                int storeItemsSortOrder = StoresTable.getStoreItemsSortingOrder(getActivity(), mStoreID);
                MyLog.i("fragStoreList", "onCreateLoader: ITEMS_LOADER: storeID = " + mStoreID + " sort order = " + storeItemsSortOrder);
                String sortOrder;
                try {
                    switch (storeItemsSortOrder) {
                        case MySettings.SORT_ALPHABETICAL:
                            mStoreListCursorAdapter.setStoreItemsSortingOrder(MySettings.SORT_ALPHABETICAL, mStoreID);
                            sortOrder = ItemsTable.SORT_ORDER_ITEM_NAME_ASC;
                            cursorLoader = ItemsTable.getAllSelectedItems(getActivity(), sortOrder);
                            break;

                        case MySettings.SORT_BY_AISLE:
                            mStoreListCursorAdapter.setStoreItemsSortingOrder(MySettings.SORT_BY_AISLE, mStoreID);
                            cursorLoader = ItemsTable.getAllSelectedItemsByLocations(getActivity(), mStoreID);
                            break;

                        case MySettings.SORT_BY_GROUP:
                            mStoreListCursorAdapter.setStoreItemsSortingOrder(MySettings.SORT_BY_GROUP, mStoreID);
                            cursorLoader = ItemsTable.getAllSelectedItemsByGroups(getActivity(), mStoreID);
                            break;

                        case MySettings.SORT_MANUALLY:
                            //TODO: join query master list manual sort order
                            sortOrder = ItemsTable.SORT_ORDER_SORT_KEY;
                            //cursorLoader = ItemsTable.getAllItemsInList(getActivity(), mActiveListID, selection, sortOrder);
                            break;

                    }

                } catch (SQLiteException e) {
                    MyLog.e("fragStoreList", "onCreateLoader: SQLiteException: " + e.getMessage());
                    return null;

                } catch (IllegalArgumentException e) {
                    MyLog.i("fragStoreList", "onCreateLoader: IllegalArgumentException: " + e.getMessage());
                    return null;
                }
                break;


        }
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {
        switch (loader.getId()) {
            case MySettings.ITEMS_LOADER:
                MyLog.i("fragStoreList", "onLoadFinished: ITEMS_LOADER: storeID = " + mStoreID);
                //String result = DatabaseUtils.dumpCursorToString(newCursor);
                mStoreListCursorAdapter.swapCursor(newCursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case MySettings.ITEMS_LOADER:
                MyLog.i("fragStoreList", "onLoaderReset: ITEMS_LOADER: storeID = " + mStoreID);
                mStoreListCursorAdapter.swapCursor(null);
                break;
        }
    }
}
