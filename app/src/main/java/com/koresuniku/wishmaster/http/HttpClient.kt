package com.koresuniku.wishmaster.http

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.koresuniku.wishmaster.http.boards_api.BoardsApiService
import com.koresuniku.wishmaster.http.single_thread_api.SingleThreadApiService
import com.koresuniku.wishmaster.http.threads_api.ThreadsApiService
import com.koresuniku.wishmaster.util.Constants

import org.jetbrains.annotations.Contract

import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.InputStream

object HttpClient {

    val client = OkHttpClient.Builder()
            .connectTimeout(5000, TimeUnit.SECONDS)
            //.proxy(setProxy())
            .readTimeout(10000, TimeUnit.SECONDS).build()!!

    val gson = GsonBuilder().create()!!

    val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl(Constants.DVACH_BASE_URL)
            .client(client)
            .build()!!

    val boardsService = retrofit.create(BoardsApiService::class.java)!!
    val threadsService = retrofit.create(ThreadsApiService::class.java)!!
    val singleThreadService = retrofit.create(SingleThreadApiService::class.java)!!

    fun getResponseFromUrl(url: String): ResponseBody? {
        val request = Request.Builder().url(url).build()
        val response = OkHttpClient().newCall(request).execute()
        val body = response.body()
        // body.toString() returns a string representing the object and not the body itself, probably
        // kotlins fault when using third party libraries. Use byteStream() and convert it to a String
        return body
    }

}
