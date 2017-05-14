package com.koresuniku.wishmaster.utils.listeners;

import android.view.View;
import android.view.animation.Animation;

public class AnimationListenerUp implements Animation.AnimationListener {
    private View mView;

    public AnimationListenerUp(View view) {
        this.mView = view;
    }

    @Override
    public void onAnimationStart(Animation animation) {
        this.mView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
