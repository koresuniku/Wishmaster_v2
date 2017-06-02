package com.koresuniku.wishmaster.ui.listener;

import android.view.View;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.github.piasy.biv.view.BigImageView;
import com.koresuniku.wishmaster.ui.activity.ThreadsActivity;
import com.koresuniku.wishmaster.ui.fragment.GalleryFragment;

public class OnImageEventListener implements SubsamplingScaleImageView.OnImageEventListener {
    private GalleryFragment mGalleryFragment;
    private BigImageView mBigImageView;
    private View mProgressBarContainer;

    public OnImageEventListener(GalleryFragment galleryFragment, BigImageView bigImageView, View progressBarContainer) {
        this.mGalleryFragment = galleryFragment;
        this.mBigImageView = bigImageView;
        this.mProgressBarContainer = progressBarContainer;
    }
    @Override
    public void onReady() {

    }

    @Override
    public void onImageLoaded() {
        mProgressBarContainer.setVisibility(View.GONE);

        if (mGalleryFragment.mActivity instanceof ThreadsActivity) {
            ((ThreadsActivity) mGalleryFragment.mActivity).imageCachePaths.add(mBigImageView.currentImageFile());
        }
    }

    @Override
    public void onPreviewLoadError(Exception e) {

    }

    @Override
    public void onImageLoadError(Exception e) {

    }

    @Override
    public void onTileLoadError(Exception e) {

    }

    @Override
    public void onPreviewReleased() {

    }
}
