package com.koresuniku.wishmaster.ui;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.koresuniku.wishmaster.App;
import com.koresuniku.wishmaster.R;
import com.koresuniku.wishmaster.ui.activity.ThreadsActivity;
import com.koresuniku.wishmaster.util.DeviceUtils;

public class UiUtils {
    private static final String LOG_TAG = UiUtils.class.getSimpleName();

    public static boolean barsAreShown = true;

    public static void setCurrentThemeColorFilterForImageView(
            Activity activity, ImageView imageView) {
        imageView.setColorFilter(activity.getResources().getColor(R.color.colorAccent),
                PorterDuff.Mode.SRC_ATOP);
    }

    public static void tintMenuIcons(Activity activity, Menu menu) {
        //Log.i(LOG_TAG, "tintMenuIcons: menu.size() == " + menu.size());
        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.getIcon() != null) {
                //Log.i(LOG_TAG, "tintMenuIcons: menu.getIcon() != null == true");
                menuItem.getIcon().setColorFilter(
                                activity.getResources().getColor(android.R.color.background_light),
                                PorterDuff.Mode.SRC_ATOP);

            }
        }
    }

    public static int getDisplayWidth(Activity activity) {
        int pixel = activity.getWindowManager().getDefaultDisplay().getWidth();
        return pixel / (int) activity.getResources().getDisplayMetrics().density;
    }

    public static void setupToolbarForNavigationBar(Activity activity, Toolbar toolbar) {
        Log.d(LOG_TAG, "setupToolbarForNavigationBar:");
        if (DeviceUtils.deviceHasNavigationBar(activity)) {
            if (activity.getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                toolbar.setPadding(0, 0,
                        (int) activity.getResources().getDimension(R.dimen.navigation_bar_height), 0);
            } else toolbar.setPadding(0, 0, 0, 0);
        }
    }

    public static void hideSystemUI(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    public static void showSystemUI(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    public static void showSystemUIExceptNavigationBar(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public static void setBarsTranslucent(Activity activity, boolean translucent) {
        setStatusBarTranslucent(activity, translucent);
        setNavigationBarTranslucent(activity, translucent);
    }

    public static void setStatusBarTranslucent(Activity activity, boolean translucent) {
        if (translucent) {
           // activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public static void setNavigationBarTranslucent(Activity activity, boolean translucent) {
        if (translucent) {
            //activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxToDp(Context context, int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

}
