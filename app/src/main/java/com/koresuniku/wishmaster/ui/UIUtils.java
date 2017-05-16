package com.koresuniku.wishmaster.ui;


import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

import com.koresuniku.wishmaster.R;
import com.koresuniku.wishmaster.activities.ThreadsActivity;
import com.koresuniku.wishmaster.utils.DeviceUtils;

import java.lang.reflect.Method;

public class UIUtils {
    private static final String LOG_TAG = UIUtils.class.getSimpleName();

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

    public static int getActionBarSize(ThreadsActivity activity, int config) {
        if (config == Configuration.ORIENTATION_PORTRAIT) {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 112 : 56;
        }
        if (config == Configuration.ORIENTATION_LANDSCAPE) {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 96 : 48;
        }
        return 0;
    }

    public static int getActionBarHeight(Activity activity) {
        TypedArray styledAttributes = activity.getTheme().obtainStyledAttributes(
                new int[] { android.R.attr.actionBarSize });
        int actionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return actionBarSize;
    }

    public static int getNavBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    public static void setupToolbarForNavigationBar(Activity activity, Toolbar toolbar) {
        Log.d(LOG_TAG, "setupToolbarForNavigationBar:");
        if (DeviceUtils.deviceHasNavigationBar(activity)) {
            if (activity.getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                toolbar.setPadding(0, 0,
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 120 : 60, 0);
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
//        if (translucent) {
//            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
////            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
////            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//            setStatusBarTranslucent(activity, translucent);
//            setNavigationBarTranslucent(activity, translucent);
//        } else {
//            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
////            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
////            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//
//        }
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setStatusBarTranslucent(activity, translucent);
        setNavigationBarTranslucent(activity, translucent);
    }

    public static void setStatusBarTranslucent(Activity activity, boolean translucent) {
        if (translucent) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    public static void setNavigationBarTranslucent(Activity activity, boolean translucent) {
        if (translucent) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    public static Animation expand(final View v, final boolean expand) {
        try {
            Method m = v.getClass().getDeclaredMethod("onMeasure", int.class, int.class);
            m.setAccessible(true);
            m.invoke(
                    v,
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(((View)v.getParent()).getMeasuredWidth(), View.MeasureSpec.AT_MOST)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        final int initialHeight = v.getMeasuredHeight();

        if (expand) {
            v.getLayoutParams().height = 0;
        }
        else {
            v.getLayoutParams().height = initialHeight;
        }
        v.setVisibility(View.VISIBLE);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                int newHeight = 0;
                if (expand) {
                    newHeight = (int) (initialHeight * interpolatedTime);
                } else {
                    newHeight = (int) (initialHeight * (1 - interpolatedTime));
                }
                v.getLayoutParams().height = newHeight;
                v.requestLayout();

                if (interpolatedTime == 1 && !expand)
                    v.setVisibility(View.GONE);
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration(250);
        return a;
    }

}
