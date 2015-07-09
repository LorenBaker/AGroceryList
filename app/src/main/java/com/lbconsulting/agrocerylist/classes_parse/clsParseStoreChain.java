package com.lbconsulting.agrocerylist.classes_parse;

/**
 * This class holds public store chain data.
 */
public class clsParseStoreChain {

    private long storeChainID;
    private String storeChainName;

    public clsParseStoreChain(long storeChainID, String storeChainName) {
        this.storeChainID = storeChainID;
        this.storeChainName = storeChainName;
    }

    public long getStoreChainID() {
        return storeChainID;
    }

    public void setStoreChainID(long storeChainID) {
        this.storeChainID = storeChainID;
    }

    public String getStoreChainName() {
        return storeChainName;
    }

    public void setStoreChainName(String storeChainName) {
        this.storeChainName = storeChainName;
    }

    @Override
    public String toString() {
        return storeChainName;
    }
}
