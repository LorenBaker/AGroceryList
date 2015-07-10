package com.lbconsulting.agrocerylist.classes_parse;

import java.util.ArrayList;

/**
 * This class holds an array of clsParseStoreChainArray
 */
public class clsParseStoreChainArray {
    private ArrayList<clsParseStoreChain> storeChains;

    public clsParseStoreChainArray(ArrayList<clsParseStoreChain> storeChains){
        this.storeChains = storeChains;
    }

    public ArrayList<clsParseStoreChain> getStoreChains() {
        return storeChains;
    }

    public void setStoreChains(ArrayList<clsParseStoreChain> storeChains) {
        this.storeChains = storeChains;
    }
}
