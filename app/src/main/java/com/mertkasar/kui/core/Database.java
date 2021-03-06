package com.mertkasar.kui.core;

import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.mertkasar.kui.models.Answer;
import com.mertkasar.kui.models.Course;
import com.mertkasar.kui.models.Question;
import com.mertkasar.kui.models.User;

import java.util.HashMap;
import java.util.Objects;

public final class Database {
    public static final String TAG = Database.class.getSimpleName();

    private static Database instance = new Database();

    public static Database getInstance() {
        return instance;
    }

    private FirebaseDatabase firebaseDB;

    private DatabaseReference refDB;

    private DatabaseReference refUsers;
    private DatabaseReference refCourses;
    private DatabaseReference refQuestions;
    private DatabaseReference refAnswers;

    private DatabaseReference refUserCourses;
    private DatabaseReference refUserSubscriptions;
    private DatabaseReference refUserRecent;
    private DatabaseReference refCourseSubscribers;
    private DatabaseReference refCourseQuestions;

    private Database() {
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDB.setPersistenceEnabled(true);

        refDB = firebaseDB.getReference();

        refUsers = firebaseDB.getReference("users");
        refCourses = firebaseDB.getReference("courses");
        refQuestions = firebaseDB.getReference("questions");
        refAnswers = firebaseDB.getReference("answers");

        refUserCourses = firebaseDB.getReference("user_courses");
        refUserSubscriptions = firebaseDB.getReference("user_subscriptions");
        refUserRecent = firebaseDB.getReference("user_recent");
        refCourseSubscribers = firebaseDB.getReference("course_subscribers");
        refCourseQuestions = firebaseDB.getReference("course_questions");

        Log.d(TAG, "Database: Created");
    }

    public DatabaseReference getDB() {
        return refDB;
    }

    public Task<Void> createUser(final String uid, final User user) {
        return refUsers.child(uid).setValue(user);
    }

    public DatabaseReference getUsers() {
        return refUsers;
    }

    public DatabaseReference getUserByKey(final String key) {
        return refUsers.child(key);
    }

    public DatabaseReference getUserCoursesByKey(final String key) {
        return refUserCourses.child(key);
    }

    public DatabaseReference getUserSubsByKey(final String key) {
        return refUserSubscriptions.child(key);
    }

    public DatabaseReference getUserRecentByKey(final String key) {
        return refUserRecent.child(key);
    }

    public Task<Void> removeUserRecentByKey(final String uid, final String questionKey) {
        return refUserRecent.child(uid).child(questionKey).removeValue();
    }

    public Task<Void> subscribeUser(final String userKey, final String courseKey) {
        HashMap<String, Object> updateBatch = new HashMap<>();

        updateBatch.put("user_subscriptions/" + userKey + "/" + courseKey, true);
        updateBatch.put("course_subscribers/" + courseKey + "/" + userKey, true);

        Task<Void> task = refDB.updateChildren(updateBatch);
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
                        course.subscriber_count = course.subscriber_count + 1;

                        //Finish transaction and report success
                        mutableData.setValue(course);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
            }
        });

        return task;
    }

    public Task<Void> unsubscribeUser(final String userKey, final String courseKey) {
        HashMap<String, Object> updateBatch = new HashMap<>();

        updateBatch.put("user_subscriptions/" + userKey + "/" + courseKey, null);
        updateBatch.put("course_subscribers/" + courseKey + "/" + userKey, null);

        Task<Void> task = refDB.updateChildren(updateBatch);
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
                        course.subscriber_count = course.subscriber_count - 1;

                        //Finish transaction and report success
                        mutableData.setValue(course);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });
            }
        });

        return task;
    }

    public Task<Void> createCourse(final Course course) {
        String courseKey = refCourses.push().getKey();

        HashMap<String, Object> updateBatch = new HashMap<>();

        updateBatch.put("courses/" + courseKey, course);
        updateBatch.put("user_courses/" + course.owner + "/" + courseKey, true);

        return refDB.updateChildren(updateBatch);
    }

    public DatabaseReference getCourses() {
        return refCourses;
    }

    public DatabaseReference getCourseByKey(String key) {
        return refCourses.child(key);
    }

    public Query getCourseBySubsKey(final String key) {
        Long createdAt = Long.valueOf(key.toLowerCase(), 36);
        return refCourses.orderByChild("created_at").equalTo(createdAt);
    }

    public DatabaseReference getCourseSubscribers(final String courseKey) {
        return refCourseSubscribers.child(courseKey);
    }

    public Query getSubscribedCourse(final String userKey, final String courseKey) {
        return refUserSubscriptions.child(userKey).child(courseKey);
    }

    public Task<Void> createQuestion(final String courseKey, final Question question) {
        final String questionKey = refQuestions.child(courseKey).push().getKey();

        HashMap<String, Object> updateBatch = new HashMap<>();

        updateBatch.put("questions/" + questionKey, question);
        updateBatch.put("course_questions/" + courseKey + "/" + questionKey, true);

        Task<Void> task = refDB.updateChildren(updateBatch);

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

        // Update recent questions collection
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                getCourseSubscribers(courseKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            HashMap<String, Object> updateBatch = new HashMap<>();

                            for (DataSnapshot user : dataSnapshot.getChildren()) {
                                updateBatch.put("user_recent/" + user.getKey() + "/" + questionKey, true);
                            }

                            getDB().updateChildren(updateBatch);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });

        //TODO: Validate question has 10 options max
        return task;
    }

    public DatabaseReference getQuestions() {
        return refQuestions;
    }

    public DatabaseReference getQuestionByKey(String questionKey) {
        return refQuestions.child(questionKey);
    }

    public DatabaseReference getQuestionsByCourseKey(final String courseKey) {
        return refCourseQuestions.child(courseKey);
    }

    public Task<Void> createAnswer(final String questionKey, final Answer answer) {
        Task<Void> task = refAnswers.child(questionKey).push().setValue(answer);

        // Update corresponding question stats upon success
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                refQuestions.child(questionKey).runTransaction(new Transaction.Handler() {
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
//
//        task.addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                removeUserRecentByKey(answer.owner, questionKey);
//            }
//        });

        return task;
    }

    public DatabaseReference getAnswers() {
        return refAnswers;
    }

    public DatabaseReference getAnswersByQuestionKey(final String questionKey) {
        return refAnswers.child(questionKey);
    }

    public DatabaseReference getAnswerByKey(String questionKey, String answerKey) {
        return refAnswers.child(questionKey).child(answerKey);
    }
}
