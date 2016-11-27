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
import com.mertkasar.kui.models.Question;

public final class Database {
    private final String TAG = Database.class.getSimpleName();

    private static Database instance = new Database();

    public static Database getInstance() {
        return instance;
    }

    private FirebaseDatabase firebaseDB;

    public DatabaseReference refQuestions;
    public DatabaseReference refQuestionAnswers;

    private Database() {
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDB.setPersistenceEnabled(true);

        refQuestions = firebaseDB.getReference("questions");
        refQuestionAnswers = firebaseDB.getReference("question-answers");

        Log.d(TAG, "Database: Created");
    }

    public Task<Void> createNewQuestion(final Question question) {
        //TODO: Validate question has 10 options max
        return refQuestions.push().setValue(question);
    }

    public Task<Void> createNewAnswer(final String questionKey, final Answer answer) {
        Task<Void> task = refQuestionAnswers.child(questionKey).push().setValue(answer);

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
                        Log.d(TAG, "onComplete: Question updated " + databaseError);
                    }
                });
            }
        });

        return task;
    }
}
