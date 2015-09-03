package com.qbix.cubecompanion;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;


public class PageSelectorPopupAdapter extends BaseAdapter implements ListAdapter {

    //region #VARIABLES
    private String[] pageNames;
    private static LayoutInflater inflater = null;
    //endregion

    public PageSelectorPopupAdapter(Activity activity, String[] pn) {
        this.pageNames = pn;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return pageNames.length; }

    @Override
    public String getItem(int position) { return pageNames[position]; }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        boolean newView = convertView == null;
        if (newView) convertView = inflater.inflate(R.layout.page_picker_text, parent, false);

        TextView item = (TextView) convertView.findViewById(R.id.item_text);
        item.setText(pageNames[position]);
        if (newView) item.setTypeface(MainActivity.typeface_thin);

        return convertView;
    }
}