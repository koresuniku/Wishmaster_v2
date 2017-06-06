package com.koresuniku.wishmaster.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koresuniku.wishmaster.presenter.DataLoader;
import com.koresuniku.wishmaster.presenter.view_interface.LoadDataView;
import com.koresuniku.wishmaster.presenter.PermissionManager;
import com.koresuniku.wishmaster.presenter.view_interface.SaveFileView;
import com.koresuniku.wishmaster.ui.adapter.BoardsExpandableListViewAdapter;
import com.koresuniku.wishmaster.R;
import com.koresuniku.wishmaster.http.boards_api.models.BoardsJsonSchema;
import com.koresuniku.wishmaster.util.Constants;
import com.koresuniku.wishmaster.ui.UiUtils;
import com.koresuniku.wishmaster.util.DeviceUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoadDataView, SaveFileView {
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private MainActivity mActivity;
    private DataLoader mDataLoader;

    private Toolbar toolbar;
    private ExpandableListView mBoardsExpListView;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    private EditText searchEditText;
    public String searchEditTextText = "";
    public Menu mMenu;
    public boolean searchIsShown = false;

    private BoardsJsonSchema mSchema;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_drawer);
        mActivity = this;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupActionBar();
        setupDrawerLayout();
        hideSearchEditText();

        setupDefaultPreferences();
        mDataLoader = new DataLoader(this);
        mDataLoader.loadBoardsData();

        if (!PermissionManager.INSTANCE.checkWriteExternalStoragePermission(this)) {
            Log.d(LOG_TAG, "needa request permission");
            PermissionManager.INSTANCE.requestWriteExternalStoragePermission(this);
        }

    }

    @Override
    public void onBackPressed() {
        Log.d(LOG_TAG, "onBachPressed: ");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        if (searchIsShown) {
            mMenu.findItem(R.id.action_search).setVisible(true);
            mMenu.findItem(R.id.action_settings).setVisible(true);
            searchEditTextText = "";
            hideSearchEditText();
            return;
        }

        super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        if (searchIsShown) {
            mMenu.findItem(R.id.action_search).setVisible(false);
            mMenu.findItem(R.id.action_settings).setVisible(false);
        }

        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(LOG_TAG, "onConfigurationChanged: ");
        setupActionBar();
        setupDrawerLayout();

        if (searchIsShown) showSearchEditText();
        else hideSearchEditText();
    }


    private void setupActionBar() {
        ((FrameLayout)findViewById(R.id.main_toolbar_container)).removeView(toolbar);
        toolbar = (Toolbar) getLayoutInflater()
                .inflate(R.layout.activity_main_toolbar_layout, null, false)
                .findViewById(R.id.toolbar);
        ((FrameLayout)findViewById(R.id.main_toolbar_container)).addView(toolbar);
        findViewById(R.id.main_toolbar_container).setLayoutParams(
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ?
                                (int) getResources().getDimension(R.dimen.action_bar_height_vertical) :
                                (int) getResources().getDimension(R.dimen.action_bar_height_horizontal)));
        toolbar.inflateMenu(R.menu.main);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        UiUtils.setCurrentThemeColorFilterForImageView(this,
                ((ImageView)toolbar.findViewById(R.id.logo)));
    }

    private void setupDrawerLayout() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void hideSearchEditText() {
        searchIsShown = false;

        findViewById(R.id.logo_container).setVisibility(View.VISIBLE);
        findViewById(R.id.search_container).setVisibility(View.GONE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.setToolbarNavigationClickListener(null);
    }

    private void showSearchEditText() {
        searchIsShown = true;

        findViewById(R.id.logo_container).setVisibility(View.GONE);
        findViewById(R.id.search_container).setVisibility(View.VISIBLE);

        searchEditText = (EditText) findViewById(R.id.search_edittext);

        toggle.setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        findViewById(R.id.search_delete_imageview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
            }
        });

        searchEditText.setText(searchEditTextText);

        if (searchEditTextText.length() > 0) {
            findViewById(R.id.search_delete_imageview).setVisibility(View.VISIBLE);
        } else findViewById(R.id.search_delete_imageview).setVisibility(View.GONE);

        searchEditText.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI|EditorInfo.IME_ACTION_DONE);
        (new Handler()).postDelayed(new Runnable() {

            public void run() {
                searchEditText.dispatchTouchEvent(
                        MotionEvent.obtain(SystemClock.uptimeMillis(),
                                SystemClock.uptimeMillis(),
                                MotionEvent.ACTION_DOWN , 0, 0, 0));
                searchEditText.dispatchTouchEvent(
                        MotionEvent.obtain(SystemClock.uptimeMillis(),
                                SystemClock.uptimeMillis(),
                                MotionEvent.ACTION_UP , 0, 0, 0));

            }
        }, 30);

        searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(LOG_TAG, "hasFocus: " + hasFocus);
                Log.d(LOG_TAG, "mActivity is null: " + (mActivity == null));
                if (!hasFocus) {
                    hideKeyboard(v);
                } else {
                    Log.d(LOG_TAG, "eddittexttextlength: " + searchEditText.length());
                    searchEditText.post(new Runnable() {
                        @Override
                        public void run() {
                            searchEditText.setSelection(searchEditTextText.length());
                        }
                    });
                }
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(LOG_TAG, "onTextChanged");

                if (count > 0) findViewById(R.id.search_delete_imageview).setVisibility(View.VISIBLE);
                else findViewById(R.id.search_delete_imageview).setVisibility(View.GONE);

                searchEditTextText = String.valueOf(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.d(LOG_TAG, "actionDone");

                    Intent intent = new Intent(mActivity, ThreadsActivity.class);

                    intent.putExtra(Constants.BOARD_ID, searchEditTextText);
                    intent.putExtra(Constants.BOARD_NAME, "");

                    if (DeviceUtils.sdkIsLollipopOrHigher()) {
                        mActivity.startActivity(intent);
                        mActivity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    } else mActivity.startActivity(intent);

                    searchEditText.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (searchIsShown) {
                                mMenu.findItem(R.id.action_search).setVisible(true);
                                mMenu.findItem(R.id.action_settings).setVisible(true);
                                searchEditTextText = "";
                                hideSearchEditText();
                            }

                        }
                    }, 500);

                }
                return false;
            }
        });


    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager
                = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == R.id.action_search) {
            Log.d(LOG_TAG, "actiob search clicked");
            mMenu.findItem(R.id.action_search).setVisible(false);
            mMenu.findItem(R.id.action_settings).setVisible(false);
            showSearchEditText();
        }
        if (id == R.id.home) {
            Log.d(LOG_TAG, "home clicked");
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupExpandableListView(BoardsJsonSchema schema) {
        BoardsExpandableListViewAdapter adapter = new BoardsExpandableListViewAdapter(this, schema);
        mBoardsExpListView = (ExpandableListView) findViewById(R.id.boards_exp_listview);
        mBoardsExpListView.setGroupIndicator(null);
        mBoardsExpListView.setAdapter(adapter);
    }

    private void setupDefaultPreferences() {
        SharedPreferences preferences = this.getPreferences(Context.MODE_PRIVATE);
        int defaultValue = Integer.parseInt(getResources().getString(R.string.sp_cache_size_default_value));
        preferences.getInt(getString(R.string.sp_cache_size_code), defaultValue);
    }

    @Override
    public void onDataLoaded(List schema) {
        mSchema = (BoardsJsonSchema) schema.get(0);
        setupExpandableListView(mSchema);
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public void showProgressBar() {

    }

    @NotNull
    @Override
    public Context getContext() {
        return this.getContext();
    }

}
