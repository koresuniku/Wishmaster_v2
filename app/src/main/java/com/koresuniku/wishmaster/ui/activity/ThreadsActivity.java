package com.koresuniku.wishmaster.ui.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.koresuniku.wishmaster.App;
import com.koresuniku.wishmaster.presenter.DataLoader;
import com.koresuniku.wishmaster.presenter.FileSaver;
import com.koresuniku.wishmaster.presenter.PermissionManager;
import com.koresuniku.wishmaster.presenter.view_interface.LoadDataView;
import com.koresuniku.wishmaster.presenter.view_interface.SaveFileView;
import com.koresuniku.wishmaster.ui.ActionBarUtils;
import com.koresuniku.wishmaster.ui.adapter.PicVidPagerAdapter;
import com.koresuniku.wishmaster.R;
import com.koresuniku.wishmaster.ui.adapter.ThreadsListViewAdapter;
import com.koresuniku.wishmaster.ui.controller.ProgressBarUnit;
import com.koresuniku.wishmaster.ui.controller.RecyclerViewUnit;
import com.koresuniku.wishmaster.ui.controller.SwipyRefreshLayoutUnit;
import com.koresuniku.wishmaster.ui.fragment.GalleryFragment;
import com.koresuniku.wishmaster.http.threads_api.models.Files;
import com.koresuniku.wishmaster.ui.ScrollbarUtils;
import com.koresuniku.wishmaster.ui.widget.FixedRecyclerView;
import com.koresuniku.wishmaster.ui.widget.HackyViewPager;
import com.koresuniku.wishmaster.ui.widget.ThreadsRecyclerViewDividerItemDecoration;
import com.koresuniku.wishmaster.ui.widget.VerticalSeekBar;
import com.koresuniku.wishmaster.util.CacheUtils;
import com.koresuniku.wishmaster.util.DeviceUtils;
import com.koresuniku.wishmaster.ui.adapter.ThreadsRecyclerViewAdapter;
import com.koresuniku.wishmaster.http.threads_api.models.ThreadsJsonSchema;
import com.koresuniku.wishmaster.util.Constants;
import com.koresuniku.wishmaster.ui.UiUtils;
import com.koresuniku.wishmaster.util.IOUtils;
import com.koresuniku.wishmaster.ui.listener.AnimationListenerDown;
import com.koresuniku.wishmaster.ui.listener.AnimationListenerUp;
import com.koresuniku.wishmaster.ui.listener.ThreadsViewPagerOnPageChangeListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThreadsActivity extends AppCompatActivity implements LoadDataView, SaveFileView {
    private final String LOG_TAG = ThreadsActivity.class.getSimpleName();

    private ThreadsActivity mActivity;

    public String boardId;
    public String boardName;

    public SharedPreferences sharedPreferences;

    public AppBarLayout appBarLayout;
    public int appBarVerticalOffSet;
    public FrameLayout fakeStatusBar;
    public Toolbar toolbar;
    public ScaleAnimation animCollapseActionBar;
    public ScaleAnimation animExpandActionBar;
    public Animation fadeOut;
    public Animation fadeIn;
    public AnimationListenerUp animationListenerUpActionBar;
    public AnimationListenerDown animationListenerDownActionBar;

    public Toolbar picVidToolbar;
    public Menu picVidBarMenu;
    public LinearLayout picVidToolbarContainer;
    public ImageView picVidToolbarExitImageView;
    public TextView picVidToolbarTitleTextView;
    public TextView picVidToolbarShortInfoTextView;
    public MenuItem picVidToolbarMenuItem;
    public String picVidToolbarFilename;
    public String picVidToolbarUrl;

    //public SwipyRefreshLayout threadsRefreshLayoutTop;
    //public SwipyRefreshLayout threadsRefreshLayoutBottom;
    public FixedRecyclerView threadsRecyclerView;
    public VerticalSeekBar mFastScrollSeekbar;
    public ListView mListView;
    public ThreadsListViewAdapter mListViewAdapter;
    private boolean fastScrollSeekbarTouchedFromUser;

    private Parcelable threadsRecyclerViewState;
    public ThreadsRecyclerViewAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    public static List<Files> files;
    public static List<String> thumbnails;
    public static List<String> imageCachePaths;
    public static Map<Integer, GalleryFragment> galleryFragments;
    public ImageLoader imageLoader;

    public RecyclerViewUnit mRecyclerViewUnit;
    public SwipyRefreshLayoutUnit mSwipyRefreshLayoutUnit;

    public FrameLayout fullPicVidContainer;
    public HackyViewPager picVidPager;
    public PicVidPagerAdapter picVidPagerAdapter;
    public ThreadsViewPagerOnPageChangeListener threadsViewPagerOnPageChangeListener;

    public DataLoader mDataLoader;

    public boolean dataLoaded = false;
    public boolean fullPicVidOpened = false;
    public boolean fullPicVidOpenedAndFullScreenModeIsOn = false;
    public int picVidOpenedPosition = -1;

    public ThreadsJsonSchema mSchema;
    private ProgressBarUnit mProgressBarUnit;
    public FileSaver mFileSaver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(LOG_TAG, "onCreate:");

        mActivity = this;

        Intent receivedIntent = getIntent();
        boardId = receivedIntent.getStringExtra(Constants.BOARD_ID);
        boardName = receivedIntent.getStringExtra(Constants.BOARD_NAME);

        setContentView(R.layout.activity_threads);

        initSharedPrefs();
        setupAppBarLayout();
        setupActionBar();
        setupAnimations();
        setupPicVidToolbar();
        //setupSwipeRefreshLayout();
        mSwipyRefreshLayoutUnit = new SwipyRefreshLayoutUnit(this);
        setupFullscreenMode();
        setupOrientationFeatures();

        mDataLoader = new DataLoader(this);
        picVidPager = (HackyViewPager) findViewById(R.id.threads_full_pic_vid_pager);
        mProgressBarUnit = new ProgressBarUnit(this, (ProgressBar) findViewById(R.id.main_progress_bar));
        mFileSaver = new FileSaver(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        App.mSettingsContentObserver.switchActivity(this);
        if (!dataLoaded) {
            mDataLoader.loadData(boardId);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setupOrientationFeatures() {
        if (DeviceUtils.sdkIsKitkatOrHigher()) {
            if (this.getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                UiUtils.showSystemUIExceptNavigationBar(this);
            } else UiUtils.showSystemUI(this);
        }
    }

    private void initSharedPrefs() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(LOG_TAG, "onConfigurationChanged:");
        UiUtils.setupToolbarForNavigationBar(this, picVidToolbar);
        if (DeviceUtils.sdkIsKitkatOrHigher()) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                UiUtils.showSystemUIExceptNavigationBar(this);
            } else UiUtils.showSystemUI(this);
            if (fullPicVidOpenedAndFullScreenModeIsOn) {
                UiUtils.hideSystemUI(mActivity);
                UiUtils.barsAreShown = false;
            }
        }

        setupActionBar();

        if (mRecyclerViewUnit != null) mRecyclerViewUnit.onConfigurationChanged(newConfig);

        fixCoordinatorLayout(newConfig);

        if (adapter != null) adapter.notifyDataSetChanged();
    }

    public int getItemCount() {
        if (mSchema != null) return mSchema.getThreads().size();
        return 0;
    }

    private void setupAppBarLayout() {
        appBarLayout = (AppBarLayout) findViewById(R.id.threads_appbar);
        appBarVerticalOffSet = 0;
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //Log.d(LOG_TAG, "verticalOffset: "  + verticalOffset);
                //Log.d(LOG_TAG, "getTotalScrollRange: " + appBarLayout.getTotalScrollRange());
                if (!fullPicVidOpened) {
                    appBarVerticalOffSet = verticalOffset;
                    //fixRefreshLayoutOnOrientation();
                }
            }
        });

        toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
    }

    private void setupActionBar() {
        ((LinearLayout)appBarLayout.findViewById(R.id.toolbar_container)).removeView(toolbar);
        toolbar = (Toolbar) getLayoutInflater()
                .inflate(R.layout.activity_toolbar_layout, null, false)
                .findViewById(R.id.activity_toolbar);
        ((LinearLayout)appBarLayout.findViewById(R.id.toolbar_container)).addView(toolbar);
        toolbar.inflateMenu(R.menu.activity_threads_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ((TextView)toolbar.findViewById(R.id.title)).setText("/" + boardId + "/ - " + boardName);
        ((TextView)toolbar.findViewById(R.id.title)).setTextSize(
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?
                        ActionBarUtils.MEDIA_TOOLBAR_TEXT_SIZE_VERTICAL :
                        ActionBarUtils.MEDIA_TOOLBAR_TEXT_SIZE_HORIZONAL);
    }

    private void setupAnimations() {
        animationListenerUpActionBar = new AnimationListenerUp(appBarLayout);
        animationListenerDownActionBar = new AnimationListenerDown(appBarLayout);
        animExpandActionBar = new ScaleAnimation(1, 1, 0, 1, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        animExpandActionBar.setDuration(250);
        animCollapseActionBar = new ScaleAnimation(1, 1, 1, 0, Animation.RELATIVE_TO_SELF, 1f, Animation.RELATIVE_TO_SELF, 0f);
        animCollapseActionBar.setDuration(250);
        animExpandActionBar.setAnimationListener(animationListenerUpActionBar);
        animCollapseActionBar.setAnimationListener(animationListenerDownActionBar);
        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(200);
        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(200);
    }

    private void setupPicVidToolbar() {
        picVidToolbar = (Toolbar) findViewById(R.id.picvid_toolbar);
        picVidToolbar.inflateMenu(R.menu.picvid_toolbar_menu);
        UiUtils.tintMenuIcons(this, picVidToolbar.getMenu());
        fullPicVidContainer = (FrameLayout) findViewById(R.id.threads_full_picvid_container);
        fullPicVidContainer.setVisibility(View.GONE);
        UiUtils.setupToolbarForNavigationBar(this, picVidToolbar);
        picVidToolbarExitImageView = ((ImageView) picVidToolbar.findViewById(R.id.exit_icon));
        picVidToolbarExitImageView.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        picVidToolbarExitImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
        picVidToolbarContainer = (LinearLayout) findViewById(R.id.picvid_toolbar_container);
        picVidToolbarContainer.setBackgroundColor(getActivity().getResources().getColor(android.R.color.transparent));
        picVidToolbarTitleTextView = (TextView) findViewById(R.id.picvid_title);
        picVidToolbarShortInfoTextView = (TextView) findViewById(R.id.picvid_short_info);
        picVidToolbarMenuItem = picVidToolbar.getMenu().findItem(R.id.action_save);

    }


    private void setupListView() {
        mListView = (ListView) findViewById(R.id.activity_threads_listview);
        mListViewAdapter = new ThreadsListViewAdapter(mActivity);
        mListView.setAdapter(mListViewAdapter);
        mListView.setFriction(ViewConfiguration.getScrollFriction() * 2);
        mListView.setVerticalScrollBarEnabled(false);
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                fastScrollSeekbarTouchedFromUser = false;
                return false;
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, final int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mFastScrollSeekbar != null && !fastScrollSeekbarTouchedFromUser) {
                    Log.d(LOG_TAG, "firstVisibleItem: " + firstVisibleItem);
                    if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                        mFastScrollSeekbar.setProgress(totalItemCount);
                    } else {
                        mFastScrollSeekbar.setProgress(firstVisibleItem);
                    }
                    mFastScrollSeekbar.updateThumb();

                }
            }
        });
        setupFastScrollSeekBar();
    }

    private void setupFastScrollSeekBar() {
        mFastScrollSeekbar = (VerticalSeekBar) findViewById(R.id.scroll_seekBar);
        ScrollbarUtils.setScrollbarSize(mActivity,
                (FrameLayout) findViewById(R.id.fast_scroll_seekbar_container),
                getResources().getConfiguration());
        mFastScrollSeekbar.setMax(adapter.getItemCount() - 1);
        findViewById(R.id.fast_scroll_seekbar_container).setVisibility(View.GONE);
        mFastScrollSeekbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(LOG_TAG, "onTouchSeekbar: ");
                fastScrollSeekbarTouchedFromUser = true;
                findViewById(R.id.fast_scroll_seekbar_container).clearAnimation();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.fast_scroll_seekbar_container).startAnimation(fadeOut);
                            findViewById(R.id.fast_scroll_seekbar_container).setVisibility(View.GONE);
                        }
                    }, 750);
                }
                return false;
            }
        });
        mFastScrollSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(LOG_TAG, "onProgressChanged: progress: " + progress);
                Log.d(LOG_TAG, "adapter count: " + adapter.getItemCount());
                if (fastScrollSeekbarTouchedFromUser) {
                   if (progress == adapter.getItemCount())
                       linearLayoutManager.scrollToPositionWithOffset(progress, Integer.MAX_VALUE);
                   else linearLayoutManager.scrollToPositionWithOffset(progress, 0);
                    mFastScrollSeekbar.updateThumb();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setupThreadsRecyclerView() {
        threadsRecyclerView = (FixedRecyclerView) findViewById(R.id.threads_recycler_view);

        threadsRecyclerView.setDrawingCacheEnabled(false);
        threadsRecyclerView.addItemDecoration(new ThreadsRecyclerViewDividerItemDecoration(this));
        linearLayoutManager = new LinearLayoutManager(this);
        threadsRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ThreadsRecyclerViewAdapter(this, boardId);
        adapter.setHasStableIds(true);

        threadsRecyclerView.setAdapter(adapter);
        final boolean[] touchedAgain = {false};
        threadsRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    //imageLoader.pause();
                    if (getActivity() != null) Glide.with(mActivity).pauseRequests();
                } else {
                    //imageLoader.resume();
                    if (getActivity() != null) Glide.with(mActivity).resumeRequests();
                }
                if (!fastScrollSeekbarTouchedFromUser) {
                    if (newState != 0) {
                        touchedAgain[0] = true;
                        if (findViewById(R.id.fast_scroll_seekbar_container).getVisibility() == View.GONE) {
                            findViewById(R.id.fast_scroll_seekbar_container).setVisibility(View.VISIBLE);
                            findViewById(R.id.fast_scroll_seekbar_container).startAnimation(fadeIn);
                        }
                    } else {
                        touchedAgain[0] = false;
                        if (findViewById(R.id.fast_scroll_seekbar_container).getVisibility() == View.VISIBLE) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!fastScrollSeekbarTouchedFromUser && !touchedAgain[0]) {
                                        findViewById(R.id.fast_scroll_seekbar_container).startAnimation(fadeOut);
                                        findViewById(R.id.fast_scroll_seekbar_container).setVisibility(View.GONE);
                                    }
                                }
                            }, 750);
                        }
                    }
                }
            }
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (linearLayoutManager.findLastCompletelyVisibleItemPosition()
                        == adapter.getItemCount() - 1) {
                    mFastScrollSeekbar.setProgress(linearLayoutManager.findLastCompletelyVisibleItemPosition());
                } else {
                    mFastScrollSeekbar.setProgress(linearLayoutManager.findFirstVisibleItemPosition());
                }
                mFastScrollSeekbar.updateThumb();
            }
        });
        threadsRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                fastScrollSeekbarTouchedFromUser = false;

                return false;
            }
        });

        onRestoreInstanceState(null);
        fixCoordinatorLayout(null);

        setupFastScrollSeekBar();

    }

    private void setupFullscreenMode() {
        if (DeviceUtils.sdkIsKitkatOrHigher()) UiUtils.showSystemUI(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_threads_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_refresh: {
                //threadsRefreshLayoutTop.setRefreshing(true);
                mDataLoader.loadData(boardId);

            }
        }
        return true;
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public void onBackPressed() {
        Log.d(LOG_TAG, "onBackPressed: ");
        if (fullPicVidContainer.getVisibility() == View.VISIBLE) {
            fullPicVidContainer.setVisibility(View.GONE);
            fullPicVidOpened = false;

            picVidPagerAdapter.stopAndReleasePlayers(true, true);
            if (imageCachePaths != null) {
                Log.d(LOG_TAG, "imageCachePAths: " + imageCachePaths.size());
                Log.d(LOG_TAG, "imageBefore : " + IOUtils.getDirSize(mActivity.getCacheDir(), 0));
                for (String path : imageCachePaths) CacheUtils.deleteDir(new File(path));
                CacheUtils.deleteDir(getCacheDir());
                Log.d(LOG_TAG, "cahce after: " + IOUtils.getDirSize(mActivity.getCacheDir(), 0));
            } else Log.d(LOG_TAG, "imageCahcePaths is null");

            files = null;
            thumbnails = null;
            galleryFragments = new HashMap<>();

            System.gc();

            UiUtils.setBarsTranslucent(this, false);

            if (!UiUtils.barsAreShown) {
                if (DeviceUtils.sdkIsKitkatOrHigher()) {
                    Log.d(LOG_TAG, "im here, bars arent shown");
                    UiUtils.showSystemUI(mActivity);
                    mActivity.fullPicVidOpenedAndFullScreenModeIsOn = false;

                }
                UiUtils.barsAreShown = true;
            }

            if (this.getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                UiUtils.showSystemUIExceptNavigationBar(this);
            }
            return;
        }


        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
        }
        System.gc();
    }


    public void fixCoordinatorLayout(Configuration configuration) {
        if (DeviceUtils.deviceHasNavigationBar(this)) {
            if (configuration == null) configuration = getResources().getConfiguration();
            if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                findViewById(R.id.coordinator).setPadding(
                        0, 0, 0, (int) getResources().getDimension(R.dimen.navigation_bar_height));
            } else findViewById(R.id.coordinator).setPadding(0, 0, 0, 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        files = null;
        thumbnails = null;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onRestoreInstanceState: ");
        if (threadsRecyclerViewState != null) {
            threadsRecyclerView.getLayoutManager().onRestoreInstanceState(threadsRecyclerViewState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState: ");
        if (threadsRecyclerView != null)
            threadsRecyclerViewState = threadsRecyclerView.getLayoutManager().onSaveInstanceState();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDataLoaded(List schema) {
        dataLoaded = true;

        mProgressBarUnit.hideProgressBar();
        mSchema = (ThreadsJsonSchema) schema.get(0);
        Log.d(LOG_TAG, "postLoadData: mSchema: treadsSize: " + mSchema.getThreads().size());
        if (boardName.equals("")) {
            boardName = mSchema.getBoardName();
            ((TextView)toolbar.findViewById(R.id.title)).setText("/" + boardId + "/ - " + boardName);
        }
        if (mRecyclerViewUnit == null) {
            mRecyclerViewUnit = new RecyclerViewUnit(this, (AppBarLayout) findViewById(R.id.threads_appbar));
        } else {
            mRecyclerViewUnit.threadsActivityOnDataLoaded();
        }
    }

    @NonNull
    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showProgressBar() {
        if (!mSwipyRefreshLayoutUnit.getRefreshing()) {
            findViewById(R.id.main_progress_bar).setVisibility(View.VISIBLE);
        }
    }

    @NotNull
    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PermissionManager.INSTANCE.getWRITE_EXTERNAL_STORAGE_PERMISSION_CODE()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mFileSaver.saveFileToExternalStorage(picVidToolbarUrl, picVidToolbarFilename);
            }
        }

    }
}
