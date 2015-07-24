package com.lbconsulting.agrocerylist.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lbconsulting.agrocerylist.database.StoreChainsTable;

/**
 * This class holds Store Chain information from the database
 */
public class clsStoreChainValues {
    private Context mContext;
    private Cursor mStoreChainCursor;
    private ContentValues cv;
    
/*    private long storeChainID;
    private String storeChainName;
    private boolean checked;*/

    public clsStoreChainValues(Context context, long storeChainID) {
        mContext = context;
        mStoreChainCursor = StoreChainsTable.getStoreChainCursor(context, storeChainID);
        if (mStoreChainCursor != null) {
            mStoreChainCursor.moveToFirst();
        }
       /* String cursorContent = DatabaseUtils.dumpCursorToString(mStoreChainCursor);
        MyLog.d("clsStoreValues", cursorContent);*/
        cv = new ContentValues();

    }

    public clsStoreChainValues(Context context, Cursor storeChainCursor) {
        mStoreChainCursor = storeChainCursor;
        cv = new ContentValues();
    }

    public boolean hasData() {
        return mStoreChainCursor != null && mStoreChainCursor.getCount() > 0;
    }

    public boolean isChecked() {
        boolean result = false;
        if (hasData()) {
            int itemCheckedValue = mStoreChainCursor.getInt(mStoreChainCursor.getColumnIndex(StoreChainsTable.COL_CHECKED));
            result = itemCheckedValue > 0;
        }
        return result;
    }

    public void putChecked(boolean checked) {
        if (cv.containsKey(StoreChainsTable.COL_CHECKED)) {
            cv.remove(StoreChainsTable.COL_CHECKED);
        }
        int checkedValue = 0;
        if (checked) {
            checkedValue = 1;
        }
        cv.put(StoreChainsTable.COL_CHECKED, checkedValue);
    }

    public long getStoreChainID() {
        long result = -1;
        if (hasData()) {
            result = mStoreChainCursor.getLong(mStoreChainCursor.getColumnIndex(StoreChainsTable.COL_ID));
        }
        return result;
    }

    public String getStoreChainName() {
        String result = "";
        if (hasData()) {
            result = mStoreChainCursor.getString(mStoreChainCursor.getColumnIndex(StoreChainsTable.COL_STORE_CHAIN_NAME));
        }
        return result;
    }

    public void putStoreChainName(String storeChainName) {
        if (cv.containsKey(StoreChainsTable.COL_STORE_CHAIN_NAME)) {
            cv.remove(StoreChainsTable.COL_STORE_CHAIN_NAME);
        }
        cv.put(StoreChainsTable.COL_STORE_CHAIN_NAME, storeChainName);
    }

    public void update() {
        if (cv.size() > 0) {
            StoreChainsTable.updateStoreChainFieldValues(mContext, getStoreChainID(), cv);
        }
    }

    @Override
    public String toString() {
        return getStoreChainName();
    }

    protected void finalize() throws Throwable {

        try {
            if (mStoreChainCursor != null) {
                mStoreChainCursor.close();
                mStoreChainCursor = null;
            }

        } finally {
            super.finalize();
        }
    }
}
