package com.mertkasar.kui.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Answer {
    public String choice;

    public Answer() {
    }

    public Answer(String choice) {
        this.choice = choice;
    }
}
