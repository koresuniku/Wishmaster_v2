
package com.koresuniku.wishmaster.http.threads_api.models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Thread {

    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("files")
    @Expose
    private List<Files> files = null;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("num")
    @Expose
    private String num;
    @SerializedName("files_count")
    @Expose
    private String filesCount;
    @SerializedName("posts_count")
    @Expose
    private String postsCount;
    @SerializedName("trip")
    @Expose
    private String trip;
    @SerializedName("subject")
    @Expose
    private String subject;

    public String getSubject() { return subject; }

    public void setSubject(String subject) { this.subject = subject; }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Files> getFiles() {
        return files;
    }

    public void setFiles(List<Files> files) {
        this.files = files;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getFilesCount() {
        return filesCount;
    }

    public void setFilesCount(String filesCount) {
        this.filesCount = filesCount;
    }

    public String getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(String postsCount) {
        this.postsCount = postsCount;
    }

    public String getTrip() {
        return trip;
    }

    public void setTrip(String trip) {
        this.trip = trip;
    }

}
