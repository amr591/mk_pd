package com.example.arm.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

public class ExtendedSimpleAdapter extends SimpleAdapter {
    private int mItemOnSelection;

    public ExtendedSimpleAdapter (Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        mItemOnSelection = -1;
    }

    public void setSelection(int position) {
        mItemOnSelection = position;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        Drawable background = view.getBackground();
        if (position == mItemOnSelection) {
            if (background instanceof ColorDrawable) {
                int color = ((ColorDrawable) background).getColor();
                if (color == Color.parseColor("#67E667")) {
                    view.setBackgroundColor(Color.parseColor("#FF7373"));
                }
                else if (color == Color.parseColor("#FF7373")) {
                    view.setBackgroundColor(Color.parseColor("#67E667"));
                }
            }
        }
        return view;
    }
}
