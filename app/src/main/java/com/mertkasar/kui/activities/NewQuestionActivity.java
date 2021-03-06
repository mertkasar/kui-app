package com.mertkasar.kui.activities;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mertkasar.kui.R;
import com.mertkasar.kui.core.Database;
import com.mertkasar.kui.models.Question;

import java.util.HashMap;

public class NewQuestionActivity extends AppCompatActivity {
    public static final String TAG = NewQuestionActivity.class.getSimpleName();

    public static final String EXTRA_COURSE_KEY = "course-key";

    private String mCourseKey;

    private EditText mTitle;
    private EditText mDescription;
    private EditText mOptionCorrect;
    private EditText mOption1;
    private EditText mOption2;
    private EditText mOption3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_question);

        mCourseKey = getIntent().getStringExtra(EXTRA_COURSE_KEY);
        if (mCourseKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_QUESTION_KEY");
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_light);
        }

        mTitle = (EditText) findViewById(R.id.title_edit_text);
        mDescription = (EditText) findViewById(R.id.description_edit_text);
        mOptionCorrect = (EditText) findViewById(R.id.option_correct);
        mOption1 = (EditText) findViewById(R.id.option_1);
        mOption2 = (EditText) findViewById(R.id.option_2);
        mOption3 = (EditText) findViewById(R.id.option_3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_question, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_question_send:
                createNewQuestion();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNewQuestion() {
        final ProgressDialog postingDialog = ProgressDialog.show(this, "", getString(R.string.dialog_message_creating), true);

        HashMap<String, String> options = new HashMap<>(4);
        options.put("option_0", mOptionCorrect.getText().toString());
        options.put("option_1", mOption1.getText().toString());
        options.put("option_2", mOption2.getText().toString());
        options.put("option_3", mOption3.getText().toString());

        final Question newQuestion = new Question(
                mCourseKey,
                mTitle.getText().toString(),
                mDescription.getText().toString(),
                options
        );

        Database.getInstance().createQuestion(mCourseKey, newQuestion).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                postingDialog.dismiss();
                setResult(RESULT_OK);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, e.toString());
                postingDialog.dismiss();
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
