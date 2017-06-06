package com.koresuniku.wishmaster.presenter

import android.util.Log

import com.koresuniku.wishmaster.http.HttpClient
import com.koresuniku.wishmaster.http.boards_api.models.BoardsJsonSchema
import com.koresuniku.wishmaster.http.single_thread_api.models.Post
import com.koresuniku.wishmaster.http.threads_api.ThreadsForPagesAsyncTask
import com.koresuniku.wishmaster.http.threads_api.models.ThreadsJsonSchema
import com.koresuniku.wishmaster.presenter.view_interface.LoadDataView
import com.koresuniku.wishmaster.ui.activity.ThreadsActivity

import java.util.Collections

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DataLoader(private val view: LoadDataView) {
    private val LOG_TAG = DataLoader::class.java.simpleName

    fun loadBoardsData() {
        val call = HttpClient.boardsService.getBoards("get_boards")
        call.enqueue(object : Callback<BoardsJsonSchema> {
            override fun onResponse(call: Call<BoardsJsonSchema>, response: Response<BoardsJsonSchema>) {
                Log.i(LOG_TAG, "onResponse: " + response.body().adults)
                view.onDataLoaded(listOf(response.body()))
            }

            override fun onFailure(call: Call<BoardsJsonSchema>, t: Throwable) {
                Log.d(LOG_TAG, "onFailure: ")
                t.printStackTrace()
            }
        })
    }

    fun loadThreadsData(boardId: String) {
        view.showProgressBar()
        if (boardId == "d" || boardId == "d") {
            ThreadsForPagesAsyncTask(view.activity as ThreadsActivity, boardId).execute()
        } else {
            val call = HttpClient.threadsService.getThreads(boardId)
            call.enqueue(object : Callback<ThreadsJsonSchema> {
                override fun onResponse(call: Call<ThreadsJsonSchema>,
                                        response: Response<ThreadsJsonSchema>) {
                    view.onDataLoaded(listOf(response.body()))
                }

                override fun onFailure(call: Call<ThreadsJsonSchema>, t: Throwable) {
                    Log.d(LOG_TAG, "onFailure: ")
                    t.printStackTrace()
                }
            })
        }
    }

    fun loadSingleThreadData(boardId: String, threadNumber: String) {
        Log.d(LOG_TAG, "loadSingleThreadData:")
        view.showProgressBar()

        val call = HttpClient.singleThreadService.getPosts("get_thread", boardId, threadNumber, 0)
        call.enqueue(object : Callback<List<Post>> {
            override fun onResponse(call: Call<List<Post>>, response: Response<List<Post>>) {
                view.onDataLoaded(response.body())
                Log.d(LOG_TAG, "data loaded:")

            }

            override fun onFailure(call: Call<List<Post>>, t: Throwable) {
                Log.d(LOG_TAG, "failure data loading")
                t.printStackTrace()
            }
        })
    }


}
