package com.koresuniku.wishmaster.ui.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;


import com.devbrackets.android.exomedia.core.listener.MetadataListener;
import com.devbrackets.android.exomedia.listener.OnBufferUpdateListener;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.github.piasy.biv.view.BigImageView;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.koresuniku.wishmaster.App;
import com.koresuniku.wishmaster.R;
import com.koresuniku.wishmaster.http.HttpClient;
import com.koresuniku.wishmaster.ui.activity.SingleThreadActivity;
import com.koresuniku.wishmaster.ui.activity.ThreadsActivity;
import com.koresuniku.wishmaster.http.threads_api.models.Files;
import com.koresuniku.wishmaster.ui.controller.VideoViewUnit;
import com.koresuniku.wishmaster.util.Constants;
import com.koresuniku.wishmaster.util.DeviceUtils;
import com.koresuniku.wishmaster.util.Formats;
import com.koresuniku.wishmaster.ui.UiUtils;
import com.koresuniku.wishmaster.ui.listener.AnimationListenerDown;
import com.koresuniku.wishmaster.ui.listener.AnimationListenerUp;
import com.koresuniku.wishmaster.ui.listener.OnImageEventListener;

import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;

public class GalleryFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
    private final String LOG_TAG = GalleryFragment.class.getSimpleName();
    private GalleryFragment thisFragment;

    public Activity mActivity;
    private Files mFile;
    public int mediaClickedPosition;
    public boolean mPlayVideo;
    public boolean onActivityStop;

    public FrameLayout layoutContainer;
    private ScaleAnimation animCollapseActionBar;
    private ScaleAnimation animExpandActionBar;
    private AnimationListenerUp animationListenerUpActionBar;
    private AnimationListenerDown animationListenerDownActionBar;

    public View rootView;
    private BigImageView mBigImageView;
    private OnImageEventListener onImageEventListener;
    private ImageView gifImageView;
    public SimpleExoPlayer player;
    public VideoViewUnit videoViewUnit;



    public GalleryFragment(Activity activity, Files file, int clickedPosition, boolean playVideo) {
        mActivity = activity;
        mFile = file;
        mediaClickedPosition = clickedPosition;
        mPlayVideo = playVideo;
        thisFragment = this;
        onActivityStop = false;
        Log.d(LOG_TAG, "GalleryFragment: " + clickedPosition);
    }

    public static GalleryFragment getInstance(
            Activity activity, Files file, int clickedPosition, boolean playVideo) {
        if (activity instanceof ThreadsActivity) {
            return new GalleryFragment(activity, file, clickedPosition, playVideo);
        }
        if (activity instanceof SingleThreadActivity) {
            return new GalleryFragment(activity, file, clickedPosition, playVideo);
        }
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //Log.d(LOG_TAG, "onCreateView:");
        if (mActivity == null) return rootView;

        setupImageAnimations();

        int v = mFile.getPath().indexOf(".");
        final String path = mFile.getPath();
        if (Formats.IMAGE_FORMATS.contains(path.substring(v + 1, path.length()))) {
            rootView = inflater.inflate(R.layout.gallery_image_layout, container, false);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    mActivity.getWindowManager().getDefaultDisplay().getWidth(),
                    mActivity.getWindowManager().getDefaultDisplay().getHeight());
            rootView.setLayoutParams(params);
            rootView.setDrawingCacheEnabled(false);
            mBigImageView = new BigImageView(mActivity);
            ((FrameLayout)rootView).addView(mBigImageView);
            mBigImageView.setOnClickListener(thisFragment);
            rootView.findViewById(R.id.big_image_progress_bar_container).bringToFront();
            rootView.findViewById(R.id.big_image_progress_bar_container).setOnClickListener(thisFragment);
            mBigImageView.setBackgroundColor(mActivity.getResources().getColor(R.color.full_media_tint));
            mBigImageView.showImage(
                    //Uri.parse(Constants.DVACH_BASE_URL + mFile.getThumbnail()),
                    Uri.parse(Constants.DVACH_BASE_URL + path));
            onImageEventListener = new OnImageEventListener(
                    thisFragment, mBigImageView, rootView.findViewById(R.id.big_image_progress_bar_container));
            mBigImageView.getSSIV().setOnImageEventListener(onImageEventListener);
            mBigImageView.getSSIV().setMaxScale(10.0f);

        } else if (Formats.GIF.equals(path.substring(v + 1, path.length()))) {
            rootView = inflater.inflate(R.layout.gallery_gif_layout, container, false);
            gifImageView = new ImageView(mActivity);
            ((FrameLayout)rootView).addView(gifImageView);
            rootView.findViewById(R.id.gif_progress_bar).bringToFront();
            Glide.with(mActivity).load(Uri.parse(Constants.DVACH_BASE_URL + path)).asGif()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .listener(new RequestListener<Uri, GifDrawable>() {
                @Override
                public boolean onException(Exception e, Uri model,
                                           Target<GifDrawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(GifDrawable resource, Uri model,
                                               Target<GifDrawable> target,
                                               boolean isFromMemoryCache, boolean isFirstResource) {
                    rootView.findViewById(R.id.gif_progress_bar).setVisibility(GONE);
                    return false;
                }
            })
                    .into(gifImageView);
            gifImageView.setOnClickListener(thisFragment);
            rootView.setBackgroundColor(mActivity.getResources().getColor(R.color.full_media_tint));
        } else if (Formats.WEBM.equals(path.substring(v + 1, path.length()))) {
            rootView = inflater.inflate(R.layout.gallery_video_layout, container, false);
            rootView.findViewById(R.id.video_progress_bar).bringToFront();
            videoViewUnit = new VideoViewUnit(this);
            videoViewUnit.onCreateView(path);
        }
        return rootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(LOG_TAG, "onConfigurationChanged: ");
        if (videoViewUnit != null) videoViewUnit.onConfigurationChanged(newConfig);
    }



    private void setupImageAnimations() {
        if (mActivity instanceof ThreadsActivity) {
            animationListenerUpActionBar = new AnimationListenerUp(((ThreadsActivity) mActivity).picVidToolbarContainer);
            animationListenerDownActionBar = new AnimationListenerDown(((ThreadsActivity) mActivity).picVidToolbarContainer);
        }
        if (mActivity instanceof SingleThreadActivity) {
            animationListenerUpActionBar = new AnimationListenerUp(((SingleThreadActivity) mActivity).picVidToolbarContainer);
            animationListenerDownActionBar = new AnimationListenerDown(((SingleThreadActivity) mActivity).picVidToolbarContainer);
        }

        animExpandActionBar = new ScaleAnimation(1, 1, 0, 1, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        animExpandActionBar.setDuration(250);
        animCollapseActionBar = new ScaleAnimation(1, 1, 1, 0, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        animCollapseActionBar.setDuration(250);
        animExpandActionBar.setAnimationListener(animationListenerUpActionBar);
        animCollapseActionBar.setAnimationListener(animationListenerDownActionBar);
    }


    @Override
    public void onClick(View v) {
        changeUiVisibility();
    }



    public void changeUiVisibility() {
        Log.d(LOG_TAG, "barsAreShown: " + UiUtils.barsAreShown);

        if (mActivity instanceof ThreadsActivity) {
            for (GalleryFragment fragment : ThreadsActivity.galleryFragments.values()) {
                if (fragment.videoViewUnit != null) fragment.videoViewUnit.showOrHideControlView();
            }
        }

        if (mActivity instanceof SingleThreadActivity) {
            for (GalleryFragment fragment : SingleThreadActivity.galleryFragments.values()) {
                if (fragment.videoViewUnit != null) fragment.videoViewUnit.showOrHideControlView();
            }
        }

        if (UiUtils.barsAreShown) performHideBars();
        else performShowBars();
    }

    private void performShowBars() {
        if (Constants.API_INT >= 19) {
            UiUtils.showSystemUI(mActivity);
            if (mActivity instanceof ThreadsActivity) {
                ((ThreadsActivity) mActivity).fullPicVidOpenedAndFullScreenModeIsOn = false;
                ((ThreadsActivity) mActivity).picVidToolbarContainer.startAnimation(animExpandActionBar);
            }
            if (mActivity instanceof SingleThreadActivity) {
                ((SingleThreadActivity) mActivity).fullPicVidOpenedAndFullScreenModeIsOn = false;
                ((SingleThreadActivity) mActivity).picVidToolbarContainer.startAnimation(animExpandActionBar);
            }
        }
        UiUtils.barsAreShown = true;
    }

    private void performHideBars() {
        if (Constants.API_INT >= 19) {
            Log.i(LOG_TAG, "picvid view clicked: ");

            if (player == null) {
                UiUtils.hideSystemUI(mActivity);
                if (mActivity instanceof ThreadsActivity) {
                    ((ThreadsActivity) mActivity).fullPicVidOpenedAndFullScreenModeIsOn = true;
                    ((ThreadsActivity) mActivity).picVidToolbarContainer.startAnimation(animCollapseActionBar);
                }
                if (mActivity instanceof SingleThreadActivity) {
                    ((SingleThreadActivity) mActivity).fullPicVidOpenedAndFullScreenModeIsOn = true;
                    ((SingleThreadActivity) mActivity).picVidToolbarContainer.startAnimation(animCollapseActionBar);
                }
            } else {
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            java.lang.Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if (mActivity instanceof ThreadsActivity) {
                            if (!((ThreadsActivity) mActivity).fullPicVidOpened) {
                                return;
                            }
                        }
                        UiUtils.hideSystemUI(mActivity);
                        if (mActivity instanceof ThreadsActivity) {
                            ((ThreadsActivity) mActivity).fullPicVidOpenedAndFullScreenModeIsOn = true;
                            if (videoViewUnit != null) {
                                ((ThreadsActivity) mActivity).picVidToolbarContainer.startAnimation(videoViewUnit.animCollapseControls);
                            } else {
                                ((ThreadsActivity) mActivity).picVidToolbarContainer.startAnimation(animCollapseActionBar);
                            }
                        }
                        if (mActivity instanceof SingleThreadActivity) {
                            ((SingleThreadActivity) mActivity).fullPicVidOpenedAndFullScreenModeIsOn = true;
                            if (videoViewUnit != null) {
                                ((SingleThreadActivity) mActivity).picVidToolbarContainer.startAnimation(videoViewUnit.animCollapseControls);
                            } else {
                                ((SingleThreadActivity) mActivity).picVidToolbarContainer.startAnimation(animCollapseActionBar);
                            }
                        }
                    }
                }.execute();
            }
        }
        UiUtils.barsAreShown = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (videoViewUnit != null) {
            onActivityStop = false;
            videoViewUnit.updateControlView();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (videoViewUnit != null) {
            videoViewUnit.pauseVideoView();
            onActivityStop = true;
        }
    }


}
