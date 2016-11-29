package com.mertkasar.kui.models;


import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public enum Type{
        STUDENT,
        TEACHER
    }

    String name;
    String email;
    Type type;

    public User(){

    }

    public User(String name, String email, Type type){
        this.name = name;
        this.email = email;
        this.type = type;
    }
}
