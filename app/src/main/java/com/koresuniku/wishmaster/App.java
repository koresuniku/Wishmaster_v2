package com.koresuniku.wishmaster;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.fresco.FrescoImageLoader;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.squareup.leakcanary.LeakCanary;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class App extends Application {

    static final String TAG = "BIV-App";

    public OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10000, TimeUnit.SECONDS)
            .proxy(setProxy())
            .readTimeout(10000, TimeUnit.SECONDS).build();

    private Proxy setProxy() {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress("94.177.233.56", 1189));
    }


    public static void fixLeakCanary696(Context context) {
        if (!isEmui()) {
            Log.w(TAG, "not emui");
            return;
        }
        try {
            Class clazz = Class.forName("android.gestureboost.GestureBoostManager");
            Log.w(TAG, "clazz " + clazz);

            Field _sGestureBoostManager = clazz.getDeclaredField("sGestureBoostManager");
            _sGestureBoostManager.setAccessible(true);
            Field _mContext = clazz.getDeclaredField("mContext");
            _mContext.setAccessible(true);

            Object sGestureBoostManager = _sGestureBoostManager.get(null);
            if (sGestureBoostManager != null) {
                _mContext.set(sGestureBoostManager, context);
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

    static boolean isEmui() {
        return !TextUtils.isEmpty(getSystemProperty("ro.build.version.emui"));
    }

    static String getSystemProperty(String propName) {
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            Log.w(TAG, "Unable to read sysprop " + propName, ex);
            return null;
        } finally {
            IOUtils.closeQuietly(input);
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return line;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);

        Log.d("Application: ", "App");
        BigImageViewer.initialize(GlideImageLoader.with(this));
    }
}

