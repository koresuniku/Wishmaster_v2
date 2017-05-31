package com.koresuniku.wishmaster.ui.listeners;

import android.support.v4.view.ViewPager;
import android.util.Log;

import com.koresuniku.wishmaster.activities.SingleThreadActivity;
import com.koresuniku.wishmaster.fragments.GalleryFragment;
import com.koresuniku.wishmaster.utils.Constants;

public class SingleThreadViewPagerOnPageChangeListener implements ViewPager.OnPageChangeListener {
    private final String LOG_TAG = SingleThreadViewPagerOnPageChangeListener.class.getSimpleName();
    private SingleThreadActivity mActivity;

    public SingleThreadViewPagerOnPageChangeListener(SingleThreadActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d(LOG_TAG, "ViewPager.OnPageChangeListener(): " + position);

        String displayName = SingleThreadActivity.files.get(position).getDisplayName();

        if (displayName == null || displayName.equals("") || displayName.equals(" ")) {
            mActivity.picVidToolbarTitleTextView.setText("noname.hz");
        } else mActivity.picVidToolbarTitleTextView.setText(displayName);

        mActivity.picVidToolbarShortInfoTextView.setText("(" + (position + 1) + "/"
                + SingleThreadActivity.files.size() + "), " + SingleThreadActivity.files.get(position).getWidth() + "x"
                + SingleThreadActivity.files.get(position).getHeight() + ", "
                + SingleThreadActivity.files.get(position).getSize() + " кб");

        GalleryFragment fragment = SingleThreadActivity.galleryFragments.get(mActivity.picVidOpenedPosition);
        if (fragment != null && fragment.videoView != null) {
            fragment.pauseVideoView();
        }

        mActivity.picVidOpenedPosition = position;

        fragment = SingleThreadActivity.galleryFragments.get(mActivity.picVidOpenedPosition);
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
