package com.koresuniku.wishmaster.utils;

import android.app.Activity;
import android.os.Build;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;

public class DeviceUtils {
    public static int getApiInt() { return Build.VERSION.SDK_INT; }

    public static boolean deviceHasNavigationBar(Activity activity) {
        boolean hasMenuKey = ViewConfiguration.get(activity).hasPermanentMenuKey();
        boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
        return !hasMenuKey && !hasBackKey;
    }
    public static boolean apiIsLollipopOrHigher() { return Build.VERSION.SDK_INT >= 20; }

    public static boolean apiIsKitkatOrHigher() { return Build.VERSION.SDK_INT >= 19; }
}
