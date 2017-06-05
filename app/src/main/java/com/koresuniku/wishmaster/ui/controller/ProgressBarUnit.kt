package com.koresuniku.wishmaster.ui.controller

import android.content.Context
import android.content.res.Configuration
import android.support.v4.view.GravityCompat
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.koresuniku.wishmaster.R
import com.koresuniku.wishmaster.util.DeviceUtils

class ProgressBarUnit(val context: Context, val mProgressBar: ProgressBar) {
    val LOG_TAG: String = "ProgressBarUnit"

    init {
        onCreated(context.resources.configuration)
    }

    fun onCreated(configuration: Configuration) {
        Log.d(LOG_TAG, "onOrientationChanged:")
        if (DeviceUtils.sdkIsKitkatOrHigher()) return
//        val params: FrameLayout.LayoutParams = FrameLayout.LayoutParams(
//                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
//        params.gravity = Gravity.CENTER
//        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            params.setMargins(0, mProgressBar.context.resources.getDimension(R.dimen.action_bar_height_horizontal).toInt() +
//                    mProgressBar.context.resources.getDimension(R.dimen.status_bar_height).toInt(),
//                    0, 0)
//            mProgressBar.layoutParams = params
//        }
//        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            var bottomMargin: Int = 0
//            if (DeviceUtils.deviceHasNavigationBar(context)) {
//                bottomMargin = context.resources.getDimension(R.dimen.navigation_bar_height).toInt()
//            }
//            params.setMargins(0, mProgressBar.context.resources.getDimension(R.dimen.action_bar_height_vertical).toInt(),
//                    0, bottomMargin)
//            mProgressBar.layoutParams = params
//        }
    }

    fun hideProgressBar() {
        mProgressBar.visibility = View.GONE
    }
}