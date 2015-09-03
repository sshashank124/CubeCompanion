package com.qbix.cubecompanion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerItemAdapter extends BaseAdapter {

    //region #VARIABLES
    private DrawerItem[] data;
    private static LayoutInflater inflater = null;
    //endregion

    public DrawerItemAdapter(Context context, DrawerItem[] data) {
        this.data = data;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return data.length; }

    @Override
    public DrawerItem getItem(int position) { return data[position]; }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.drawer_item, parent, false);

        ((ImageView) convertView.findViewById(R.id.drawer_item_icon))
                .setImageResource(data[position].iconId);

        TextView title = (TextView) convertView.findViewById(R.id.drawer_item_text);
        title.setText(data[position].text);
        title.setTypeface(MainActivity.typeface_thin);

        return convertView;
    }
}