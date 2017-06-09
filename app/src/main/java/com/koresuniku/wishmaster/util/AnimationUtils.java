package com.koresuniku.wishmaster.util;

import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ImageView;

import com.koresuniku.wishmaster.R;

import java.lang.reflect.Method;

public class AnimationUtils {
    public static final String LOG_TAG = AnimationUtils.class.getSimpleName();

    public static final long THUMBNAIL_ANIMATION_DURATION = 100;

    public static Animation expand(final View v, final boolean expand) {
        try {
            Method m = v.getClass().getDeclaredMethod("onMeasure", int.class, int.class);
            m.setAccessible(true);
            m.invoke(
                    v,
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(((View)v.getParent()).getMeasuredWidth(), View.MeasureSpec.AT_MOST)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }

        final int initialHeight = v.getMeasuredHeight();

        if (expand) {
            v.getLayoutParams().height = 0;
        }
        else {
            v.getLayoutParams().height = initialHeight;
        }
        v.setVisibility(View.VISIBLE);

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                int newHeight = 0;
                if (expand) {
                    newHeight = (int) (initialHeight * interpolatedTime);
                } else {
                    newHeight = (int) (initialHeight * (1 - interpolatedTime));
                }
                v.getLayoutParams().height = newHeight;
                v.requestLayout();

                if (interpolatedTime == 1 && !expand)
                    v.setVisibility(View.GONE);
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setDuration(250);
        return a;
    }



    public static Animation resizeThumbnail(final ImageView v, final Bitmap resource,
                                            final int initialHeight, int finalHeight) {

        final boolean up = initialHeight > finalHeight;
        final int difference = Math.abs(initialHeight - finalHeight);
        final float maxHeight = v.getContext().getResources().getDimension(R.dimen.thumbnail_max_height);

        Animation a = new Animation() {

            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                int heightToAdd = (int) (difference * interpolatedTime);
                if (up) {
                    v.getLayoutParams().height = initialHeight - heightToAdd;
                } else {
                    if (initialHeight + heightToAdd <= maxHeight) {
                        v.getLayoutParams().height = initialHeight + heightToAdd;
                    }
                }
                v.requestLayout();

            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                v.requestLayout();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        a.setDuration(THUMBNAIL_ANIMATION_DURATION);
        return a;
    }
}
