package com.koresuniku.wishmaster.ui.controller;

import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.IntRange;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.devbrackets.android.exomedia.core.listener.MetadataListener;
import com.devbrackets.android.exomedia.listener.OnBufferUpdateListener;
import com.devbrackets.android.exomedia.listener.OnCompletionListener;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
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
import com.koresuniku.wishmaster.ui.UiUtils;
import com.koresuniku.wishmaster.ui.fragment.GalleryFragment;
import com.koresuniku.wishmaster.ui.listener.AnimationListenerDown;
import com.koresuniku.wishmaster.ui.listener.AnimationListenerUp;
import com.koresuniku.wishmaster.util.Constants;
import com.koresuniku.wishmaster.util.DeviceUtils;

import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;

public class VideoViewUnit implements OnPreparedListener{
    private final String LOG_TAG = VideoViewUnit.class.getSimpleName();

    public com.devbrackets.android.exomedia.ui.widget.VideoView videoView;
    private GalleryFragment mFragment;

    private boolean isPrepared;
    private boolean isCompleted = false;
    private long seekToMillis;
    private int volume = 1;

    public AnimationListenerUp animationListenerUpControls;
    public AnimationListenerDown animationListenerDownControls;
    public ScaleAnimation animCollapseControls;
    public ScaleAnimation animExpandControls;

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

    public VideoViewUnit(GalleryFragment fragment) {
        this.mFragment = fragment;
    }

    public void onCreateView(String path) {
        createVideoView(path);
        createControlView(true);
        setupVideoAnimations();
    }

    public void setupVideoAnimations() {
        animationListenerDownControls = new AnimationListenerDown(controlViewContainer);
        animationListenerUpControls = new AnimationListenerUp(controlViewContainer);

        animExpandControls = new ScaleAnimation(1, 1, 0, 1,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        animExpandControls.setDuration(250);
        animCollapseControls = new ScaleAnimation(1, 1, 1, 0,
                Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f);
        animCollapseControls.setDuration(250);

        animExpandControls.setAnimationListener(animationListenerUpControls);
        animCollapseControls.setAnimationListener(animationListenerDownControls);

    }

    public void createVideoView(String path) {
        mFragment.layoutContainer = (FrameLayout) mFragment.rootView.findViewById(R.id.full_video_layout_container);
        videoView = new VideoView(mFragment.mActivity);
        videoView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFragment.layoutContainer.addView(videoView);
        videoView.setControls(null);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        OkHttpDataSourceFactory dataSourceFactory =
                new OkHttpDataSourceFactory(HttpClient.INSTANCE.getClient(),
                        Util.getUserAgent(mFragment.mActivity, mFragment.mActivity.getString(R.string.app_name)),
                        (TransferListener<? super DataSource>) bandwidthMeter);
        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
        MediaSource videoSource = new ExtractorMediaSource(Uri.parse(Constants.DVACH_BASE_URL + path),
                dataSourceFactory, extractorsFactory, null, null);
        videoView.setVideoURI(Uri.parse(Constants.DVACH_BASE_URL + path), videoSource);
        Log.d(LOG_TAG, "path: " + Constants.DVACH_BASE_URL + path);
        videoView.setOnPreparedListener(this);
        videoView.setBackgroundColor(mFragment.mActivity.getResources().getColor(R.color.full_media_tint));
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
                if (mFragment.rootView.findViewById(R.id.video_progress_bar).getVisibility() == View.VISIBLE) {
                    mFragment.rootView.findViewById(R.id.video_progress_bar).setVisibility(GONE);
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

    public void createControlView(boolean firstTime) {
        if (mFragment.mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            controlViewContainer = (FrameLayout) mFragment.mActivity.getLayoutInflater()
                    .inflate(R.layout.webm_video_controls_redesign, null, false);
        } else  controlViewContainer = (FrameLayout) mFragment.mActivity.getLayoutInflater()
                .inflate(R.layout.webm_video_controls, null, false);


        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        controlViewContainer.setLayoutParams(params);
        mFragment.layoutContainer.addView(controlViewContainer);

        controlView = controlViewContainer.findViewById(R.id.control_view);
        if (DeviceUtils.deviceHasNavigationBar(mFragment.mActivity) && Build.VERSION.SDK_INT >= 19) {
            if (mFragment.mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                Log.d(LOG_TAG, "controlView is null: " + (controlView == null));
                controlViewContainer.setPadding(0, 0, 0, Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? 96 : 48);
            } else {
                if (DeviceUtils.deviceHasNavigationBar(mFragment.mActivity)) {
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

    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(LOG_TAG, "onConfigurationChanged: ");
        if (controlViewContainer != null) {
            mFragment.layoutContainer.removeView(controlViewContainer);
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

    private View.OnClickListener videoOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            controlViewContainer.startAnimation(UiUtils.barsAreShown ? animCollapseControls : animExpandControls);
            mFragment.changeUiVisibility();
        }
    };

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

    private View.OnClickListener switchSoundOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AudioManager audio = (AudioManager) mFragment.mActivity.getSystemService(Context.AUDIO_SERVICE);
            if (audio.getStreamVolume(AudioManager.STREAM_MUSIC) != 0) {
                Log.d(LOG_TAG, "stream volume != 0");
                volume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                App.mSettingsContentObserver.onChange(true);
                soundSwitcher.setImageResource(R.drawable.ic_volume_off_black_24dp);
            } else {
                Log.d(LOG_TAG, "stream volume is 0");
                audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);

                App.mSettingsContentObserver.onChange(true);
                Log.d(LOG_TAG, "stream volume after " + audio.getStreamVolume(AudioManager.STREAM_MUSIC));
                soundSwitcher.setImageResource(R.drawable.ic_volume_up_black_24dp);
            }
        }
    };

    public void switchSound(int volume) {
        Log.d(LOG_TAG, "switching sound:");
        if (videoView == null) return;

        AudioManager audio = (AudioManager) mFragment.mActivity.getSystemService(Context.AUDIO_SERVICE);

        Log.d(LOG_TAG, "setting volume " + volume);
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        if (volume == 0) {
            soundSwitcher.setImageResource(R.drawable.ic_volume_off_black_24dp);
        } else {
            soundSwitcher.setImageResource(R.drawable.ic_volume_up_black_24dp);
        }
    }

    public void restartVideoView(long seekToMillis, int progress) {
        videoView.restart();
        videoView.seekTo(seekToMillis);
        seekbar.setProgress(progress);
        isCompleted = false;
    }

    public void startVideoView() {
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

    public void showOrHideControlView() {
        Log.d(LOG_TAG, "showOrHideControlView: ");
        if (controlViewContainer != null) {
            if (UiUtils.barsAreShown) controlViewContainer.setVisibility(GONE);
            else controlViewContainer.setVisibility(View.VISIBLE);
        }
    }

    public void pauseVideoView() {
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
                mFragment.rootView.findViewById(R.id.video_progress_bar).setVisibility(View.VISIBLE);
                restartVideoView(0L, 0);
            }
        });
    }

    @Override
    public void onPrepared() {
        Log.d(LOG_TAG, "video prepared");
        isPrepared = true;
        mFragment.rootView.findViewById(R.id.video_progress_bar).setVisibility(GONE);

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
        if (mFragment.mPlayVideo) {
            startVideoView();
        }
        setPlayPauseImage(false);
    }

    public void updateControlView() {
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
            if (!mFragment.onActivityStop) mHandler.postDelayed(this, 200);
            if (mFragment.onActivityStop) {
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
                                mFragment.rootView.findViewById(R.id.video_progress_bar).setVisibility(View.VISIBLE);
                            } else {
                                mFragment.rootView.findViewById(R.id.video_progress_bar).setVisibility(GONE);
                            }
                            checkingForPauseStarted = false;
                        }
                    }, 200);
                } else mFragment.rootView.findViewById(R.id.video_progress_bar).setVisibility(GONE);
            }
            if (!mFragment.onActivityStop) mHandler.postDelayed(this, 200);
            if (mFragment.onActivityStop) {
                mHandler.removeCallbacks(this);
            }
        }
    };


}

