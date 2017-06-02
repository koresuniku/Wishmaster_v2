package com.koresuniku.wishmaster.presenter;

import android.app.Activity;

import com.koresuniku.wishmaster.http.IBaseJsonSchema;

import java.util.List;

public interface ILoadData<T extends IBaseJsonSchema> {

    void onDataLoaded(List<T> schema);

    Activity getActivity();
}
