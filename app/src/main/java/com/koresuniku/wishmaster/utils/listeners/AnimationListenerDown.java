package com.koresuniku.wishmaster.utils.listeners;

import android.view.View;
import android.view.animation.Animation;

public class AnimationListenerDown implements Animation.AnimationListener {
    private View mView;

    public AnimationListenerDown(View view) {
        this.mView = view;
    }

    @Override
    public void onAnimationStart(Animation animation) {
        this.mView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        this.mView.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
