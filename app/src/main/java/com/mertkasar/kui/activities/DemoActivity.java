package com.mertkasar.kui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mertkasar.kui.R;
import com.mertkasar.kui.core.App;
import com.mertkasar.kui.core.Database;
import com.mertkasar.kui.models.Answer;
import com.mertkasar.kui.models.User;

public class DemoActivity extends AppCompatActivity {
    public static final String TAG = DemoActivity.class.getSimpleName();

    //Request codes
    public final int RC_NEW_COURSE = 1;
    public final int RC_NEW_QUESTION = 2;

    private final String USER_KEY = "-AbC68u";
    private final String COURSE_KEY = "-KYQFLU24sjQCsgYptzw";
    private final String QUESTION_KEY = "-KYfEN4nhc9rbFGnM0ro";
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_NEW_COURSE:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, R.string.toast_create_course_success, Toast.LENGTH_SHORT).show();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, R.string.toast_create_course_failure, Toast.LENGTH_SHORT).show();
                }
                break;
            case RC_NEW_QUESTION:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, R.string.toast_create_question_success, Toast.LENGTH_SHORT).show();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, R.string.toast_create_question_failure, Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
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
        Intent newPostIntent = new Intent(this, NewCourseActivity.class);
        newPostIntent.putExtra("EXTRA_USER_KEY", USER_KEY);
        startActivityForResult(newPostIntent, RC_NEW_COURSE);
    }

    public void onPostQButtonClick(View view) {
        Intent newPostIntent = new Intent(this, NewQuestionActivity.class);
        newPostIntent.putExtra("EXTRA_QUESTION_KEY", COURSE_KEY);
        startActivityForResult(newPostIntent, RC_NEW_QUESTION);
    }

    public void onPostAButtonClick(View view) {
//        if (!app.isConnected()) {
//            Toast.makeText(this, R.string.toast_not_connected, Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        final Answer newAnswer = new Answer("option_0");
//
//        db.createAnswer(QUESTION_KEY, newAnswer).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                String status = newAnswer.is_correct ? "correct" : "false";
//                Toast.makeText(DemoActivity.this, "Your answer was " + status, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    public void onGetUButtonClick(View view) {
        db.getUserByKey(USER_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
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
        Intent newPostIntent = new Intent(this, CourseDetailActivity.class);
        newPostIntent.putExtra("EXTRA_QUESTION_KEY", COURSE_KEY);
        startActivity(newPostIntent);
    }

    public void onGetQButtonClick(View view) {
        db.getQuestionByKey(QUESTION_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, dataSnapshot.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onGetAButtonClick(View view) {
        db.getAnswerByKey(QUESTION_KEY, ANSWER_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
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

    public void onUnsubsButtonClick(View view) {
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

    public void onUCButtonClick(View view) {
        db.getUserCoursesByKey(USER_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    db.getCourseByKey(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, dataSnapshot.toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onUSubButtonClick(View view) {
        db.getUserSubsByKey(USER_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    db.getCourseByKey(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d(TAG, dataSnapshot.toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onQAllButtonClick(View view) {
        Intent newPostIntent = new Intent(this, QuizActivity.class);
        newPostIntent.putExtra("EXTRA_QUIZ_MODE", QuizActivity.QUIZ_MODE_ALL);
        startActivity(newPostIntent);
    }

    public void onQSingleButtonClick(View view) {
        Intent newPostIntent = new Intent(this, QuizActivity.class);

        newPostIntent.putExtra("EXTRA_QUIZ_MODE", QuizActivity.QUIZ_MODE_SINGLE);
        newPostIntent.putExtra("EXTRA_QUESTION_KEY", QUESTION_KEY);

        startActivity(newPostIntent);
    }
}
