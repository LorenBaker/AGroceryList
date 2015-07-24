package com.lbconsulting.agrocerylist.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.database.GroupsTable;
import com.lbconsulting.agrocerylist.database.ItemsTable;


public class ItemsByGroupCursorAdapter extends CursorAdapter {
    private long mColorThemeID;
    //private Context mContext;

    public ItemsByGroupCursorAdapter(Context context, Cursor c, int flags, long colorThemeID) {
        super(context, c, flags);
        this.mColorThemeID = colorThemeID;
        // TODO: get colors from the ColorsTable using the colorThemeID
        //this.mContext = context;
        MyLog.d("ItemsByGroupCursorAdapter", "constructor: color theme id = " + colorThemeID);
    }

    @Override
    protected void finalize() throws Throwable {
        // TODO: Close the ColorsTheme cursor
        super.finalize();
    }

    private boolean showGroupSeparator(TextView tv, Cursor itemsCursor) {
        boolean result;
        long currentGroupID = itemsCursor.getLong(itemsCursor.getColumnIndex(ItemsTable.COL_GROUP));
        long previousGroupID;
        if (itemsCursor.moveToPrevious()) {
            previousGroupID = itemsCursor.getLong(itemsCursor.getColumnIndex(ItemsTable.COL_GROUP));
            itemsCursor.moveToNext();
            if (currentGroupID == previousGroupID) {
                tv.setVisibility(View.GONE);
                result = false;
            } else {
                tv.setVisibility(View.VISIBLE);
                result = true;
            }
        } else {
            tv.setVisibility(View.VISIBLE);
            itemsCursor.moveToFirst();
            result = true;
        }
        return result;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor != null) {
            TextView tvItemsSeparator = (TextView) view.findViewById(R.id.tvItemsSeparator);
            if (tvItemsSeparator != null) {
                if (showGroupSeparator(tvItemsSeparator, cursor)) {
                    String separatorText = cursor.getString(cursor.getColumnIndex(GroupsTable.COL_GROUP_NAME));
                    tvItemsSeparator.setText(separatorText);
                    // TODO: set tvItemsSeparator colors
                } else {
                    tvItemsSeparator.setVisibility(View.GONE);
                }
            }

            CheckBox ckBoxItemName = (CheckBox) view.findViewById(R.id.ckBoxItemName);
            if (ckBoxItemName != null) {
                ckBoxItemName.setText(cursor.getString(cursor.getColumnIndex(ItemsTable.COL_ITEM_NAME)));
                boolean isChecked = cursor.getInt(cursor.getColumnIndex(ItemsTable.COL_CHECKED)) > 0;
                ckBoxItemName.setChecked(isChecked);
                // TODO: set ckBoxItemName colors
            }
        }
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.row_items_by_group, parent, false);
    }

    @Override
    public int getCount() {
        if (getCursor() == null) {
            return 0;
        }
        return super.getCount();
    }

/*    @Override
    public long getItemId(int position) {
        long itemID = INVALID_ID;
        Cursor cursor = getCursor();
        if (cursor != null) {
            if (position > -1 && position < cursor.getCount()) {
                int currentCursorPosition = cursor.getPosition();
                cursor.moveToPosition(position);
                itemID = cursor.getLong(cursor.getColumnIndexOrThrow(ItemsTable.COL_ID));
                cursor.moveToPosition(currentCursorPosition);
            }
            // cursor.close(); DON'T CLOSE THE CURSOR
        }
        return itemID;
    }*/

/*    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void swap(long mobileItemID, long switchItemID, long previousSwitchItemID) {
        ItemsTable.SwapManualSortOrder(mContext, mobileItemID, switchItemID, previousSwitchItemID);
    }*/

}
