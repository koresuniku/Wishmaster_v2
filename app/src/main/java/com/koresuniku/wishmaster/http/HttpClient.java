package com.koresuniku.wishmaster.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koresuniku.wishmaster.http.boards_api.BoardsApiService;
import com.koresuniku.wishmaster.http.single_thread_api.SingleThreadApiService;
import com.koresuniku.wishmaster.http.threads_api.ThreadsApiService;
import com.koresuniku.wishmaster.util.Constants;

import org.jetbrains.annotations.Contract;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HttpClient {

    public static OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(5000, TimeUnit.SECONDS)
                //.proxy(setProxy())
                .readTimeout(10000, TimeUnit.SECONDS).build();

    public static Gson gson = new GsonBuilder().create();

    public static Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(Constants.DVACH_BASE_URL)
                .client(client)
                .build();

    public static BoardsApiService boardsService = retrofit.create(BoardsApiService.class);
    public static ThreadsApiService threadsService = retrofit.create(ThreadsApiService.class);
    public static SingleThreadApiService singleThreadService = retrofit.create(SingleThreadApiService.class);



}
