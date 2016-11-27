package com.mertkasar.kui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.mertkasar.kui.core.Database;
import com.mertkasar.kui.models.Answer;
import com.mertkasar.kui.models.Question;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    private final String QUESTION_KEY = "-KXbQQqYtYIznA7NOp9C";

    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = Database.getInstance();
    }

    public void onPostQButtonClick(View view) {
        HashMap<String, String> options = new HashMap<>();
        options.put("option_0", "13");
        options.put("option_1", "9");
        options.put("option_2", "1");
        options.put("option_3", "6");
        Question newQuestion = new Question("Fibonacci?", "What is the next number in the following sequence: 1 1 2 3 5 8 ?", options);

        db.createNewQuestion(newQuestion).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, R.string.post_question, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onPostAButtonClick(View view) {
        Answer newAnswer = new Answer("option_0");
        db.createNewAnswer(QUESTION_KEY, newAnswer);

        String status = newAnswer.is_correct ? "correct" : "false";
        Toast.makeText(MainActivity.this, "Your answer was " + status, Toast.LENGTH_SHORT).show();
    }
}
