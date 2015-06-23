package com.lbconsulting.agrocerylist.classes;

/**
 * This class holds store chain data
 */
public class clsStoreChain {

    private long storeChainID;
    private String storeChainName;
    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
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
}
