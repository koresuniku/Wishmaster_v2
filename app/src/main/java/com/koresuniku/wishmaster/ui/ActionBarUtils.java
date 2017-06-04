package com.koresuniku.wishmaster.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.koresuniku.wishmaster.R;
import com.koresuniku.wishmaster.util.DeviceUtils;

public class ActionBarUtils {

    public static final int MEDIA_TOOLBAR_TEXT_SIZE_VERTICAL = 20;
    public static final int MEDIA_TOOLBAR_TEXT_SIZE_HORIZONAL = 16;

    public static void setProperActionBarContainerHeight(Activity activity, LinearLayout container) {
        int height;
        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            height = (int) activity.getResources().getDimension(R.dimen.action_bar_height_vertical);
        } else {
            height = (int) activity.getResources().getDimension(R.dimen.action_bar_height_horizontal);
        }
        container.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
    }

    public static void setupAppBarLayoutSizeDependingOnOrientation(
            Activity activity, AppBarLayout appBarLayout, int offset) {
        int appBarHeight;
        if (DeviceUtils.sdkIsLollipopOrHigher()) {
            appBarHeight = 48 + offset;
        } else appBarHeight = 24 + offset;

//        if (activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
//            if (DeviceUtils.sdkIsLollipopOrHigher()) {
//                appBarHeight += offset;
//            } else appBarHeight += offset;
//        } else {
//            if (DeviceUtils.sdkIsLollipopOrHigher()) {
//                appBarHeight = 144;
//            } else appBarHeight = 72;
//        }

        appBarLayout.setLayoutParams(new CoordinatorLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, appBarHeight));
    }
}
