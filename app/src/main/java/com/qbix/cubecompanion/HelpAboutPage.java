package com.qbix.cubecompanion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class HelpAboutPage extends Fragment {

    public static HelpAboutPage newInstance() {
        return new HelpAboutPage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vi = inflater.inflate(R.layout.fragment_help_about_page, container, false);
        MainActivity.setFont((ViewGroup) vi);

        TextView aboutText = (TextView) vi.findViewById(R.id.help_about_text);
        aboutText.setText(Html.fromHtml(getResources().getString(R.string.help_about)));

        return vi;
    }
}