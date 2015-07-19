package com.lbconsulting.agrocerylist.classes_parse;

/**
 * This class holds public group data.
 */
public class clsParseInitialItem {

    private long itemID, groupID;
    private int manualSortOrder;
    private String itemName;

    public clsParseInitialItem(long itemID, String itemName, long groupID, int manualSortOrder) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.groupID = groupID;
        this.manualSortOrder = manualSortOrder;
    }


    public long getItemID() {
        return itemID;
    }

    public void setItemID(long itemID) {
        this.itemID = itemID;
    }

    public long getGroupID() {
        return groupID;
    }

    public void setGroupID(long groupID) {
        this.groupID = groupID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }


    public int getManualSortOrder() {
        return manualSortOrder;
    }

    public void setManualSortOrder(int manualSortOrder) {
        this.manualSortOrder = manualSortOrder;
    }

    @Override
    public String toString() {
        return itemName;
    }
}
