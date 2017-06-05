package com.koresuniku.wishmaster.presenter;

import android.util.Log;

import com.koresuniku.wishmaster.http.HttpClient;
import com.koresuniku.wishmaster.http.boards_api.models.BoardsJsonSchema;
import com.koresuniku.wishmaster.http.single_thread_api.models.Post;
import com.koresuniku.wishmaster.http.threads_api.ThreadsForPagesAsyncTask;
import com.koresuniku.wishmaster.http.threads_api.models.ThreadsJsonSchema;
import com.koresuniku.wishmaster.presenter.view_interface.LoadDataView;
import com.koresuniku.wishmaster.ui.activity.ThreadsActivity;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DataLoader {
    private final String LOG_TAG = DataLoader.class.getSimpleName();

    private LoadDataView view;

    public DataLoader(LoadDataView view) {
        this.view = view;
    }

    public void collectBoardsData() {
        Call<BoardsJsonSchema> call = HttpClient.boardsService.getBoards("get_boards");
        call.enqueue(new Callback<BoardsJsonSchema>() {
            @Override
            public void onResponse(Call<BoardsJsonSchema> call, Response<BoardsJsonSchema> response) {
                Log.i(LOG_TAG, "onResponse: " + response.body().getAdults());
                view.onDataLoaded(Collections.singletonList(response.body()));
            }

            @Override
            public void onFailure(Call<BoardsJsonSchema> call, Throwable t) {
                Log.d(LOG_TAG, "onFailure: ");
                t.printStackTrace();
            }
        });
    }

    public void loadData(String boardId) {
        view.showProgressBar();
        if (boardId.equals("d") || boardId.equals("d")) {
            new ThreadsForPagesAsyncTask((ThreadsActivity)view.getActivity(), boardId).execute();
        } else {
            Call<ThreadsJsonSchema> call = HttpClient.threadsService.getThreads(boardId);
            call.enqueue(new Callback<ThreadsJsonSchema>() {
                @Override
                public void onResponse(Call<ThreadsJsonSchema> call,
                                       Response<ThreadsJsonSchema> response) {
                    view.onDataLoaded(Collections.singletonList(response.body()));
                }

                @Override
                public void onFailure(Call<ThreadsJsonSchema> call, Throwable t) {
                    Log.d(LOG_TAG, "onFailure: ");
                    t.printStackTrace();
                }
            });
        }
    }

    public void loadData(String boardId, String threadNumber) {
        Log.d(LOG_TAG, "loadData:");
        view.showProgressBar();

        Call<List<Post>> call =
                HttpClient.singleThreadService.getPosts("get_thread", boardId, threadNumber, 0);
        call.enqueue(new Callback<List<Post>>() {
            @Override
            public void onResponse(Call<List<Post>> call, Response<List<Post>> response) {
                view.onDataLoaded(response.body());
                Log.d(LOG_TAG, "data loaded:");

            }

            @Override
            public void onFailure(Call<List<Post>> call, Throwable t) {
                Log.d(LOG_TAG, "failure data loading");
                t.printStackTrace();
            }
        });
    }


}
