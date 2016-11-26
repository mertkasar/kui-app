package com.mertkasar.kui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.mertkasar.kui.core.Database;
import com.mertkasar.kui.models.Answer;
import com.mertkasar.kui.models.Question;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    private final String QUESTION_KEY = "-KXXUB1JrFuqvHONLHxm";

    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = Database.getInstance();
    }

    public void onPostQButtonClick(View view) {
        Question newQuestion = new Question("Fibonacci?", "What is the next number in the following sequence: 1 1 2 3 5 8 ?");

        db.createNewQuestion(newQuestion).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, R.string.post_question, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onPostAButtonClick(View view) {
        Answer newAnswer = new Answer("13");
        db.createNewAnswer(QUESTION_KEY, newAnswer).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, R.string.post_answer, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
