package com.mertkasar.kui.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Answer {
    public String owner;
    public String choice;
    public Boolean is_correct;

    public Answer() {
    }

    public Answer(String owner, String choice) {
        this.owner = owner;
        this.choice = choice;

        if (choice.endsWith("_0")) {
            is_correct = true;
        } else
            is_correct = false;
    }
}
