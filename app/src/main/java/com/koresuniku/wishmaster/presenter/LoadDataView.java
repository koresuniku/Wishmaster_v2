package com.koresuniku.wishmaster.presenter;

import android.app.Activity;
import android.widget.ProgressBar;

import com.koresuniku.wishmaster.http.IBaseJsonSchema;

import java.util.List;

public interface LoadDataView<T extends IBaseJsonSchema> {

    void onDataLoaded(List<T> schema);

    Activity getActivity();

    void showProgressBar();
}
