package com.mertkasar.kui.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.Calendar;
import java.util.Locale;

public class Course {
    public String title;
    public String description;
    public Integer question_count;
    public Object created_at;

    public Course() {

    }

    public Course(String title, String description) {
        this.title = title;
        this.description = description;

        created_at = ServerValue.TIMESTAMP;
        question_count = 0;
    }

    @Exclude
    public String getEnrollKey() {
        if (!(created_at instanceof Long))
            return null;

        // Convert timestamp to Base36
        return Long.toString((long) created_at, 36);
    }
}
