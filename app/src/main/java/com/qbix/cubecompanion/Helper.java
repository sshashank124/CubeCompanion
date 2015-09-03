package com.qbix.cubecompanion;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.ListPopupWindow;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

public class Helper {

    public static String millisToShortTime(long millis) {
        long second = (millis / 1000) % 60;
        long minute = (millis / (1000 * 60)) % 60;
        return String.format("%02d:%02d.%1d", minute, second, (millis / 100) % 10);
    }

    public static ListPopupWindow createPageSelectorPopup(Activity activity, String[] titles,
                                               final TextView anchorView) {
        final ListPopupWindow lpw = new ListPopupWindow(activity);
        anchorView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.expand, 0);
        lpw.setAdapter(new PageSelectorPopupAdapter(activity, titles));
        lpw.setModal(true);
        lpw.setBackgroundDrawable(new ColorDrawable());
        lpw.setAnimationStyle(R.style.popup_animation);
        anchorView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (lpw.isShowing()) {
                    lpw.dismiss();
                } else {
                    lpw.setWidth(anchorView.getWidth() - 20);
                    lpw.setHorizontalOffset(10);
                    lpw.show();
                    anchorView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                            R.drawable.collapse, 0);
                }
            }
        });
        lpw.setAnchorView(anchorView);
        lpw.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                anchorView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.expand, 0);
            }
        });
        return lpw;
    }
}