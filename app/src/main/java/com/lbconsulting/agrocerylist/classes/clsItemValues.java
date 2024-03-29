package com.lbconsulting.agrocerylist.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.lbconsulting.agrocerylist.database.ItemsTable;

/**
 * This class holds Store information from the database
 */
public class clsItemValues {
    private Context mContext;
    private Cursor mItemCursor;
    private ContentValues cv;


/*    public clsItemValues(Context context, long itemID) {
        mContext = context;
        mItemCursor = ItemsTable.getItemCursor(context, itemID);
        if (mItemCursor != null) {
            mItemCursor.moveToFirst();
        }
        cv = new ContentValues();
*//*        String cursorContent = DatabaseUtils.dumpCursorToString(mItemCursor);
        MyLog.i("clsItemValues: \n", cursorContent);*//*

    }

    public clsItemValues(Context context, Cursor itemCursor) {
        mContext = context;
        mItemCursor = itemCursor;
        if (hasData()) {
        }
        cv = new ContentValues();
*//*        String cursorContent = DatabaseUtils.dumpCursorToString(mItemCursor);
        MyLog.i("clsItemValues: \n", cursorContent);*//*
    }

    public boolean hasData() {
        return mItemCursor != null && mItemCursor.getCount() > 0;
    }


    public long getDateTimeLastUsed() {
        long result = 0;
        if (hasData()) {
            result = mItemCursor.getLong(mItemCursor.getColumnIndex(ItemsTable.COL_LAST_TIME_USED));
        }
        return result;
    }

*//*
    public void putDateTimeLastUsed(long dateTimeLastUsed) {
        if (cv.containsKey(ItemsTable.COL_LAST_TIME_USED)) {
            cv.remove(ItemsTable.COL_LAST_TIME_USED);
        }
        cv.put(ItemsTable.COL_LAST_TIME_USED, dateTimeLastUsed);
    }
*//*

    public long getLocationID() {
        long result = -1;
        if (hasData()) {
            result = mItemCursor.getLong(mItemCursor.getColumnIndex(ItemsTable.COL_ID));
        }
        return result;
    }

    public void putGroupID(long groupID) {
        if (cv.containsKey(ItemsTable.COL_ID)) {
            cv.remove(ItemsTable.COL_ID);
        }
        cv.put(ItemsTable.COL_ID, groupID);
    }

    public boolean isItemSelected() {
        boolean result = false;
        if (hasData()) {
            int itemSelectedValue = mItemCursor.getInt(mItemCursor.getColumnIndex(ItemsTable.COL_SELECTED));
            result = itemSelectedValue > 0;
        }
        return result;
    }

    public void putItemSelected(boolean itemSelected) {
        if (cv.containsKey(ItemsTable.COL_SELECTED)) {
            cv.remove(ItemsTable.COL_SELECTED);
        }
        int itemSelectedValue = 0;
        if (itemSelected) {
            itemSelectedValue = 1;
        }
        cv.put(ItemsTable.COL_SELECTED, itemSelectedValue);
    }

    public boolean isItemChecked() {
        boolean result = false;
        if (hasData()) {
            int itemCheckedValue = mItemCursor.getInt(mItemCursor.getColumnIndex(ItemsTable.COL_CHECKED));
            result = itemCheckedValue > 0;
        }
        return result;
    }

    public void putItemChecked(boolean itemChecked) {
        if (cv.containsKey(ItemsTable.COL_CHECKED)) {
            cv.remove(ItemsTable.COL_CHECKED);
        }
        int itemCheckedValue = 0;
        if (itemChecked) {
            itemCheckedValue = 1;
        }
        cv.put(ItemsTable.COL_CHECKED, itemCheckedValue);
    }

    public long getItemID() {
        long result = -1;
        if (hasData()) {
            result = mItemCursor.getLong(mItemCursor.getColumnIndex(ItemsTable.COL_ID));
        }
        return result;
    }


    public String getItemName() {
        String result = "";
        if (hasData()) {
            result = mItemCursor.getString(mItemCursor.getColumnIndex(ItemsTable.COL_ITEM_NAME));
        }
        return result;
    }

    public void putItemName(String itemName) {
        if (cv.containsKey(ItemsTable.COL_ITEM_NAME)) {
            cv.remove(ItemsTable.COL_ITEM_NAME);
        }
        cv.put(ItemsTable.COL_ITEM_NAME, itemName);
    }

    public String getItemNote() {
        String result = "";
        if (hasData()) {
            result = mItemCursor.getString(mItemCursor.getColumnIndex(ItemsTable.COL_ITEM_NOTE));
        }
        return result;
    }

    public void putItemNote(String itemNote) {
        if (cv.containsKey(ItemsTable.COL_ITEM_NOTE)) {
            cv.remove(ItemsTable.COL_ITEM_NOTE);
        }
        cv.put(ItemsTable.COL_ITEM_NOTE, itemNote);
    }

    public boolean isItemStruckOut() {
        boolean result = false;
        if (hasData()) {
            int itemStrikeoutValue = mItemCursor.getInt(mItemCursor.getColumnIndex(ItemsTable.COL_STRUCK_OUT));
            result = itemStrikeoutValue > 0;
        }
        return result;
    }

    public void putItemStruckOut(boolean itemStruckOut) {
        if (cv.containsKey(ItemsTable.COL_STRUCK_OUT)) {
            cv.remove(ItemsTable.COL_STRUCK_OUT);
        }
        int itemStruckOutValue = 0;
        if (itemStruckOut) {
            itemStruckOutValue = 1;
        }
        cv.put(ItemsTable.COL_STRUCK_OUT, itemStruckOutValue);
    }

    public int getManualSortOrder() {
        int result = -1;
        if (hasData()) {
            result = mItemCursor.getInt(mItemCursor.getColumnIndex(ItemsTable.COL_SORT_KEY));
        }
        return result;
    }

    public void putManualSortOrder(int manualSortOrder) {
        if (cv.containsKey(ItemsTable.COL_SORT_KEY)) {
            cv.remove(ItemsTable.COL_SORT_KEY);
        }
        cv.put(ItemsTable.COL_SORT_KEY, manualSortOrder);
    }


    public void update() {
        if (cv.size() > 0) {
            ItemsTable.updateItemFieldValues(mContext, getItemID(), cv);
        }
    }

    @Override
    public String toString() {
        return getItemName();
    }

    protected void finalize() throws Throwable {

        try {
            if (mItemCursor != null) {
                mItemCursor.close();
                mItemCursor = null;
            }

        } finally {
            super.finalize();
        }
    }*/
}
