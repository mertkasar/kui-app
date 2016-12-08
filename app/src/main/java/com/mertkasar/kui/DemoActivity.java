package com.mertkasar.kui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mertkasar.kui.core.App;
import com.mertkasar.kui.core.Database;
import com.mertkasar.kui.models.Answer;
import com.mertkasar.kui.models.Course;
import com.mertkasar.kui.models.Question;
import com.mertkasar.kui.models.User;

import java.util.HashMap;

public class DemoActivity extends AppCompatActivity {
    public static final String TAG = DemoActivity.class.getSimpleName();

    private final String USER_KEY = "-AbC68u";
    private final String COURSE_KEY = "-KYQFLU24sjQCsgYptzw";
    private final String QUESTION_KEY = "";
    private final String ANSWER_KEY = "";
    private final String SUBS_KEY = "IWFGU7UR";

    private App app;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);

        app = App.getInstance();
        db = Database.getInstance();
    }

    public void onCreateUButtonClick(View view) {
        User newUser = new User("Mert Kasar", "mertkasar93@gmail.com");
        db.createUser(USER_KEY, newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DemoActivity.this, R.string.toast_create_user, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onPostCButtonClick(View view) {
        Course newCourse = new Course("MATH103", "Discrete Mathematics", USER_KEY);
        db.createCourse(newCourse).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DemoActivity.this, R.string.toast_create_course, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onPostQButtonClick(View view) {
        if (!app.isConnected()) {
            Toast.makeText(this, R.string.toast_not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, String> options = new HashMap<>();
        options.put("option_0", "13");
        options.put("option_1", "9");
        options.put("option_2", "1");
        options.put("option_3", "6");
        final Question newQuestion = new Question("Fibonacci?", "What is the next number in the following sequence: 1 1 2 3 5 8 ?", options);

        db.createQuestion(COURSE_KEY, newQuestion).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DemoActivity.this, R.string.toast_create_question, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onPostAButtonClick(View view) {
        if (!app.isConnected()) {
            Toast.makeText(this, R.string.toast_not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        final Answer newAnswer = new Answer("option_0");

        db.createAnswer(COURSE_KEY, QUESTION_KEY, newAnswer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                String status = newAnswer.is_correct ? "correct" : "false";
                Toast.makeText(DemoActivity.this, "Your answer was " + status, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onGetUButtonClick(View view) {
        db.retrieveUser(USER_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Log.d(TAG, dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onGetCButtonClick(View view) {
        db.retrieveCourse(COURSE_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Course course = dataSnapshot.getValue(Course.class);
                Log.d(TAG, dataSnapshot.toString());
                Log.d(TAG, "subs_key: " + course.getSubsKey());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onGetQButtonClick(View view) {
        db.retrieveQuestion(COURSE_KEY, QUESTION_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Question question = dataSnapshot.getValue(Question.class);
                Log.d(TAG, dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onGetAButtonClick(View view) {
        db.retrieveAnswer(QUESTION_KEY, ANSWER_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Answer answer = dataSnapshot.getValue(Answer.class);
                Log.d(TAG, dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onInternetButtonClick(View view) {
        if (app.isConnected()) Log.d(TAG, "onInternetButtonClick: Connected");
        else Log.d(TAG, "onInternetButtonClick: Disconnected");
    }

    public void onSubsButtonClick(View view) {
        if (!app.isConnected()) {
            Toast.makeText(this, R.string.toast_not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        db.getSubscribedCourse(USER_KEY, COURSE_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(DemoActivity.this, R.string.toast_already_subscribed, Toast.LENGTH_SHORT).show();
                    return;
                }

                db.subscribeUser(USER_KEY, COURSE_KEY).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(DemoActivity.this, R.string.toast_subscribe, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onKSubsButtonClick(View view) {
        if (!app.isConnected()) {
            Toast.makeText(this, R.string.toast_not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        db.getCourseBySubsKey(SUBS_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Toast.makeText(DemoActivity.this, R.string.toast_wrong_subscription_key, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (dataSnapshot.getChildrenCount() != 1) {
                    Log.e(Database.TAG, "Failed to subscribe: Multiple subscription key " + SUBS_KEY);
                    return;
                }

                DataSnapshot courseSnapshot = dataSnapshot.getChildren().iterator().next();
                final String courseKey = courseSnapshot.getKey();

                db.getSubscribedCourse(USER_KEY, courseKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(DemoActivity.this, R.string.toast_already_subscribed, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        db.subscribeUser(USER_KEY, courseKey).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(DemoActivity.this, R.string.toast_subscribe, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onUnsubsButtonClick(View view){
        if (!app.isConnected()) {
            Toast.makeText(DemoActivity.this, R.string.toast_not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        db.unsubscribeUser(USER_KEY, COURSE_KEY).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(DemoActivity.this, R.string.toast_unsubscribed, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
