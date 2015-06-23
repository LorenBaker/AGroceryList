package com.lbconsulting.agrocerylist.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.classes.MyEvents;
import com.lbconsulting.agrocerylist.classes.MyLog;
import com.lbconsulting.agrocerylist.classes.MySettings;

import de.greenrobot.event.EventBus;


/**
 * A placeholder fragment containing a simple view.
 */
public class fragHome extends Fragment {

    public fragHome() {
    }

    public static fragHome newInstance() {

        return new fragHome();
/*        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_TWO_PANE, isTwoPane);
        fragment.setArguments(args);
        return fragment;*/
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MyLog.i("fragHome", "onCreateView");
        View rootView = inflater.inflate(R.layout.frag_home, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("fragHome", "onActivityCreated");
        MySettings.setActiveFragmentID(MySettings.FRAG_HOME);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        MyLog.i("fragHome", "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        MyLog.i("fragHome", "onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        MyLog.i("fragHome", "onResume");
        super.onResume();
    }

    @Override
    public void onDestroy() {
        MyLog.i("fragHome", "onDestroy");
        super.onDestroy();
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

}
