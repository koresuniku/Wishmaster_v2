package com.koresuniku.wishmaster.ui.listener;

import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;

import com.koresuniku.wishmaster.ui.activity.SingleThreadActivity;
import com.koresuniku.wishmaster.ui.fragment.GalleryFragment;
import com.koresuniku.wishmaster.util.Constants;

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
    public void onPageSelected(final int position) {
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
        if (fragment != null && fragment.videoViewUnit != null) {
            fragment.videoViewUnit.pauseVideoView();
        }

        mActivity.picVidOpenedPosition = position;

        fragment = SingleThreadActivity.galleryFragments.get(mActivity.picVidOpenedPosition);
        if (fragment != null && fragment.videoViewUnit != null) {
            if (mActivity.sharedPreferences.getInt(Constants.SP_AUTOPLAY, 1) == 1) {
                Log.d(LOG_TAG, "startuem!");
                fragment.videoViewUnit.startVideoView();
            } else Log.d(LOG_TAG, "smth wrong");
        }

        mActivity.picVidToolbarMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(LOG_TAG, "onMenuItemClick");
                mActivity.picVidToolbarUrl =
                        Constants.DVACH_BASE_URL + SingleThreadActivity.files.get(position).getPath();
                mActivity.picVidToolbarFilename =
                        SingleThreadActivity.files.get(position).getDisplayName();
                mActivity.mFileSaver.saveFileToExternalStorage(
                        mActivity.picVidToolbarUrl, mActivity.picVidToolbarFilename);
                return false;
            }
        });
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
