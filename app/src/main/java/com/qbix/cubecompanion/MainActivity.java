package com.qbix.cubecompanion;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.kociemba.twophase.PruneTableLoader;

public class MainActivity extends FragmentActivity {

    //region #VARIABLES
    public static Typeface typeface_thin;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerItems;
    //endregion

    //region #CONSTANTS
    public static final int TIMER = 0;
    public static final int STATISTICS = 1;
    public static final int ALGS = 2;
    public static final int BEGINNERS = 3;
    public static final int SETTINGS = 4;
    public static final int FEEDBACK = 5;
    public static final int RATE = 6;
    public static final int HELP = 7;

    public static final String[] mTitles = {"Timer", "Statistics", "Algorithms", "Beginners",
    "Settings", "Feedback", "Rate", "Help / About"};
    //endregion

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        typeface_thin = Typeface.createFromAsset(getAssets(), "fonts/thin.otf");

        loadRSScrambleTables();

        findViews();
        bindViews();

        if (getPreferences(MODE_PRIVATE).getBoolean("firstTime", true)) {
            getPreferences(MODE_PRIVATE).edit().putBoolean("firstTime", false).apply();
            selectItem(HELP);
            return;
        }

        if (savedInstanceState == null) selectItem(ALGS);
    }

    @Override
    public void onBackPressed() {
        if (!SettingsFragment.confirmBeforeExit(this)) {
            super.onBackPressed(); return;
        }
        PopupDialog pd = new PopupDialog(this, "Are you sure you want to exit?",
                "Cancel", "Exit");
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
                finish();
            }
        });
        ad.show();
    }

    public void findViews() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerItems = (ListView) findViewById(R.id.left_drawer);
    }

    public void bindViews() {
        DrawerItem[] drawerItems = new DrawerItem[8];
        drawerItems[TIMER] = new DrawerItem(R.drawable.stopwatch, mTitles[TIMER]);
        drawerItems[STATISTICS] = new DrawerItem(R.drawable.statistics, mTitles[STATISTICS]);
        drawerItems[ALGS] = new DrawerItem(R.drawable.cfop, mTitles[ALGS]);
        drawerItems[BEGINNERS] = new DrawerItem(R.drawable.beginners, mTitles[BEGINNERS]);
        drawerItems[SETTINGS] = new DrawerItem(R.drawable.settings, mTitles[SETTINGS]);
        drawerItems[FEEDBACK] = new DrawerItem(R.drawable.feedback, mTitles[FEEDBACK]);
        drawerItems[RATE] = new DrawerItem(R.drawable.rate, mTitles[RATE]);
        drawerItems[HELP] = new DrawerItem(R.drawable.help, mTitles[HELP]);

        mDrawerItems.setAdapter(new DrawerItemAdapter(this, drawerItems));
        mDrawerItems.setOnItemClickListener(new DrawerItemClickListener());
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        //region #ASSIGN APPROPRIATE FRAGMENT
        Fragment fragment = null;
        switch (position) {
            case TIMER:
                fragment = TimerFragment.newInstance();
                break;
            case STATISTICS:
                fragment = StatisticsFragment.newInstance();
                break;
            case ALGS:
                fragment = AlgorithmsFragment.newInstance();
                break;
            case BEGINNERS:
                fragment = BeginnersFragment.newInstance();
                break;
            case SETTINGS:
                fragment = SettingsFragment.newInstance();
                break;
            case FEEDBACK:
                Intent feedback = new Intent(Intent.ACTION_SEND);
                feedback.setType("text/email");
                feedback.putExtra(Intent.EXTRA_EMAIL, new String[]{"sshashank124@gmail.com"});
                feedback.putExtra(Intent.EXTRA_SUBJECT, "Cube Companion Feedback");
                mDrawerLayout.closeDrawer(mDrawerItems);
                startActivity(feedback);
                break;
            case RATE:
                Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=com.qbix.cubecompanion"));
                startActivity(intent);
                mDrawerLayout.closeDrawer(mDrawerItems);
                break;
            case HELP:
                fragment = HelpFragment.newInstance();
                break;
            default:
                break;
        }
        //endregion

        //region #UPDATE CONTENT_CONTAINER WITH NEW FRAGMENT AND CLOSE DRAWER
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

            mDrawerItems.setItemChecked(position, true);
            mDrawerItems.setSelection(position);
            mDrawerLayout.closeDrawer(mDrawerItems);
        }
        //endregion
    }

    public static void setFont(ViewGroup group) {
        for (int i = 0; i < group.getChildCount(); i++) {
            View v = group.getChildAt(i);
            if (v instanceof TextView)
                ((TextView) v).setTypeface(typeface_thin);
            else if (v instanceof ViewGroup)
                setFont((ViewGroup) v);
        }
    }

    private void loadRSScrambleTables() {
        (new Thread(new Runnable() {
            @Override
            public void run() {
                PruneTableLoader ptl = new PruneTableLoader(getResources());
                while (!ptl.loadingFinished())
                    ptl.loadNext();
            }
        })).start();
    }
}