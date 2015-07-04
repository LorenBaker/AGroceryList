package com.lbconsulting.agrocerylist.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.database.ProductsTable;

/**
 * This class holds Product information
 */
public class clsProductValues {


    //public static String NAME_NOT_AVAILABLE = getString(R.string.product_name_not_available);

    private Context mContext;
    private Cursor mCursor;
    private ContentValues cv;


    public clsProductValues(Context context, String gtin) {
        mContext = context;
        mCursor = ProductsTable.getProductCursor(context, gtin, ProductsTable.SORT_ORDER_TIME_STAMP);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        String cursorContent = DatabaseUtils.dumpCursorToString(mCursor);
        MyLog.d("clsProductValues", cursorContent);
        cv = new ContentValues();

    }

    public boolean hasData() {
        return mCursor != null && mCursor.getCount() > 0;
    }

    public long getProductID() {
        return mCursor.getLong(mCursor.getColumnIndex(ProductsTable.COL_PRODUCT_ID));
    }

    public String getBarCodeFormat() {
        return mCursor.getString(mCursor.getColumnIndex(ProductsTable.COL_BAR_CODE_FORMAT));
    }

    public void setBarCodeFormat(String barCodeFormat) {
        if (cv.containsKey(ProductsTable.COL_BAR_CODE_FORMAT)) {
            cv.remove(ProductsTable.COL_BAR_CODE_FORMAT);
        }
        cv.put(ProductsTable.COL_BAR_CODE_FORMAT, barCodeFormat);
    }

    public String getBarCodeNumber() {
/*        String barcodeNumber = mCursor.getString(mCursor.getColumnIndex(ProductsTable.COL_BAR_CODE_NUMBER));
        if (getBarCodeFormat().equals(ProductsTable.UPC_E)) {
            barcodeNumber = clsUtils.UpcA2E(barcodeNumber);
        }*/
        return mCursor.getString(mCursor.getColumnIndex(ProductsTable.COL_BAR_CODE_NUMBER));
    }

    public void setBarCodeNumber(String barCodeNumber) {
        if (cv.containsKey(ProductsTable.COL_BAR_CODE_NUMBER)) {
            cv.remove(ProductsTable.COL_BAR_CODE_NUMBER);
        }
        cv.put(ProductsTable.COL_BAR_CODE_NUMBER, barCodeNumber);
    }


    public long getProductCategoryID() {
        return mCursor.getLong(mCursor.getColumnIndex(ProductsTable.COL_ITEM_ID));
    }

    public void setProductCategoryID(long productCategoryID) {
        if (cv.containsKey(ProductsTable.COL_ITEM_ID)) {
            cv.remove(ProductsTable.COL_ITEM_ID);
        }
        cv.put(ProductsTable.COL_ITEM_ID, productCategoryID);
    }

    public String getProductTitle() {
        String message = "";

        try {
            message = mCursor.getString(mCursor.getColumnIndex(ProductsTable.COL_PRODUCT_TITLE));
        } catch (Exception e) {
            MyLog.e("clsProductValues", "getProductTitle" + e.getMessage());
            e.printStackTrace();
        }

        return message;
    }

    public void setProductTitle(String productTitle) {
        if (cv.containsKey(ProductsTable.COL_PRODUCT_TITLE)) {
            cv.remove(ProductsTable.COL_PRODUCT_TITLE);
        }
        cv.put(ProductsTable.COL_PRODUCT_TITLE, productTitle);
    }

    public long getTimeStamp() {
        return mCursor.getLong(mCursor.getColumnIndex(ProductsTable.COL_TIME_STAMP));
    }

    public void setTimeStamp(long timeStamp) {
        if (cv.containsKey(ProductsTable.COL_TIME_STAMP)) {
            cv.remove(ProductsTable.COL_TIME_STAMP);
        }
        cv.put(ProductsTable.COL_TIME_STAMP, timeStamp);
    }

    public String displayMessage() {
        String message;

        if (hasData()) {
            message = getProductTitle() +
                    "\n\n" + getBarCodeFormat() + ": " + clsUtils.formatGTIN(getBarCodeNumber())
                    + "\n" + mContext.getString(R.string.key_word_title)
                    + "\n" + mContext.getString(R.string.location_group_title);
        } else {
            message = mContext.getString(R.string.error_no_available_information);
        }

        return message;
    }

    public void update() {
        if (cv.size() > 0) {
            ProductsTable.updateProductFields(mContext, getProductID(), cv);
        }
    }

    protected void finalize() throws Throwable {
        if (hasData()) {
            try {
                mCursor.close();
                mCursor = null;
            } finally {
                super.finalize();
            }
        }
    }
}
