package com.lbconsulting.agrocerylist.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lbconsulting.agrocerylist.database.StoreChainsTable;
import com.lbconsulting.agrocerylist.database.StoresTable;

/**
 * This class holds Store information from the database
 */
public class clsStoreValues {
    private Context mContext;
    private Cursor mStoreCursor;
    private Cursor mStoreChainCursor;
    private ContentValues cv;

    private final String dash = " " + "\u2013" + " ";

    public clsStoreValues(Context context, long storeID) {
        mContext = context;
        mStoreCursor = StoresTable.getStoreCursor(context, storeID);
        if (mStoreCursor != null) {
            mStoreCursor.moveToFirst();
        }

        if (hasData()) {
            long storeChainID = mStoreCursor.getLong(mStoreCursor.getColumnIndex(StoresTable.COL_STORE_CHAIN_ID));
            mStoreChainCursor = StoreChainsTable.getStoreChainCursor(context, storeChainID);
            if (mStoreChainCursor != null) {
                mStoreChainCursor.moveToFirst();
            }
        }
       /* String cursorContent = DatabaseUtils.dumpCursorToString(mStoreCursor);
        MyLog.d("clsStoreValues", cursorContent);*/
        cv = new ContentValues();

    }

    public clsStoreValues(Context context, Cursor storeCursor) {
        mStoreCursor = storeCursor;
        if (hasData()) {
            long storeChainID = mStoreCursor.getLong(mStoreCursor.getColumnIndex(StoresTable.COL_STORE_CHAIN_ID));
            mStoreChainCursor = StoreChainsTable.getStoreChainCursor(context, storeChainID);
            if (mStoreChainCursor != null) {
                mStoreChainCursor.moveToFirst();
            }
        }
        cv = new ContentValues();
    }

    public boolean hasData() {
        return mStoreCursor != null && mStoreCursor.getCount() > 0;
    }


    public String getCity() {
        String result = "";
        if (hasData()) {
            result = mStoreCursor.getString(mStoreCursor.getColumnIndex(StoresTable.COL_CITY));
        }
        return result;
    }

    public void putCity(String city) {
        if (cv.containsKey(StoresTable.COL_CITY)) {
            cv.remove(StoresTable.COL_CITY);
        }
        cv.put(StoresTable.COL_CITY, city);
    }

    public String getGpsLatitude() {
        String result = "";
        if (hasData()) {
            result = mStoreCursor.getString(mStoreCursor.getColumnIndex(StoresTable.COL_GPS_LATITUDE));
        }
        return result;
    }

    public void putGpsLatitude(String gpsLatitude) {
        if (cv.containsKey(StoresTable.COL_GPS_LATITUDE)) {
            cv.remove(StoresTable.COL_GPS_LATITUDE);
        }
        cv.put(StoresTable.COL_GPS_LATITUDE, gpsLatitude);
    }

    public String getGpsLongitude() {
        String result = "";
        if (hasData()) {
            result = mStoreCursor.getString(mStoreCursor.getColumnIndex(StoresTable.COL_GPS_LONGITUDE));
        }
        return result;
    }

    public void putGpsLongitude(String gpsLongitude) {
        if (cv.containsKey(StoresTable.COL_GPS_LONGITUDE)) {
            cv.remove(StoresTable.COL_GPS_LONGITUDE);
        }
        cv.put(StoresTable.COL_GPS_LONGITUDE, gpsLongitude);
    }

    public String getPhoneNumber() {
        String result = "";
        if (hasData()) {
            result = mStoreCursor.getString(mStoreCursor.getColumnIndex(StoresTable.COL_PHONE_NUMBER));
        }
        return result;
    }

    public void putPhoneNumber(String phoneNumber) {
        if (cv.containsKey(StoresTable.COL_PHONE_NUMBER)) {
            cv.remove(StoresTable.COL_PHONE_NUMBER);
        }
        cv.put(StoresTable.COL_PHONE_NUMBER, phoneNumber);
    }

    public String getState() {
        String result = "";
        if (hasData()) {
            result = mStoreCursor.getString(mStoreCursor.getColumnIndex(StoresTable.COL_STATE));
        }
        return result;
    }

    public void putState(String state) {
        if (cv.containsKey(StoresTable.COL_STATE)) {
            cv.remove(StoresTable.COL_STATE);
        }
        cv.put(StoresTable.COL_STATE, state);
    }

    public long getStoreChainID() {
        long result = -1;
        if (hasData()) {
            result = mStoreCursor.getLong(mStoreCursor.getColumnIndex(StoresTable.COL_STORE_CHAIN_ID));
        }
        return result;
    }

    public void putStoreChainID(long storeChainID) {
        if (cv.containsKey(StoresTable.COL_STORE_CHAIN_ID)) {
            cv.remove(StoresTable.COL_STORE_CHAIN_ID);
        }
        cv.put(StoresTable.COL_STORE_CHAIN_ID, storeChainID);
    }

    public boolean isStoreChecked() {
        boolean result = false;
        if (hasData()) {
            result = mStoreCursor.getInt(mStoreCursor.getColumnIndex(StoresTable.COL_CHECKED)) > 0;
        }
        return result;
    }

    public void putStoreChecked(boolean storeChecked) {
        if (cv.containsKey(StoresTable.COL_CHECKED)) {
            cv.remove(StoresTable.COL_CHECKED);
        }
        int checkedValue = 0;
        if (storeChecked) {
            checkedValue = 1;
        }
        cv.put(StoresTable.COL_CHECKED, checkedValue);
    }

    public long getStoreID() {
        long result = -1;
        if (hasData()) {
            result = mStoreCursor.getLong(mStoreCursor.getColumnIndex(StoresTable.COL_STORE_ID));
        }
        return result;
    }


    public String getStoreRegionalName() {
        String result = "";
        if (hasData()) {
            result = mStoreCursor.getString(mStoreCursor.getColumnIndex(StoresTable.COL_STORE_REGIONAL_NAME));
        }
        return result;
    }

    public void putStoreRegionalName(String storeRegionalName) {
        if (cv.containsKey(StoresTable.COL_STORE_REGIONAL_NAME)) {
            cv.remove(StoresTable.COL_STORE_REGIONAL_NAME);
        }
        cv.put(StoresTable.COL_STORE_REGIONAL_NAME, storeRegionalName);
    }

    public String getStreet1() {
        String result = "";
        if (hasData()) {
            result = mStoreCursor.getString(mStoreCursor.getColumnIndex(StoresTable.COL_STREET1));
        }
        return result;
    }

    public void putStreet1(String street1) {
        if (cv.containsKey(StoresTable.COL_STREET1)) {
            cv.remove(StoresTable.COL_STREET1);
        }
        cv.put(StoresTable.COL_STREET1, street1);
    }

    public String getStreet2() {
        String result = "";
        if (hasData()) {
            result = mStoreCursor.getString(mStoreCursor.getColumnIndex(StoresTable.COL_STREET2));
        }
        return result;
    }

    public void putStreet2(String street2) {
        if (cv.containsKey(StoresTable.COL_STREET2)) {
            cv.remove(StoresTable.COL_STREET2);
        }
        cv.put(StoresTable.COL_STREET2, street2);
    }

    public String getWebsiteURL() {
        String result = "";
        if (hasData()) {
            result = mStoreCursor.getString(mStoreCursor.getColumnIndex(StoresTable.COL_WEBSITE_URL));
        }
        return result;
    }

    public void putWebsiteURL(String websiteURL) {
        if (cv.containsKey(StoresTable.COL_WEBSITE_URL)) {
            cv.remove(StoresTable.COL_WEBSITE_URL);
        }
        cv.put(StoresTable.COL_WEBSITE_URL, websiteURL);
    }

    public String getZip() {
        String result = "";
        if (hasData()) {
            result = mStoreCursor.getString(mStoreCursor.getColumnIndex(StoresTable.COL_ZIP));
        }
        return result;
    }

    public void putZip(String zip) {
        if (cv.containsKey(StoresTable.COL_ZIP)) {
            cv.remove(StoresTable.COL_ZIP);
        }
        cv.put(StoresTable.COL_ZIP, zip);
    }

    public String getStoreChainName() {
        String result = "";
        if (mStoreChainCursor != null & mStoreChainCursor.getCount() > 0) {
            result = mStoreChainCursor.getString(mStoreCursor.getColumnIndex(StoreChainsTable.COL_STORE_CHAIN_NAME));
        }
        return result;
    }

    public String getDisplayName() {
        String result = "";
        if (hasData()) {
            result = getStoreChainName();
            String storeRegionalName = getStoreRegionalName();
            String city = getCity();
            String state = getState();

            if (!storeRegionalName.isEmpty()) {
                result = result + dash + storeRegionalName;
            }

            if (!city.isEmpty()) {
                result = result + ", " + city;
            }

            if (!state.isEmpty()) {
                result = result + ", " + state;
            }
        }
        return result;
    }

    public void update() {
        if (cv.size() > 0) {
            StoresTable.updateStoreFieldValues(mContext, getStoreID(), cv);
        }
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    protected void finalize() throws Throwable {

        try {
            if (mStoreCursor != null) {
                mStoreCursor.close();
                mStoreCursor = null;
            }
            if (mStoreChainCursor != null) {
                mStoreChainCursor.close();
                mStoreChainCursor = null;
            }
        } finally {
            super.finalize();
        }
    }
}
