package com.koresuniku.wishmaster.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.koresuniku.wishmaster.utils.DeviceUtils;

public class ActionBarUtils {

    public static void setProperActionBarContainerHeight(Activity activity, LinearLayout container) {
        int height;
        if (activity.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT) {
            if (DeviceUtils.sdkIsLollipopOrHigher()) {
                height = 112;
            } else {
                height = 56;
            }
        } else {
            if (DeviceUtils.sdkIsLollipopOrHigher()) {
                height = 96;
            } else {
                height = 48;
            }
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
