package com.lbconsulting.agrocerylist.classes_parse;

/**
 * a ParseObject that holds initial Parse group data
 */

import android.database.Cursor;

import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.database.GroupsTable;
import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

@ParseClassName("Groups")
public class Groups extends ParseObject {
    private static final String AUTHOR = "author";

    public Groups() {
        // A default constructor is required.
    }

    public void setGroup(String groupName, long sortKey) {
        groupName = groupName.trim();
        if (!groupName.isEmpty()) {
            setGroupName(groupName);
            setSortKey(sortKey);
            setAuthor(ParseUser.getCurrentUser());
        }
    }

    public void setGroupCursor(Cursor cursor) {
        if (cursor == null) {
            return;
        }
        // String currentRow = DatabaseUtils.dumpCurrentRowToString(cursor);
        setGroupName(cursor.getString(cursor.getColumnIndex(GroupsTable.COL_GROUP_NAME)));
        setSortKey(cursor.getLong(cursor.getColumnIndex(GroupsTable.COL_SORT_KEY)));
        setAuthor(ParseUser.getCurrentUser());
    }

    public long getSortKey() {
        return getLong(GroupsTable.COL_SORT_KEY);
    }

    public void setSortKey(long sortKey) {
        put(GroupsTable.COL_SORT_KEY, sortKey);
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


    public static ParseQuery<Groups> getQuery() {
        return ParseQuery.getQuery(Groups.class);
    }

    public static ParseObject getGroup(String parseObjectID) {
        ParseObject group = null;
        try {
            ParseQuery<Groups> query = getQuery();
            query.whereEqualTo("objectID", parseObjectID);
            List groups = query.find();
            if (groups!=null && groups.size() > 0) {
                group = (ParseObject) groups.get(0);
            }
        } catch (ParseException e) {
            MyLog.e("Groups", "getGroup: ParseException: " + e.getMessage());
        }
        return group;

    }

    public static void saveGroupToParse(Groups group, int saveType) {
        ParseACL groupACL = new ParseACL(ParseUser.getCurrentUser());
        groupACL.setPublicReadAccess(true);
        group.setACL(groupACL);
        try {
            switch (saveType) {
                case clsParseUtils.SAVE_THIS_THREAD:
                    // use SAVE_THIS_THREAD if this method is being called in an AsyncTask or background thread.
                    group.save();
                    MyLog.i("Groups", "saveGroupToParse: SAVE(): name = " + group.getGroupName());
                    break;

                case clsParseUtils.SAVE_IN_BACKGROUND:
                    group.saveInBackground();
                    MyLog.i("Groups", "saveGroupToParse: SAVE_IN_BACKGROUND(): name = " + group.getGroupName());
                    break;

                case clsParseUtils.SAVE_EVENTUALLY:
                    group.saveEventually();
                    MyLog.i("Groups", "saveGroupToParse: SAVE_EVENTUALLY(): name = " + group.getGroupName());
                    break;
            }

        } catch (ParseException e) {
            MyLog.e("Groups", "saveGroupToParse: ParseException: " + e.getMessage());
        }
    }
}


