package com.mertkasar.kui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mertkasar.kui.core.Database;
import com.mertkasar.kui.models.Answer;
import com.mertkasar.kui.models.Course;
import com.mertkasar.kui.models.Question;
import com.mertkasar.kui.models.User;

import java.util.HashMap;

public class DemoActivity extends AppCompatActivity {
    private final String TAG = DemoActivity.class.getSimpleName();

    private final String USER_KEY = "-AbC68u";
    private final String COURSE_KEY = "-KXfg48qX_zPZrn9gdC5";
    private final String QUESTION_KEY = "-KXgEJNNTrZPXawBquDf";

    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        db = Database.getInstance();

        db.refCourses.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Course course = dataSnapshot.getValue(Course.class);
                Log.d(TAG, "onChildAdded: " + course.getEnrollKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onCreateUButtonClick(View view) {
        User newUser = new User("Mert Kasar", "mertkasar93@gmail.com", User.Type.STUDENT);
        db.createNewUser(USER_KEY, newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DemoActivity.this, R.string.toast_create_user, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onPostCButtonClick(View view) {
        Course newCourse = new Course("MATH103", "Discrete Mathematics");
        db.createNewCourse(newCourse).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DemoActivity.this, R.string.toast_create_course, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onPostQButtonClick(View view) {
        HashMap<String, String> options = new HashMap<>();
        options.put("option_0", "13");
        options.put("option_1", "9");
        options.put("option_2", "1");
        options.put("option_3", "6");
        final Question newQuestion = new Question("Fibonacci?", "What is the next number in the following sequence: 1 1 2 3 5 8 ?", options);

        db.createNewQuestion(COURSE_KEY, newQuestion).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DemoActivity.this, R.string.toast_create_question, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onPostAButtonClick(View view) {
        final Answer newAnswer = new Answer("option_0");

        db.createNewAnswer(COURSE_KEY, QUESTION_KEY, newAnswer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String status = newAnswer.is_correct ? "correct" : "false";
                Toast.makeText(DemoActivity.this, "Your answer was " + status, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
