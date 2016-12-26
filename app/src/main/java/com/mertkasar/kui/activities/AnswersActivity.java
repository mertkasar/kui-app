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
import com.mertkasar.kui.models.Answer;
import com.mertkasar.kui.models.Question;

import java.util.ArrayList;

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

    private int[] choiceCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        choiceCount = new int[4];

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

        getAnswers();
    }

    private void getQuestion() {
        mDB.getQuestionByKey(mQuestionKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Question question = dataSnapshot.getValue(Question.class);
                    bindLayout(question);
                    mAdapter.notifyDataSetChanged();
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
                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        Answer answer = snap.getValue(Answer.class);
                        int position = getChoiceAsInt(answer.choice);

                        choiceCount[position] = choiceCount[position] + 1;

                        mAnswerDataSet.add(snap);
                    }
                }

                getQuestion();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void bindLayout(Question question) {
        mTitleTextView.setText(question.title);
        mDescriptionTextView.setText(question.description);

        int totalCount = 0;
        for (int i : choiceCount)
            totalCount = totalCount + i;

        int count = question.options.size();
        for (int i = 0; i < count; i++) {
            String key = "option_" + i;
            String option = question.options.get(key);
            int percent = (int) ((double) choiceCount[i] / (double) totalCount * 100);
            addOption(key, option, percent);
        }
    }

    private void addOption(final String tag, final String text, int percent) {
        getLayoutInflater().inflate(R.layout.item_option_review, mContent);
        LinearLayout item = (LinearLayout) mContent.getChildAt(mContent.getChildCount() - 1);

        TextView option = (TextView) item.findViewById(R.id.option);
        TextView content = (TextView) item.findViewById(R.id.content);
        TextView percentage = (TextView) item.findViewById(R.id.percentage);

        option.setText(getChoiceAsChar(tag));
        content.setText(text);
        percentage.setText("%" + percent);

        if (tag.endsWith("0")) {
            option.setBackgroundResource(R.color.correct_ghost);
        } else
            option.setBackgroundResource(R.color.wrong_ghost);
    }

    public static String getChoiceAsChar(String tag) {
        switch (tag) {
            case "option_0":
                return "A";
            case "option_1":
                return "B";
            case "option_2":
                return "C";
            case "option_3":
                return "D";
        }

        return "A";
    }

    public static int getChoiceAsInt(String tag) {
        if (tag.endsWith("0"))
            return 0;
        else if (tag.endsWith("1"))
            return 1;
        else if (tag.endsWith("2"))
            return 2;
        else if (tag.endsWith("3"))
            return 3;

        return -1;
    }
}
