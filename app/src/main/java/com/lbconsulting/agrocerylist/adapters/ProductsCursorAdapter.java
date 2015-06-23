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
import com.lbconsulting.agrocerylist.classes.clsUtils;
import com.lbconsulting.agrocerylist.database.ProductsTable;


/**
 * CursorAdapter to populate a list view of item names
 */
public class ProductsCursorAdapter extends CursorAdapter {

    public ProductsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        MyLog.i("ProductsCursorAdapter", "constructor.");
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
        return inflater.inflate(R.layout.row_product, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor == null) {
            return;
        }
        TextView tvProductTitle = (TextView) view.findViewById(R.id.tvProductTitle);
        TextView tvFormat = (TextView) view.findViewById(R.id.tvFormat);
        TextView tvBarCodeNumber = (TextView) view.findViewById(R.id.tvBarCodeNumber);

        String productTitle = cursor.getString(cursor.getColumnIndex(ProductsTable.COL_PRODUCT_TITLE));
        String barCodeFormat = cursor.getString(cursor.getColumnIndex(ProductsTable.COL_BAR_CODE_FORMAT));
        String barCodeNumber = cursor.getString(cursor.getColumnIndex(ProductsTable.COL_BAR_CODE_NUMBER));
        barCodeNumber = clsUtils.formatGTIN(barCodeNumber);

        tvProductTitle.setText(productTitle);
        tvFormat.setText(barCodeFormat + ":");
        tvBarCodeNumber.setText(barCodeNumber);
    }
}