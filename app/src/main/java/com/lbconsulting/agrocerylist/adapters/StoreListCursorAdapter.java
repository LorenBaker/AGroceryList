package com.lbconsulting.agrocerylist.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.database.ItemsTable;
import com.lbconsulting.agrocerylist.database.SelectedItemsTable;


/**
 * CursorAdapter to populate a list view of a store's item names
 */
public class StoreListCursorAdapter extends CursorAdapter {

    Context mContext;

    public StoreListCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
                MyLog.i("StoreListCursorAdapter", "constructor.");
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
        View view = inflater.inflate(R.layout.row_store_list, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor == null) {
            return;
        }
        TextView tvItemName = (TextView) view.findViewById(R.id.tvItemName);

        // set the checkbox text to the item's name
        String itemName = cursor.getString(cursor.getColumnIndex(ItemsTable.COL_ITEM_NAME));
        String itemNote = cursor.getString(cursor.getColumnIndex(ItemsTable.COL_ITEM_NOTE));
        if (!itemNote.isEmpty()) {
            itemName = itemName + " (" + itemNote + ")";
        }
        tvItemName.setText(itemName);
        boolean isStruckOut = cursor.getInt(cursor.getColumnIndex(ItemsTable.COL_STRUCK_OUT))>0;

        if (isStruckOut) {
            // item has been struck out
            tvItemName.setTypeface(null, Typeface.ITALIC);
            //tvItemName.setTextColor(this.mListSettings.getItemStrikeoutTextColor());
            tvItemName.setPaintFlags(tvItemName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            // item is NOT struck out
            tvItemName.setTypeface(null, Typeface.NORMAL);
            //tvItemName.setTextColor(this.mListSettings.getItemNormalTextColor());
            tvItemName.setPaintFlags(tvItemName.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // save the item's ID so it can be received later
        long itemID = cursor.getLong(cursor.getColumnIndex(SelectedItemsTable.COL_ITEM_ID));
        view.setTag(itemID);

    }

}