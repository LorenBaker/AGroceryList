package com.lbconsulting.agrocerylist.classes_parse;

import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.database.StoresTable;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * This class holds Store information
 */
@ParseClassName("Stores")
public class Stores extends ParseObject {
    private static final String COL_STORE_ID = "storeID";
    private static final String AUTHOR = "author";

    public Stores() {
        // A default constructor is required.
    }

    public void setStore(ParseObject storeChain,
                         String storeRegionalName,
                         String address1,  // number and street
                         String address2,  // ste, etc
                         String city, String state, String zipCode) {

        setStoreChain(storeChain);
        setStoreRegionalName(storeRegionalName);
        setAddress1(address1);
        setAddress2(address2);
        setCity(city);
        setState(state);
        setZip(zipCode);
        setCountry("USA");
        setAuthor(ParseUser.getCurrentUser());
        setWebsiteURL("");
        setPhoneNumber("");
        //  Geo Location set in cloud-code
    }

/*    public void setStoresCursor(Cursor cursor) {
        if (cursor == null) {
            return;
        }
        setStoreID(cursor.getLong(cursor.getColumnIndex(StoresTable.COL_ID)));
        setStoreChain(cursor.getLong(cursor.getColumnIndex(StoresTable.COL_ID)));
        setStoreRegionalName(cursor.getString(cursor.getColumnIndex(StoresTable.COL_STORE_REGIONAL_NAME)));
        setAddress1(cursor.getString(cursor.getColumnIndex(StoresTable.COL_ADDRESS1)));
        setAddress2(cursor.getString(cursor.getColumnIndex(StoresTable.COL_ADDRESS2)));
        setCity(cursor.getString(cursor.getColumnIndex(StoresTable.COL_CITY)));
        setState(cursor.getString(cursor.getColumnIndex(StoresTable.COL_STATE)));
        setZip(cursor.getString(cursor.getColumnIndex(StoresTable.COL_ZIP)));
*//*        setGpsLatitude(cursor.getString(cursor.getColumnIndex(StoresTable.COL_LATITUDE)));
        setGpsLongitude(cursor.getString(cursor.getColumnIndex(StoresTable.COL_LONGITUDE)));*//*
        setWebsiteURL(cursor.getString(cursor.getColumnIndex(StoresTable.COL_WEBSITE_URL)));
        setPhoneNumber(cursor.getString(cursor.getColumnIndex(StoresTable.COL_PHONE_NUMBER)));
        setAuthor(ParseUser.getCurrentUser());
    }*/

/*    public long getStoreID() {
        return getLong(COL_STORE_ID);
    }

    public void setStoreID(long locationID) {
        put(COL_STORE_ID, locationID);
    }*/


/*    public ParseObject getStoreChain() {
        // TODO: Is there a better way to getStoreChain?
        ParseObject storeChain = null;
        String storeID = getObjectId();
        if(storeID!=null && !storeID.isEmpty()) {
            ParseObject store = getStore(storeID);
            if (store != null) {
                try {
                    ParseQuery<StoreChains> query = StoreChains.getQuery();
                    query.whereEqualTo("objectID", store.getString(StoresTable.COL_STORE_CHAIN_ID));
                    List storeChainsList = query.find();
                    if (storeChainsList != null && storeChainsList.size() > 0) {
                        storeChain = (ParseObject) storeChainsList.get(0);
                    }
                } catch (ParseException e) {
                    MyLog.e("Stores", "getStoreChain: ParseException: " + e.getMessage());
                }
            }
        }
        return storeChain;
    }*/

    public void setStoreChain(ParseObject storeChain) {
        put(StoresTable.COL_STORE_CHAIN_ID, storeChain);
    }

    public String getStoreRegionalName() {
        return getString(StoresTable.COL_STORE_REGIONAL_NAME);
    }

    public void setStoreRegionalName(String storeRegionalName) {
        put(StoresTable.COL_STORE_REGIONAL_NAME, storeRegionalName);
    }

    public String getAddress1() {
        return getString(StoresTable.COL_ADDRESS1);
    }


    public void setAddress1(String address1) {
        put(StoresTable.COL_ADDRESS1, address1);
    }

    public String getAddress2() {
        return getString(StoresTable.COL_ADDRESS2);
    }

    public void setAddress2(String address2) {
        put(StoresTable.COL_ADDRESS2, address2);
    }

    public String getCity() {
        return getString(StoresTable.COL_CITY);
    }

    public void setCity(String city) {
        put(StoresTable.COL_CITY, city);
    }

    public String getState() {
        return getString(StoresTable.COL_STATE);
    }

    public void setState(String state) {
        put(StoresTable.COL_STATE, state);
    }

    public String getZip() {
        return getString(StoresTable.COL_ZIP);
    }

    public void setZip(String zip) {
        put(StoresTable.COL_ZIP, zip);
    }

    public String getCountry() {
        return getString(StoresTable.COL_COUNTRY);
    }

    public void setCountry(String country) {
        put(StoresTable.COL_COUNTRY, country);
    }

    public String getPhoneNumber() {
        return getString(StoresTable.COL_PHONE_NUMBER);
    }

    public void setPhoneNumber(String phoneNumber) {
        put(StoresTable.COL_PHONE_NUMBER, phoneNumber);
    }

    public String getWebsiteURL() {
        return getString(StoresTable.COL_WEBSITE_URL);
    }

    public void setWebsiteURL(String websiteURL) {
        put(StoresTable.COL_WEBSITE_URL, websiteURL);
    }

    public ParseUser getAuthor() {
        return getParseUser(AUTHOR);
    }

    public void setAuthor(ParseUser currentUser) {
        put(AUTHOR, currentUser);
    }

    public ParseGeoPoint getGeoPoint() {
        return getParseGeoPoint(StoresTable.COL_PARSE_LOCATION);
    }

    public static ParseQuery<Stores> getQuery() {
        return ParseQuery.getQuery(Stores.class);
    }


    public static ParseObject getStore(String parseObjectID) {
        ParseObject store = null;
        try {
            ParseQuery<Stores> query = getQuery();
            query.whereEqualTo("objectID", parseObjectID);
            List stores = query.find();
            if (stores != null && stores.size() > 0) {
                store = (ParseObject) stores.get(0);
            }
        } catch (ParseException e) {
            MyLog.e("Stores", "getStore: ParseException: " + e.getMessage());
        }
        return store;

    }

}
