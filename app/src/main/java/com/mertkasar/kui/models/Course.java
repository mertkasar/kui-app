package com.mertkasar.kui.models;

public class Course {
    public String title;
    public String description;
    //TODO: Unique enroll key
    public Integer question_count;

    public Course() {

    }

    public Course(String title, String description){
        this.title = title;
        this.description = description;

        question_count = 0;
    }
}
