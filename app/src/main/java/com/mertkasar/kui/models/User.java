package com.mertkasar.kui.models;


import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

@IgnoreExtraProperties
public class User {
    public String name;
    public String email;

    public HashMap<String, Boolean> courses_enrolled;
    public HashMap<String, Boolean> courses_created;

    public User() {

    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;

        this.courses_enrolled = new HashMap<>();
        this.courses_created = new HashMap<>();
    }

    @Exclude
    public void addCoursesEnrolled(final String courseKey) {
        courses_enrolled.put(courseKey, true);
    }

    @Exclude
    public void addCoursesCreated(final String courseKey) {
        courses_created.put(courseKey, true);
    }
}
