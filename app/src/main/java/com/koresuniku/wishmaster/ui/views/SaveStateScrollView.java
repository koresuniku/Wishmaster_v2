package com.koresuniku.wishmaster.ui.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

public class SaveStateScrollView extends ScrollView {
    private final String TAG = SaveStateScrollView.class.getSimpleName();

    public int scrollY = 0;

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
        Log.d(TAG, "onOverScrolled: " + scrollY);
        this.scrollY = scrollY;
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    public void scrollToSavedState() {
        this.scrollTo(0, this.scrollY);
    }
}
