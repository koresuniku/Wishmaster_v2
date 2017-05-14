package com.koresuniku.wishmaster.http.threads_api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ThreadForPage {

    @SerializedName("files_count")
    @Expose
    private String filesCount;
    @SerializedName("posts_count")
    @Expose
    private String postsCount;
    @SerializedName("thread_num")
    @Expose
    private String threadNum;
    @SerializedName("posts")
    @Expose
    private List<Thread> posts = null;

    public String getFilesCount() { return  filesCount; }

    public void setFilesCount(String filesCount) { this.filesCount = filesCount; }

    public String getPostsCount() { return postsCount; }

    public void setPostsCount(String postsCount) { this.postsCount = postsCount; }

    public List<Thread> getPosts() { return posts; }

    public void setPosts(List<Thread> posts) { this.posts = posts; }

    public String getThreadNum() { return threadNum; }

    public void setThreadNum(String threadNum) { this.threadNum = threadNum; }


}
