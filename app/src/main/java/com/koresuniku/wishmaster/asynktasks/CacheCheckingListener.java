package com.koresuniku.wishmaster.asynktasks;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.koresuniku.wishmaster.R;
import com.koresuniku.wishmaster.utils.CacheUtils;
import com.koresuniku.wishmaster.utils.IOUtils;

public class CacheCheckingListener extends AsyncTask<Void, Void, Void> {
    private final String LOG_TAG = CacheCheckingListener.class.getSimpleName();

    private Activity mActivity;
    private SharedPreferences mSharedPreferences;
    private long allowedCacheSize;

    public CacheCheckingListener(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    protected void onPreExecute() {
        mSharedPreferences = mActivity.getPreferences(Context.MODE_PRIVATE);
        allowedCacheSize = mSharedPreferences.getLong(
                mActivity.getString(R.string.sp_cache_size_code), IOUtils.convertMbToBytes(1));
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (true) {
            try {
                java.lang.Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

//            if (IOUtils.getDirSize(mActivity.getCacheDir(), 0) > allowedCacheSize) {
//                Log.d(LOG_TAG, "cache size before: " + IOUtils.getDirSize(mActivity.getCacheDir(), 0));
//                //CacheUtils.deleteDir(mActivity.getCacheDir());
//                Log.d(LOG_TAG, "cache size after: " + IOUtils.getDirSize(mActivity.getCacheDir(), 0));
//                //Log.d(LOG_TAG, "cache exceeded " + allowedCacheSize + ", though cacheDir deleted");
//            }
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
