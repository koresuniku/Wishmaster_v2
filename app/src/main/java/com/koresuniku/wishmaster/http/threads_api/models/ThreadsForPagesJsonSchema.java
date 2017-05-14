package com.koresuniku.wishmaster.http.threads_api.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ThreadsForPagesJsonSchema {

    @SerializedName("pages")
    @Expose
    private List pages;
    @SerializedName("default_name")
    @Expose
    private String defaultName;
    @SerializedName("threads")
    @Expose
    private List<ThreadForPage> threads = null;

    protected ThreadsForPagesJsonSchema(Parcel in) {
        defaultName = in.readString();
    }


    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public List<ThreadForPage> getThreads() {
        return threads;
    }

    public void setThreads(List<ThreadForPage> threads) {
        this.threads = threads;
    }

    public List<Integer> getPages() { return pages; }

    public void setPages(List<Integer> pages) { this.pages = pages; }

}
