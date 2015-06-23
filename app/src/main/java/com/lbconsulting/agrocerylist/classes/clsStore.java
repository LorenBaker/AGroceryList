package com.lbconsulting.agrocerylist.classes;

/**
 * This class holds Store information
 */
public class clsStore {

    private long storeID;
    private long storeChainID;
    private String storeRegionalName;
    private boolean storeChecked;
    private String street1;
    private String street2;
    private String city;
    private String state;
    private String zip;
    private String gpsLatitude;
    private String gpsLongitude;
    private String websiteURL;
    private String phoneNumber;

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

    public boolean isStoreChecked() {
        return storeChecked;
    }

    public void setStoreChecked(boolean storeChecked) {
        this.storeChecked = storeChecked;
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

    public String getStreet1() {
        return street1;
    }

    public void setStreet1(String street1) {
        this.street1 = street1;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
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
}
