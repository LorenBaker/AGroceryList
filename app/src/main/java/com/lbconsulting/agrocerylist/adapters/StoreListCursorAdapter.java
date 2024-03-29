package com.lbconsulting.agrocerylist.adapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.classes.MyEvents;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;
import com.lbconsulting.agrocerylist.database.GroupsTable;
import com.lbconsulting.agrocerylist.database.ItemsTable;
import com.lbconsulting.agrocerylist.database.LocationsTable;
import com.lbconsulting.agrocerylist.database.StoreMapsTable;
import com.lbconsulting.agrocerylist.database.StoresTable;

import de.greenrobot.event.EventBus;


/**
 * CursorAdapter to populate a list view of a store's selected items
 */
public class StoreListCursorAdapter extends CursorAdapter {

    private TextView tvItemsSeparator;

    private Context mContext;
    private long mStoreID;
    private int mStoreItemsSortingOrder;

    public StoreListCursorAdapter(Context context, Cursor c, int flags, long storeID) {
        super(context, c, flags);
        mContext = context;
        mStoreID = storeID;
        mStoreItemsSortingOrder = StoresTable.getStoreItemsSortingOrder(context, storeID);
        MyLog.i("StoreListCursorAdapter", "constructor. StoreID = " + storeID);
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
        return inflater.inflate(R.layout.row_store_list, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor == null) {
            return;
        }
        // set the Item's ID so it can be retrieved latter in the onClick callbacks
        long itemID = cursor.getLong(cursor.getColumnIndex(ItemsTable.COL_ID));
        view.setTag(itemID);

        TextView tvItemName = (TextView) view.findViewById(R.id.tvItemName);
        tvItemsSeparator = (TextView) view.findViewById(R.id.tvItemsSeparator);
        if (tvItemName == null || tvItemsSeparator == null) {
            return;
        }

        // set the tvItemName text to the item's name and add the note if it's avaiable.
        String itemName = cursor.getString(cursor.getColumnIndex(ItemsTable.COL_ITEM_NAME));
        String itemNote = cursor.getString(cursor.getColumnIndex(ItemsTable.COL_ITEM_NOTE));

        if (!itemNote.isEmpty()) {
            itemName = itemName + " (" + itemNote + ")";
        }
        tvItemName.setText(itemName);

        // set tvItemName typeface
        boolean isStruckOut = cursor.getInt(cursor.getColumnIndex(ItemsTable.COL_STRUCK_OUT)) > 0;
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

        // show separator header as appropriate
        switch (mStoreItemsSortingOrder) {
            case MySettings.SORT_ALPHABETICAL:
                tvItemsSeparator.setVisibility(View.GONE);
                break;

            case MySettings.SORT_BY_GROUP:
                if (showGroupSeparator(cursor)) {
                    String separatorText = cursor.getString(cursor.getColumnIndex(GroupsTable.COL_GROUP_NAME));
                    long groupID = cursor.getLong(cursor.getColumnIndex(StoreMapsTable.COL_GROUP_ID));
                    long locationID = cursor.getLong(cursor.getColumnIndex(StoreMapsTable.COL_LOCATION_ID));
                    String locationName = cursor.getString(cursor.getColumnIndex(LocationsTable.COL_LOCATION_NAME));
                    if (!locationName.isEmpty() && groupID > 1) {
                        if (locationID > 1) {
                            separatorText = separatorText + " (" + locationName + ")";
                        } else {
                            separatorText = separatorText + " " + locationName;

                        }
                        tvItemsSeparator.setText(separatorText);
                    }
                }
                    break;

                    case MySettings.SORT_BY_AISLE:
                        if (showAisleSeparator(cursor)) {
                            String separatorText = cursor.getString(cursor.getColumnIndex(LocationsTable.COL_LOCATION_NAME));
                            tvItemsSeparator.setText(separatorText);
                        }
                        break;

                    case MySettings.SORT_MANUALLY:

                        break;
                }

                tvItemName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout viewParent = (LinearLayout) v.getParent();
                        long itemID = (long) viewParent.getTag();
                        EventBus.getDefault().post(new MyEvents.toggleItemStrikeOut(itemID));
                    }
                });

                tvItemName.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        LinearLayout viewParent = (LinearLayout) v.getParent();
                        long itemID = (long) viewParent.getTag();
                        EventBus.getDefault().post(new MyEvents.showEditItemDialog(itemID));
                        return true;
                    }
                });

                tvItemsSeparator.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LinearLayout viewParent = (LinearLayout) v.getParent();
                        long itemID = (long) viewParent.getTag();
                        long groupID;
                        long currentLocationID;
                        Cursor cursor = ItemsTable.getItemCursor(mContext, itemID);
                        if (cursor != null && cursor.getCount() > 0) {
                            cursor.moveToFirst();
                            groupID = cursor.getLong(cursor.getColumnIndex(ItemsTable.COL_GROUP));
                            if (groupID > 1) {
                                currentLocationID = StoreMapsTable.getLocationID(mContext, groupID, mStoreID);
                                switch (mStoreItemsSortingOrder) {
                                    case MySettings.SORT_ALPHABETICAL:
                                        // do nothing
                                        break;

                                    case MySettings.SORT_BY_GROUP:
                                        EventBus.getDefault().post(new MyEvents.showSelectGroupLocationDialog(itemID, groupID, currentLocationID, mStoreID));
                                        break;

                                    case MySettings.SORT_BY_AISLE:
                                        // TODO: Show select location for sort by aisle
                                        break;

                                    case MySettings.SORT_MANUALLY:
                                        // TODO: Show select location for manual sort
                                        break;
                                }
                            }
                        }
                    }
                });

        }

    private boolean showAisleSeparator(Cursor itemsCursor) {
        boolean result;
        long currentLocationID = itemsCursor.getLong(itemsCursor.getColumnIndex(StoreMapsTable.COL_LOCATION_ID));
        long previousLocationID;
        if (itemsCursor.moveToPrevious()) {
            previousLocationID = itemsCursor.getLong(itemsCursor.getColumnIndex(StoreMapsTable.COL_LOCATION_ID));
            itemsCursor.moveToNext();
            if (currentLocationID == previousLocationID) {
                tvItemsSeparator.setVisibility(View.GONE);
                result = false;
            } else {
                tvItemsSeparator.setVisibility(View.VISIBLE);
                result = true;
            }
        } else {
            tvItemsSeparator.setVisibility(View.VISIBLE);
            itemsCursor.moveToFirst();
            result = true;
        }
        return result;
    }

    private boolean showGroupSeparator(Cursor itemsCursor) {
        boolean result;
        long currentGroupID = itemsCursor.getLong(itemsCursor.getColumnIndex(ItemsTable.COL_GROUP));
        long previousGroupID;
        if (itemsCursor.moveToPrevious()) {
            previousGroupID = itemsCursor.getLong(itemsCursor.getColumnIndex(ItemsTable.COL_GROUP));
            itemsCursor.moveToNext();
            if (currentGroupID == previousGroupID) {
                tvItemsSeparator.setVisibility(View.GONE);
                result = false;
            } else {
                tvItemsSeparator.setVisibility(View.VISIBLE);
                result = true;
            }
        } else {
            tvItemsSeparator.setVisibility(View.VISIBLE);
            itemsCursor.moveToFirst();
            result = true;
        }
        return result;
    }

    public void setStoreItemsSortingOrder(int sortingMethod, long storeID) {
        if (storeID == mStoreID) {
            mStoreItemsSortingOrder = sortingMethod;
        }
    }
}