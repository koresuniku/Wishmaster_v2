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
import com.koresuniku.wishmaster.util.Constants;
import com.koresuniku.wishmaster.util.DeviceUtils;
import com.koresuniku.wishmaster.util.Formats;
import com.koresuniku.wishmaster.ui.UiUtils;
import com.koresuniku.wishmaster.ui.listener.AnimationListenerDown;
import com.koresuniku.wishmaster.ui.listener.AnimationListenerUp;
import com.koresuniku.wishmaster.ui.listener.OnImageEventListener;

import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;

public class GalleryFragment extends android.support.v4.app.Fragment implements View.OnClickListener, OnPreparedListener {
    private final String LOG_TAG = GalleryFragment.class.getSimpleName();
    private GalleryFragment thisFragment;

    public Activity mActivity;
    private Files mFile;
    private int mediaClickedPosition;
    private boolean mPlayVideo;

    public boolean onActivityStop;
    private boolean isCompleted = false;
    private long seekToMillis;
    private int volume = 1;

    private FrameLayout layoutContainer;
    private ScaleAnimation animCollapseActionBar;
    private ScaleAnimation animExpandActionBar;
    private AnimationListenerUp animationListenerUpActionBar;
    private AnimationListenerDown animationListenerDownActionBar;
    private AnimationListenerUp animationListenerUpControls;
    private AnimationListenerDown animationListenerDownControls;
    private ScaleAnimation animCollapseControls;
    private ScaleAnimation animExpandControls;

    public View rootView;
    private BigImageView mBigImageView;
    private OnImageEventListener onImageEventListener;
    private ImageView gifImageView;
    public SimpleExoPlayer player;
    public com.devbrackets.android.exomedia.ui.widget.VideoView videoView;
    private boolean isPrepared;

    public View controlView;
    public ImageView playPause;
    public FrameLayout controlViewContainer;
    private SeekBar seekbar;
    private TextView progressTime;
    private TextView overallDuration;
    private Handler mHandler;
    private View playPauseContainer;
    private View soundSwitcherContainer;
    private ImageView soundSwitcher;
    private ImageView exitImageView;


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
            createVideoView(path);
            createControlView(true);
            setupVideoAnimations();
        }
        return rootView;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(LOG_TAG, "onConfigurationChanged: ");
        if (controlViewContainer != null) {
            layoutContainer.removeView(controlViewContainer);
            createControlView(false);
            setPlayPauseImage(false);
            playPauseContainer.setEnabled(true);
            playPauseContainer.setClickable(true);
            playPauseContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changePlayPauseImage();
                }
            });
            soundSwitcherContainer.setEnabled(true);
            soundSwitcherContainer.setClickable(true);
            soundSwitcherContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            playPauseContainer.bringToFront();
            soundSwitcherContainer.bringToFront();
            controlView.requestLayout();
            seekbar.setEnabled(true);
            updateControlView();
            videoView.setOnBufferUpdateListener(new OnBufferUpdateListener() {
                @Override
                public void onBufferingUpdate(@IntRange(from = 0L, to = 100L) int percent) {
                    seekbar.setSecondaryProgress(percent);
                }
            });
            if (isCompleted) {
                completeVideoView();
            }
        }

    }

    private void createVideoView(String path) {
        layoutContainer = (FrameLayout) rootView.findViewById(R.id.full_video_layout_container);
        videoView = new VideoView(mActivity);
        videoView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        layoutContainer.addView(videoView);
        videoView.setControls(null);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        OkHttpDataSourceFactory dataSourceFactory =
                new OkHttpDataSourceFactory(HttpClient.client,
                Util.getUserAgent(mActivity, mActivity.getString(R.string.app_name)),
                (TransferListener<? super DataSource>) bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(Constants.DVACH_BASE_URL + path),
                dataSourceFactory, extractorsFactory, null, null);
        videoView.setVideoURI(Uri.parse(Constants.DVACH_BASE_URL + path), videoSource);
        Log.d(LOG_TAG, "path: " + Constants.DVACH_BASE_URL + path);
        videoView.setOnPreparedListener(this);
        videoView.setBackgroundColor(mActivity.getResources().getColor(R.color.full_media_tint));
        videoView.setOnClickListener(videoOnClickListener);
        videoView.setOnBufferUpdateListener(new OnBufferUpdateListener() {
            @Override
            public void onBufferingUpdate(@IntRange(from = 0L, to = 100L) int percent) {
                seekbar.setSecondaryProgress(percent);
            }
        });
        videoView.setOnCompletionListener(new OnCompletionListener() {
            @Override
            public void onCompletion() {
                isCompleted = true;
                completeVideoView();
                if (rootView.findViewById(R.id.video_progress_bar).getVisibility() == View.VISIBLE) {
                    rootView.findViewById(R.id.video_progress_bar).setVisibility(GONE);
                }
            }
        });
        videoView.setId3MetadataListener(new MetadataListener() {
            @Override
            public void onMetadata(Metadata metadata) {
                Log.d(LOG_TAG, "metadata: ");
                for (int i = 0; i < metadata.length(); i++) {
                    Log.d(LOG_TAG, metadata.get(i).toString() + " ");
                }
            }
        });
        isPrepared = false;

    }

    private void createControlView(boolean firstTime) {
        if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            controlViewContainer = (FrameLayout) mActivity.getLayoutInflater()
                    .inflate(R.layout.webm_video_controls_redesign, null, false);
        } else  controlViewContainer = (FrameLayout) mActivity.getLayoutInflater()
                .inflate(R.layout.webm_video_controls, null, false);


        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        controlViewContainer.setLayoutParams(params);
        layoutContainer.addView(controlViewContainer);

        controlView = controlViewContainer.findViewById(R.id.control_view);
        if (DeviceUtils.deviceHasNavigationBar(mActivity) && Build.VERSION.SDK_INT >= 19) {
            if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                Log.d(LOG_TAG, "controlView is null: " + (controlView == null));
                controlViewContainer.setPadding(0, 0, 0, Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 96 : 48);
            } else {
                if (DeviceUtils.deviceHasNavigationBar(mActivity)) {
                    controlView.findViewById(R.id.control_view).setPadding(
                            0, 0, Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 96 : 48, 0);
                } else {
                    controlView.findViewById(R.id.control_view).setPadding(0, 0, 0, 0);
                }
            }
        }

        controlViewContainer.setVisibility(UiUtils.barsAreShown ? View.VISIBLE : GONE);
        controlViewContainer.bringToFront();

        initControlChildViews(firstTime);
    }

    public void pauseVideoView() {
        Log.d(LOG_TAG, "pauseVideoView: " + mediaClickedPosition);
        videoView.pause();
        setPlayPauseImage(false);
        videoView.requestLayout();
    }


    public void releaseVideoView() {
        videoView.release();
    }


    public void completeVideoView() {
        playPause.setImageResource(R.drawable.ic_replay_black_24dp);
        playPauseContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.video_progress_bar).setVisibility(View.VISIBLE);
                restartVideoView(0L, 0);
            }
        });
    }

    public void switchSound(int volume) {
        Log.d(LOG_TAG, "switching sound:");
        if (videoView == null) return;

        AudioManager audio = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);

        Log.d(LOG_TAG, "setting volume " + volume);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        if (volume == 0) {
            soundSwitcher.setImageResource(R.drawable.ic_volume_off_black_24dp);
        } else {
            soundSwitcher.setImageResource(R.drawable.ic_volume_up_black_24dp);
        }
    }



    private View.OnClickListener switchSoundOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AudioManager audio = (AudioManager) mActivity.getSystemService(Context.AUDIO_SERVICE);
            if (audio.getStreamVolume(AudioManager.STREAM_MUSIC) != 0) {
                Log.d(LOG_TAG, "stream volume != 0");
                thisFragment.volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                App.mSettingsContentObserver.onChange(true);
                soundSwitcher.setImageResource(R.drawable.ic_volume_off_black_24dp);
            } else {
                Log.d(LOG_TAG, "stream volume is 0");
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, thisFragment.volume, 0);

                App.mSettingsContentObserver.onChange(true);
                Log.d(LOG_TAG, "stream volume after " + audio.getStreamVolume(AudioManager.STREAM_MUSIC));
                soundSwitcher.setImageResource(R.drawable.ic_volume_up_black_24dp);
            }
        }
    };


    public void restartVideoView(long seekToMillis, int progress) {
        videoView.restart();
        videoView.seekTo(seekToMillis);
        seekbar.setProgress(progress);
        isCompleted = false;
    }

    public void startVideoView() {
        Log.d(LOG_TAG, "startVideoView: " + mediaClickedPosition);
        videoView.start();
        setPlayPauseImage(false);
        updateControlView();
        videoView.requestLayout();
        soundSwitcherContainer.requestLayout();
        soundSwitcherContainer.bringToFront();
        soundSwitcherContainer.setOnClickListener(switchSoundOnClickListener);
    }

    private void changePlayPauseImage() {
        if (videoView.isPlaying()) {
            pauseVideoView();
        } else {
            startVideoView();
        }
    }

    private void setPlayPauseImage(boolean firstTime) {
        if (firstTime) {
            Log.d(LOG_TAG, "setPlayPauseImage: firstTime");
            playPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            return;
        }
        if (videoView.isPlaying()) {
            playPause.setImageResource(R.drawable.ic_pause_black_24dp);
        } else {
            playPause.setImageResource(R.drawable.ic_play_arrow_black_24dp);
        }
        if (isCompleted) {
            playPause.setImageResource(R.drawable.ic_replay_black_24dp);
        }

    }

    private void initControlChildViews(boolean firstTime) {
        Log.d(LOG_TAG, "control view is null " + (controlView == null));
        seekbar = (SeekBar) controlView.findViewById(R.id.video_progress);
        progressTime = (TextView) controlView.findViewById(R.id.progress_time);
        overallDuration = (TextView) controlView.findViewById(R.id.overall_duration);
        playPauseContainer = controlView.findViewById(R.id.play_pause_container);
        playPauseContainer.requestLayout();
        playPause = (ImageView) controlView.findViewById(R.id.play_pause);
        seekbar.setMax(100);
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    seekToMillis = videoView.getDuration() * progress / 100;
                    if (isCompleted) restartVideoView(seekToMillis, progress);
                    else videoView.seekTo(seekToMillis);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                pauseVideoView();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                startVideoView();
            }
        });
        playPauseContainer.setOnClickListener(null);
        setPlayPauseImage(firstTime);
        seekbar.setEnabled(false);
        soundSwitcher = (ImageView) controlView.findViewById(R.id.sound_switcher);
        soundSwitcherContainer = controlView.findViewById(R.id.sound_switcher_container);
        soundSwitcherContainer.requestLayout();
        soundSwitcherContainer.post(new Runnable() {
            @Override
            public void run() {
                soundSwitcherContainer.setEnabled(true);
                soundSwitcherContainer.setClickable(true);
                soundSwitcherContainer.setOnClickListener(switchSoundOnClickListener);

            }
        });
        switchSound(App.soundVolume);
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

    private void setupVideoAnimations() {
        animationListenerDownControls = new AnimationListenerDown(controlViewContainer);
        animationListenerUpControls = new AnimationListenerUp(controlViewContainer);

        animExpandControls = new ScaleAnimation(1, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        animExpandControls.setDuration(250);
        animCollapseControls = new ScaleAnimation(1, 1, 1, 0, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        animCollapseControls.setDuration(250);

        animExpandControls.setAnimationListener(animationListenerUpControls);
        animCollapseControls.setAnimationListener(animationListenerDownControls);

    }

    private void showOrHideControlView() {
        Log.d(LOG_TAG, "showOrHideControlView: ");
        if (controlViewContainer != null) {
            if (UiUtils.barsAreShown) controlViewContainer.setVisibility(GONE);
            else controlViewContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        changeUiVisibility();
    }

    private View.OnClickListener videoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            controlViewContainer.startAnimation(UiUtils.barsAreShown ? animCollapseControls : animExpandControls);
            changeUiVisibility();
        }
    };

    private void changeUiVisibility() {
        Log.d(LOG_TAG, "barsAreShown: " + UiUtils.barsAreShown);

        if (mActivity instanceof ThreadsActivity) {
            for (GalleryFragment fragment : ThreadsActivity.galleryFragments.values()) {
                if (fragment != null) fragment.showOrHideControlView();
            }
        }

        if (mActivity instanceof SingleThreadActivity) {
            for (GalleryFragment fragment : SingleThreadActivity.galleryFragments.values()) {
                if (fragment != null) fragment.showOrHideControlView();
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
                            if (videoView != null) {
                                ((ThreadsActivity) mActivity).picVidToolbarContainer.startAnimation(animCollapseControls);
                            } else {
                                ((ThreadsActivity) mActivity).picVidToolbarContainer.startAnimation(animCollapseActionBar);
                            }
                        }
                        if (mActivity instanceof SingleThreadActivity) {
                            ((SingleThreadActivity) mActivity).fullPicVidOpenedAndFullScreenModeIsOn = true;
                            if (videoView != null) {
                                ((SingleThreadActivity) mActivity).picVidToolbarContainer.startAnimation(animCollapseControls);
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
        if (videoView != null) {
            onActivityStop = false;
            updateControlView();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (videoView != null) {
            pauseVideoView();
            onActivityStop = true;
        }
    }

    @Override
    public void onPrepared() {
        Log.d(LOG_TAG, "video prepared");
        isPrepared = true;
        rootView.findViewById(R.id.video_progress_bar).setVisibility(GONE);

        playPauseContainer.post(new Runnable() {
            @Override
            public void run() {
                playPauseContainer.setEnabled(true);
                playPauseContainer.setClickable(true);
                playPauseContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changePlayPauseImage();
                    }
                });
            }
        });

        seekbar.setEnabled(true);
        if (isCompleted) {
            isCompleted = false;
            videoView.seekTo(0);
        }
        if (mPlayVideo) {
            startVideoView();
        }
        setPlayPauseImage(false);
    }


    private void updateControlView() {
        Log.d(LOG_TAG, "udpateControlView()");
        mHandler = new Handler();
        overallDuration.setText(getFormattedProgressString(videoView.getDuration()));
        mHandler.postDelayed(updateVideoProgressTask, 200);
        mHandler.postDelayed(updateProgressBarTask, 200);
    }

    private long minutes;
    private String minutesString;
    private long seconds;
    private String secondsString;

    private String getFormattedProgressString(long position) {
        minutes = TimeUnit.MILLISECONDS.toMinutes(position);
        seconds = TimeUnit.MILLISECONDS.toSeconds(position);
        if (minutes < 10) minutesString = "0" + minutes;
        secondsString = String.valueOf(seconds - (minutes * 60));
        if (Integer.parseInt(secondsString) < 10) secondsString = "0" + secondsString;
        return minutesString + ":" + secondsString;
    }


    private Runnable updateVideoProgressTask = new Runnable() {
        @Override
        public void run() {
            progressTime.setText(getFormattedProgressString(videoView.getCurrentPosition()));
            if (videoView.getCurrentPosition() != 0) {
                seekbar.setProgress((int) (videoView.getCurrentPosition() * 100 / videoView.getDuration()));
                soundSwitcherContainer.bringToFront();
                soundSwitcherContainer.requestFocusFromTouch();
                overallDuration.setText(getFormattedProgressString(videoView.getDuration()));
                if (videoView.getCurrentPosition() == videoView.getDuration()) {
                    completeVideoView();
                }
            }
            if (!onActivityStop) mHandler.postDelayed(this, 200);
            if (onActivityStop) {
                mHandler.removeCallbacks(this);
                pauseVideoView();
            }
        }
    };

    long previousProgress = 0L;
    boolean checkingForPauseStarted = false;
    private Runnable updateProgressBarTask = new Runnable() {
        @Override
        public void run() {
            if (!checkingForPauseStarted) {
                previousProgress = videoView.getCurrentPosition();
                if (videoView.getCurrentPosition() == previousProgress) {
                    checkingForPauseStarted = true;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if ((previousProgress == videoView.getCurrentPosition()
                                    && videoView.isPlaying() || !isPrepared) && !isCompleted) {
                                rootView.findViewById(R.id.video_progress_bar).setVisibility(View.VISIBLE);
                            } else {
                                rootView.findViewById(R.id.video_progress_bar).setVisibility(GONE);
                            }
                            checkingForPauseStarted = false;
                        }
                    }, 200);
                } else rootView.findViewById(R.id.video_progress_bar).setVisibility(GONE);
            }
            if (!onActivityStop) mHandler.postDelayed(this, 200);
            if (onActivityStop) {
                mHandler.removeCallbacks(this);
            }
        }
    };


}
