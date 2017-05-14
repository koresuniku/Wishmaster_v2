package com.koresuniku.wishmaster.asynktasks;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.devbrackets.android.exomedia.ui.widget.VideoView;

public class VideoDurationUpdater extends AsyncTask<Long, Integer, Void> {
    private final String LOG_TAG = VideoDurationUpdater.class.getSimpleName();
    private Activity mActivity;
    private VideoView mVideoView;
    private ProgressBar mSeekbar;
    private boolean stopVideo;

    public VideoDurationUpdater(Activity activity, VideoView videoView, SeekBar seekbar) {
        this.mActivity = activity;
        this.mVideoView = videoView;
        this.mSeekbar = seekbar;
        this.stopVideo = false;
    }
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Void doInBackground(Long... params) {
        Log.d(LOG_TAG, "duration: " + params[0]);
        final long[] current = {0};

        do {
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    current[0] = mVideoView.getCurrentPosition();
                }
            });
            //Log.d(LOG_TAG, "current: " + current);


        } while (!stopVideo);
        return null;
    }
}
