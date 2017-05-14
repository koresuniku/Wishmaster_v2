package com.koresuniku.wishmaster.http.threads_api;


import com.koresuniku.wishmaster.http.threads_api.models.ThreadsForPagesJsonSchema;
import com.koresuniku.wishmaster.http.threads_api.models.ThreadsJsonSchema;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ThreadsApiService {

    @GET("/{id}/catalog.json")
    Call<ThreadsJsonSchema> getThreads(@Path("id") String boardId);

    @GET("/{id}/{page}.json")
    Call<ThreadsForPagesJsonSchema> getThreadsForPages(@Path("id") String boardId, @Path("page") String page);

}
