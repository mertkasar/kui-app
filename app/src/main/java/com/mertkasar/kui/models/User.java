package com.mertkasar.kui.models;


import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String name;
    public String email;

    public User() {

    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User(FirebaseUser user) {
        this.name = user.getDisplayName();
        this.email = user.getEmail();
    }
}
