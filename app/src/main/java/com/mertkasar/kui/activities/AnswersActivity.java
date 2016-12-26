package com.mertkasar.kui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mertkasar.kui.R;
import com.mertkasar.kui.adapters.AnswerRecyclerViewAdapter;
import com.mertkasar.kui.core.Database;
import com.mertkasar.kui.models.Question;

import java.util.ArrayList;
import java.util.Set;

public class AnswersActivity extends AppCompatActivity {
    public static final String TAG = CourseDetailActivity.class.getSimpleName();

    public static final String EXTRA_QUESTION_KEY = "question-key";

    private Database mDB;

    private TextView mTitleTextView;
    private TextView mDescriptionTextView;
    private LinearLayout mContent;
    private RecyclerView mAnswerList;

    private String mQuestionKey;

    private ArrayList<DataSnapshot> mAnswerDataSet;
    private AnswerRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDB = Database.getInstance();

        mTitleTextView = (TextView) findViewById(R.id.title);
        mDescriptionTextView = (TextView) findViewById(R.id.description);
        mContent = (LinearLayout) findViewById(R.id.content);

        mAnswerList = (RecyclerView) findViewById(R.id.answer_list);
        mAnswerDataSet = new ArrayList<>();
        mAdapter = new AnswerRecyclerViewAdapter(mAnswerDataSet);
        mAnswerList.setAdapter(mAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, layoutManager.getOrientation());

        mAnswerList.setLayoutManager(layoutManager);
        mAnswerList.addItemDecoration(dividerItemDecoration);

        mQuestionKey = getIntent().getStringExtra(EXTRA_QUESTION_KEY);
        if (mQuestionKey == null) {
            throw new IllegalArgumentException("Must pass " + EXTRA_QUESTION_KEY);
        }

        getQuestion();
        getAnswers();
    }

    private void getQuestion() {
        mDB.getQuestionByKey(mQuestionKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Question question = dataSnapshot.getValue(Question.class);
                    bindLayout(question);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getAnswers() {
        mDB.getAnswersByQuestionKey(mQuestionKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Log.d(TAG, dataSnapshot.toString());
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        mAnswerDataSet.add(snap);
                    }

                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void bindLayout(Question question) {
        mTitleTextView.setText(question.title);
        mDescriptionTextView.setText(question.description);

        Set<String> keys = question.options.keySet();
        for (String key : keys) {
            String option = question.options.get(key);
            addOption(key, option);
        }
    }

    private void addOption(final String tag, final String text) {
        getLayoutInflater().inflate(R.layout.item_option_review, mContent);
        TextView option = (TextView) mContent.getChildAt(mContent.getChildCount() - 1);

        option.setTag(tag);
        option.setText(text);
    }
}
