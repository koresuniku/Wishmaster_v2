package com.koresuniku.wishmaster.http.single_thread_api;

import com.koresuniku.wishmaster.http.single_thread_api.models.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SingleThreadApiService {

    @GET("/makaba/mobile.fcgi")
    Call<List<Post>> getPosts(@Query("task") String task,
                              @Query("board") String board,
                              @Query("thread") String thread,
                              @Query("post") int post);
}
