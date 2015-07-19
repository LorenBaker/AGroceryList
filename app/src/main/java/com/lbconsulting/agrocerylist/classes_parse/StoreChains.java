package com.lbconsulting.agrocerylist.classes_parse;

/**
 * a ParseObject that holds Parse StoreChains data
 */

import android.database.Cursor;

import com.lbconsulting.agrocerylist.database.StoreChainsTable;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("StoreChains")
public class StoreChains extends ParseObject {
    private static final String COL_STORE_CHAIN_ID = "storeChainID";
    private static final String AUTHOR = "author";

    public StoreChains() {
        // A default constructor is required.
    }

    public void setStoreChain(long id, String storeChainName) {
        setStoreChainID(id);
        setStoreChainName(storeChainName);
        setAuthor(ParseUser.getCurrentUser());
    }
    public void setStoreChainsCursor(Cursor cursor) {
        if (cursor == null) {
            return;
        }
        // String currentRow = DatabaseUtils.dumpCurrentRowToString(cursor);
        setStoreChainID(cursor.getLong(cursor.getColumnIndex(StoreChainsTable.COL_STORE_CHAIN_ID)));
        setStoreChainName(cursor.getString(cursor.getColumnIndex(StoreChainsTable.COL_STORE_CHAIN_NAME)));
        setAuthor(ParseUser.getCurrentUser());
    }

    public long getStoreChainID() {
        return getLong(COL_STORE_CHAIN_ID);
    }

    public void setStoreChainID(long locationID) {
        put(COL_STORE_CHAIN_ID, locationID);
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



}


