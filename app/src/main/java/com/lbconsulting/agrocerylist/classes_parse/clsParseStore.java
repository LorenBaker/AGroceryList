package com.lbconsulting.agrocerylist.classes_parse;

/**
 * This class holds public store data.
 */
public class clsParseStore {

    private long storeID;
    private long storeChainID;
    private String storeRegionalName;
    private String parseStoreMapName;

    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zip;
    private String gpsLatitude;
    private String gpsLongitude;
    private String websiteURL;
    private String phoneNumber;
    private int manualSortKey;

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getGpsLatitude() {
        return gpsLatitude;
    }

    public void setGpsLatitude(String gpsLatitude) {
        this.gpsLatitude = gpsLatitude;
    }

    public String getGpsLongitude() {
        return gpsLongitude;
    }

    public void setGpsLongitude(String gpsLongitude) {
        this.gpsLongitude = gpsLongitude;
    }

    public String getParseStoreMapName() {
        return parseStoreMapName;
    }

    public void setParseStoreMapName(String parseStoreMapName) {
        this.parseStoreMapName = parseStoreMapName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getStoreChainID() {
        return storeChainID;
    }

    public void setStoreChainID(long storeChainID) {
        this.storeChainID = storeChainID;
    }

    public long getStoreID() {
        return storeID;
    }

    public void setStoreID(long storeID) {
        this.storeID = storeID;
    }

    public String getStoreRegionalName() {
        return storeRegionalName;
    }

    public void setStoreRegionalName(String storeRegionalName) {
        this.storeRegionalName = storeRegionalName;
    }

    public String getWebsiteURL() {
        return websiteURL;
    }

    public void setWebsiteURL(String websiteURL) {
        this.websiteURL = websiteURL;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public int getManualSortKey() {
        return manualSortKey;
    }

    public void setManualSortKey(int manualSortKey) {
        this.manualSortKey = manualSortKey;
    }
}
