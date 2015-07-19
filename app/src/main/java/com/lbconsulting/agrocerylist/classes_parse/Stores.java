package com.lbconsulting.agrocerylist.classes_parse;

import android.database.Cursor;

import com.lbconsulting.agrocerylist.database.StoresTable;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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

    public void setStore(long id, long storeChainID,
                         String storeRegionalName,
                         String address1,  // number and street
                         String address2,  // ste, etc
                         String city, String state, String zipCode) {
        setStoreID(id);
        setStoreChainID(storeChainID);
        setStoreRegionalName(storeRegionalName);
        setAddress1(address1);
        setAddress2(address2);
        setCity(city);
        setState(state);
        setZip(zipCode);
        setAuthor(ParseUser.getCurrentUser());

        //TODO: set store geo location
        setGpsLatitude("");
        setGpsLongitude("");
        setWebsiteURL("");
        setPhoneNumber("");
    }

    public void setStoresCursor(Cursor cursor) {
        if (cursor == null) {
            return;
        }
        setStoreID(cursor.getLong(cursor.getColumnIndex(StoresTable.COL_STORE_ID)));
        setStoreChainID(cursor.getLong(cursor.getColumnIndex(StoresTable.COL_STORE_CHAIN_ID)));
        setStoreRegionalName(cursor.getString(cursor.getColumnIndex(StoresTable.COL_STORE_REGIONAL_NAME)));
        setAddress1(cursor.getString(cursor.getColumnIndex(StoresTable.COL_ADDRESS1)));
        setAddress2(cursor.getString(cursor.getColumnIndex(StoresTable.COL_ADDRESS2)));
        setCity(cursor.getString(cursor.getColumnIndex(StoresTable.COL_CITY)));
        setState(cursor.getString(cursor.getColumnIndex(StoresTable.COL_STATE)));
        setZip(cursor.getString(cursor.getColumnIndex(StoresTable.COL_ZIP)));
        setGpsLatitude(cursor.getString(cursor.getColumnIndex(StoresTable.COL_GPS_LATITUDE)));
        setGpsLongitude(cursor.getString(cursor.getColumnIndex(StoresTable.COL_GPS_LONGITUDE)));
        setWebsiteURL(cursor.getString(cursor.getColumnIndex(StoresTable.COL_WEBSITE_URL)));
        setPhoneNumber(cursor.getString(cursor.getColumnIndex(StoresTable.COL_PHONE_NUMBER)));
        setAuthor(ParseUser.getCurrentUser());
    }

    public long getStoreID() {
        return getLong(COL_STORE_ID);
    }

    public void setStoreID(long locationID) {
        put(COL_STORE_ID, locationID);
    }

    public String getCity() {
        return getString(StoresTable.COL_CITY);
    }

    public void setCity(String city) {
        put(StoresTable.COL_CITY, city);
    }

    public String getGpsLatitude() {
        return getString(StoresTable.COL_GPS_LATITUDE);
    }

    public void setGpsLatitude(String gpsLatitude) {
        put(StoresTable.COL_GPS_LATITUDE, gpsLatitude);
    }

    public String getGpsLongitude() {
        return getString(StoresTable.COL_GPS_LONGITUDE);
    }

    public void setGpsLongitude(String gpsLongitude) {
        put(StoresTable.COL_GPS_LONGITUDE, gpsLongitude);
    }

    public String getPhoneNumber() {
        return getString(StoresTable.COL_PHONE_NUMBER);
    }

    public void setPhoneNumber(String phoneNumber) {
        put(StoresTable.COL_PHONE_NUMBER, phoneNumber);
    }

    public String getState() {
        return getString(StoresTable.COL_STATE);
    }

    public void setState(String state) {
        put(StoresTable.COL_STATE, state);
    }

    public long getStoreChainID() {
        return getLong(StoresTable.COL_STORE_CHAIN_ID);
    }

    public void setStoreChainID(long storeChainID) {
        put(StoresTable.COL_STORE_CHAIN_ID, storeChainID);
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

    public String getWebsiteURL() {
        return getString(StoresTable.COL_WEBSITE_URL);
    }

    public void setWebsiteURL(String websiteURL) {
        put(StoresTable.COL_WEBSITE_URL, websiteURL);
    }

    public String getZip() {
        return getString(StoresTable.COL_ZIP);
    }

    public void setZip(String zip) {
        put(StoresTable.COL_ZIP, zip);
    }

    public ParseUser getAuthor() {
        return getParseUser(AUTHOR);
    }

    public void setAuthor(ParseUser currentUser) {
        put(AUTHOR, currentUser);
    }

    public static ParseQuery<Stores> getQuery() {
        return ParseQuery.getQuery(Stores.class);
    }
}
