package com.koresuniku.wishmaster.ui.views;

import android.content.Context;
import android.util.AttributeSet;

import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;

public class CustomSwipeRefreshLayout extends SwipyRefreshLayout {
    public CustomSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
    }


}
