package com.koresuniku.wishmaster.activities;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.ViewCompat;
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
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koresuniku.wishmaster.App;
import com.koresuniku.wishmaster.adapters.PicVidPagerAdapter;
import com.koresuniku.wishmaster.R;
import com.koresuniku.wishmaster.adapters.ThreadsListViewAdapter;
import com.koresuniku.wishmaster.fragments.GalleryFragment;
import com.koresuniku.wishmaster.http.threads_api.ThreadsForPagesAsynktask;
import com.koresuniku.wishmaster.http.threads_api.models.Files;
import com.koresuniku.wishmaster.ui.ScrollbarUtils;
import com.koresuniku.wishmaster.ui.views.FixedRecyclerView;
import com.koresuniku.wishmaster.ui.views.HackyViewPager;
import com.koresuniku.wishmaster.ui.views.SpeedyLinearLayoutManager;
import com.koresuniku.wishmaster.ui.views.ThreadsRecyclerViewDividerItemDecoration;
import com.koresuniku.wishmaster.ui.views.VerticalSeekBar;
import com.koresuniku.wishmaster.utils.CacheUtils;
import com.koresuniku.wishmaster.utils.DeviceUtils;
import com.koresuniku.wishmaster.adapters.ThreadsRecyclerViewAdapter;
import com.koresuniku.wishmaster.http.threads_api.ThreadsApiService;
import com.koresuniku.wishmaster.http.threads_api.models.ThreadsJsonSchema;
import com.koresuniku.wishmaster.utils.Constants;
import com.koresuniku.wishmaster.ui.UIUtils;
import com.koresuniku.wishmaster.utils.IOUtils;
import com.koresuniku.wishmaster.utils.listeners.AnimationListenerDown;
import com.koresuniku.wishmaster.utils.listeners.AnimationListenerUp;
import com.koresuniku.wishmaster.utils.listeners.ThreadsViewPagerOnPageChangeListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.io.File;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ThreadsActivity extends AppCompatActivity {
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

    public SwipyRefreshLayout threadsRefreshLayoutTop;
    public SwipyRefreshLayout threadsRefreshLayoutBottom;
    public FixedRecyclerView threadsRecyclerView;
    public VerticalSeekBar mFastScrollSeekbar;
    public ListView mListView;
    public ThreadsListViewAdapter mListViewAdapter;
    private boolean fastScrollSeekbarTouchedFromUser;

    private Parcelable threadsRecyclerViewState;
    private ThreadsRecyclerViewAdapter adapter;
    private SpeedyLinearLayoutManager linearLayoutManager;
    public static List<Files> files;
    public static List<String> thumbnails;
    public static List<String> imageCachePaths;
    public static Map<Integer, GalleryFragment> galleryFragments;
    public ImageLoader imageLoader;

    public FrameLayout fullPicVidContainer;
    public HackyViewPager picVidPager;
    public PicVidPagerAdapter picVidPagerAdapter;
    public ThreadsViewPagerOnPageChangeListener threadsViewPagerOnPageChangeListener;

    public boolean dataLoaded = false;
    public boolean fullPicVidOpened = false;
    public boolean fullPicVidOpenedAndFullScreenModeIsOn = false;
    public int picVidOpenedPosition = -1;

    public OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10000, TimeUnit.SECONDS)
            .proxy(setProxy())
            .readTimeout(10000, TimeUnit.SECONDS).build();
    public Gson gson = new GsonBuilder().create();
    public Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(Constants.DVACH_BASE_URL)
            .client(client)
            .build();
    public ThreadsApiService service = retrofit.create(ThreadsApiService.class);
    public ThreadsJsonSchema mSchema;


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
        setupSwipeRefreshLayout();
        setupFullscreenMode();
        setupOrientationFeatures();

        picVidPager = (HackyViewPager) findViewById(R.id.threads_full_pic_vid_pager);
    }

    private Proxy setProxy() {
        return new Proxy(Proxy.Type.HTTP, new InetSocketAddress("94.177.233.56", 1189));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!dataLoaded) {
            loadData();
        } else {
//            if (mSchema != null) {
//                //setupThreadsRecyclerView();
//            }
//            else loadData();
        }
    }

    private void setupOrientationFeatures() {
        if (Constants.API_INT >= 19) {
            if (this.getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                UIUtils.showSystemUIExceptNavigationBar(this);
            } else UIUtils.showSystemUI(this);
        }
    }

    private void initSharedPrefs() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(LOG_TAG, "onConfigurationChanged:");
        UIUtils.setupToolbarForNavigationBar(this, picVidToolbar);
        if (Constants.API_INT >= 19) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                UIUtils.showSystemUIExceptNavigationBar(this);
            } else UIUtils.showSystemUI(this);
            if (fullPicVidOpenedAndFullScreenModeIsOn) {
                UIUtils.hideSystemUI(mActivity);
                UIUtils.barsAreShown = false;
            }
        }

        setupActionBar();
        ScrollbarUtils.setScrollbarSize(mActivity,
                (FrameLayout) findViewById(R.id.fast_scroll_seekbar_container),
                newConfig);

        fixCoordinatorLayout(newConfig);
        fixRefreshLayoutOnOrientation();

        if (adapter != null) adapter.notifyDataSetChanged();
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

    private void fixRefreshLayoutOnOrientation() {
        defineIfNeedToEnableRefreshLayout();
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
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? 20 : 16);
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
        UIUtils.tintMenuIcons(this, picVidToolbar.getMenu());
        fullPicVidContainer = (FrameLayout) findViewById(R.id.threads_full_picvid_container);
        fullPicVidContainer.setVisibility(View.GONE);
        UIUtils.setupToolbarForNavigationBar(this, picVidToolbar);
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

    }

    private void setupSwipeRefreshLayout() {
        threadsRefreshLayoutTop = (SwipyRefreshLayout) findViewById(R.id.threads_refresh_layout_top);
        threadsRefreshLayoutBottom = (SwipyRefreshLayout) findViewById(R.id.threads_refresh_layout_bottom);

        threadsRefreshLayoutTop.setDistanceToTriggerSync(75);
        threadsRefreshLayoutBottom.setDistanceToTriggerSync(75);

        threadsRefreshLayoutTop.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d(LOG_TAG, "refreshing epta");
                threadsRefreshLayoutTop.post(new Runnable() {
                    @Override
                    public void run() {
                        threadsRefreshLayoutTop.setRefreshing(true);
                    }
                });
                loadData();
            }
        });
        threadsRefreshLayoutBottom.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d(LOG_TAG, "refreshing epta");
                threadsRefreshLayoutBottom.post(new Runnable() {
                    @Override
                    public void run() {
                        threadsRefreshLayoutBottom.setRefreshing(true);
                    }
                });
                loadData();
            }
        });

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
        setupFastScrollSeekbar();
    }

    private void setupFastScrollSeekbar() {
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
                //if (findViewById(R.id.fast_scroll_seekbar_container).getVisibility() == View.VISIBLE) {


            }
        });
    }

    private void setupThreadsRecyclerView() {
        threadsRecyclerView = (FixedRecyclerView) findViewById(R.id.threads_recycler_view);
        //threadsRecyclerView.setAutoHideDelay(0);

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mActivity)
                .imageDownloader(new BaseImageDownloader(mActivity, 50 * 1000, 20 * 1000)).build();
        Glide.get(this).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();

        threadsRecyclerView.setViewCacheExtension(new RecyclerView.ViewCacheExtension() {
            @Override
            public View getViewForPositionAndType(RecyclerView.Recycler recycler, int position, int type) {
                recycler.clear();
                return null;
            }
        });

        threadsRecyclerView.setDrawingCacheEnabled(false);
        threadsRecyclerView.addItemDecoration(new ThreadsRecyclerViewDividerItemDecoration(this));
        linearLayoutManager = new SpeedyLinearLayoutManager(this);
        threadsRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ThreadsRecyclerViewAdapter(this, boardId);
        adapter.setHasStableIds(true);

        threadsRecyclerView.setAdapter(adapter);
        final boolean[] touchedAgain = {false};
        threadsRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    imageLoader.pause();
                } else {
                    imageLoader.resume();
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
                defineIfNeedToEnableRefreshLayout();
                fastScrollSeekbarTouchedFromUser = false;

                return false;
            }
        });

        onRestoreInstanceState(null);
        fixCoordinatorLayout(null);

        setupFastScrollSeekbar();

    }


    private void setupFullscreenMode() {
        if (Constants.API_INT >= 19) UIUtils.showSystemUI(this);
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
                threadsRefreshLayoutTop.setRefreshing(true);
                loadData();

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
            App.fixLeakCanary696(getApplicationContext());
            System.gc();

            UIUtils.setBarsTranslucent(this, false);

            if (!UIUtils.barsAreShown) {
                if (Constants.API_INT >= 19) {
                    Log.d(LOG_TAG, "im here, bars arent shown");
                    UIUtils.showSystemUI(mActivity);
                    mActivity.fullPicVidOpenedAndFullScreenModeIsOn = false;
                    //appBarLayout.startAnimation(animExpandActionBar);
                }
                UIUtils.barsAreShown = true;
            }

            if (this.getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                UIUtils.showSystemUIExceptNavigationBar(this);
            }
            return;
        }

        //imageLoader.clearMemoryCache();

        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            overridePendingTransition(R.anim.slide_in_back, R.anim.slide_out_back);
        }
        System.gc();
    }

    private void loadData() {

        dataLoaded = true;

        if (boardId.equals("d") || boardId.equals("d")) {
            new ThreadsForPagesAsynktask(this, boardId).execute();
        } else {
            Call<ThreadsJsonSchema> call = service.getThreads(boardId);
            call.enqueue(new Callback<ThreadsJsonSchema>() {
                @Override
                public void onResponse(Call<ThreadsJsonSchema> call,
                                       Response<ThreadsJsonSchema> response) {
                    mSchema = response.body();
                    Log.d(LOG_TAG, "data loaded:");
                    postLoadData();
                }

                @Override
                public void onFailure(Call<ThreadsJsonSchema> call, Throwable t) {

                }
            });
        }
    }

    public void postLoadData() {
        Log.d(LOG_TAG, "postLoadData: mSchema: treadsSize: " + mSchema.getThreads().size());
        if (boardName.equals("")) {
            boardName = mSchema.getBoardName();
            ((TextView)toolbar.findViewById(R.id.title)).setText("/" + boardId + "/ - " + boardName);
        }
        if (threadsRecyclerView == null) {
            setupThreadsRecyclerView();
        } else {
            if (adapter == null) {
                adapter = new ThreadsRecyclerViewAdapter(mActivity, boardId);
                threadsRecyclerView.setAdapter(adapter);
                if (threadsRefreshLayoutTop.isEnabled())
                    threadsRefreshLayoutTop.setRefreshing(false);
                if (threadsRefreshLayoutBottom.isEnabled())
                    threadsRefreshLayoutBottom.setRefreshing(false);
            } else {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
                if (threadsRefreshLayoutTop.isEnabled() || threadsRefreshLayoutTop.isRefreshing())
                    threadsRefreshLayoutTop.setRefreshing(false);
                if (threadsRefreshLayoutBottom.isEnabled())
                    threadsRefreshLayoutBottom.setRefreshing(false);
                appBarLayout.setExpanded(true);
                threadsRecyclerView.scrollToPosition(0);


                Log.d(LOG_TAG, "adapter.size " + adapter.getItemCount());
            }
        }
//        if (mListView == null) {
//            setupListView();
//        }
    }

    private void fixCoordinatorLayout(Configuration configuration) {
        if (DeviceUtils.deviceHasNavigationBar(this)) {
            if (configuration == null) configuration = getResources().getConfiguration();
            if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                findViewById(R.id.coordinator).setPadding(0, 0, 0, DeviceUtils.apiIs20OrHigher() ? 96 : 48);
            } else findViewById(R.id.coordinator).setPadding(0, 0, 0, 0);
        }
    }

    private void defineIfNeedToEnableRefreshLayout() {
        if (threadsRecyclerView != null) {
            if (((LinearLayoutManager)threadsRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition() == -1
                    && ((LinearLayoutManager)threadsRecyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition() == -1) {
                if (!threadsRecyclerView.canScrollVertically(-1) && appBarVerticalOffSet == 0) {
                    threadsRecyclerView.setEnabled(false);
                    threadsRecyclerView.setEnabled(true);
                    return;
                }
                if (!threadsRecyclerView.canScrollVertically(1) && (appBarVerticalOffSet * - 1) == appBarLayout.getTotalScrollRange()) {
                    threadsRecyclerView.setEnabled(true);
                    threadsRecyclerView.setEnabled(false);
                    return;
                }
            }
            if (((LinearLayoutManager)threadsRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition() == 0
                    && ((LinearLayoutManager)threadsRecyclerView.getLayoutManager())
                    .findLastCompletelyVisibleItemPosition() == threadsRecyclerView.getChildCount() - 1) {
                Log.d(LOG_TAG, "buzzfeezz 0");
                if (appBarVerticalOffSet == 0) {
                    threadsRefreshLayoutBottom.setEnabled(false);
                    threadsRefreshLayoutTop.setEnabled(true);
                    return;
                }
                if ((appBarVerticalOffSet * - 1) == appBarLayout.getTotalScrollRange()) {
                    threadsRefreshLayoutBottom.setEnabled(true);
                    threadsRefreshLayoutTop.setEnabled(false);
                    return;
                }
            }
            if (appBarVerticalOffSet == 0
                    && ((LinearLayoutManager)threadsRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition() == 0) {
                //Log.d(LOG_TAG, "enabling top");
                threadsRefreshLayoutBottom.setEnabled(false);
                threadsRefreshLayoutTop.setEnabled(true);
                return;
            }
            if ((appBarVerticalOffSet * - 1) == appBarLayout.getTotalScrollRange()
                    && ViewCompat.canScrollVertically(threadsRecyclerView, -1)) {
                //Log.d(LOG_TAG, "enabling bottom");
                threadsRefreshLayoutBottom.setEnabled(true);
                threadsRefreshLayoutTop.setEnabled(false);
                return;
            }

            Log.d(LOG_TAG, "disabling refresh layouts");
            threadsRefreshLayoutTop.setEnabled(false);
            threadsRefreshLayoutBottom.setEnabled(false);

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
        threadsRecyclerViewState = threadsRecyclerView.getLayoutManager().onSaveInstanceState();
        super.onSaveInstanceState(outState);
    }

}
