package com.mertkasar.kui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.mertkasar.kui.R;
import com.mertkasar.kui.core.App;
import com.mertkasar.kui.core.Database;
import com.mertkasar.kui.models.Question;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuizActivity extends AppCompatActivity {
    public static final String TAG = QuizActivity.class.getSimpleName();

    public static final String EXTRA_QUIZ_MODE = "quiz_mode";
    public static final String EXTRA_QUESTION_KEY = "question_key";

    public static final int QUIZ_MODE_SINGLE = 1;
    public static final int QUIZ_MODE_RECENT = 2;
    public static final int QUIZ_MODE_ALL = 3;

    Database mDB;

    ArrayDeque<DataSnapshot> mQuestions;
    String mCurrentQuestionKey;
    Question mCurrentQuestion;

    private int mQuizMode;
    private String mUID;
    private long mSize;

    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private RadioGroup mOptionsRadioGroup;
    private ViewFlipper mButtonViewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_dark);
        }

        mDB = Database.getInstance();

        mQuizMode = getIntent().getIntExtra(EXTRA_QUIZ_MODE, -1);
        if (mQuizMode == -1) {
            throw new IllegalArgumentException("Must pass " + EXTRA_QUIZ_MODE);
        }

        mUID = App.getInstance().uid;

        mTitleTextView = (TextView) findViewById(R.id.title);
        mDescriptionTextView = (TextView) findViewById(R.id.description);
        mOptionsRadioGroup = (RadioGroup) findViewById(R.id.options);
        mButtonViewFlipper = (ViewFlipper) findViewById(R.id.flipper_button);

        getQuestions(mQuizMode);
    }

    private void getQuestions(final int quizMode) {
        mQuestions = new ArrayDeque<>();

        switch (quizMode) {
            case QUIZ_MODE_SINGLE:
                onQuizModeSingle();
                break;

            case QUIZ_MODE_RECENT:
                onQuizModeRecent();
                break;

            case QUIZ_MODE_ALL:
                onQuizModeAll();
                break;

            default:
                break;
        }

    }

    public void onQuizModeSingle() {
        final String QUESTION_KEY = getIntent().getStringExtra(EXTRA_QUESTION_KEY);

        if (QUESTION_KEY == null) {
            throw new IllegalArgumentException("Must pass " + EXTRA_QUESTION_KEY);
        }

        mDB.getQuestionByKey(QUESTION_KEY).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mQuestions.push(dataSnapshot);
                    getNextQuestion();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onQuizModeRecent() {
        mDB.getUserRecentByKey(mUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    mSize = dataSnapshot.getChildrenCount();

                    for (DataSnapshot questionSnap : dataSnapshot.getChildren()) {
                        mDB.getQuestionByKey(questionSnap.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    mQuestions.push(dataSnapshot);

                                    if (mQuestions.size() == mSize) {
                                        getNextQuestion();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void onQuizModeAll() {
        mDB.getQuestions().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot questionSnap : dataSnapshot.getChildren())
                        mQuestions.push(questionSnap);

                    getNextQuestion();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getNextQuestion() {
        DataSnapshot questionSnap = mQuestions.pop();

        mCurrentQuestionKey = questionSnap.getKey();
        mCurrentQuestion = questionSnap.getValue(Question.class);

        bindLayout(mCurrentQuestion);
    }

    private void bindLayout(Question question) {
        mTitleTextView.setText(question.title);
        mDescriptionTextView.setText(question.description);

        mOptionsRadioGroup.removeAllViewsInLayout();

        List<String> keys = new ArrayList<>(question.options.keySet());
        Collections.shuffle(keys);
        for (String key : keys) {
            String option = question.options.get(key);
            addRadioButton(key, option);
        }
    }

    private void addRadioButton(final String tag, final String text) {
        getLayoutInflater().inflate(R.layout.item_option, mOptionsRadioGroup);
        RadioButton button = (RadioButton) mOptionsRadioGroup.getChildAt(mOptionsRadioGroup.getChildCount() - 1);

        button.setTag(tag);
        button.setText(text);
    }

    public void onCheckButtonCallback(View view) {
        int checkedID = mOptionsRadioGroup.getCheckedRadioButtonId();

        if (checkedID == -1) {
            Toast.makeText(QuizActivity.this, R.string.toast_make_a_choice, Toast.LENGTH_SHORT).show();
            return;
        }

        //Disable elements
        for (int i = 0; i < mOptionsRadioGroup.getChildCount(); i++) {
            mOptionsRadioGroup.getChildAt(i).setEnabled(false);
        }

        //Show next button state
        mButtonViewFlipper.showNext();

        final RadioButton checkedButton = (RadioButton) mOptionsRadioGroup.findViewById(checkedID);
        String tag = (String) checkedButton.getTag();

        final boolean isCorrect = tag.equals("option_0");

        mDB.getQuestionByKey(mCurrentQuestionKey).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                // Retrieve the question
                Question question = mutableData.getValue(Question.class);
                if (question == null) {
                    return Transaction.success(mutableData);
                }

                // Update values
                question.answer_count = question.answer_count + 1;
                if (isCorrect)
                    question.correct_count = question.correct_count + 1;

                //Finish transaction and report success
                mutableData.setValue(question);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError == null) {
//                    mDB.removeUserRecentByKey(App.getInstance().uid, mCurrentQuestionKey);

                    onCheckResult(checkedButton, isCorrect);

                    if (mQuestions.isEmpty()) {
                        setResult(RESULT_OK);
                        finish();
                    }

                    mButtonViewFlipper.showNext();
                }
            }
        });
    }

    private void onCheckResult(RadioButton selected, boolean isCorrect) {
        if (isCorrect) {
            selected.setBackgroundResource(R.color.correct);
            Toast.makeText(QuizActivity.this, R.string.toast_correct_answer, Toast.LENGTH_LONG).show();
        } else {
            selected.setBackgroundResource(R.color.wrong);

            for (int i = 0; i < mOptionsRadioGroup.getChildCount(); ++i) {
                RadioButton currentButton = (RadioButton) mOptionsRadioGroup.getChildAt(i);

                if (currentButton.getTag().equals("option_0")) {
                    currentButton.setBackgroundResource(R.color.correct);
                    currentButton.setTextColor(getResources().getColor(android.R.color.white));

                    break;
                }
            }

            Toast.makeText(QuizActivity.this, R.string.toast_wrong_answer, Toast.LENGTH_LONG).show();
        }
    }

    public void onContinueButtonCallback(View view) {
        getNextQuestion();
        mButtonViewFlipper.showNext();
    }

    public void onOptionSelected(View view) {
        for (int i = 0; i < mOptionsRadioGroup.getChildCount(); ++i) {
            RadioButton currentButton = (RadioButton) mOptionsRadioGroup.getChildAt(i);

            if (currentButton.isChecked()) {
                currentButton.setBackgroundResource(R.color.color_primary);
                currentButton.setTextColor(getResources().getColor(android.R.color.white));
            } else {
                currentButton.setBackgroundResource(android.R.color.white);
                currentButton.setTextColor(getResources().getColor(android.R.color.black));
            }
        }
    }
}
