package com.lbconsulting.agrocerylist.classes_parse;

/**
 * This class holds public group data.
 */
public class clsParseGroup {

    private long groupID;
    private String groupName;

    public clsParseGroup(long groupID, String groupName) {
        this.groupID = groupID;
        this.groupName = groupName;
    }

    public long getGroupID() {
        return groupID;
    }

    public void setGroupID(long groupID) {
        this.groupID = groupID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }


    @Override
    public String toString() {
        return groupName;
    }
}
