package com.lbconsulting.agrocerylist.classes_parse;

import java.util.ArrayList;

/**
 * This class holds an array of clsParseGroup
 */
public class clsParseGroupArray {
    private ArrayList<clsParseGroup> groups;

    public clsParseGroupArray(ArrayList<clsParseGroup> groups){
        this.groups = groups;
    }

    public ArrayList<clsParseGroup> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList<clsParseGroup> groups) {
        this.groups = groups;
    }
}
