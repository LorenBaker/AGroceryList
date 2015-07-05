package com.lbconsulting.agrocerylist.classes;

/**
 * EventBus events.
 */
public class MyEvents {

    public static class updateUI {
        public updateUI() {
        }
    }

    public static class setActionBarTitle {
        final String mTitle;

        public setActionBarTitle(String title) {
            mTitle = title;
        }

        public String getTitle() {
            return mTitle;
        }
    }

    public static class showOkDialog {
        final String mTitle;
        final String mMessage;

        public showOkDialog(String title, String message) {
            mTitle = title;
            mMessage = message;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getMessage() {
            return mMessage;
        }
    }

    public static class showToast {
        final String mMessage;

        public showToast(String message) {
            mMessage = message;
        }

        public String getMessage() {
            return mMessage;
        }
    }

    public static class onActiveStoreChange {
        long mActiveStoreID;

        public onActiveStoreChange(long activeStoreID) {
            mActiveStoreID = activeStoreID;
        }

        public long getActiveStoreID() {
            return mActiveStoreID;
        }
    }

    public static class restartLoader {
        int mLoaderID;

        public restartLoader(int loaderID) {
            mLoaderID = loaderID;
        }

        public int getLoaderID() {
            return mLoaderID;
        }
    }

    public static class showFragment {
        int mFragmentID;

        public showFragment(int fragmentID) {
            mFragmentID = fragmentID;
        }

        public int getFragmentID() {
            return mFragmentID;
        }

    }

    public static class toggleItemSelection {
        long mItemID;

        public toggleItemSelection(long itemID) {
            mItemID = itemID;
        }

        public long getItemID() {
            return mItemID;
        }
    }

    public static class showEditItemDialog {
        long mItemID;

        public showEditItemDialog(long itemID) {
            mItemID = itemID;
        }

        public long getItemID() {
            return mItemID;
        }
    }

    public static class toggleItemStrikeOut {
        long mItemID;

        public toggleItemStrikeOut(long itemID) {
            mItemID = itemID;
        }

        public long getItemID() {
            return mItemID;
        }
    }

    public static class showSelectGroupLocationDialog {
        long mItemID;
        long mGroupID;
        long mLocationID;
        long mStoreID;
        public showSelectGroupLocationDialog(long itemID, long groupID, long locationID, long storeID) {
            mItemID = itemID;
            mGroupID = groupID;
            mLocationID = locationID;
            mStoreID=storeID;
        }

        public long getGroupID() {
            return mGroupID;
        }

        public long getItemID() {
            return mItemID;
        }

        public long getLocationID() {
            return mLocationID;
        }

        public long getStoreID() {
            return mStoreID;
        }
    }

/*    public static class onClick_masterListItem {
        long mItemID;
        public onClick_masterListItem(long itemID) {
            mItemID = itemID;
        }
        public long getItemID(){
            return mItemID;
        }
    }*/
}
