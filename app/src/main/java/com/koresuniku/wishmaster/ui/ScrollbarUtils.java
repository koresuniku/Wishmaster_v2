package com.koresuniku.wishmaster.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.koresuniku.wishmaster.utils.Constants;
import com.koresuniku.wishmaster.utils.DeviceUtils;

public class ScrollbarUtils {

    public static void setScrollbarSize(Activity activity, FrameLayout container, Configuration configuration) {
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(0, DeviceUtils.apiIs20OrHigher() ? 48 + 112 : 24 + 56,
                    0, DeviceUtils.deviceHasNavigationBar(activity) ? 96 : 0);
            params.gravity = Gravity.END;
            container.setLayoutParams(params);
        } else {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(0, DeviceUtils.apiIs20OrHigher() ? 48 + 96 : 24 + 48, 0, 0);
            params.gravity = Gravity.END;
            container.setLayoutParams(params);
        }

    }
}
