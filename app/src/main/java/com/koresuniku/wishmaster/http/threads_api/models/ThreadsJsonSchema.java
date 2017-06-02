
package com.koresuniku.wishmaster.http.threads_api.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.koresuniku.wishmaster.http.IBaseJsonSchema;

public class ThreadsJsonSchema implements IBaseJsonSchema {

    @SerializedName("BoardName")
    @Expose
    private String boardName;
    @SerializedName("default_name")
    @Expose
    private String defaultName;
    @SerializedName("threads")
    @Expose
    private List<Thread> threads = null;

    protected ThreadsJsonSchema(Parcel in) {
        defaultName = in.readString();
    }

    public String getBoardName() { return  boardName; }

    public void setBoardName(String boardName) { this.boardName = boardName; }

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public List<Thread> getThreads() {
        return threads;
    }

    public void setThreads(List<Thread> threads) {
        this.threads = threads;
    }

}
