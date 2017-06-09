package com.koresuniku.wishmaster.ui.controller

import android.content.res.Configuration
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.SeekBar
import com.koresuniku.wishmaster.R
import com.koresuniku.wishmaster.ui.ScrollbarUtils
import com.koresuniku.wishmaster.ui.activity.ThreadsActivity
import com.koresuniku.wishmaster.ui.widget.VerticalSeekBar
import org.jetbrains.anko.find

class FastScrollSeekBarUnit(val mActivity: ThreadsActivity) {
    var fastScrollSeekBarTouchedFromUser: Boolean = false

    val mFastScrollSeekBar: VerticalSeekBar = mActivity.findViewById(R.id.scroll_seekBar) as VerticalSeekBar
    val mFastScrollSeekBarContainer: View = mActivity.findViewById(R.id.fast_scroll_seekbar_container)

    var fadeOut: Animation? = null
    var fadeIn: Animation? = null

    init {
        initAnimations()
        initFastScrollSeekBar()
    }

    fun initAnimations() {
        fadeOut = AlphaAnimation(1f, 0f)
        fadeOut!!.interpolator = AccelerateInterpolator()
        fadeOut!!.duration = 200
        fadeIn = AlphaAnimation(0f, 1f)
        fadeIn!!.interpolator = DecelerateInterpolator()
        fadeIn!!.duration = 200
    }

    fun initFastScrollSeekBar() {

        ScrollbarUtils.setScrollbarSize(mActivity, mFastScrollSeekBarContainer as FrameLayout,
                mActivity.resources.configuration)
        mFastScrollSeekBar.max = mActivity.itemCount - 1
        mFastScrollSeekBarContainer.visibility = View.GONE
        mFastScrollSeekBar.setOnTouchListener(View.OnTouchListener { v, event ->
            fastScrollSeekBarTouchedFromUser = true
            mFastScrollSeekBarContainer.clearAnimation()
            if (event.action == MotionEvent.ACTION_UP) {
                Handler().postDelayed({
                    mFastScrollSeekBarContainer.startAnimation(fadeOut)
                    mFastScrollSeekBarContainer.visibility = View.GONE
                }, 750)
            }
            false
        })
        mFastScrollSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                if (fastScrollSeekBarTouchedFromUser) {
                    if (progress == mActivity.mRecyclerViewUnit.mRecyclerViewAdapter.itemCount)
                        mActivity.mRecyclerViewUnit.mLinearLayoutManager!!.scrollToPositionWithOffset(progress, Integer.MAX_VALUE)
                    else
                        mActivity.mRecyclerViewUnit.mLinearLayoutManager!!.scrollToPositionWithOffset(progress, 0)
                    mFastScrollSeekBar.updateThumb()
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
    }

    fun onConfigurationChanged(configuration: Configuration) {
        ScrollbarUtils.setScrollbarSize(mActivity, mFastScrollSeekBarContainer as FrameLayout,
                configuration)
    }
}