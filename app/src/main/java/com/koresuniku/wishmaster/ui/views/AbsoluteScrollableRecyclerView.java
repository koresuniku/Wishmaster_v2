package com.koresuniku.wishmaster.ui.views;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

public class AbsoluteScrollableRecyclerView extends RecyclerView {
    public AbsoluteScrollableRecyclerView(Context context) {
        super(context);
    }

    public AbsoluteScrollableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public AbsoluteScrollableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void scrollTo(int x, int y) {

    }
}
