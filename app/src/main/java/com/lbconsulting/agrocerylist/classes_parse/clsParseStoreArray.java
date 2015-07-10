package com.lbconsulting.agrocerylist.classes_parse;

import java.util.ArrayList;

/**
 * This class holds an array of clsParseStore
 */
public class clsParseStoreArray {
    private ArrayList<clsParseStore> stores;

    public clsParseStoreArray(ArrayList<clsParseStore> stores){
        this.stores = stores;
    }

    public ArrayList<clsParseStore> getStores() {
        return stores;
    }

    public void setStores(ArrayList<clsParseStore> stores) {
        this.stores = stores;
    }
}
