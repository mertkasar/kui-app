package com.mertkasar.kui.core;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.mertkasar.kui.models.Answer;
import com.mertkasar.kui.models.Course;
import com.mertkasar.kui.models.Question;
import com.mertkasar.kui.models.User;

public final class Database {
    private final String TAG = Database.class.getSimpleName();

    private static Database instance = new Database();

    public static Database getInstance() {
        return instance;
    }

    private FirebaseDatabase firebaseDB;

    public DatabaseReference refUsers;
    public DatabaseReference refCourses;
    public DatabaseReference refQuestions;
    public DatabaseReference refAnswers;

    private Database() {
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDB.setPersistenceEnabled(true);

        refUsers = firebaseDB.getReference("users");
        refCourses = firebaseDB.getReference("courses");
        refQuestions = firebaseDB.getReference("questions");
        refAnswers = firebaseDB.getReference("answers");

        Log.d(TAG, "Database: Created");
    }

    public Task<Void> createNewUser(final String uid, final User user) {
        return refUsers.child(uid).setValue(user);
    }

    public Task<Void> createNewCourse(final Course course) {
        return refCourses.push().setValue(course);
    }

    public Task<Void> createNewQuestion(final String courseKey, final Question question) {
        Task<Void> task = refQuestions.child(courseKey).push().setValue(question);

        // Update corresponding question stats upon success
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                refCourses.child(courseKey).runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        //Retrieve the course
                        Course course = mutableData.getValue(Course.class);
                        if (course == null) {
                            Log.d(TAG, "doTransaction: Cant find the course");
                            return Transaction.success(mutableData);
                        }

                        //Update stats
                        course.question_count = course.question_count + 1;

                        //Finish transaction and report success
                        mutableData.setValue(course);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onComplete: Course updated. Error:" + databaseError);
                    }
                });
            }
        });

        //TODO: Validate question has 10 options max
        return task;
    }

    public Task<Void> createNewAnswer(final String courseKey, final String questionKey, final Answer answer) {
        Task<Void> task = refAnswers.child(questionKey).push().setValue(answer);

        // Update corresponding question stats upon success
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                refQuestions.child(courseKey).child(questionKey).runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        // Retrieve the question
                        Question question = mutableData.getValue(Question.class);
                        if (question == null) {
                            return Transaction.success(mutableData);
                        }

                        // Update values
                        question.answer_count = question.answer_count + 1;
                        if (answer.is_correct)
                            question.correct_count = question.correct_count + 1;

                        //Finish transaction and report success
                        mutableData.setValue(question);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        Log.d(TAG, "onComplete: Question updated. Error:" + databaseError);
                    }
                });
            }
        });

        return task;
    }
}
