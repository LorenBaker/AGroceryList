package com.lbconsulting.agrocerylist.classes;

/**
 * This class holds item data
 */
public class clsItem {

    private long itemID;
    private String itemName;
    private String itemNote;
    private long groupID;
    private boolean itemStruckOut;
    private boolean itemChecked;
    private int manualSortOrder;
    private int manualSortSwitch;
    private long dateTimeLastUsed;

    public long getDateTimeLastUsed() {
        return dateTimeLastUsed;
    }

    public void setDateTimeLastUsed(long dateTimeLastUsed) {
        this.dateTimeLastUsed = dateTimeLastUsed;
    }

    public long getGroupID() {
        return groupID;
    }

    public void setGroupID(long groupID) {
        this.groupID = groupID;
    }

    public boolean isItemChecked() {
        return itemChecked;
    }

    public void setItemChecked(boolean itemChecked) {
        this.itemChecked = itemChecked;
    }

    public long getItemID() {
        return itemID;
    }

    public void setItemID(long itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemNote() {
        return itemNote;
    }

    public void setItemNote(String itemNote) {
        this.itemNote = itemNote;
    }

    public boolean isItemStruckOut() {
        return itemStruckOut;
    }

    public void setItemStruckOut(boolean itemStruckOut) {
        this.itemStruckOut = itemStruckOut;
    }

    public int getManualSortOrder() {
        return manualSortOrder;
    }

    public void setManualSortOrder(int manualSortOrder) {
        this.manualSortOrder = manualSortOrder;
    }

    public int getManualSortSwitch() {
        return manualSortSwitch;
    }

    public void setManualSortSwitch(int manualSortSwitch) {
        this.manualSortSwitch = manualSortSwitch;
    }
}
