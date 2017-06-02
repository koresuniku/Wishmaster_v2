package com.koresuniku.wishmaster.ui;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.koresuniku.wishmaster.util.DeviceUtils;

public class ScrollbarUtils {

    public static void setScrollbarSize(Activity activity, FrameLayout container, Configuration configuration) {
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(0, DeviceUtils.sdkIsLollipopOrHigher() ? 48 + 112 : 24 + 56,
                    0, DeviceUtils.deviceHasNavigationBar(activity) ? 96 : 0);
            params.gravity = Gravity.END;
            container.setLayoutParams(params);
        } else {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(0, DeviceUtils.sdkIsLollipopOrHigher() ? 48 + 96 : 24 + 48, 0, 0);
            params.gravity = Gravity.END;
            container.setLayoutParams(params);
        }

    }
}
