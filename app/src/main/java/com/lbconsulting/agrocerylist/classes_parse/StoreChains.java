package com.lbconsulting.agrocerylist.classes_parse;

/**
 * a ParseObject that holds Parse StoreChains data
 */

import android.database.Cursor;

import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.database.StoreChainsTable;
import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("StoreChains")
public class StoreChains extends ParseObject {
    private static final String AUTHOR = "author";

    public StoreChains() {
        // A default constructor is required.
    }

    public void setStoreChain(String storeChainName, long sortKey) {
        setStoreChainName(storeChainName);
        setSortKey(sortKey);
        setAuthor(ParseUser.getCurrentUser());
    }

    public void setStoreChainsCursor(Cursor cursor) {
        if (cursor == null) {
            return;
        }
        // String currentRow = DatabaseUtils.dumpCurrentRowToString(cursor);
        setStoreChainName(cursor.getString(cursor.getColumnIndex(StoreChainsTable.COL_STORE_CHAIN_NAME)));
        setSortKey(cursor.getLong(cursor.getColumnIndex(StoreChainsTable.COL_SORT_KEY)));
        setAuthor(ParseUser.getCurrentUser());
    }

    public long getSortKey() {
        return getLong(StoreChainsTable.SORT_ORDER_SORT_KEY);
    }

    public void setSortKey(long sortKey) {
        put(StoreChainsTable.COL_SORT_KEY, sortKey);
    }

    public String getStoreChainName() {
        return getString(StoreChainsTable.COL_STORE_CHAIN_NAME);
    }

    public void setStoreChainName(String storeChainName) {
        put(StoreChainsTable.COL_STORE_CHAIN_NAME, storeChainName);
    }

    public ParseUser getAuthor() {
        return getParseUser(AUTHOR);
    }

    public void setAuthor(ParseUser currentUser) {
        put(AUTHOR, currentUser);
    }

    public static ParseQuery<StoreChains> getQuery() {
        return ParseQuery.getQuery(StoreChains.class);
    }

    public static ParseObject getStoreChain(String parseObjectID) {
        ParseObject storeChain = null;
        try {
            ParseQuery<StoreChains> query = getQuery();
            query.whereEqualTo("objectID", parseObjectID);
            List chains = query.find();
            if (chains != null && chains.size() > 0) {
                storeChain = (ParseObject) chains.get(0);
            }
        } catch (ParseException e) {
            MyLog.e("StoreChains", "getStoreChain: ParseException: " + e.getMessage());
        }
        return storeChain;
    }

    public static void saveStoreChainToParse(StoreChains storeChain, int saveType) {
        ParseACL storeChainACL = new ParseACL(ParseUser.getCurrentUser());
        storeChainACL.setPublicReadAccess(true);
        storeChainACL.setPublicWriteAccess(true);
        storeChain.setACL(storeChainACL);
        try {
            switch (saveType) {
                case clsParseUtils.SAVE_THIS_THREAD:
                    // use SAVE_THIS_THREAD if this method is being called in an AsyncTask or background thread.
                    storeChain.save();
                    MyLog.i("clsParseUtils", "saveStoreChainToParse: SAVE(): name = " + storeChain.getStoreChainName());
                    break;

                case clsParseUtils.SAVE_IN_BACKGROUND:
                    storeChain.saveInBackground();
                    MyLog.i("clsParseUtils", "saveStoreChainToParse: SAVE_IN_BACKGROUND(): name = " + storeChain.getStoreChainName());
                    break;

                case clsParseUtils.SAVE_EVENTUALLY:
                    storeChain.saveEventually();
                    MyLog.i("clsParseUtils", "saveStoreChainToParse: SAVE_EVENTUALLY(): name = " + storeChain.getStoreChainName());
                    break;
            }

        } catch (ParseException e) {
            MyLog.e("StoreChains", "saveStoreChainToParse: ParseException: " + e.getMessage());
        }
    }
}


