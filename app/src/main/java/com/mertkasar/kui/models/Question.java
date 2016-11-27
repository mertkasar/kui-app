package com.mertkasar.kui.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class Question {
    public String title;
    public String description;
    public HashMap<String, String> options;
    public Integer answer_count;
    public Integer correct_count;

    public Question() {
    }

    public Question(String title, String description, HashMap<String, String> options) {
        this.title = title;
        this.description = description;
        this.options = options;

        answer_count  = 0;
        correct_count = 0;
    }
}
