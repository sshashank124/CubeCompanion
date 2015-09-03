package com.qbix.cubecompanion;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PopupDialog {

    //region #VARIABLES
    public LinearLayout view;
    public Button button1, button2;

    public ListView options;

    public Button button;

    private AlertDialog.Builder adb;
    //endregion

    public PopupDialog (Activity activity, String m, String b1, String b2) {
        view = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.dialog_action, null);
        MainActivity.setFont(view);
        TextView message = (TextView) view.findViewById(R.id.dialog_message);
        message.setText(m);
        button1 = (Button) view.findViewById(R.id.dialog_button1);
        button1.setText(b1);
        button2 = (Button) view.findViewById(R.id.dialog_button2);
        button2.setText(b2);
        adb = new AlertDialog.Builder(activity).setView(view);
    }

    public PopupDialog (Activity activity, String t, String[] texts) {
        view = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.dialog_choices, null);
        MainActivity.setFont(view);
        TextView title = (TextView) view.findViewById(R.id.dialog_title);
        title.setText(t);
        options = (ListView) view.findViewById(R.id.dialog_choices_container);
        options.setAdapter(new ArrayAdapter<>(activity, R.layout.dialog_choices_option, texts));
        MainActivity.setFont(options);
        adb = new AlertDialog.Builder(activity).setView(view);
    }

    public PopupDialog (Activity activity, String t, String b) {
        view = (LinearLayout) activity.getLayoutInflater().inflate(R.layout.dialog_text, null);
        MainActivity.setFont(view);
        TextView text = (TextView) view.findViewById(R.id.dialog_text);
        text.setText(t);
        button = (Button) view.findViewById(R.id.dialog_button);
        button.setText(b);
        adb = new AlertDialog.Builder(activity).setView(view);
    }

    public AlertDialog getDialog() {
        return adb.create();
    }
}