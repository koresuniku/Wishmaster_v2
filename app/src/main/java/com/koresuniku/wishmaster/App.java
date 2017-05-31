package com.koresuniku.wishmaster;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.koresuniku.wishmaster.ui.listeners.SettingsContentObserver;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class App extends Application {

    static final String LOG_TAG = App.class.getSimpleName();
    public static int soundVolume;
    public static SettingsContentObserver mSettingsContentObserver;

    public OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10000, TimeUnit.SECONDS)
            .proxy(setProxy())
            .readTimeout(10000, TimeUnit.SECONDS).build();

    private Proxy setProxy() {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress("94.177.233.56", 1189));
    }
//
//    public static void fixLeakCanary696(Context context) {
//        if (!isEmui()) {
//            Log.w(LOG_TAG, "not emui");
//            return;
//        }
//        try {
//            Class clazz = Class.forName("android.gestureboost.GestureBoostManager");
//            Log.w(LOG_TAG, "clazz " + clazz);
//
//            Field _sGestureBoostManager = clazz.getDeclaredField("sGestureBoostManager");
//            _sGestureBoostManager.setAccessible(true);
//            Field _mContext = clazz.getDeclaredField("mContext");
//            _mContext.setAccessible(true);
//
//            Object sGestureBoostManager = _sGestureBoostManager.get(null);
//            if (sGestureBoostManager != null) {
//                _mContext.set(sGestureBoostManager, context);
//            }
//        } catch (Exception ignored) {
//            ignored.printStackTrace();
//        }
//    }
//
//    static boolean isEmui() {
//        return !TextUtils.isEmpty(getSystemProperty("ro.build.version.emui"));
//    }
//
//    static String getSystemProperty(String propName) {
//        String line;
//        BufferedReader input = null;
//        try {
//            Process p = Runtime.getRuntime().exec("getprop " + propName);
//            input = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"), 1024);
//            line = input.readLine();
//            input.close();
//        } catch (IOException ex) {
//            Log.w(LOG_TAG, "Unable to read sysprop " + propName, ex);
//            return null;
//        } finally {
//            IOUtils.closeQuietly(input);
//            try {
//                input.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return line;
//    }

    @Override
    public void onCreate() {
        super.onCreate();
//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);

        Log.d("Application: ", "App");
        BigImageViewer.initialize(GlideImageLoader.with(this));
        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        App.soundVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        setupContentObserver();

    }


    private void setupContentObserver() {
        mSettingsContentObserver = new SettingsContentObserver(this.getBaseContext(), new Handler());
        getApplicationContext().getContentResolver().registerContentObserver(
                android.provider.Settings.System.CONTENT_URI, true, mSettingsContentObserver);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(LOG_TAG, "onTerminate:");
        getApplicationContext().getContentResolver().unregisterContentObserver(mSettingsContentObserver);
    }
}

