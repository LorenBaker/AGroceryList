package com.lbconsulting.agrocerylist.classes_parse;

/**
 * a ParseObject that holds PublicTables data
 * tableName is the unique table name
 * jsonContent is a JSON string that holds the data
 * timestamp is the Android system time expressed in milliseconds
 */

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("PublicTablesData")
public class PublicTablesData extends ParseObject {

    public PublicTablesData() {
        // A default constructor is required.
    }

    public String getTableName() {
        return getString("tableName");
    }
    public void setTableName(String tableName) {
        put("tableName", tableName);
    }

    public String getJsonContent() {
        return getString("jsonContent");
    }
    public void setJsonContent(String jsonContent) {
        put("jsonContent", jsonContent);
    }

    public long getTimestamp() {
        return getLong("timestamp");
    }
    public void setTimestamp(long timestamp) {
        put("timestamp", timestamp);
    }

}


