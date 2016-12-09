package com.mertkasar.kui.models;

import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

public class Course {
    public String title;
    public String description;
    public String owner;
    public Boolean is_public;

    public Object created_at;
    public Integer question_count;
    public Integer subscriber_count;

    public Course() {

    }

    public Course(String owner, String title, String description, boolean isPublic) {
        this.owner = owner;
        this.title = title;
        this.description = description;
        this.is_public = isPublic;

        created_at = ServerValue.TIMESTAMP;
        question_count = 0;
        subscriber_count = 0;
    }

    @Exclude
    public String getSubsKey() {
        if (!(created_at instanceof Long))
            return null;

        // Convert timestamp to Base36
        return Long.toString((long) created_at, 36);
    }
}
