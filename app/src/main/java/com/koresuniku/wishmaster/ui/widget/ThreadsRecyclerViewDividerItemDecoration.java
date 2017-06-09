package com.koresuniku.wishmaster.ui.widget;


import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.koresuniku.wishmaster.R;

public class ThreadsRecyclerViewDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public ThreadsRecyclerViewDividerItemDecoration(Activity activity) {
            mDivider = activity.getResources().getDrawable(R.drawable.recycler_view_divider);
//            mDivider.setColorFilter(activity.getResources().getColor(
//                    android.R.color.background_light), PorterDuff.Mode.SRC_ATOP);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft() + 24;
            int right = parent.getWidth() - parent.getPaddingRight() - 24;

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);

                mDivider.draw(c);
            }
        }
}
