package com.lbconsulting.agrocerylist.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.lbconsulting.agrocerylist.R;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Helper methods for Application Settings
 */
public class MySettings {


    public static final int FRAG_STORE_LISTS = 1;
    public static final int FRAG_PRODUCTS_LIST = 2;
    public static final int FRAG_MASTER_LIST = 3;
    public static final int FRAG_SET_GROUPS = 4;
    public static final int FRAG_CULL_ITEMS = 5;
    public static final int FRAG_GROUPS_LIST = 6;
    public static final int FRAG_ITEMS_BY_GROUP = 7;

    public static String getFragmentTag(int fragmentID) {
        String fragmentTag = "";
        switch (fragmentID) {
            case FRAG_STORE_LISTS:
                fragmentTag = "FRAG_STORE_LISTS";
                break;

            case FRAG_PRODUCTS_LIST:
                fragmentTag = "FRAG_PRODUCTS_LIST";
                break;

            case FRAG_MASTER_LIST:
                fragmentTag = "FRAG_MASTER_LIST";
                break;

            case FRAG_SET_GROUPS:
                fragmentTag = "FRAG_SET_GROUPS";
                break;

            case FRAG_CULL_ITEMS:
                fragmentTag = "FRAG_CULL_ITEMS";
                break;

            case FRAG_GROUPS_LIST:
                fragmentTag = "FRAG_GROUPS_LIST";
                break;

            case FRAG_ITEMS_BY_GROUP:
                fragmentTag = "FRAG_ITEMS_BY_GROUP";
                break;
        }

        return fragmentTag;
    }

    public static final int ITEMS_LOADER = 1;
    public static final int PRODUCTS_LOADER = 2;
    public static final int GROUPS_LOADER = 3;

    public static final int INITIAL_NUMBER_OF_AISLES = 25;
    public static final int NETWORK_WIFI_ONLY = 0;
    public static final int NETWORK_ANY = 1;
    public static final String NOT_AVAILABLE = "N/A...N/A";
    public static final String UNKNOWN = "UNKNOWN";

    private static final String A_GROCERY_LIST_SAVED_STATES = "aGroceryListSavedStates";
    private static final String DROPBOX_FILENAME = "aGroceryList.txt";
    private static final String SETTING_ACTIVE_FRAGMENT_ID = "activeFragmentID";
    private static final String SETTING_ACTIVE_USER_ID = "activeUserID";
    private static final String SETTING_DROPBOX_ACCESS_TOKEN = "dropboxAccessToken";
    private static final String SETTING_DROPBOX_FILE_REV = "dropboxFileRev";
    private static final String SETTING_DROPBOX_FOLDER_NAME = "dropboxFolderName";
    private static final String SETTING_IS_VERBOSE = "isVerbose";
    private static final String SETTING_NETWORK_PREFERENCE = "networkPreference";
    private static final String SETTING_MASTER_LIST_SORT_ORDER = "masterListSortOrder";
    public static final String SETTING_ACTIVE_STORE_ID = "activeStoreID";
    private static final String SETTING_STORE_SORTING_ORDER = "storeSortingOrder";
    public static final String SETTING_SHOW_FAVORITES = "showFavorites";
    //public static final String SETTING_PARSE_ITEMS_TIMESTAMP = "parseItemsTimestamp";
    public static final String SETTING_CLOSEST_STORES_NUMBER = "closestStoresNumber";
    public static final String SETTING_LAST_LONGITUDE = "lastLongitude";
    public static final String SETTING_LAST_LATITUDE = "lastLatitude";


    public static final String SETTING_LAST_SYNC_DATE_GROUPS = "lastSyncDateGroups";
    public static final String SETTING_LAST_SYNC_DATE_ITEMS = "lastSyncDateItems";
    public static final String SETTING_LAST_SYNC_DATE_LOCATIONS = "lastSyncDateLocations";
    public static final String SETTING_LAST_SYNC_DATE_STORE_CHAINS = "lastSyncDateStoreChains";
    public static final String SETTING_LAST_SYNC_DATE_STORE_MAPS = "lastSyncDateStoreMaps";
    public static final String SETTING_LAST_SYNC_DATE_STORES = "lastSyncDateStores";
    public static final int LAST_SYNC_DATE_GROUPS = 201;
    public static final int LAST_SYNC_DATE_ITEMS = 202;
    public static final int LAST_SYNC_DATE_LOCATIONS = 203;
    public static final int LAST_SYNC_DATE_STORE_CHAINS = 204;
    public static final int LAST_SYNC_DATE_STORE_MAPS = 205;
    public static final int LAST_SYNC_DATE_STORES = 206;


    public static final int SORT_ALPHABETICAL = 0;
    public static final int SORT_BY_AISLE = 1;
    public static final int SORT_BY_GROUP = 2;
    public static final int SORT_FAVORITES_FIRST = 3;
    public static final int SORT_LAST_USED = 4;
    public static final int SORT_MANUALLY = 5;
    public static final int SORT_SELECTED_FIRST = 6;


    private static Context mContext;

    public static void setContext(Context context) {

        mContext = context;
    }

    public static int getActiveFragmentID() {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return savedState.getInt(SETTING_ACTIVE_FRAGMENT_ID, FRAG_STORE_LISTS);
    }

    public static void setActiveFragmentID(int activeFragmentID) {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = savedState.edit();
        editor.putInt(SETTING_ACTIVE_FRAGMENT_ID, activeFragmentID);
        editor.apply();
    }

    public static boolean getIsVerbose() {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return savedState.getBoolean(SETTING_IS_VERBOSE, false);
    }

    public static void setIsVerbose(boolean isVerbose) {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = savedState.edit();
        editor.putBoolean(SETTING_IS_VERBOSE, isVerbose);
        editor.apply();
    }

    public static long getActiveUserID() {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return savedState.getLong(SETTING_ACTIVE_USER_ID, -1);
    }

    public static void setActiveUserID(long userID) {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = savedState.edit();
        editor.putLong(SETTING_ACTIVE_USER_ID, userID);
        editor.apply();
    }

    //region Dropbox Token, Folder, and Rev Settings
    public static String getDropboxAccessToken() {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return savedState.getString(SETTING_DROPBOX_ACCESS_TOKEN, UNKNOWN);
    }

    public static void setDropboxAccessToken(String accessToken) {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = savedState.edit();
        editor.putString(SETTING_DROPBOX_ACCESS_TOKEN, accessToken);
        editor.apply();
    }

    public static String getDropboxFolderName() {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return savedState.getString(SETTING_DROPBOX_FOLDER_NAME, mContext.getString(R.string.dropbox_default_path));
    }

    public static void setDropboxFolderName(String dropboxFolderName) {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = savedState.edit();
        editor.putString(SETTING_DROPBOX_FOLDER_NAME, dropboxFolderName);
        editor.apply();
    }

    public static String getDropboxFilename() {
        return getDropboxFolderName() + "/" + DROPBOX_FILENAME;
    }

    public static String getDropboxFileRev() {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return savedState.getString(SETTING_DROPBOX_FILE_REV, UNKNOWN);
    }

    public static void setFileRev(String dropboxFileRev) {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = savedState.edit();
        editor.putString(SETTING_DROPBOX_FILE_REV, dropboxFileRev);
        editor.apply();
    }
    //endregion

    public static int getNetworkPreference() {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return savedState.getInt(SETTING_NETWORK_PREFERENCE, NETWORK_ANY);
    }

    public static void setNetworkPreference(int networkPreference) {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = savedState.edit();
        editor.putInt(SETTING_NETWORK_PREFERENCE, networkPreference);
        editor.apply();
    }


    public static int getMasterListSortOrder() {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return savedState.getInt(SETTING_MASTER_LIST_SORT_ORDER, SORT_ALPHABETICAL);
    }

    public static void setMasterListSortOrder(int masterListSortOrder) {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = savedState.edit();
        editor.putInt(SETTING_MASTER_LIST_SORT_ORDER, masterListSortOrder);
        editor.apply();
    }

    public static long getActiveStoreID() {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return savedState.getLong(SETTING_ACTIVE_STORE_ID, -1);
    }

    public static void setActiveStoreID(long storeID) {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = savedState.edit();
        editor.putLong(SETTING_ACTIVE_STORE_ID, storeID);
        editor.apply();
    }

    public static int getStoreSortingOrder() {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return savedState.getInt(SETTING_STORE_SORTING_ORDER, SORT_ALPHABETICAL);
    }

    public static void setStoreSortingOrder(int storeSortingOrder) {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = savedState.edit();
        editor.putInt(SETTING_STORE_SORTING_ORDER, storeSortingOrder);
        editor.apply();
    }

/*    public static boolean showAllSelectedItemsInStoreList() {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return savedState.getBoolean(SETTING_SHOW_ALL_ITEMS_IN_STORE_LIST, false);
    }

    public static void setShowAllSelectedItemsInStoreList(boolean showAllSelectedItemsInStoreList) {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = savedState.edit();
        editor.putBoolean(SETTING_SHOW_ALL_ITEMS_IN_STORE_LIST, showAllSelectedItemsInStoreList);
        editor.apply();
    }*/

    public static boolean showFavorites() {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return savedState.getBoolean(SETTING_SHOW_FAVORITES, false);
    }

    public static void setShowFavorites(boolean showFavorites) {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = savedState.edit();
        editor.putBoolean(SETTING_SHOW_FAVORITES, showFavorites);
        editor.apply();
    }

/*    public static long getParseItemsTimestamp() {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return savedState.getLong(SETTING_PARSE_ITEMS_TIMESTAMP, 0);
    }

    public static void setParseItemsTimestamp(long parseItemsTimestamp) {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = savedState.edit();
        editor.putLong(SETTING_PARSE_ITEMS_TIMESTAMP, parseItemsTimestamp);
        editor.apply();
    }*/

    private static Date mills2Date(long mills) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(mills);
        cal.setTimeZone(TimeZone.getDefault());
        return cal.getTime();
    }

    public static Date getLastSyncDate(int updatedAt) {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        long lastSyncDateMills = 0;
        switch (updatedAt) {
            case LAST_SYNC_DATE_GROUPS:
                lastSyncDateMills = savedState.getLong(SETTING_LAST_SYNC_DATE_GROUPS, 0);
                break;

            case LAST_SYNC_DATE_ITEMS:
                lastSyncDateMills = savedState.getLong(SETTING_LAST_SYNC_DATE_ITEMS, 0);
                break;

            case LAST_SYNC_DATE_LOCATIONS:
                lastSyncDateMills = savedState.getLong(SETTING_LAST_SYNC_DATE_LOCATIONS, 0);
                break;

            case LAST_SYNC_DATE_STORE_CHAINS:
                lastSyncDateMills = savedState.getLong(SETTING_LAST_SYNC_DATE_STORE_CHAINS, 0);
                break;

            case LAST_SYNC_DATE_STORE_MAPS:
                lastSyncDateMills = savedState.getLong(SETTING_LAST_SYNC_DATE_STORE_MAPS, 0);
                break;

            case LAST_SYNC_DATE_STORES:
                lastSyncDateMills = savedState.getLong(SETTING_LAST_SYNC_DATE_STORES, 0);
                break;
        }
        return mills2Date(lastSyncDateMills);
    }

    public static void setLastSyncDate(int table, Date date) {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = savedState.edit();
        switch (table) {
            case LAST_SYNC_DATE_GROUPS:
                editor.putLong(SETTING_LAST_SYNC_DATE_GROUPS, date.getTime());
                break;

            case LAST_SYNC_DATE_ITEMS:
                editor.putLong(SETTING_LAST_SYNC_DATE_ITEMS, date.getTime());
                break;

            case LAST_SYNC_DATE_LOCATIONS:
                editor.putLong(SETTING_LAST_SYNC_DATE_LOCATIONS, date.getTime());
                break;

            case LAST_SYNC_DATE_STORE_CHAINS:
                editor.putLong(SETTING_LAST_SYNC_DATE_STORE_CHAINS, date.getTime());
                break;

            case LAST_SYNC_DATE_STORE_MAPS:
                editor.putLong(SETTING_LAST_SYNC_DATE_STORE_MAPS, date.getTime());
                break;

            case LAST_SYNC_DATE_STORES:
                editor.putLong(SETTING_LAST_SYNC_DATE_STORES, date.getTime());
                break;
        }
        editor.apply();
    }

    public static int getClosestStoreNumber() {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return savedState.getInt(SETTING_CLOSEST_STORES_NUMBER, 5);
    }

    public static void setClosestStoreNumber(int closestStoreNumber) {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = savedState.edit();
        editor.putInt(SETTING_CLOSEST_STORES_NUMBER, closestStoreNumber);
        editor.apply();
    }

    public static Location getLastLocation() {
        Location location = null;
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        long latitude = savedState.getLong(SETTING_LAST_LATITUDE, 0);
        long longitude = savedState.getLong(SETTING_LAST_LONGITUDE, 0);

        if (latitude != 0) {
            location = new Location("");
            location.setLatitude(Double.longBitsToDouble(latitude));
            location.setLongitude(Double.longBitsToDouble(longitude));
        }

        return location;
    }

    public static void setLastLocation(Location location) {
        SharedPreferences savedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = savedState.edit();
        if (location == null) {
            editor.putLong(SETTING_LAST_LATITUDE, 0);
            editor.putLong(SETTING_LAST_LONGITUDE, 0);
        } else {
            long latitude = Double.doubleToRawLongBits(location.getLatitude());
            long longitude = Double.doubleToRawLongBits(location.getLongitude());
            editor.putLong(SETTING_LAST_LATITUDE, latitude);
            editor.putLong(SETTING_LAST_LONGITUDE, longitude);
        }
        editor.apply();
    }
}
