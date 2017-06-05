package com.koresuniku.wishmaster.presenter.view_interface

import android.app.Activity
import android.widget.ProgressBar

import com.koresuniku.wishmaster.http.IBaseJsonSchema

interface LoadDataView<in T : IBaseJsonSchema> {

    fun onDataLoaded(schema: List<T>)

    val activity: Activity

    fun showProgressBar()
}
