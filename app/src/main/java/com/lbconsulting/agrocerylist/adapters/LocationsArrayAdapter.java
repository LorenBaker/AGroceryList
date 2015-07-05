package com.lbconsulting.agrocerylist.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lbconsulting.agrocerylist.R;
import com.lbconsulting.agrocerylist.classes.clsLocation;

import java.util.ArrayList;

/**
 * An array adapter for showing location in a list view
 */
public class LocationsArrayAdapter extends ArrayAdapter<clsLocation> {
public LocationsArrayAdapter(Context context, ArrayList<clsLocation> locations) {
        super(context, 0, locations);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                // Get the data item for this position
                clsLocation location = getItem(position);
                // Check if an existing view is being reused, otherwise inflate the view
                if (convertView == null) {
                        convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_simple_list, parent, false);
                }
                // Lookup view for data population
                TextView tvText = (TextView) convertView.findViewById(R.id.tvText);
                // Populate the data into the template view using the data object
                tvText.setText(location.getLocationName());
                // Return the completed view to render on screen
                return convertView;
        }
}
