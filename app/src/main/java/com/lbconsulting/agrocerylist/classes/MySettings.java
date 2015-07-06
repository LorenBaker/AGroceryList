package com.lbconsulting.agrocerylist.classes;

import android.content.Context;
import android.content.SharedPreferences;

import com.lbconsulting.agrocerylist.R;

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

    public static final int INITIAL_NUMBER_OF_AISLES = 10;
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
    // public static final String SETTING_SHOW_ALL_ITEMS_IN_STORE_LIST = "showAllItemsInStoreList";
    public static final String SETTING_SHOW_FAVORITES = "showFavorites";

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
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return passwordsSavedState.getInt(SETTING_ACTIVE_FRAGMENT_ID, FRAG_STORE_LISTS);
    }

    public static void setActiveFragmentID(int activeFragmentID) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_ACTIVE_FRAGMENT_ID, activeFragmentID);
        editor.apply();
    }

    public static boolean getIsVerbose() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return passwordsSavedState.getBoolean(SETTING_IS_VERBOSE, false);
    }

    public static void setIsVerbose(boolean isVerbose) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putBoolean(SETTING_IS_VERBOSE, isVerbose);
        editor.apply();
    }

    public static long getActiveUserID() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return passwordsSavedState.getLong(SETTING_ACTIVE_USER_ID, -1);
    }

    public static void setActiveUserID(long userID) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putLong(SETTING_ACTIVE_USER_ID, userID);
        editor.apply();
    }

    //region Dropbox Token, Folder, and Rev Settings
    public static String getDropboxAccessToken() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return passwordsSavedState.getString(SETTING_DROPBOX_ACCESS_TOKEN, UNKNOWN);
    }

    public static void setDropboxAccessToken(String accessToken) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putString(SETTING_DROPBOX_ACCESS_TOKEN, accessToken);
        editor.apply();
    }

    public static String getDropboxFolderName() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return passwordsSavedState.getString(SETTING_DROPBOX_FOLDER_NAME, mContext.getString(R.string.dropbox_default_path));
    }

    public static void setDropboxFolderName(String dropboxFolderName) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putString(SETTING_DROPBOX_FOLDER_NAME, dropboxFolderName);
        editor.apply();
    }

    public static String getDropboxFilename() {
        return getDropboxFolderName() + "/" + DROPBOX_FILENAME;
    }

    public static String getDropboxFileRev() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return passwordsSavedState.getString(SETTING_DROPBOX_FILE_REV, UNKNOWN);
    }

    public static void setFileRev(String dropboxFileRev) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putString(SETTING_DROPBOX_FILE_REV, dropboxFileRev);
        editor.apply();
    }
    //endregion

    public static int getNetworkPreference() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return passwordsSavedState.getInt(SETTING_NETWORK_PREFERENCE, NETWORK_ANY);
    }

    public static void setNetworkPreference(int networkPreference) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_NETWORK_PREFERENCE, networkPreference);
        editor.apply();
    }


    public static int getMasterListSortOrder() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return passwordsSavedState.getInt(SETTING_MASTER_LIST_SORT_ORDER, SORT_ALPHABETICAL);
    }

    public static void setMasterListSortOrder(int masterListSortOrder) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_MASTER_LIST_SORT_ORDER, masterListSortOrder);
        editor.apply();
    }

    public static long getActiveStoreID() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return passwordsSavedState.getLong(SETTING_ACTIVE_STORE_ID, -1);
    }

    public static void setActiveStoreID(long storeID) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putLong(SETTING_ACTIVE_STORE_ID, storeID);
        editor.apply();
    }

    public static int getStoreSortingOrder() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return passwordsSavedState.getInt(SETTING_STORE_SORTING_ORDER, SORT_ALPHABETICAL);
    }

    public static void setStoreSortingOrder(int storeSortingOrder) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_STORE_SORTING_ORDER, storeSortingOrder);
        editor.apply();
    }

/*    public static boolean showAllSelectedItemsInStoreList() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return passwordsSavedState.getBoolean(SETTING_SHOW_ALL_ITEMS_IN_STORE_LIST, false);
    }

    public static void setShowAllSelectedItemsInStoreList(boolean showAllSelectedItemsInStoreList) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putBoolean(SETTING_SHOW_ALL_ITEMS_IN_STORE_LIST, showAllSelectedItemsInStoreList);
        editor.apply();
    }*/

    public static boolean showFavorites() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        return passwordsSavedState.getBoolean(SETTING_SHOW_FAVORITES, false);
    }

    public static void setShowFavorites(boolean showFavorites) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(A_GROCERY_LIST_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putBoolean(SETTING_SHOW_FAVORITES, showFavorites);
        editor.apply();
    }
}
