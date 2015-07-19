package com.lbconsulting.agrocerylist.classes_parse;

/**
 * a ParseObject that holds initial item data
 */

import android.database.Cursor;

import com.lbconsulting.agrocerylist.database.ItemsTable;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

@ParseClassName("Initial_Items")
public class Initial_Items extends ParseObject {
    private static final String COL_ITEM_ID = "itemID";

    public Initial_Items() {
        // A default constructor is required.
    }


    public void setInitialItem(long id, String itemName, String groupIDString, long manualSortOrder) {
        setItemID(id);
        setItemName(itemName);
        long groupID = Long.parseLong(groupIDString);
        setGroupID(groupID);
        setManualSortOrder(manualSortOrder);
    }

    public void setInitialItemCursor(Cursor cursor) {
        if (cursor == null) {
            return;
        }
        // String currentRow = DatabaseUtils.dumpCurrentRowToString(cursor);
        setItemID(cursor.getLong(cursor.getColumnIndex(ItemsTable.COL_ITEM_ID)));
        setItemName(cursor.getString(cursor.getColumnIndex(ItemsTable.COL_ITEM_NAME)));
        setGroupID(cursor.getLong(cursor.getColumnIndex(ItemsTable.COL_GROUP_ID)));
        setManualSortOrder(cursor.getLong(cursor.getColumnIndex(ItemsTable.COL_MANUAL_SORT_ORDER)));
        //setItemNote(cursor.getString(cursor.getColumnIndex(ItemsTable.COL_ITEM_NOTE)));
        //setProductID(cursor.getLong(cursor.getColumnIndex(ItemsTable.COL_PRODUCT_ID)));
        //setSelected(cursor.getInt(cursor.getColumnIndex(ItemsTable.COL_SELECTED)) > 0);
        //setStruckOut(cursor.getInt(cursor.getColumnIndex(ItemsTable.COL_STRUCK_OUT)) > 0);
        //setChecked(cursor.getInt(cursor.getColumnIndex(ItemsTable.COL_CHECKED)) > 0);
        //setFavorite(cursor.getInt(cursor.getColumnIndex(ItemsTable.COL_FAVORITE)) > 0);
    }

    public long getItemID() {
        return getLong(COL_ITEM_ID);
    }

    public void setItemID(long itemID) {
        put(COL_ITEM_ID, itemID);
    }

    public String getItemName() {
        return getString(ItemsTable.COL_ITEM_NAME);
    }

    public void setItemName(String itemName) {
        put(ItemsTable.COL_ITEM_NAME, itemName);
    }

    public long getGroupID() {
        return getLong(ItemsTable.COL_GROUP_ID);
    }

    public void setGroupID(long groupID) {
        put(ItemsTable.COL_GROUP_ID, groupID);
    }

    public int getManualSortOrder() {
        return getInt(ItemsTable.COL_MANUAL_SORT_ORDER);
    }

    public void setManualSortOrder(long manualSortOrder) {
        put(ItemsTable.COL_MANUAL_SORT_ORDER, manualSortOrder);
    }

/*    public String getItemNote() {
        return getString(ItemsTable.COL_ITEM_NOTE);
    }

    public void setItemNote(String itemNote) {
        put(ItemsTable.COL_ITEM_NOTE, itemNote);
    }*/



/*    public long getProductID() {
        return getLong(ItemsTable.COL_PRODUCT_ID);
    }

    public void setProductID(long productID) {
        put(ItemsTable.COL_PRODUCT_ID, productID);
    }

    public boolean isSelected() {
        return getBoolean(ItemsTable.COL_SELECTED);
    }

    public void setSelected(boolean isSelected) {
        put(ItemsTable.COL_SELECTED, isSelected);
    }

    public boolean isStruckOut() {
        return getBoolean(ItemsTable.COL_STRUCK_OUT);
    }

    public void setStruckOut(boolean isStruckOut) {
        put(ItemsTable.COL_STRUCK_OUT, isStruckOut);
    }

    public boolean isChecked() {
        return getBoolean(ItemsTable.COL_CHECKED);
    }

    public void setChecked(boolean isChecked) {
        put(ItemsTable.COL_CHECKED, isChecked);
    }

    public boolean isFavorite() {
        return getBoolean(ItemsTable.COL_FAVORITE);
    }

    public void setFavorite(boolean isFavorite) {
        put(ItemsTable.COL_FAVORITE, isFavorite);
    }*/


    public static ParseQuery<Initial_Items> getQuery() {
        return ParseQuery.getQuery(Initial_Items.class);
    }


}


