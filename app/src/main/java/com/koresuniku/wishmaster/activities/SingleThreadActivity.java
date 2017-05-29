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
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koresuniku.wishmaster.App;
import com.koresuniku.wishmaster.R;
import com.koresuniku.wishmaster.adapters.PicVidPagerAdapter;
import com.koresuniku.wishmaster.adapters.SingleThreadRecyclerViewAdapter;
import com.koresuniku.wishmaster.fragments.GalleryFragment;
import com.koresuniku.wishmaster.http.single_thread_api.models.Post;
import com.koresuniku.wishmaster.http.single_thread_api.SingleThreadApiService;
import com.koresuniku.wishmaster.http.threads_api.models.Files;
import com.koresuniku.wishmaster.ui.ScrollbarUtils;
import com.koresuniku.wishmaster.ui.UIUtils;
import com.koresuniku.wishmaster.ui.text.AnswersLinkMovementMethod;
import com.koresuniku.wishmaster.ui.text.CommentLinkMovementMethod;
import com.koresuniku.wishmaster.ui.views.FixedRecyclerView;
import com.koresuniku.wishmaster.ui.views.HackyViewPager;
import com.koresuniku.wishmaster.ui.views.SaveStateScrollView;
import com.koresuniku.wishmaster.ui.views.ThreadsRecyclerViewDividerItemDecoration;
import com.koresuniku.wishmaster.ui.views.VerticalSeekBar;
import com.koresuniku.wishmaster.utils.CacheUtils;
import com.koresuniku.wishmaster.utils.Constants;
import com.koresuniku.wishmaster.utils.DeviceUtils;
import com.koresuniku.wishmaster.utils.IOUtils;
import com.koresuniku.wishmaster.utils.listeners.AnimationListenerDown;
import com.koresuniku.wishmaster.utils.listeners.AnimationListenerUp;
import com.koresuniku.wishmaster.utils.listeners.SingleThreadViewPagerOnPageChangeListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import java.io.File;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Collections;
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

import static android.view.View.GONE;

public class SingleThreadActivity extends AppCompatActivity {
    private final String LOG_TAG = SingleThreadActivity.class.getSimpleName();

    private SingleThreadActivity mActivity;

    public SharedPreferences sharedPreferences;

    private String boardId;
    private String boardName;
    private String threadNumber;

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

    private CardView mAnswerLayout;
    public List<List<View>> mAnswerViews;
    private LinearLayout mAnswerList;
    private List<Integer> mAnswersScrollStates;
    public boolean answerOpened;

    public boolean dataLoaded = false;
    public boolean fullPicVidOpened = false;
    public boolean fullPicVidOpenedAndFullScreenModeIsOn = false;
    public int picVidOpenedPosition = -1;

    public OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(5000, TimeUnit.SECONDS)
            //.proxy(setProxy())
            .readTimeout(10000, TimeUnit.SECONDS).build();
    public Gson gson = new GsonBuilder().create();
    public Retrofit retrofit = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(Constants.DVACH_BASE_URL)
            //.client(client)
            .build();

    SingleThreadApiService service = retrofit.create(SingleThreadApiService.class);
    public static List<Post> mPosts;


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
        setupAnswers();
        setupAnswersLayoutContainer(getResources().getConfiguration());

        picVidPager = (HackyViewPager) findViewById(R.id.threads_full_pic_vid_pager);

        loadData();
    }

    private Proxy setProxy() {
        return new Proxy(Proxy.Type.SOCKS, new InetSocketAddress("188.165.243.106", 9050));
    }

    @Override
    protected void onStart() {
        super.onStart();
        //if (adapter != null) adapter.notifyDataSetChanged();
        App.mSettingsContentObserver.switchActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!dataLoaded) {
            loadData();
        } else {
            //if (mPosts != null) setupThreadsRecyclerView();
            //else loadData();
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
        setupAnswersLayoutContainer(newConfig);

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
        fullPicVidContainer.setVisibility(GONE);
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
                loadData();
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
                loadData();
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

    private void setupThreadsRecyclerView() {
        singleThreadRecyclerView = (FixedRecyclerView) findViewById(R.id.single_thread_recycler_view);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mActivity)
                .imageDownloader(new BaseImageDownloader(mActivity, 50 * 1000, 200 * 1000)).build();
        ImageLoader.getInstance().init(config);
        imageLoader = ImageLoader.getInstance();
        Glide.get(this).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client));
        singleThreadRecyclerView.setItemViewCacheSize(0);
        singleThreadRecyclerView.addItemDecoration(new ThreadsRecyclerViewDividerItemDecoration(this));
        linearLayoutManager = new LinearLayoutManager(this);
        singleThreadRecyclerView.setLayoutManager(linearLayoutManager);
        adapter = new SingleThreadRecyclerViewAdapter(this, boardId);
        adapter.setHasStableIds(true);
        if (adapter.getItemCount() < 20) {

        }
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


        setupFastScrollSeekBar();
    }

    private void setupFullscreenMode() {
        if (Constants.API_INT >= 19) UIUtils.showSystemUI(this);
    }

    private void fixRefreshLayoutOnOrientation() {
        defineIfNeedToEnableRefreshLayout();
    }

    private void defineIfNeedToEnableRefreshLayout() {
        if (singleThreadRecyclerView != null) {
            //((LinearLayoutManager)singleThreadRecyclerView.getLayoutManager()).visi
            //Log.d(LOG_TAG, "canScrollUpwards: " + singleThreadRecyclerView.canScrollVertically(-1));
            //Log.d(LOG_TAG, "canScrollDownwards: " + singleThreadRecyclerView.canScrollVertically(1));
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
                findViewById(R.id.coordinator).setPadding(0, 0, 0, DeviceUtils.apiIsLollipopOrHigher() ? 96 : 48);
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
                loadData();
            }
        }
        return true;
    }

    private void loadData() {
        Log.d(LOG_TAG, "loadData:");
        dataLoaded = true;
        Call<List<Post>> call = service.getPosts("get_thread", boardId, threadNumber, 0);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call,
                                   Response<List<Post>> response) {
                final int beforeCount;
                if (mPosts != null) {
                    beforeCount = mPosts.size();
                } else beforeCount = -1;
                mPosts = response.body();
                Log.d(LOG_TAG, "data loaded:");
                if (singleThreadRecyclerView == null) {
                    setupThreadsRecyclerView();
                } else {
                    if (adapter == null) {
                        adapter = new SingleThreadRecyclerViewAdapter(mActivity, boardId);
                        singleThreadRecyclerView.setAdapter(adapter);
                        if (singleThreadRefreshLayoutTop.isEnabled())
                            singleThreadRefreshLayoutTop.setRefreshing(false);
                        if (singleThreadRefreshLayoutBottom.isEnabled())
                            singleThreadRefreshLayoutBottom.setRefreshing(false);
                    } else {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                int afterCount = mPosts.size();
                                adapter.onAdapterChanges();
                                adapter.notifyDataSetChanged();
                                adapter.notifyNewPosts(beforeCount, afterCount);
                                mFastScrollSeekBar.setMax(adapter.getItemCount());
                                mFastScrollSeekBar.updateThumb();
                                if (linearLayoutManager.findLastCompletelyVisibleItemPosition()
                                        == beforeCount - 1) {
                                    linearLayoutManager.scrollToPositionWithOffset(beforeCount, 0);
                                    singleThreadRefreshLayoutBottom.requestLayout();
                                } else {
                                    Log.d(LOG_TAG, "");
                                }

                            }
                        });
                        if (singleThreadRefreshLayoutTop.isRefreshing())
                            singleThreadRefreshLayoutTop.setRefreshing(false);
                        if (singleThreadRefreshLayoutBottom.isRefreshing())
                            singleThreadRefreshLayoutBottom.setRefreshing(false);

                        Log.d(LOG_TAG, "adapter.size " + adapter.getItemCount());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.d(LOG_TAG, "failure data loading");
                t.printStackTrace();
            }
        });
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
           // App.fixLeakCanary696(getApplicationContext());
            System.gc();

            if (Constants.API_INT >= 19) {
                UIUtils.showSystemUI(mActivity);
                UIUtils.barsAreShown = true;
                mActivity.fullPicVidOpenedAndFullScreenModeIsOn = false;
                if (answerOpened) {
                    UIUtils.setStatusBarTranslucent(this, true);
                    UIUtils.setNavigationBarTranslucent(this, false);
                } else UIUtils.setBarsTranslucent(this, false);
            }

            if (this.getResources().getConfiguration().orientation
                    == Configuration.ORIENTATION_LANDSCAPE) {
                UIUtils.showSystemUIExceptNavigationBar(this);
            }
            return;
        }

        if (answerOpened) {
            showPreviousAnswer();
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

    private void setupAnswers() {
        mAnswerViews = new ArrayList<>();
        mAnswerLayout = (CardView) findViewById(R.id.answer_layout);
        mAnswerList = (LinearLayout) findViewById(R.id.answer_layout_list);
        mAnswersScrollStates = new ArrayList<>();
        answerOpened = false;
    }

    private void setupAnswersLayoutContainer(Configuration configuration) {
        findViewById(R.id.answer_layout_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPreviousAnswer();
            }
        });
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (DeviceUtils.getApiInt() >= 19) {
                findViewById(R.id.answer_layout_container).setPadding(
                        0, DeviceUtils.apiIsLollipopOrHigher() ? 48 : 24,
                        0, DeviceUtils.apiIsLollipopOrHigher() ? 96 : 48);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                params.setMargins(
                        DeviceUtils.apiIsLollipopOrHigher() ? 24 : 12,
                        DeviceUtils.apiIsLollipopOrHigher() ? 50 : 24,
                        DeviceUtils.apiIsLollipopOrHigher() ? 24 : 12,
                        DeviceUtils.apiIsLollipopOrHigher() ? 50 : 25);
                mAnswerLayout.setLayoutParams(params);
                mAnswerLayout.requestLayout();
            }
        } else {
            if (DeviceUtils.getApiInt() >= 19) {
                findViewById(R.id.answer_layout_container).setPadding(
                        0, DeviceUtils.apiIsLollipopOrHigher() ? 48 : 24, 0, 0);

                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.gravity = Gravity.CENTER;
                params.setMargins(
                        DeviceUtils.apiIsLollipopOrHigher() ? 24 : 12,
                        DeviceUtils.apiIsLollipopOrHigher() ? 50 : 25,
                        DeviceUtils.apiIsLollipopOrHigher() ? 24 : 12,
                        DeviceUtils.apiIsLollipopOrHigher() ? 50 : 25);
                mAnswerLayout.setLayoutParams(params);
                mAnswerLayout.requestLayout();
            }
        }
    }

    public void writeInAnswerScrollState() {
        mAnswersScrollStates.add(mAnswerLayout.findViewById(R.id.answer_layout_scrollview).getScrollY());
    }

    public void showAnswer(String postNumberToGo, String postNumberFrom) {
        if (DeviceUtils.getApiInt() >= 19) UIUtils.setStatusBarTranslucent(mActivity, true);

        int position = -1;
        for (Post post : mPosts) {
            if (post.getNum().equals(postNumberToGo)) {
                position = mPosts.indexOf(post);
                break;
            }
        }
        Log.d(LOG_TAG, "getting child for position: " + position);
        View answerView = adapter.getViewForPosition(position);
        if (postNumberFrom != null) {
            if (((TextView)answerView.findViewById(R.id.post_comment)).getMovementMethod() instanceof CommentLinkMovementMethod) {
                ((CommentLinkMovementMethod) ((TextView) answerView.findViewById(R.id.post_comment)).getMovementMethod())
                        .setForegroundSpanForParticularLocation(postNumberFrom);
            }
            if (((TextView)answerView.findViewById(R.id.answers)).getMovementMethod() instanceof AnswersLinkMovementMethod) {
                ((AnswersLinkMovementMethod) ((TextView) answerView.findViewById(R.id.answers)).getMovementMethod())
                        .setForegroundSpanForParticularLocation(postNumberFrom);
            }
        }
        setupAnswersLayoutContainer(getResources().getConfiguration());
        //setupCommentTextViewForRecyclerView(position);
        mAnswerViews.add(new ArrayList<>(Collections.singletonList(answerView)));
        //mAnswerLayout.removeAllViews();
        mAnswerList.removeAllViews();
        //mAnswerLayout.addView(answerView);
        mAnswerList.addView(answerView);
        if (answerOpened) {
            writeInAnswerScrollState();
        }
        findViewById(R.id.answer_layout_container).setVisibility(View.VISIBLE);
        answerOpened = true;
        adapter.notifySingleView = true;
    }

    public void showAnswerList(String postNumberFrom) {

        if (DeviceUtils.getApiInt() >= 19) UIUtils.setStatusBarTranslucent(mActivity, true);

        List<String> answersNumbers = adapter.mAnswers.get(postNumberFrom);
        List<Integer> answersPositions = new ArrayList<>();

        Post post;
        for (int i = 0; i < mPosts.size(); i++) {
            post = mPosts.get(i);
            if (answersNumbers.contains(post.getNum())) {
                answersPositions.add(mPosts.indexOf(post));
            }
        }
        View answerView;
        mAnswerViews.add(new ArrayList<View>());
        for (Integer answerPosition : answersPositions) {
            answerView = adapter.getViewForPosition(answerPosition);
            if (((TextView)answerView.findViewById(R.id.post_comment)).getMovementMethod() instanceof CommentLinkMovementMethod) {
                ((CommentLinkMovementMethod) ((TextView) answerView.findViewById(R.id.post_comment)).getMovementMethod())
                        .setForegroundSpanForParticularLocation(postNumberFrom);
            }
            if (((TextView)answerView.findViewById(R.id.answers)).getMovementMethod() instanceof AnswersLinkMovementMethod) {
                ((AnswersLinkMovementMethod) ((TextView) answerView.findViewById(R.id.answers)).getMovementMethod())
                        .setForegroundSpanForParticularLocation(postNumberFrom);
            }
            //setupCommentTextViewForRecyclerView(answerPosition);
            mAnswerViews.get(mAnswerViews.size() - 1).addAll(Collections.singletonList(answerView));
        }
        setupAnswersLayoutContainer(getResources().getConfiguration());

        //mAnswerLayout.removeAllViews();
        mAnswerList.removeAllViews();
        //mAnswerLayout.addView(answerView);
        ImageView lineDivider;
        for (View view : mAnswerViews.get(mAnswerViews.size() - 1)) {
            mAnswerList.addView(view);
            lineDivider = new ImageView(mActivity);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.height = DeviceUtils.apiIsLollipopOrHigher() ? 2 : 1;
            lineDivider.setLayoutParams(params);
            lineDivider.setPadding(DeviceUtils.apiIsLollipopOrHigher() ? 16 : 8, 0,
                    DeviceUtils.apiIsLollipopOrHigher() ? 16 : 8, 0);
            lineDivider.setImageResource(android.R.color.darker_gray);
            lineDivider.setBackgroundColor(getResources().getColor(R.color.common_background_color));
            mAnswerList.addView(lineDivider);
        }
        mAnswerList.removeViewAt(mAnswerList.getChildCount() - 1);
        if (answerOpened) {
            writeInAnswerScrollState();
        }
        findViewById(R.id.answer_layout_container).setVisibility(View.VISIBLE);
        answerOpened = true;
        adapter.notifySingleView = true;
    }

    private void showPreviousAnswer() {

       // mAnswerLayout.removeAllViews();
        mAnswerList.removeAllViews();
        if (mAnswerViews.size() <= 1) {
            closeAnswerViews();
            return;
        }
        mAnswerViews.remove(mAnswerViews.size() - 1);
        if (((TextView)mAnswerViews.get(mAnswerViews.size() - 1).get(0)
                .findViewById(R.id.answers)).getMovementMethod() instanceof AnswersLinkMovementMethod) {
            ((AnswersLinkMovementMethod) ((TextView)mAnswerViews.get(mAnswerViews.size() - 1).get(0)
                    .findViewById(R.id.answers)).getMovementMethod()).allowActionCancel = true;
        } else if (((TextView)mAnswerViews.get(mAnswerViews.size() - 1).get(mAnswerViews.size() - 1)
                .findViewById(R.id.post_comment)).getMovementMethod() instanceof CommentLinkMovementMethod) {
            ((CommentLinkMovementMethod) ((TextView)mAnswerViews.get(mAnswerViews.size() - 1).get(0)
                    .findViewById(R.id.post_comment)).getMovementMethod()).allowActionCancel = true;
        }

        //mAnswerLayout.addView(mAnswerViews.get(mAnswerViews.size() - 1));
        mAnswerList.addView(mAnswerViews.get(mAnswerViews.size() - 1).get(0));
        //((SaveStateScrollView)mAnswerLayout.getChildAt(0)).scrollToSavedState();
        Log.d(LOG_TAG, "scroll states: " + mAnswersScrollStates.size());
//        mAnswerLayout.findViewById(R.id.answer_layout_scrollview).post(new Runnable() {
//            @Override
//            public void run() {
//
//                mAnswerLayout.findViewById(R.id.answer_layout_scrollview)
//                        .scrollTo(0, mAnswersScrollStates.get(mAnswersScrollStates.size() - 1));
//            }
//        });
        ((SaveStateScrollView)mAnswerLayout.findViewById(R.id.answer_layout_scrollview))
                .scrollToWithGuarantees(0, mAnswersScrollStates.get(mAnswersScrollStates.size() - 1));

        mAnswersScrollStates.remove(mAnswerViews.size() - 1);
    }

    private void closeAnswerViews() {
        answerOpened = false;
        adapter.notifySingleView = false;
        if (DeviceUtils.getApiInt() >= 19) UIUtils.setStatusBarTranslucent(mActivity, false);
        mAnswerViews = new ArrayList<>();
        //mAnswerLayout.removeAllViews();
        mAnswersScrollStates = new ArrayList<>();
        mAnswerList.removeAllViews();
        findViewById(R.id.answer_layout_container).setVisibility(GONE);
    }

    private void setupCommentTextViewForRecyclerView(int position) {
        adapter.notifyItemChanged(position);
    }



}
