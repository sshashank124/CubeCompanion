package com.qbix.cubecompanion;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class StatisticsTimesListAdapter extends BaseAdapter {

    //region #VARIABLES
    private List<SolveTime> data;
    private static LayoutInflater inflater = null;
    private Activity activity;
    //endregion

    public StatisticsTimesListAdapter(Activity activity, List<SolveTime> data) {
        this.data = data;
        this.activity = activity;
        inflater = (LayoutInflater) this.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return data.size(); }

    @Override
    public SolveTime getItem(int position) { return data.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.solve_time, parent, false);

        TextView index = (TextView) convertView.findViewById(R.id.solve_time_list_item_index);
        TextView time = (TextView) convertView.findViewById(R.id.solve_time_list_item_time);
        TextView date = (TextView) convertView.findViewById(R.id.solve_time_list_item_date);
        ImageView delete = (ImageView) convertView.findViewById(R.id.solve_time_list_item_delete);

        index.setText(""+(position+1));
        time.setText(getItem(position).getFormattedTime());
        date.setText(getItem(position).getShortFormattedDate());

        index.setTypeface(MainActivity.typeface_thin);
        time.setTypeface(MainActivity.typeface_thin);
        date.setTypeface(MainActivity.typeface_thin);

        final String t = (String) time.getText();
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!SettingsFragment.confirmBeforeDelete(activity)) {
                    new DeleteSolveTime().execute(data.remove(position));
                    notifyDataSetChanged();
                    return;
                }
                PopupDialog pd = new PopupDialog(activity, "Delete this solve time?\n"+t,
                        "Cancel", "Delete");
                final AlertDialog ad = pd.getDialog();
                pd.button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ad.dismiss();
                    }
                });
                pd.button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DeleteSolveTime().execute(data.remove(position));
                        ad.dismiss();
                        notifyDataSetChanged();
                    }
                });
                ad.show();
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SolveTime st = getItem(position);
                String text = st.getFormattedTime()+"\n"+st.getLongFormattedDate()+"\n\n";
                text += DBHandlerSolveTimes.getInstance(activity).getScramble(st.getID());
                PopupDialog pd = new PopupDialog(activity, text, "Close");
                final AlertDialog ad = pd.getDialog();
                pd.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ad.dismiss();
                    }
                });
                ad.show();
            }
        });

        return convertView;
    }

    private class DeleteSolveTime extends AsyncTask<SolveTime, Void, Void> {
        @Override
        protected Void doInBackground(SolveTime... st) {
            DBHandlerSolveTimes.getInstance(activity).deleteSolveTime(st[0]);
            return null;
        }
    }
}