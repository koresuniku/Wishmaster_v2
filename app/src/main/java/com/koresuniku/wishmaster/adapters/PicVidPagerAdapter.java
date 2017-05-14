package com.koresuniku.wishmaster.adapters;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import com.koresuniku.wishmaster.activities.SingleThreadActivity;
import com.koresuniku.wishmaster.activities.ThreadsActivity;
import com.koresuniku.wishmaster.fragments.GalleryFragment;
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
//            ThreadsActivity.galleryFragments[position] = GalleryFragment.getInstance(
//                    mActivity, mFiles.get(position), mediaClickedPosition, mediaClickedPosition == position);
//            return ThreadsActivity.galleryFragments[position];
            ThreadsActivity.galleryFragments.put(position, GalleryFragment.getInstance(
                    mActivity, mFiles.get(position), mediaClickedPosition, mediaClickedPosition == position));
            return ThreadsActivity.galleryFragments.get(position);
        }
        if (mActivity instanceof SingleThreadActivity) {
//            SingleThreadActivity.galleryFragments[position] = GalleryFragment.getInstance(
//                    mActivity, mFiles.get(position), mediaClickedPosition, mediaClickedPosition == position);
//            return SingleThreadActivity.galleryFragments[position];
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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        //Log.d(LOG_TAG, "instantiateItem: position: " + position);
        return super.instantiateItem(container, position);
    }

    public void stopAndReleasePlayers(boolean pause, boolean release) {
        if (mActivity instanceof ThreadsActivity) {
            for (GalleryFragment fragment : ThreadsActivity.galleryFragments.values()) {
                if (fragment != null && fragment.videoView != null) {
                    if (pause) fragment.pauseVideoView();
                    if (release) fragment.releaseVideoView();
                    fragment.onActivityStop = true;
                }
            }
        }
        if (mActivity instanceof SingleThreadActivity) {
            for (GalleryFragment fragment : SingleThreadActivity.galleryFragments.values()) {
                if (fragment != null && fragment.videoView != null) {
                    if (pause) fragment.pauseVideoView();
                    if (release) fragment.releaseVideoView();
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
            if (fragment != null && fragment.videoView != null) {
                fragment.pauseVideoView();
                fragment.releaseVideoView();
                ThreadsActivity.galleryFragments.remove(position);
            }
        }

        if (mActivity instanceof SingleThreadActivity) {
            GalleryFragment fragment = SingleThreadActivity.galleryFragments.get(position);
            if (fragment != null && fragment.videoView != null) {
                fragment.pauseVideoView();
                fragment.releaseVideoView();
                SingleThreadActivity.galleryFragments.remove(position);
            }
        }
    }
}
