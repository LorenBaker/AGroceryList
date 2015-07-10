package com.lbconsulting.agrocerylist.activities;

import android.app.Application;

import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes_parse.ParseStoreMap;
import com.lbconsulting.agrocerylist.classes_parse.PublicTablesData;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;
import com.parse.ParseUser;


public class AGroceryListApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    MyLog.i("AGroceryListApplication", "onCreate");


    // Initialize Crash Reporting.
    //ParseCrashReporting.enable(this);

    // Enable Local Datastore.
   // Parse.enableLocalDatastore(this);

    // Add your initialization code here
    ParseObject.registerSubclass(ParseStoreMap.class);
    ParseObject.registerSubclass(PublicTablesData.class);
    Parse.initialize(this, "Z1uTyZFcvSsV74AdrqbfWPe44WhqtTvwmJupITew", "ZuBh1PV8oBebw2xgpURpdF5XDms5zS11QpYW9Kpn");
    MyLog.i("AGroceryListApplication", "onCreate: Parse initialized");

   // ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
    //user's data is only accessible by the user itself unless explicit permission is given
    ParseACL.setDefaultACL(defaultACL, true);
    ParseACL.setDefaultACL(new ParseACL(), true);

  }
}
