package com.mertkasar.kui.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Question {
    public String title;
    public String description;
    //TODO: Random generated enroll code

    public Question() {
    }

    public Question(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
