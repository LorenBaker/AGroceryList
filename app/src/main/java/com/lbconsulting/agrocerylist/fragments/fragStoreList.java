package com.lbconsulting.agrocerylist.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;


/**
 * A fragment showing the store's selected items.
 */
public class fragStoreList extends Fragment {

    private static final String ARG_STORE_ID = "argStoreID";
    private static final String ARG_STORE_POSITION = "argStorePosition";
    private static final String ARG_DISPLAY_NAME = "argDisplayName";
    private static final String ARG_COLOR_THEME_ID = "argColorThemeID";

    private TextView tvStoreTitle;
    private ListView lvStoreItems;

    private int mStorePosition = -1;
    private long mStoreID = -1;
    private String mDisplayName;
    private long mColorThemeID;
    //private clsStoreValues mStoreValues;

    public fragStoreList() {
    }

    public static fragStoreList newInstance(int position, long storeID, String displayName, long colorThemeID) {

        fragStoreList fragment = new fragStoreList();
        Bundle args = new Bundle();
        args.putInt(ARG_STORE_POSITION, position);
        args.putLong(ARG_STORE_ID, storeID);
        args.putString(ARG_DISPLAY_NAME, displayName);
        args.putLong(ARG_COLOR_THEME_ID, colorThemeID);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //EventBus.getDefault().register(this);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MyLog.i("fragStoreList", "onCreateView");
        View rootView = inflater.inflate(R.layout.frag_store_list, container, false);

        tvStoreTitle = (TextView) rootView.findViewById(R.id.tvStoreTitle);
        lvStoreItems = (ListView) rootView.findViewById(R.id.lvStoreItems);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("fragStoreList", "onActivityCreated");
        MySettings.setActiveFragmentID(MySettings.HOME_FRAG_STORE_LIST);

        if (savedInstanceState == null) {
            Bundle args = getArguments();
            if (args.containsKey(ARG_STORE_ID)) {
                mStorePosition = args.getInt(ARG_STORE_POSITION);
                mStoreID = args.getLong(ARG_STORE_ID);
                mDisplayName = args.getString(ARG_DISPLAY_NAME);
                mColorThemeID = args.getLong(ARG_COLOR_THEME_ID);
            }

        } else {
            if (savedInstanceState.containsKey(ARG_STORE_ID)) {
                mStorePosition = savedInstanceState.getInt(ARG_STORE_POSITION);
                mStoreID = savedInstanceState.getLong(ARG_STORE_ID);
                mDisplayName = savedInstanceState.getString(ARG_DISPLAY_NAME);
                mColorThemeID = savedInstanceState.getLong(ARG_COLOR_THEME_ID);
            }
        }

        tvStoreTitle.setText(mDisplayName + ": color=" + mColorThemeID);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        MyLog.i("fragStoreList", "onSaveInstanceState");
        outState.putInt(ARG_STORE_POSITION, mStorePosition);
        outState.putLong(ARG_STORE_ID, mStoreID);
        outState.putString(ARG_DISPLAY_NAME, mDisplayName);
        outState.putLong(ARG_COLOR_THEME_ID, mColorThemeID);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        MyLog.i("fragStoreList", "onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        MyLog.i("fragStoreList", "onResume");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        MyLog.i("fragStoreList", "onDestroy");
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

}
