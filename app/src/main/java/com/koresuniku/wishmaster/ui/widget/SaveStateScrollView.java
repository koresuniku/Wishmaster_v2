package com.koresuniku.wishmaster.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.ScrollView;

public class SaveStateScrollView extends ScrollView {
    private final String TAG = SaveStateScrollView.class.getSimpleName();

    public int scrollY = 0;
    private int desiredScrollX = -1;
    private int desiredScrollY = -1;
    private ViewTreeObserver.OnGlobalLayoutListener gol;

    public SaveStateScrollView(Context context) {
        super(context);
    }

    public SaveStateScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SaveStateScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        //Log.d(TAG, "onOverScrolled: " + scrollY);
        this.scrollY = scrollY;
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    public void scrollToSavedState() {
        this.scrollTo(0, this.scrollY);
    }

    public void scrollToWithGuarantees(int __x, int __y) {
        // REALLY Scrolls to a position
        // When adding items to a scrollView, you can't immediately scroll to it - it takes a while
        // for the new addition to cycle back and update the scrollView's max scroll... so we have
        // to wait and re-set as necessary

        Log.d(TAG, "scrollToWithGuarantees: ");

        scrollTo(__x, __y);

        desiredScrollX = -1;
        desiredScrollY = -1;

        if (getScrollX() != __x || getScrollY() != __y) {
            // Didn't scroll properly: will create an event to try scrolling again later

            if (getScrollX() != __x) desiredScrollX = __x;
            if (getScrollY() != __y) desiredScrollY = __y;

            if (gol == null) {
                gol = new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        int nx = desiredScrollX == -1 ? getScrollX() : desiredScrollX;
                        int ny = desiredScrollY == -1 ? getScrollY() : desiredScrollY;
                        desiredScrollX = -1;
                        desiredScrollY = -1;
                        scrollTo(nx, ny);
                    }
                };

                getViewTreeObserver().addOnGlobalLayoutListener(gol);
            }
        }
    }
}
