package com.qbix.cubecompanion;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class AlgorithmsListAdapter extends BaseAdapter {

    //region #VARIABLES
    public final List<CubeAlgorithm> data;
    private Context context;
    private static LayoutInflater inflater = null;
    private boolean[] changed;
    //endregion

    public AlgorithmsListAdapter(Context c, final List<CubeAlgorithm> d) {
        data = d;
        changed = new boolean[data.size()];
        context = c;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() { return data.size(); }

    @Override
    public CubeAlgorithm getItem(int position) { return data.get(position); }

    @Override
    public long getItemId(int position) { return position; }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final CubeAlgorithm ca = getItem(position);
        boolean newView = (convertView == null);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.algorithm_db, parent, false);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.cube_state);
        Picasso.with(context).load(ca.resId).fit().centerInside().into(imageView);

        TextView cn = (TextView) convertView.findViewById(R.id.case_number);
        cn.setText("Case " + ca.algCase);
        if (newView) cn.setTypeface(MainActivity.typeface_thin);

        final TextView alg = (TextView) convertView.findViewById(R.id.algorithm);
        alg.setText(ca.getAlg());
        if (newView) alg.setTypeface(MainActivity.typeface_thin);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alg.setText(ca.getNextAlg());
                changed[position] = true;
            }
        });

        return convertView;
    }

    public void updatePreferences(final int page) {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                DBHandlerAlgorithms adh = DBHandlerAlgorithms.getInstance(context);
                List<CubeAlgorithm> caUpdate = new ArrayList<>();
                for (int i = 0; i < data.size(); ++i) {
                    if (changed[i]) caUpdate.add(data.get(i));
                }
                adh.updateAlgorithms(page, caUpdate);
            }
        })).start();
    }
}