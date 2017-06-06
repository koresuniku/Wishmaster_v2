package com.koresuniku.wishmaster.presenter.view_interface

import android.app.Activity
import android.widget.ProgressBar

import com.koresuniku.wishmaster.http.IBaseJsonSchema

interface LoadDataView {

    fun onDataLoaded(schema: List<IBaseJsonSchema>)

    val activity: Activity

    fun showProgressBar()
}
