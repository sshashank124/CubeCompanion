package com.qbix.cubecompanion;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;


public class HelpExpandableListAdapter extends BaseExpandableListAdapter {

    //region #VARIABLES
    private List<String> helpGroupTitle;
    private HashMap<String, List<String>> helpChildText;
    private static LayoutInflater inflater = null;
    //endregion

    public HelpExpandableListAdapter(Activity act, List<String> hTitle,
                                     HashMap<String, List<String>> hText) {
        this.helpGroupTitle = hTitle;
        this.helpChildText = hText;
        inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public String getChild(int groupPosition, int childPosition) {
        return this.helpChildText.get(this.helpGroupTitle.get(groupPosition))
                .get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        boolean newView = false;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.help_child, parent, false);
            newView = true;
        }

        String childText = getChild(groupPosition, childPosition);
        TextView childTextView = (TextView) convertView.findViewById(R.id.help_child_text);
        if (newView) childTextView.setTypeface(MainActivity.typeface_thin);
        childTextView.setText(childText);

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.helpChildText.get(this.helpGroupTitle.get(groupPosition)).size();
    }

    @Override
    public String getGroup(int groupPosition) {
        return this.helpGroupTitle.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.helpGroupTitle.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        boolean newView = false;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.help_parent, parent, false);
            newView = true;
        }

        String parentText = getGroup(groupPosition);
        TextView parentTextView = (TextView) convertView.findViewById(R.id.help_parent_text);
        if (newView) parentTextView.setTypeface(MainActivity.typeface_thin);
        parentTextView.setText(parentText);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}