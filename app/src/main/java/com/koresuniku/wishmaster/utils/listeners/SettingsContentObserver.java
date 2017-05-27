package com.koresuniku.wishmaster.utils.listeners;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

import com.koresuniku.wishmaster.App;
import com.koresuniku.wishmaster.activities.SingleThreadActivity;
import com.koresuniku.wishmaster.activities.ThreadsActivity;
import com.koresuniku.wishmaster.fragments.GalleryFragment;


public class SettingsContentObserver extends ContentObserver {
    private final String LOG_TAG = SettingsContentObserver.class.getSimpleName();
    private Activity mActivity;
    int previousVolume;
    Context context;

    public SettingsContentObserver(Context c, Handler handler) {
        super(handler);
        this.context = c;


        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        previousVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.d(LOG_TAG, "onChange:");
        AudioManager audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        App.soundVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

        GalleryFragment fragment;
        if (mActivity instanceof ThreadsActivity) {
            if (ThreadsActivity.galleryFragments == null) return;
//            for (int i = 0; i < ThreadsActivity.galleryFragments.size(); i++) {
//                fragment = ThreadsActivity.galleryFragments.get(i);
//                Log.d(LOG_TAG, "switching sound for fragment: " + i);
//                fragment.switchSound(App.soundVolume);
//            }
            for (GalleryFragment fragment1 : ThreadsActivity.galleryFragments.values()) {
                Log.d(LOG_TAG, "switching sound for fragment: ");
                fragment1.switchSound(App.soundVolume);
            }
        }
        if (mActivity instanceof SingleThreadActivity) {
            if (SingleThreadActivity.galleryFragments == null) return;
            Log.d(LOG_TAG, "galleryFragmentsSize: " + SingleThreadActivity.galleryFragments.size());
//            for (int i = 0; i < SingleThreadActivity.galleryFragments.size(); i++) {
//                fragment = SingleThreadActivity.galleryFragments.get(i);
//                Log.d(LOG_TAG, "switching sound for fragment: " + i);
//                fragment.switchSound(App.soundVolume);
//            }
            for (GalleryFragment fragment1 : SingleThreadActivity.galleryFragments.values()) {
                Log.d(LOG_TAG, "switching sound for fragment: ");
                fragment1.switchSound(App.soundVolume);
            }
        }
    }

    public void switchActivity(Activity activity) {
        this.mActivity = activity;
    }
}