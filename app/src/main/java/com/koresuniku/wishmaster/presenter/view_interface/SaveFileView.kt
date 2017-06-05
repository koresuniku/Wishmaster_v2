package com.koresuniku.wishmaster.presenter.view_interface

import android.app.Activity
import android.content.Context

interface SaveFileView {
    fun getContext(): Context

    fun getActivity(): Activity
}