package com.koresuniku.wishmaster.ui.controller

import android.util.Log
import com.koresuniku.wishmaster.R
import com.koresuniku.wishmaster.ui.activity.ThreadsActivity
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection

class SwipyRefreshLayoutUnit(val mActivity: ThreadsActivity) {
    var mRefreshLayout: SwipyRefreshLayout? = null

    init {
        initSwipyRefreshLayout()
    }

    fun initSwipyRefreshLayout() {
        mRefreshLayout = mActivity.findViewById(R.id.threads_refresh_layout) as SwipyRefreshLayout
        mRefreshLayout!!.direction = SwipyRefreshLayoutDirection.BOTH
        mRefreshLayout!!.setDistanceToTriggerSync(75)
        mRefreshLayout!!.isEnabled = true

        mRefreshLayout!!.setOnRefreshListener(SwipyRefreshLayout.OnRefreshListener {
            mActivity.mDataLoader.loadData(mActivity.boardId)
        })
    }

    fun disableRefreshLayout() {
        mRefreshLayout!!.isEnabled = false
    }

    fun enableTop() {
        mRefreshLayout!!.direction = SwipyRefreshLayoutDirection.TOP
        mRefreshLayout!!.isEnabled = true
    }

    fun enableBottom() {
        mRefreshLayout!!.direction = SwipyRefreshLayoutDirection.BOTTOM
        mRefreshLayout!!.isEnabled = true
    }

    fun setRefreshing(refreshing: Boolean) {
        mRefreshLayout!!.isRefreshing = refreshing
    }

    fun getRefreshing(): Boolean {
        return mRefreshLayout!!.isRefreshing
    }
}