package com.lbconsulting.agrocerylist.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.database.StoreChainsTable;
import com.lbconsulting.agrocerylist.database.StoresTable;
import com.lbconsulting.agrocerylist.fragments.fragStoreList;

//FragmentStatePagerAdapter
//FragmentPagerAdapter
public class StoreListPagerAdapter extends FragmentStatePagerAdapter {
    private final String dash = " " + "\u2013" + " ";

    private Cursor mAllStoresCursor;
    private Context mContext;
    private int mCount;

    private long mStoreID;
    private String mDisplayName;
    private long mColorThemeID;

    public StoreListPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
        setAllStoresCursor();
    }

    @Override
    public Fragment getItem(int position) {
        getFragmentArgs(position);
        //MyLog.d("StoreListPagerAdapter", "getItem:  position=" + position + "; storeID=" + storeID);
        Fragment newStoreListFragment = fragStoreList.newInstance(position, mStoreID, mDisplayName, mColorThemeID);
        return newStoreListFragment;
    }

    private void getFragmentArgs(int position) {
        mStoreID = -1;
        mDisplayName = "";
        mColorThemeID = -1;
        try {
            mAllStoresCursor.moveToPosition(position);
            mStoreID = mAllStoresCursor.getLong(mAllStoresCursor.getColumnIndex(StoresTable.COL_STORE_ID));

            String storeChainName = mAllStoresCursor.getString(mAllStoresCursor.getColumnIndex(StoreChainsTable.COL_STORE_CHAIN_NAME));
            String storeRegionalName = mAllStoresCursor.getString(mAllStoresCursor.getColumnIndex(StoresTable.COL_STORE_REGIONAL_NAME));
            mDisplayName = storeChainName + dash + storeRegionalName;

            mColorThemeID = mAllStoresCursor.getLong(mAllStoresCursor.getColumnIndex(StoresTable.COL_COLOR_THEME_ID));

        } catch (Exception e) {
            MyLog.e("StoreListPagerAdapter", "getStoreID: Exception: " + e.getMessage());
        }

    }

    @Override
    public int getCount() {
        return mCount;
    }

    private Cursor setAllStoresCursor() {
        mCount = 0;
        mAllStoresCursor = StoresTable.getAllStoresWithChainNames(mContext, StoresTable.SORT_ORDER_CHAIN_NAME_THEN_STORE_NAME);
        if (mAllStoresCursor != null) {
            mCount = mAllStoresCursor.getCount();
        }
        return mAllStoresCursor;
    }

/*    private long getStoreID(int position) {
        long storeID = -1;
        try {
            mAllStoresCursor.moveToPosition(position);
            storeID = mAllStoresCursor.getLong(mAllStoresCursor.getColumnIndex(StoresTable.COL_STORE_ID));
        } catch (Exception e) {
            MyLog.e("StoreListPagerAdapter", "getStoreID: Exception: " + e.getMessage());
        }
        return storeID;
    }*/

/*    private String getStoreDisplayName(int position) {
        String displayName = "";
        mAllStoresCursor.moveToPosition(position);
        String storeChainName = mAllStoresCursor.getString(mAllStoresCursor.getColumnIndex(StoreChainsTable.COL_STORE_CHAIN_NAME));
        String storeRegionalName = mAllStoresCursor.getString(mAllStoresCursor.getColumnIndex(StoresTable.COL_STORE_REGIONAL_NAME));
        displayName = storeChainName + dash + storeRegionalName;
        return displayName;
    }*/
}
