package com.mertkasar.kui.core;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mertkasar.kui.models.Question;

public final class Database {
    private final String TAG = Database.class.getSimpleName();

    private static Database instance = new Database();

    public static Database getInstance() {
        return instance;
    }

    private FirebaseDatabase firebaseDB;

    public DatabaseReference refQuestions;

    private Database() {
        firebaseDB = FirebaseDatabase.getInstance();
        firebaseDB.setPersistenceEnabled(true);

        refQuestions = firebaseDB.getReference("questions");

        Log.d(TAG, "Database: Created");
    }

    public Task<Void> postNewQuestion(Question question){
        return refQuestions.push().setValue(question);
    }
}
