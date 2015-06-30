package com.lbconsulting.agrocerylist.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.database.GroupsTable;


/**
 * CursorAdapter to populate a list view of item names
 */
public class GroupsCursorAdapter extends CursorAdapter {

    public GroupsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        MyLog.i("GroupsCursorAdapter", "constructor.");
    }

    @Override
    public int getCount() {
        if (getCursor() == null) {
            return 0;
        }
        return super.getCount();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.row_items_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor == null) {
            return;
        }
        CheckBox ckBox = (CheckBox) view.findViewById(R.id.ckBox);
        String groupName = cursor.getString(cursor.getColumnIndex(GroupsTable.COL_GROUP_NAME));
        ckBox.setText(groupName);

        boolean isChecked = cursor.getInt(cursor.getColumnIndex(GroupsTable.COL_CHECKED)) > 0;
        ckBox.setChecked(isChecked);
    }
}