package com.lbconsulting.agrocerylist.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.database.ItemsTable;


/**
 * CursorAdapter to populate a list view of item names
 */
public class MasterListCursorAdapter extends CursorAdapter {

    Context mContext;

    public MasterListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
        MyLog.i("MasterListCursorAdapter", "constructor.");
    }

    @Override
    public int getCount() {
        if (getCursor() == null) {
            return 0;
        }
        return super.getCount();
    }

    @Override
    public View newView(final Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_items_list, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor == null) {
            return;
        }
        CheckBox ckBox = (CheckBox) view.findViewById(R.id.ckBox);

        // set the checkbox text to the item's name
        String itemName = cursor.getString(cursor.getColumnIndex(ItemsTable.COL_ITEM_NAME));
        String itemNote = cursor.getString(cursor.getColumnIndex(ItemsTable.COL_ITEM_NOTE));
        if (!itemNote.isEmpty()) {
            itemName = itemName + " (" + itemNote + ")";
        }
        ckBox.setText(itemName);


        // A check mark means the item is selected for the active store
        int checkMarkValue = cursor.getInt(cursor.getColumnIndex(ItemsTable.COL_CHECKED));
        ckBox.setChecked(checkMarkValue > 0);


        int selectedValue = cursor.getInt(cursor.getColumnIndex(ItemsTable.COL_SELECTED));
        switch (selectedValue){
            case 0:
                // white normal font means the item is not selected in any store
                ckBox.setTextColor(context.getResources().getColor(R.color.white));
                ckBox.setTypeface(null, Typeface.NORMAL);
                break;

            case 1:
                // grey italic means the item is selected for only one store
                ckBox.setTextColor(context.getResources().getColor(R.color.grey));
                ckBox.setTypeface(null, Typeface.ITALIC);
                break;
            case 2:
                // red italic means the item is selected for more than one store
                ckBox.setTextColor(context.getResources().getColor(R.color.redDark));
                ckBox.setTypeface(null, Typeface.ITALIC);
                break;
        }

        // save the item's ID so it can be received later
        long itemID = cursor.getLong(cursor.getColumnIndex(ItemsTable.COL_ITEM_ID));
        view.setTag(itemID);

    }

}