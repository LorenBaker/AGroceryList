package com.lbconsulting.agrocerylist.classes;

import android.content.Context;

/**
 * Application settings helper methods
 */
public class MySettings {


    public static final int FRAG_HOME = 1;
    public static final int INITIAL_NUMBER_OF_AISLES = 10;

    private static Context mContext;

    public static void setContext(Context context) {
        //mContext = context.getApplicationContext();
        mContext = context;
    }
}
