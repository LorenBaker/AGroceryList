package com.lbconsulting.agrocerylist.activities;

import android.app.Application;

import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes_parse.Groups;
import com.lbconsulting.agrocerylist.classes_parse.Initial_Items;
import com.lbconsulting.agrocerylist.classes_parse.Items;
import com.lbconsulting.agrocerylist.classes_parse.Locations;
import com.lbconsulting.agrocerylist.classes_parse.ParseStoreMap;
import com.lbconsulting.agrocerylist.classes_parse.PublicTablesData;
import com.lbconsulting.agrocerylist.classes_parse.StoreChains;
import com.lbconsulting.agrocerylist.classes_parse.Stores;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;


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
/*    ParseObject.registerSubclass(ParseStoreMap.class);
    ParseObject.registerSubclass(PublicTablesData.class);*/
    ParseObject.registerSubclass(Groups.class);
    ParseObject.registerSubclass(Locations.class);
    ParseObject.registerSubclass(StoreChains.class);
    ParseObject.registerSubclass(Stores.class);
    ParseObject.registerSubclass(Initial_Items.class);
    ParseObject.registerSubclass(Items.class);
    //TODO: remove Initial_Items
    ParseObject.registerSubclass(Initial_Items.class);
    Parse.initialize(this, "Z1uTyZFcvSsV74AdrqbfWPe44WhqtTvwmJupITew", "ZuBh1PV8oBebw2xgpURpdF5XDms5zS11QpYW9Kpn");
    MyLog.i("AGroceryListApplication", "onCreate: Parse initialized");

   // ParseUser.enableAutomaticUser();
    ParseACL defaultACL = new ParseACL();
    //user's data is only accessible by the user itself unless explicit permission is given
    ParseACL.setDefaultACL(defaultACL, true);

  }
}
