package com.lbconsulting.agrocerylist.classes;

/**
 * This class holds group data.
 */
public class clsGroup {

    private long mGroupID;
    private String mGroupName;
    private boolean mIsChecked;

    public clsGroup(long groupID, String groupName, boolean isChecked) {
        this.mGroupID = groupID;
        this.mGroupName = groupName;
        this.mIsChecked = isChecked;
    }

    public long getGroupID() {
        return mGroupID;
    }

    public void setGroupID(long groupID) {
        this.mGroupID = groupID;
    }

    public String getGroupName() {
        return mGroupName;
    }

    public void setGroupName(String groupName) {
        this.mGroupName = groupName;
    }

    public boolean isChecked() {
        return mIsChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.mIsChecked = isChecked;
    }

    @Override
    public String toString() {
        return mGroupName;
    }
}
