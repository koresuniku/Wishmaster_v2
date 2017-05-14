package com.koresuniku.wishmaster.http.threads_api;

import android.os.AsyncTask;
import android.util.Log;

import com.koresuniku.wishmaster.activities.ThreadsActivity;
import com.koresuniku.wishmaster.http.threads_api.models.Thread;
import com.koresuniku.wishmaster.http.threads_api.models.ThreadForPage;
import com.koresuniku.wishmaster.http.threads_api.models.ThreadsForPagesJsonSchema;
import com.koresuniku.wishmaster.http.threads_api.models.ThreadsJsonSchema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ThreadsForPagesAsynktask extends AsyncTask<Void, Void, Void> {
    private final String LOG_TAG = ThreadsForPagesAsynktask.class.getSimpleName();

    private ThreadsActivity mActivity;
    private String mBoardId;

    private int pagesCount;
    private List<Thread> threadList;

    public ThreadsForPagesAsynktask(ThreadsActivity activity, String boardId) {
        super();
        mActivity = activity;
        mBoardId = boardId;
        threadList = new ArrayList<>();
    }

    private void getThreadsForThreadList(Response<ThreadsForPagesJsonSchema> response) {
        Thread thread;
        ThreadForPage threadForPage;
        for (int i = 0; i < response.body().getThreads().size(); i++) {
            thread = response.body().getThreads().get(i).getPosts().get(0);
            threadForPage = response.body().getThreads().get(i);

            thread.setNum(threadForPage.getThreadNum());
            thread.setFilesCount(threadForPage.getFilesCount());
            thread.setPostsCount(threadForPage.getPostsCount());

            threadList.add(thread);
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        Call<ThreadsJsonSchema> callMainSchema = mActivity.service.getThreads(mBoardId);
        try {
            mActivity.mSchema = callMainSchema.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Call<ThreadsForPagesJsonSchema> call = mActivity.service.getThreadsForPages(mBoardId, "index");

        call.enqueue(new Callback<ThreadsForPagesJsonSchema>() {
            @Override
            public void onResponse(Call<ThreadsForPagesJsonSchema> call, Response<ThreadsForPagesJsonSchema> response) {
                pagesCount = response.body().getPages().size();
                Log.d(LOG_TAG, "pagesCount: " + pagesCount);

                getThreadsForThreadList(response);

                for (int i = 1; i < pagesCount - 1; i++) {
                    final int iCopy = i;
                    Log.d(LOG_TAG, "loading page " + i);
                    final Call<ThreadsForPagesJsonSchema> callForPage
                            = mActivity.service.getThreadsForPages(mBoardId, String.valueOf(i));
                        callForPage.enqueue(new Callback<ThreadsForPagesJsonSchema>() {
                            @Override
                            public void onResponse(Call<ThreadsForPagesJsonSchema> call, final Response<ThreadsForPagesJsonSchema> response) {
                                new AsyncTask<Void, Void, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        getThreadsForThreadList(response);
                                        if (iCopy == pagesCount - 2) {
                                            finish();
                                        }
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void aVoid) {

                                    }
                                }.execute();
                            }

                            @Override
                            public void onFailure(Call<ThreadsForPagesJsonSchema> call, Throwable t) {

                            }
                        });
                }
            }

            @Override
            public void onFailure(Call<ThreadsForPagesJsonSchema> call, Throwable t) {
                Log.d(LOG_TAG, "failure with d");
            }
        });
        return null;
    }

    private void finish() {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(LOG_TAG, "finish: ");
                mActivity.mSchema.setThreads(threadList);
                mActivity.postLoadData();
            }
        });
    }

    @Override
    protected void onPostExecute(Void aVoid) {

    }
}
