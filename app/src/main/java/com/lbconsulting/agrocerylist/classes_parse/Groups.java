package com.lbconsulting.agrocerylist.classes_parse;

/**
 * a ParseObject that holds initial Parse group data
 */

import android.database.Cursor;

import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.database.GroupsTable;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

@ParseClassName("Groups")
public class Groups extends ParseObject {
    private static final String COL_GROUP_ID = "groupID";
    private static final String AUTHOR = "author";

    public Groups() {
        // A default constructor is required.
    }

    public void setGroup(long id, String groupName) {
        groupName = groupName.trim();
        if (id > 0 && !groupName.isEmpty()) {
            setGroupID(id);
            setGroupName(groupName);
            setAuthor(ParseUser.getCurrentUser());
        }
    }

    public void setGroupCursor(Cursor cursor) {
        if (cursor == null) {
            return;
        }

        // String currentRow = DatabaseUtils.dumpCurrentRowToString(cursor);
        setGroupID(cursor.getLong(cursor.getColumnIndex(GroupsTable.COL_GROUP_ID)));
        setGroupName(cursor.getString(cursor.getColumnIndex(GroupsTable.COL_GROUP_NAME)));
        setAuthor(ParseUser.getCurrentUser());
    }

    public long getGroupID() {
        return getLong(COL_GROUP_ID);
    }

    public void setGroupID(long itemID) {
        put(COL_GROUP_ID, itemID);
    }

    public String getGroupName() {
        return getString(GroupsTable.COL_GROUP_NAME);
    }

    public void setGroupName(String groupName) {
        put(GroupsTable.COL_GROUP_NAME, groupName);
    }

    public ParseUser getAuthor() {
        return getParseUser(AUTHOR);
    }

    public void setAuthor(ParseUser currentUser) {
        put(AUTHOR, currentUser);
    }

/*
    public boolean isChecked() {
        return getBoolean(GroupsTable.COL_CHECKED);
    }

    public void setChecked(boolean isChecked) {
        put(GroupsTable.COL_CHECKED, isChecked);
    }

*/

    public static ParseQuery<Groups> getQuery() {
        return ParseQuery.getQuery(Groups.class);
    }


}


