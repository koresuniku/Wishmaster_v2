package com.koresuniku.wishmaster.ui.views;

import android.content.Context;
import android.support.annotation.IntRange;
import android.util.AttributeSet;

import com.devbrackets.android.exomedia.ui.widget.*;
import com.koresuniku.wishmaster.R;

public class WebmVideoControls extends com.devbrackets.android.exomedia.ui.widget.VideoControls {
    @Override
    public void setPosition(@IntRange(from = 0) long position) {

    }

    @Override
    public void setDuration(@IntRange(from = 0) long duration) {

    }

    @Override
    public void updateProgress(@IntRange(from = 0) long position, @IntRange(from = 0) long duration, @IntRange(from = 0, to = 100) int bufferPercent) {

    }

    @Override
    protected int getLayoutResource() {
        //return R.layout.webm_video_controls_2;
        return 0;
    }

    @Override
    protected void animateVisibility(boolean toVisible) {

    }

    @Override
    protected void updateTextContainerVisibility() {

    }

    @Override
    public void showLoading(boolean initialLoad) {

    }

    @Override
    public void finishLoading() {

    }

    public WebmVideoControls(Context context) {
        super(context);
    }

    public WebmVideoControls(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebmVideoControls(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WebmVideoControls(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
