package com.lbconsulting.agrocerylist.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.database.StoreChainsTable;
import com.lbconsulting.agrocerylist.database.StoresTable;


public class StoresSpinnerCursorAdapter extends CursorAdapter {

    private final String dash = " " + "\u2013" + " ";

    public StoresSpinnerCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        MyLog.i("StoresSpinnerCursorAdapter", "constructor.");
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String displayName = cursor.getString(cursor.getColumnIndex(StoreChainsTable.COL_STORE_CHAIN_NAME))
                + dash + cursor.getString(cursor.getColumnIndex(StoresTable.COL_STORE_REGIONAL_NAME));

        switch (view.getId()) {
            case R.id.rowLinearLayout:
                TextView rowTextView = (TextView) view.findViewById(R.id.rowTextView);

                if (rowTextView != null) {
                    rowTextView.setText(displayName);
                    rowTextView.setTextColor(context.getResources().getColor(android.R.color.white));
                }
                break;

            case R.id.rowLinearLayoutDropdown:
                TextView rowTextViewDropdown = (TextView) view.findViewById(R.id.rowTextViewDropdown);
                if (rowTextViewDropdown != null) {
                    rowTextViewDropdown.setText(displayName);
                    rowTextViewDropdown.setTextColor(context.getResources().getColor(android.R.color.white));
                }
                break;
            default:
                break;
        }
    }

    @Override
    public View newDropDownView(Context context, Cursor cursor, ViewGroup parent) {
        View v = null;
        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.row_spinner_dropdown, parent, false);
        bindView(v, context, cursor);
        return v;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = null;
        LayoutInflater inflater = LayoutInflater.from(context);
        v = inflater.inflate(R.layout.row_spinner, parent, false);
        bindView(v, context, cursor);
        return v;
    }

}
