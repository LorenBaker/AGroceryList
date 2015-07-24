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

    public Initial_Items() {
        // A default constructor is required.
    }


    public void setInitialItem(String itemName, ParseObject group, long sortKey) {
        setItemName(itemName);
        setGroup(group);
        setSortKey(sortKey);
    }

    public void setInitialItemCursor(Cursor cursor) {
        if (cursor == null) {
            return;
        }
        // String currentRow = DatabaseUtils.dumpCurrentRowToString(cursor);
        setItemName(cursor.getString(cursor.getColumnIndex(ItemsTable.COL_ITEM_NAME)));
        // TODO: get group ParseObject
        //setGroup(cursor.getString(cursor.getColumnIndex(ItemsTable.COL_GROUP)));
        setSortKey(cursor.getLong(cursor.getColumnIndex(ItemsTable.COL_SORT_KEY)));
    }


    public String getItemName() {
        return getString(ItemsTable.COL_ITEM_NAME);
    }

    public void setItemName(String itemName) {
        put(ItemsTable.COL_ITEM_NAME, itemName);
    }

    public String getGroupID() {
        return getString(ItemsTable.COL_GROUP);
    }

    public void setGroup(ParseObject group) {
        put(ItemsTable.COL_GROUP, group);
    }

    public long getSortKey() {
        return getLong(ItemsTable.COL_SORT_KEY);
    }

    public void setSortKey(long manualSortOrder) {
        put(ItemsTable.COL_SORT_KEY, manualSortOrder);
    }


    public static ParseQuery<Initial_Items> getQuery() {
        return ParseQuery.getQuery(Initial_Items.class);
    }


}


