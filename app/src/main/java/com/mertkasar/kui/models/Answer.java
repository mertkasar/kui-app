package com.mertkasar.kui.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Answer {
    public String choice;
    public Boolean is_correct;

    public Answer() {
    }

    public Answer(String choice) {
        this.choice = choice;

        if (choice.endsWith("_0")){
            is_correct = true;
        } else
            is_correct = false;
    }
}
