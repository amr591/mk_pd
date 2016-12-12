package com.example.arm.adapter;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import android.content.Context;
import java.lang.String;

public class ExtendedArrayAdapter<String> extends ArrayAdapter<String> {
    private static final int NOT_SELECTED = -1;
    private int selectedPos = NOT_SELECTED;

    public ExtendedArrayAdapter(Context context, int textViewResourceId, ArrayList<String> objects) {
        super(context, textViewResourceId, objects);
    }

    public void setSelection(int position) {
        selectedPos = position;
        notifyDataSetChanged();
    }

    public void clearSelection() {
        selectedPos = NOT_SELECTED;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        if (position == selectedPos) {
            view.setBackgroundColor(Color.parseColor("#ABB2E1"));
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }
        return view;
    }
}
