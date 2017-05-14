package com.koresuniku.wishmaster.http.boards_api;


import com.koresuniku.wishmaster.http.boards_api.models.BoardsJsonSchema;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface BoardsApiService {

    @GET("/makaba/mobile.fcgi")
   Call<BoardsJsonSchema> getBoards(@Query("task") String task);
}
