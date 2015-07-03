package com.lbconsulting.agrocerylist.database;

import android.net.Uri;

/**
 * Created by Loren on 6/30/2015.
 */
public class JoinedTables {

    //region STORES_WITH_CHAIN_NAMES
    /*  SELECT tblStores._id, tblStores.storeRegionalName, tblStoreChains.storeChainName
        FROM tblStores
        JOIN tblStoreChains
        ON tblStores.storeChainID = tblStoreChains._id
        ORDER BY storeChainName ASC, storeRegionalName ASC  */
    public static final String[] PROJECTION_STORES_WITH_CHAIN_NAMES = {
            StoresTable.TABLE_STORES + "." + StoresTable.COL_STORE_ID,
            StoresTable.TABLE_STORES + "." + StoresTable.COL_STORE_REGIONAL_NAME,
            StoresTable.TABLE_STORES + "." + StoresTable.COL_MANUAL_SORT_KEY,
            StoresTable.TABLE_STORES + "." + StoresTable.COL_STORE_ITEMS_SORTING_ORDER,
            StoresTable.TABLE_STORES + "." + StoresTable.COL_COLOR_THEME_ID,
            StoreChainsTable.TABLE_STORE_CHAINS + "." + StoreChainsTable.COL_STORE_CHAIN_NAME};

    public static final String CONTENT_PATH_STORES_WITH_CHAIN_NAMES = "storesWithChainNames";

    public static final Uri CONTENT_URI_STORES_WITH_CHAIN_NAMES = Uri.parse("content://" + aGroceryListContentProvider.AUTHORITY
            + "/" + CONTENT_PATH_STORES_WITH_CHAIN_NAMES);

    public static final String SORT_ORDER_CHAIN_NAME_THEN_STORE_NAME =
            StoreChainsTable.COL_STORE_CHAIN_NAME + " ASC, " + StoresTable.COL_STORE_REGIONAL_NAME + " ASC";
    //endregion

    //region ITEMS_BY_GROUPS
    /*  SELECT tblItems._id, groupName, itemName, itemNote, groupID, itemSelected, itemStruckOut, itemChecked
        FROM tblItems JOIN tblGroups
        ON (tblItems.groupID = tblGroups._id)
        ORDER BY groupName, itemName*/
    public static final String[] PROJECTION_ITEMS_BY_GROUPS = {
            ItemsTable.TABLE_ITEMS + "." + ItemsTable.COL_ITEM_ID,
            GroupsTable.COL_GROUP_NAME, ItemsTable.COL_ITEM_NAME, ItemsTable.COL_ITEM_NOTE,
            ItemsTable.COL_GROUP_ID, ItemsTable.COL_SELECTED, ItemsTable.COL_STRUCK_OUT,
            ItemsTable.COL_CHECKED, ItemsTable.COL_FAVORITE
    };
    public static final String CONTENT_PATH_ITEMS_BY_GROUPS = "itemsByGroups";
    public static final Uri CONTENT_URI_ITEMS_BY_GROUPS = Uri.parse("content://" + aGroceryListContentProvider.AUTHORITY
            + "/" + CONTENT_PATH_ITEMS_BY_GROUPS);
    public static final String SORT_ORDER_ITEMS_BY_GROUP =
            GroupsTable.COL_GROUP_NAME + " ASC, " + ItemsTable.COL_ITEM_NAME + " ASC";
    //endregion

}
