package com.lbconsulting.agrocerylist.adapters;

import android.app.FragmentManager;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.classes.MyEvents;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.database.ItemsTable;
import com.lbconsulting.agrocerylist.dialogs.dialog_edit_item;

import de.greenrobot.event.EventBus;


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
        ImageButton btnFavorite = (ImageButton) view.findViewById(R.id.btnFavorite);
        if (ckBox == null || btnFavorite == null) {
            return;
        }

        // set the checkbox text to the item's name
        String itemName = cursor.getString(cursor.getColumnIndex(ItemsTable.COL_ITEM_NAME));
        String itemNote = cursor.getString(cursor.getColumnIndex(ItemsTable.COL_ITEM_NOTE));
        if (!itemNote.isEmpty()) {
            itemName = itemName + " (" + itemNote + ")";
        }
        ckBox.setText(itemName);

        boolean isSelected = cursor.getInt(cursor.getColumnIndex(ItemsTable.COL_SELECTED)) > 0;
        if (isSelected) {
            ckBox.setTextColor(context.getResources().getColor(R.color.grey));
            ckBox.setTypeface(null, Typeface.ITALIC);
        } else {
            ckBox.setTextColor(context.getResources().getColor(R.color.white));
            ckBox.setTypeface(null, Typeface.NORMAL);
        }
        ckBox.setChecked(isSelected);
        ckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout viewParent = (LinearLayout) v.getParent();
                long itemID = (long) viewParent.getTag();
                EventBus.getDefault().post(new MyEvents.toggleItemSelection(itemID));
            }
        });

        ckBox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                LinearLayout viewParent = (LinearLayout) v.getParent();
                long itemID = (long) viewParent.getTag();
                EventBus.getDefault().post(new MyEvents.showEditItemDialog(itemID));
                return true;
            }
        });

        boolean isFavorite = cursor.getInt(cursor.getColumnIndex(ItemsTable.COL_FAVORITE)) > 0;
        if (isFavorite) {
            btnFavorite.setImageResource(R.drawable.ic_action_favorite_light);
        } else {
            btnFavorite.setImageResource(R.drawable.ic_action_favorite_dark);
        }

        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout viewParent = (LinearLayout) v.getParent();
                long itemID = (long) viewParent.getTag();
                ItemsTable.toggleFavorite(mContext, itemID);
            }
        });

        // save the item's ID so it can be received later
        long itemID = cursor.getLong(cursor.getColumnIndex(ItemsTable.COL_ITEM_ID));
        view.setTag(itemID);
        //btnFavorite.setTag(itemID);

    }

}