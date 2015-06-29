package com.lbconsulting.agrocerylist.fragments;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
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

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.adapters.ProductsCursorAdapter;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;
import com.lbconsulting.agrocerylist.database.ProductsTable;


/**
 * A placeholder fragment containing a simple view.
 */
public class fragProductsList extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private ListView lvProducts;
    private ProductsCursorAdapter mProductsCursorAdapter;

    public fragProductsList() {
    }

    public static fragProductsList newInstance() {

        return new fragProductsList();
/*        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_TWO_PANE, isTwoPane);
        fragment.setArguments(args);
        return fragment;*/
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MyLog.i("fragProductsList", "onCreateView");
        View rootView = inflater.inflate(R.layout.frag_products_list, container, false);
        lvProducts = (ListView) rootView.findViewById(R.id.lvProducts);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("fragProductsList", "onActivityCreated");
        MySettings.setActiveFragmentID(MySettings.FRAG_PRODUCTS_LIST);

        LoaderManager.LoaderCallbacks<Cursor> mProductsCallbacks = this;
        LoaderManager mLoaderManager = getLoaderManager();

        mProductsCursorAdapter = new ProductsCursorAdapter(getActivity(), null, 0);
        lvProducts.setAdapter(mProductsCursorAdapter);
        mLoaderManager.initLoader(MySettings.PRODUCTS_LOADER, null, mProductsCallbacks);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        MyLog.i("fragProductsList", "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        MyLog.i("fragProductsList", "onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        MyLog.i("fragProductsList", "onResume");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        MyLog.i("fragProductsList", "onDestroy");
        super.onDestroy();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader cursorLoader = null;
        String sortOrder = ProductsTable.SORT_ORDER_TIME_STAMP;
        switch (id) {
            case MySettings.PRODUCTS_LOADER:
                MyLog.i("fragProductsList", "onCreateLoader. Loading MySettings.PRODUCTS_LOADER");
                cursorLoader = ProductsTable.getAllProductsCursorLoader(getActivity(), sortOrder);
                break;
        }

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor newCursor) {

        switch (loader.getId()) {
            case MySettings.PRODUCTS_LOADER:
                MyLog.i("fragProductsList", "onLoadFinished MySettings.PRODUCTS_LOADER");
                mProductsCursorAdapter.swapCursor(newCursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        switch (loader.getId()) {
            case MySettings.PRODUCTS_LOADER:
                MyLog.i("fragProductsList", "onLoaderReset MySettings.PRODUCTS_LOADER");
                mProductsCursorAdapter.swapCursor(null);
                break;

        }
    }
}
