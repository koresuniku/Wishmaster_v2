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
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.koresuniku.wishmaster.App;
import com.koresuniku.wishmaster.R;
import com.koresuniku.wishmaster.http.HttpClient;
import com.koresuniku.wishmaster.presenter.DataLoader;
import com.koresuniku.wishmaster.presenter.FileSaver;
import com.koresuniku.wishmaster.presenter.PermissionManager;
import com.koresuniku.wishmaster.presenter.view_interface.LoadDataView;
import com.koresuniku.wishmaster.presenter.view_interface.SaveFileView;
import com.koresuniku.wishmaster.ui.ActionBarUtils;
import com.koresuniku.wishmaster.ui.adapter.PicVidPagerAdapter;
import com.koresuniku.wishmaster.ui.adapter.SingleThreadRecyclerViewAdapter;
import com.koresuniku.wishmaster.ui.controller.ProgressBarUnit;
import com.koresuniku.wishmaster.ui.fragment.GalleryFragment;
import com.koresuniku.wishmaster.http.single_thread_api.models.Post;
import com.koresuniku.wishmaster.http.threads_api.models.Files;
import com.koresuniku.wishmaster.ui.controller.AnswersController;
import com.koresuniku.wishmaster.ui.ScrollbarUtils;
import com.koresuniku.wishmaster.ui.UiUtils;
import com.koresuniku.wishmaster.ui.widget.FixedRecyclerView;
import com.koresuniku.wishmaster.ui.widget.HackyViewPager;
import com.koresuniku.wishmaster.ui.widget.ThreadsRecyclerViewDividerItemDecoration;
import com.koresuniku.wishmaster.ui.widget.VerticalSeekBar;
import com.koresuniku.wishmaster.util.CacheUtils;
import com.koresuniku.wishmaster.util.Constants;
import com.koresuniku.wishmaster.util.DeviceUtils;
import com.koresuniku.wishmaster.util.IOUtils;
import com.koresuniku.wishmaster.ui.listener.AnimationListenerDown;
import com.koresuniku.wishmaster.ui.listener.AnimationListenerUp;
import com.koresuniku.wishmaster.ui.listener.SingleThreadViewPagerOnPageChangeListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;

public class SingleThreadActivity extends AppCompatActivity implements LoadDataView, SaveFileView {
    private final String LOG_TAG = SingleThreadActivity.class.getSimpleName();

    private SingleThreadActivity mActivity;

    public SharedPreferences sharedPreferences;

    private String boardId;
    private String boardName;
    private String threadNumber;

    private ProgressBarUnit mProgressBarUnit;

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

    public SwipyRefreshLayout singleThreadRefreshLayoutTop;
    public SwipyRefreshLayout singleThreadRefreshLayoutBottom;
    public FixedRecyclerView singleThreadRecyclerView;
    private VerticalSeekBar mFastScrollSeekBar;
    private boolean fastScrollSeekBarTouchedFromUser;
    private Parcelable singleThreadRecyclerViewState;
    public SingleThreadRecyclerViewAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    public static List<Files> files;
    public static List<String> thumbnails;
    public static List<String> imageCachePaths;
    public static Map<Integer, GalleryFragment> galleryFragments;
    public ImageLoader imageLoader;
    public Toast mNewPostsNotifierToast;

    public FrameLayout fullPicVidContainer;
    public HackyViewPager picVidPager;
    public PicVidPagerAdapter picVidPagerAdapter;
    public SingleThreadViewPagerOnPageChangeListener singleThreadViewPagerOnPageChangeListener;

    public AnswersController mAnswersManager;
    public CardView mAnswerLayout;
    public List<List<View>> mAnswerViews;
    public LinearLayout mAnswerList;
    public List<Integer> mAnswersScrollStates;
    public boolean answerOpened;

    public boolean dataLoaded = false;
    private boolean dataLoadedFirstTime = true;
    public boolean fullPicVidOpened = false;
    public boolean fullPicVidOpenedAndFullScreenModeIsOn = false;
    public int picVidOpenedPosition = -1;

    private DataLoader mDataLoader;
    public List<Post> mPosts;
    public FileSaver mFileSaver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;

        Intent receivedIntent = getIntent();
        boardId = receivedIntent.getStringExtra(Constants.BOARD_ID);
        boardName = receivedIntent.getStringExtra(Constants.BOARD_NAME);
        threadNumber = receivedIntent.getStringExtra(Constants.THREAD_NUMBER);

        setContentView(R.layout.activity_single_thread);

        initSharedPrefs();
        setupAppBarLayout();
        setupActionBar();
        setupAnimations();
        setupPicVidToolbar();
        setupSwipeRefreshLayout();
        setupFullscreenMode();
        setupOrientationFeatures();
        mAnswersManager = new AnswersController(this);
        mAnswersManager.setupAnswers();
        mAnswersManager.setupAnswersLayoutContainer(this.getResources().getConfiguration());
        mProgressBarUnit = new ProgressBarUnit(this, (ProgressBar) findViewById(R.id.main_progress_bar));

        picVidPager = (HackyViewPager) findViewById(R.id.threads_full_pic_vid_pager);

        mDataLoader = new DataLoader(this);
        mFileSaver = new FileSaver(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
        App.mSettingsContentObserver.switchActivity(this);
        if (!dataLoaded) {
            //loadData();
            mDataLoader.loadData(boardId, threadNumber);
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
        ScrollbarUtils.setScrollbarSize(mActivity,
                (FrameLayout) findViewById(R.id.fast_scroll_seekbar_container),
                newConfig);

        fixCoordinatorLayout(newConfig);
        fixRefreshLayoutOnOrientation();
        mAnswersManager.setupAnswersLayoutContainer(newConfig);

        if (adapter != null) adapter.notifyDataSetChanged();

        if (singleThreadRefreshLayoutTop.isEnabled())
            singleThreadRefreshLayoutTop.setRefreshing(false);
        if (singleThreadRefreshLayoutBottom.isEnabled())
            singleThreadRefreshLayoutBottom.setRefreshing(false);
    }

    private void initSharedPrefs() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
    }

    private void setupAppBarLayout() {
        appBarLayout = (AppBarLayout) findViewById(R.id.single_thread_appbar);
        appBarVerticalOffSet = 0;
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //Log.d(LOG_TAG, "verticalOffset: "  + verticalOffset);
//                Log.d(LOG_TAG, "getTotalScrollRange: " + appBarLayout.getTotalScrollRange());
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
        fullPicVidContainer.setVisibility(GONE);
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
        picVidToolbarTitleTextView = (TextView) findViewById(R.id.picvid_title);
        picVidToolbarShortInfoTextView = (TextView) findViewById(R.id.picvid_short_info);
        picVidToolbarMenuItem = picVidToolbar.getMenu().findItem(R.id.action_save);
    }

    private void setupSwipeRefreshLayout() {
        singleThreadRefreshLayoutTop = (SwipyRefreshLayout) findViewById(R.id.single_thread_refresh_layout_top);
        singleThreadRefreshLayoutBottom = (SwipyRefreshLayout) findViewById(R.id.single_thread_refresh_layout_bottom);

        singleThreadRefreshLayoutTop.setDistanceToTriggerSync(75);
        singleThreadRefreshLayoutBottom.setDistanceToTriggerSync(75);

        singleThreadRefreshLayoutTop.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d(LOG_TAG, "refreshing from top");
                singleThreadRefreshLayoutTop.post(new Runnable() {
                    @Override
                    public void run() {
                        singleThreadRefreshLayoutTop.setRefreshing(true);
                    }
                });
                //loadData();
                mDataLoader.loadData(boardId, threadNumber);
            }
        });
        singleThreadRefreshLayoutBottom.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d(LOG_TAG, "refreshing from bottom");
                singleThreadRefreshLayoutTop.post(new Runnable() {
                    @Override
                    public void run() {
                        singleThreadRefreshLayoutBottom.setRefreshing(true);
                    }
                });
                //loadData();
                mDataLoader.loadData(boardId, threadNumber);
            }
        });

    }

    private void setupFastScrollSeekBar() {
        mFastScrollSeekBar = (VerticalSeekBar) findViewById(R.id.scroll_seekBar);
        ScrollbarUtils.setScrollbarSize(mActivity,
                (FrameLayout) findViewById(R.id.fast_scroll_seekbar_container),
                getResources().getConfiguration());
        mFastScrollSeekBar.setMax(adapter.getItemCount() - 1);
        findViewById(R.id.fast_scroll_seekbar_container).setVisibility(GONE);
        mFastScrollSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(LOG_TAG, "onTouchSeekbar: ");
                fastScrollSeekBarTouchedFromUser = true;
                findViewById(R.id.fast_scroll_seekbar_container).clearAnimation();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.fast_scroll_seekbar_container).startAnimation(fadeOut);
                            findViewById(R.id.fast_scroll_seekbar_container).setVisibility(GONE);
                        }
                    }, 750);
                }
                return false;
            }
        });
        mFastScrollSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(LOG_TAG, "onProgressChanged: progress: " + progress);
                Log.d(LOG_TAG, "adapter count: " + adapter.getItemCount());
                if (fastScrollSeekBarTouchedFromUser) {
                    if (progress == adapter.getItemCount())
                        linearLayoutManager.scrollToPositionWithOffset(progress, Integer.MAX_VALUE);
                    else linearLayoutManager.scrollToPositionWithOffset(progress, 0);
                    mFastScrollSeekBar.updateThumb();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //if (findViewById(R.id.fast_scroll_seekbar_container).getVisibility() == View.VISIBLE) {


            }
        });
    }

    private void setupRecyclerView() {
        singleThreadRecyclerView = (FixedRecyclerView) findViewById(R.id.single_thread_recycler_view);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mActivity)
                .imageDownloader(new BaseImageDownloader(mActivity, 50 * 1000, 200 * 1000)).build();
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();
        Glide.get(this).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(HttpClient.client));
        singleThreadRecyclerView.setItemViewCacheSize(0);
        singleThreadRecyclerView.addItemDecoration(new ThreadsRecyclerViewDividerItemDecoration(this));
        linearLayoutManager = new LinearLayoutManager(this);
        singleThreadRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SingleThreadRecyclerViewAdapter(this, boardId);
        adapter.setHasStableIds(true);
        singleThreadRecyclerView.setAdapter(adapter);
        final boolean[] touchedAgain = {false};
        singleThreadRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    imageLoader.pause();
                } else {
                    imageLoader.resume();
                }
                if (!fastScrollSeekBarTouchedFromUser) {
                    if (newState != 0) {
                        touchedAgain[0] = true;
                        if (findViewById(R.id.fast_scroll_seekbar_container).getVisibility() == GONE) {
                            findViewById(R.id.fast_scroll_seekbar_container).setVisibility(View.VISIBLE);
                            findViewById(R.id.fast_scroll_seekbar_container).startAnimation(fadeIn);
                        }
                    } else {
                        touchedAgain[0] = false;
                        if (findViewById(R.id.fast_scroll_seekbar_container).getVisibility() == View.VISIBLE) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!fastScrollSeekBarTouchedFromUser && !touchedAgain[0]) {
                                        findViewById(R.id.fast_scroll_seekbar_container).startAnimation(fadeOut);
                                        findViewById(R.id.fast_scroll_seekbar_container).setVisibility(GONE);
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
                    mFastScrollSeekBar.setProgress(linearLayoutManager.findLastCompletelyVisibleItemPosition());
                } else {
                    mFastScrollSeekBar.setProgress(linearLayoutManager.findFirstVisibleItemPosition());
                }
                mFastScrollSeekBar.updateThumb();

            }
        });
        singleThreadRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                defineIfNeedToEnableRefreshLayout();
                fastScrollSeekBarTouchedFromUser = false;
                return false;
            }
        });
        onRestoreInstanceState(null);
        fixCoordinatorLayout(null);
        adapter = new SingleThreadRecyclerViewAdapter(mActivity, boardId);
        singleThreadRecyclerView.setAdapter(adapter);

        setupFastScrollSeekBar();
    }

    private void setupFullscreenMode() {
        if (DeviceUtils.sdkIsKitkatOrHigher()) UiUtils.showSystemUI(this);
    }

    private void fixRefreshLayoutOnOrientation() {
        defineIfNeedToEnableRefreshLayout();
    }

    private void defineIfNeedToEnableRefreshLayout() {
        if (singleThreadRecyclerView != null) {
            if (((LinearLayoutManager)singleThreadRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition() == -1
                    && ((LinearLayoutManager)singleThreadRecyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition() == -1) {
                if (!singleThreadRecyclerView.canScrollVertically(-1) && appBarVerticalOffSet == 0) {
                    singleThreadRefreshLayoutBottom.setEnabled(false);
                    singleThreadRefreshLayoutTop.setEnabled(true);
                    return;
                }
                if (!singleThreadRecyclerView.canScrollVertically(1) && (appBarVerticalOffSet * - 1) == appBarLayout.getTotalScrollRange()) {
                    singleThreadRefreshLayoutBottom.setEnabled(true);
                    singleThreadRefreshLayoutTop.setEnabled(false);
                    return;
                }
            }
            if (((LinearLayoutManager)singleThreadRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition() == 0
                && ((LinearLayoutManager)singleThreadRecyclerView.getLayoutManager())
                    .findLastCompletelyVisibleItemPosition() == singleThreadRecyclerView.getChildCount() - 1) {
                if (appBarVerticalOffSet == 0) {
                    singleThreadRefreshLayoutBottom.setEnabled(false);
                    singleThreadRefreshLayoutTop.setEnabled(true);
                    return;
                }
                if ((appBarVerticalOffSet * - 1) == appBarLayout.getTotalScrollRange()) {
                    singleThreadRefreshLayoutBottom.setEnabled(true);
                    singleThreadRefreshLayoutTop.setEnabled(false);
                    return;
                }
            }
            if (appBarVerticalOffSet == 0
                    && ((LinearLayoutManager)singleThreadRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition() == 0) {
                //Log.d(LOG_TAG, "enabling top");
                singleThreadRefreshLayoutBottom.setEnabled(false);
                singleThreadRefreshLayoutTop.setEnabled(true);
                return;
            }
            if ((appBarVerticalOffSet * - 1) == appBarLayout.getTotalScrollRange()
                    && ViewCompat.canScrollVertically(singleThreadRecyclerView, -1)) {
                //Log.d(LOG_TAG, "enabling bottom");
                singleThreadRefreshLayoutBottom.setEnabled(true);
                singleThreadRefreshLayoutTop.setEnabled(false);
                return;
            }

            //Log.d(LOG_TAG, "disabling refresh layouts");
            singleThreadRefreshLayoutTop.setEnabled(false);
            singleThreadRefreshLayoutBottom.setEnabled(false);

        }
    }

    private void fixCoordinatorLayout(Configuration configuration) {
        if (DeviceUtils.deviceHasNavigationBar(this)) {
            if (configuration == null) configuration = getResources().getConfiguration();
            if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                findViewById(R.id.coordinator).setPadding(0, 0, 0, (int) getResources().getDimension(R.dimen.navigation_bar_height));
            } else findViewById(R.id.coordinator).setPadding(0, 0, 0, 0);
            if (singleThreadRefreshLayoutTop != null) {
                singleThreadRefreshLayoutTop.requestLayout();
            }
            if (singleThreadRefreshLayoutBottom != null) {
                singleThreadRefreshLayoutBottom.requestLayout();
            }
        }
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
                singleThreadRefreshLayoutTop.setRefreshing(true);
               // loadData();
                mDataLoader.loadData(boardId, threadNumber);
            }
        }
        return true;
    }

    @SuppressLint("UseSparseArrays")
    @Override
    public void onBackPressed() {
        Log.d(LOG_TAG, "onBackPressed: ");

        if (fullPicVidContainer.getVisibility() == View.VISIBLE) {
            fullPicVidContainer.setVisibility(GONE);
            fullPicVidOpened = false;

            picVidPagerAdapter.stopAndReleasePlayers(true, true);
            if (imageCachePaths != null) {
                Log.d(LOG_TAG, "imageCachePAths: " + imageCachePaths.size());
                Log.d(LOG_TAG, "imageBefore : " + IOUtils.getDirSize(mActivity.getCacheDir(), 0));
                for (String path : imageCachePaths) CacheUtils.deleteDir(new File(path));
                CacheUtils.deleteDir(getCacheDir());
                Log.d(LOG_TAG, "cahce after: " + IOUtils.getDirSize(mActivity.getCacheDir(), 0));
            } else Log.d(LOG_TAG, "imageCahcePaths is null");

            thumbnails = null;
            galleryFragments = new HashMap<>();

            if (DeviceUtils.sdkIsKitkatOrHigher()) {
                UiUtils.showSystemUI(mActivity);
                UiUtils.barsAreShown = true;
                mActivity.fullPicVidOpenedAndFullScreenModeIsOn = false;
                if (answerOpened) {
                    UiUtils.setStatusBarTranslucent(this, true);
                    UiUtils.setNavigationBarTranslucent(this, false);
                } else UiUtils.setBarsTranslucent(this, false);
            }

            if (this.getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                UiUtils.showSystemUIExceptNavigationBar(this);
            }
            return;
        }

        if (answerOpened) {
            mAnswersManager.showPreviousAnswer();
            return;
        }

        if (imageLoader != null) imageLoader.clearMemoryCache();
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
        }
        System.gc();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        files = null;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onRestoreInstanceState: ");
        if (singleThreadRecyclerViewState != null) {
            singleThreadRecyclerView.getLayoutManager().onRestoreInstanceState(singleThreadRecyclerViewState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(LOG_TAG, "onSaveInstanceState: ");
        singleThreadRecyclerViewState = singleThreadRecyclerView.getLayoutManager().onSaveInstanceState();
        super.onSaveInstanceState(outState);
    }



    @Override
    public void onDataLoaded(List schema) {
        dataLoaded = true;

        mProgressBarUnit.hideProgressBar();
        final int beforeCount = mPosts == null ? 0 : mPosts.size();

        mPosts = (List<Post>) schema;

        Log.d(LOG_TAG, "data loaded: first time " + dataLoadedFirstTime);
        if (dataLoadedFirstTime) setupRecyclerView();
        int afterCount = mPosts.size();
        adapter.onAdapterChanges();
        adapter.notifyDataSetChanged();
        Log.d(LOG_TAG, "beforeCount: " + beforeCount + ", afterCount: " + afterCount);
        if (!dataLoadedFirstTime) {
            adapter.notifyNewPosts(beforeCount, afterCount);
            if (linearLayoutManager.findLastCompletelyVisibleItemPosition() == beforeCount - 1) {
                linearLayoutManager.scrollToPositionWithOffset(beforeCount, 0);
                singleThreadRefreshLayoutBottom.requestLayout();
            }
        } else dataLoadedFirstTime = false;
        mFastScrollSeekBar.setMax(adapter.getItemCount());
        mFastScrollSeekBar.updateThumb();

        if (singleThreadRefreshLayoutTop.isRefreshing())
            singleThreadRefreshLayoutTop.setRefreshing(false);
        if (singleThreadRefreshLayoutBottom.isRefreshing())
            singleThreadRefreshLayoutBottom.setRefreshing(false);

        Log.d(LOG_TAG, "adapter.size " + adapter.getItemCount());

    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showProgressBar() {
        if (!singleThreadRefreshLayoutTop.isRefreshing() && !singleThreadRefreshLayoutBottom.isRefreshing()) {
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
