package com.koresuniku.wishmaster.ui.controller

import android.content.res.Configuration
import android.os.Handler
import android.support.design.widget.AppBarLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.AbsListView
import com.bumptech.glide.Glide
import com.koresuniku.wishmaster.R
import com.koresuniku.wishmaster.ui.activity.ThreadsActivity
import com.koresuniku.wishmaster.ui.adapter.ThreadsRecyclerViewAdapter
import com.koresuniku.wishmaster.ui.widget.FixedRecyclerView
import com.koresuniku.wishmaster.ui.widget.ThreadsRecyclerViewDividerItemDecoration
import org.jetbrains.anko.sdk25.coroutines.onTouch

class RecyclerViewUnit(val mActivity: ThreadsActivity, val mAppBarLayout: AppBarLayout) {
    val LOG_TAG: String = RecyclerViewUnit::class.java.simpleName!!

    var mRecyclerView: FixedRecyclerView = mActivity.findViewById(R.id.threads_recycler_view) as FixedRecyclerView
    var mLinearLayoutManager: LinearLayoutManager? = null
    var mRecyclerViewAdapter: ThreadsRecyclerViewAdapter = ThreadsRecyclerViewAdapter(mActivity, mActivity.boardId)

    var mFastScrollSeekBarUnit: FastScrollSeekBarUnit? = null

    var appBarVerticalOffset: Int = 0
    var appBarLayoutExpandedValue: Int = mAppBarLayout.totalScrollRange

    init {
        initAppBarLayout()
        initRecyclerView()

    }
    
    fun initAppBarLayout() {
        mAppBarLayout.addOnOffsetChangedListener(
                { appBarLayout, verticalOffset -> appBarVerticalOffset = verticalOffset })
    }

    fun initFastScrollSeekBar() {
        mFastScrollSeekBarUnit = FastScrollSeekBarUnit(mActivity)
    }

    fun initRecyclerView() {
        mRecyclerView.isDrawingCacheEnabled = false
        mRecyclerView.addItemDecoration(ThreadsRecyclerViewDividerItemDecoration(mActivity))
        mLinearLayoutManager = LinearLayoutManager(mActivity)
        mRecyclerView.layoutManager = mLinearLayoutManager
        mRecyclerViewAdapter.setHasStableIds(true)

        mRecyclerView.adapter = mRecyclerViewAdapter
        mRecyclerView.setOnScrollListener(OnScrollListener())
        mRecyclerView.onTouch { v, event -> kotlin.run {
            checkRefreshAvailability()
            //Log.d(LOG_TAG, "y: " + event.y)
            }
        }

        initFastScrollSeekBar()

        mActivity.fixCoordinatorLayout(mActivity.resources.configuration)
    }

    val touchedAgain = booleanArrayOf(false)
    inner class OnScrollListener : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            if (mLinearLayoutManager!!.findLastCompletelyVisibleItemPosition() == mRecyclerViewAdapter.itemCount - 1) {
                mFastScrollSeekBarUnit!!.mFastScrollSeekBar.progress = mLinearLayoutManager!!.findLastCompletelyVisibleItemPosition()
            } else {
                mFastScrollSeekBarUnit!!.mFastScrollSeekBar.progress = mLinearLayoutManager!!.findFirstVisibleItemPosition()
            }
            mFastScrollSeekBarUnit!!.mFastScrollSeekBar.updateThumb()
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                Glide.with(mActivity).pauseRequests()
            } else {
                Glide.with(mActivity).resumeRequests()
            }
            if (!mFastScrollSeekBarUnit!!.fastScrollSeekBarTouchedFromUser) {
                if (newState != 0) {
                    touchedAgain[0] = true
                    if (mFastScrollSeekBarUnit!!.mFastScrollSeekBarContainer!!.visibility == View.GONE) {
                        mFastScrollSeekBarUnit!!.mFastScrollSeekBarContainer!!.visibility = View.VISIBLE
                        mFastScrollSeekBarUnit!!.mFastScrollSeekBarContainer!!.startAnimation(mFastScrollSeekBarUnit!!.fadeIn)
                    }
                } else {
                    touchedAgain[0] = false
                    if ( mFastScrollSeekBarUnit!!.mFastScrollSeekBarContainer!!.visibility == View.VISIBLE) {
                        Handler().postDelayed({
                            if (!mFastScrollSeekBarUnit!!.fastScrollSeekBarTouchedFromUser && !touchedAgain[0]) {
                                mFastScrollSeekBarUnit!!.mFastScrollSeekBarContainer!!.startAnimation(mFastScrollSeekBarUnit!!.fadeOut)
                                mFastScrollSeekBarUnit!!.mFastScrollSeekBarContainer!!.visibility = View.GONE
                            }
                        }, 750)
                    }
                }
            }
        }
    }

    fun checkRefreshAvailability() {
        val readyToRefreshTop: Boolean = !mRecyclerView!!.canScrollVertically(-1)
                && appBarVerticalOffset == 0
        val readyToRefreshBottom: Boolean = !mRecyclerView!!.canScrollVertically(1)
                && Math.abs(appBarVerticalOffset) == appBarLayoutExpandedValue

        if (readyToRefreshTop) mActivity.mSwipyRefreshLayoutUnit.enableTop()
        if (readyToRefreshBottom) mActivity.mSwipyRefreshLayoutUnit.enableBottom()

        if (!readyToRefreshTop && !readyToRefreshBottom) mActivity.mSwipyRefreshLayoutUnit.disableRefreshLayout()
    }

    fun threadsActivityOnDataLoaded() {
        mRecyclerViewAdapter!!.notifyDataSetChanged()
        mAppBarLayout.setExpanded(true)
        mRecyclerView!!.scrollToPosition(0)
        mActivity.mSwipyRefreshLayoutUnit.setRefreshing(false)
    }

    fun onConfigurationChanged(configuration: Configuration) {
        mFastScrollSeekBarUnit!!.onConfigurationChanged(configuration)
        appBarLayoutExpandedValue = mAppBarLayout.totalScrollRange
    }

}