package com.lbconsulting.agrocerylist.classes_parse;

/**
 * a ParseObject that holds item data
 */

import android.database.Cursor;

import com.lbconsulting.agrocerylist.database.ItemsTable;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Items")
public class Items extends ParseObject {
    private static final String COL_ITEM_ID = "itemID";
    private static final String AUTHOR = "author";

    public Items() {
        // A default constructor is required.
    }

    public void setItemCursor(Cursor cursor) {
        if (cursor == null) {
            return;
        }

       // String currentRow = DatabaseUtils.dumpCurrentRowToString(cursor);

        setItemID(cursor.getLong(cursor.getColumnIndex(ItemsTable.COL_ID)));
        setAuthor(ParseUser.getCurrentUser());
        setItemName(cursor.getString(cursor.getColumnIndex(ItemsTable.COL_ITEM_NAME)));
        setItemNote(cursor.getString(cursor.getColumnIndex(ItemsTable.COL_ITEM_NOTE)));
        setGroupID(cursor.getLong(cursor.getColumnIndex(ItemsTable.COL_GROUP)));
        setProductID(cursor.getLong(cursor.getColumnIndex(ItemsTable.COL_PRODUCT_ID)));
        setSelected(cursor.getInt(cursor.getColumnIndex(ItemsTable.COL_SELECTED)) > 0);
        setStruckOut(cursor.getInt(cursor.getColumnIndex(ItemsTable.COL_STRUCK_OUT)) > 0);
        setChecked(cursor.getInt(cursor.getColumnIndex(ItemsTable.COL_CHECKED)) > 0);
        setFavorite(cursor.getInt(cursor.getColumnIndex(ItemsTable.COL_FAVORITE)) > 0);
        setManualSortOrder(cursor.getLong(cursor.getColumnIndex(ItemsTable.COL_SORT_KEY)));
    }

    public long getItemID() {
        return getLong(COL_ITEM_ID);
    }

    public void setItemID(long itemID) {
        put(COL_ITEM_ID, itemID);
    }

    public ParseUser getAuthor() {
        return getParseUser(AUTHOR);
    }

    public void setAuthor(ParseUser currentUser) {
        put(AUTHOR, currentUser);
    }

    public String getItemName() {
        return getString(ItemsTable.COL_ITEM_NAME);
    }

    public void setItemName(String itemName) {
        put(ItemsTable.COL_ITEM_NAME, itemName);
    }

    public String getItemNote() {
        return getString(ItemsTable.COL_ITEM_NOTE);
    }

    public void setItemNote(String itemNote) {
        put(ItemsTable.COL_ITEM_NOTE, itemNote);
    }

    public long getGroupID() {
        return getLong(ItemsTable.COL_GROUP);
    }

    public void setGroupID(long groupID) {
        put(ItemsTable.COL_GROUP, groupID);
    }

    public long getProductID() {
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
    }

    public int getManualSortOrder() {
        return getInt(ItemsTable.COL_SORT_KEY);
    }

    public void setManualSortOrder(long manualSortOrder) {
        put(ItemsTable.COL_SORT_KEY, manualSortOrder);
    }


    public static ParseQuery<Items> getQuery() {
        return ParseQuery.getQuery(Items.class);
    }


}


