package com.koresuniku.wishmaster.ui.adapter;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.koresuniku.wishmaster.ui.activity.SingleThreadActivity;
import com.koresuniku.wishmaster.ui.activity.ThreadsActivity;
import com.koresuniku.wishmaster.ui.fragment.GalleryFragment;
import com.koresuniku.wishmaster.http.threads_api.models.Files;

import java.util.HashMap;
import java.util.List;

public class PicVidPagerAdapter extends FragmentStatePagerAdapter {
    private final String LOG_TAG = PicVidPagerAdapter.class.getSimpleName();


    private Activity mActivity;
    private List<Files> mFiles;
    private Files mFile;
    public int mediaClickedPosition = -1;

    @SuppressLint("UseSparseArrays")
    public PicVidPagerAdapter(FragmentManager fm, Activity activity,
                              List<Files> files, int clickedPosition) {
        super(fm);
        mActivity = activity;
        mFiles = files;
        mediaClickedPosition = clickedPosition;
        if (activity instanceof ThreadsActivity) {
            //ThreadsActivity.galleryFragments = new GalleryFragment[files.size()];
            ThreadsActivity.galleryFragments = new HashMap<>();
        }
        if (activity instanceof SingleThreadActivity) {
            SingleThreadActivity.galleryFragments = new HashMap<>();
        }
    }



    @Override
    public Fragment getItem(int position) {
        Log.d(LOG_TAG, "position: " + position);
        if (mActivity instanceof ThreadsActivity) {
            ThreadsActivity.galleryFragments.put(position, GalleryFragment.getInstance(
                    mActivity, mFiles.get(position), mediaClickedPosition, mediaClickedPosition == position));
            return ThreadsActivity.galleryFragments.get(position);
        }
        if (mActivity instanceof SingleThreadActivity) {
            SingleThreadActivity.galleryFragments.put(position, GalleryFragment.getInstance(
                    mActivity, mFiles.get(position), mediaClickedPosition, mediaClickedPosition == position));
            return SingleThreadActivity.galleryFragments.get(position);
        }
        return null;
    }

    @Override
    public int getCount() {
        return mFiles.size();
    }

    @Override
    public Parcelable saveState()
    {
        return null;
    }

    public void stopAndReleasePlayers(boolean pause, boolean release) {
        if (mActivity instanceof ThreadsActivity) {
            for (GalleryFragment fragment : ThreadsActivity.galleryFragments.values()) {
                if (fragment != null && fragment.videoViewUnit != null) {
                    if (pause) fragment.videoViewUnit.pauseVideoView();
                    if (release) {
                        fragment.videoViewUnit.releaseVideoView();
                    }
                    fragment.onActivityStop = true;
                }
            }
        }
        if (mActivity instanceof SingleThreadActivity) {
            for (GalleryFragment fragment : SingleThreadActivity.galleryFragments.values()) {
                if (fragment != null && fragment.videoViewUnit != null) {
                    if (pause) fragment.videoViewUnit.pauseVideoView();
                    if (release) {
                        fragment.videoViewUnit.releaseVideoView();
                    }
                    fragment.onActivityStop = true;
                }
            }

        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        Log.d(LOG_TAG, "destroyItem: " + position);
        super.destroyItem(container, position, object);

        if (mActivity instanceof ThreadsActivity) {
            GalleryFragment fragment = ThreadsActivity.galleryFragments.get(position);
            if (fragment != null && fragment.videoViewUnit != null) {
                fragment.videoViewUnit.pauseVideoView();
                fragment.videoViewUnit.releaseVideoView();
                ThreadsActivity.galleryFragments.remove(position);
            }
        }

        if (mActivity instanceof SingleThreadActivity) {
            GalleryFragment fragment = SingleThreadActivity.galleryFragments.get(position);
            if (fragment != null && fragment.videoViewUnit != null) {
                fragment.videoViewUnit.pauseVideoView();
                fragment.videoViewUnit.releaseVideoView();
                SingleThreadActivity.galleryFragments.remove(position);
            }
        }
    }
}
