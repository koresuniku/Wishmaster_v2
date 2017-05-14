package com.koresuniku.wishmaster.utils.listeners;

import android.support.v4.view.ViewPager;
import android.util.Log;

import com.koresuniku.wishmaster.activities.ThreadsActivity;
import com.koresuniku.wishmaster.fragments.GalleryFragment;
import com.koresuniku.wishmaster.utils.Constants;

public class ThreadsViewPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {
    private final String LOG_TAG = ThreadsViewPagerOnPageChangeListener.class.getSimpleName();
    private ThreadsActivity mActivity;

    public ThreadsViewPagerOnPageChangeListener(ThreadsActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d(LOG_TAG, "ViewPager.OnPageChangeListener(): " + position);

        String displayName = ThreadsActivity.files.get(position).getDisplayName();

        if (displayName == null || displayName.equals("") || displayName.equals(" ")) {
            mActivity.picVidToolbarTitleTextView.setText("noname.hz");
        } else mActivity.picVidToolbarTitleTextView.setText(displayName);

        mActivity.picVidToolbarShortInfoTextView.setText("(" + (position + 1) + "/"
                + ThreadsActivity.files.size() + "), " + ThreadsActivity.files.get(position).getWidth() + "x"
                + ThreadsActivity.files.get(position).getHeight() + ", "
                + ThreadsActivity.files.get(position).getSize() + " кб");

        GalleryFragment fragment = ThreadsActivity.galleryFragments.get(mActivity.picVidOpenedPosition);
        if (fragment != null && fragment.videoView != null) {
            fragment.pauseVideoView();
        }

        mActivity.picVidOpenedPosition = position;

        fragment = ThreadsActivity.galleryFragments.get(mActivity.picVidOpenedPosition);
        if (fragment != null && fragment.videoView != null) {
            if (mActivity.sharedPreferences.getInt(Constants.SP_AUTOPLAY, 1) == 1) {
                Log.d(LOG_TAG, "startuem!");
                fragment.startVideoView();
            } else Log.d(LOG_TAG, "smth wrong");
        }

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
