package com.koresuniku.wishmaster.http.single_thread_api.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.koresuniku.wishmaster.http.threads_api.models.Files;

import java.util.List;

public class Post {
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("num")
    @Expose
    private String num;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("trip")
    @Expose
    private String trip;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("op")
    @Expose
    private String op;
    @SerializedName("files")
    @Expose
    private List<Files> files = null;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTrip() {
        return trip;
    }

    public void setTrip(String trip) {
        this.trip = trip;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public List<Files> getFiles() {
        return files;
    }

    public void setFiles(List<Files> files) {
        this.files = files;
    }

}
