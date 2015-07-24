package com.lbconsulting.agrocerylist.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;
import com.lbconsulting.agrocerylist.database.JoinedTables;
import com.lbconsulting.agrocerylist.database.StoreChainsTable;
import com.lbconsulting.agrocerylist.database.StoresTable;
import com.lbconsulting.agrocerylist.fragments.fragStoreList;

//FragmentStatePagerAdapter
//FragmentPagerAdapter
public class StoreListPagerAdapter extends FragmentStatePagerAdapter {
    private final String dash = " " + "\u2013" + " ";

    private static Cursor mAllStoresCursor;
    private Context mContext;
    private int mCount;

    private long mStoreID;
    private String mDisplayName;
    private long mColorThemeID;
    //private int mStoreItemsSortOrder;

    public StoreListPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        setAllStoresCursor();
    }

    @Override
    public Fragment getItem(int position) {
        getFragmentArgs(position);
        MyLog.d("StoreListPagerAdapter", "getItem:  position=" + position + "; storeID=" + mStoreID);
        Fragment newStoreListFragment = fragStoreList.newInstance(position, mStoreID, mDisplayName,
                mColorThemeID);
        return newStoreListFragment;
    }

    private void getFragmentArgs(int position) {
        mStoreID = -1;
        mDisplayName = "";
        mColorThemeID = -1;
        //mStoreItemsSortOrder = 0;
        try {
            mAllStoresCursor.moveToPosition(position);
            mStoreID = mAllStoresCursor.getLong(mAllStoresCursor.getColumnIndex(StoresTable.COL_ID));

            String storeChainName = mAllStoresCursor.getString(mAllStoresCursor.getColumnIndex(StoreChainsTable.COL_STORE_CHAIN_NAME));
            String storeRegionalName = mAllStoresCursor.getString(mAllStoresCursor.getColumnIndex(StoresTable.COL_STORE_REGIONAL_NAME));
            mDisplayName = storeChainName + dash + storeRegionalName;

           // mColorThemeID = mAllStoresCursor.getLong(mAllStoresCursor.getColumnIndex(StoresTable.COL_COLOR_THEME_ID));

            //mStoreItemsSortOrder = mAllStoresCursor.getInt(mAllStoresCursor.getColumnIndex(StoresTable.COL_STORE_ITEMS_SORTING_ORDER));

        } catch (Exception e) {
            MyLog.e("StoreListPagerAdapter", "getFragmentArgs: Exception: " + e.getMessage());
        }

    }

    public static long getStoreID(int position) {
        long storeID = -1;
        try {
            mAllStoresCursor.moveToPosition(position);
            storeID = mAllStoresCursor.getLong(mAllStoresCursor.getColumnIndex(StoresTable.COL_ID));
        } catch (Exception e) {
            MyLog.e("StoreListPagerAdapter", "getStoreID: Exception: " + e.getMessage());
        }
        return storeID;
    }

    @Override
    public int getCount() {
        return mCount;
    }

    private Cursor setAllStoresCursor() {
        mCount = 0;
        String sortOrder = JoinedTables.SORT_ORDER_CHAIN_NAME_THEN_STORE_NAME;
        switch (MySettings.getStoreSortingOrder()) {
            case MySettings.SORT_MANUALLY:
                sortOrder = StoresTable.SORT_ORDER_SORT_KEY;
                break;

            default:
                // do nothing ... use the default SORT_ORDER_CHAIN_NAME_THEN_STORE_NAME
        }
        mAllStoresCursor = StoresTable.getAllStoresWithChainNames(mContext, sortOrder);
        if (mAllStoresCursor != null) {
            mCount = mAllStoresCursor.getCount();
        }
        return mAllStoresCursor;
    }

    public static int findStoreIDPosition(long soughtStoreID) {
        // this method searches the stores cursor for the sought storeID
        // if the store is not found, it returns 0.
        int position = 0;
        int index = -1;
        long storeID;
        mAllStoresCursor.moveToPosition(-1);
        while (mAllStoresCursor.moveToNext()) {
            index++;
            storeID = mAllStoresCursor.getLong(mAllStoresCursor.getColumnIndex(StoresTable.COL_ID));
            if (storeID == soughtStoreID) {
                position = index;
                break;
            }
        }
        return position;
    }
}
