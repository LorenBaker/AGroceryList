package com.lbconsulting.agrocerylist.classes_parse;

import java.util.ArrayList;

/**
 * This class holds an array of clsParseInitialItem
 */
public class clsParseItemArray {
    private ArrayList<clsParseInitialItem> initialItems;

    public clsParseItemArray(ArrayList<clsParseInitialItem> items){
        this.initialItems = items;
    }

    public ArrayList<clsParseInitialItem> getInitialItems() {
        return initialItems;
    }

    public void setInitialItems(ArrayList<clsParseInitialItem> initialItems) {
        this.initialItems = initialItems;
    }
}
