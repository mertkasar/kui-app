package com.mertkasar.kui.activities;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mertkasar.kui.R;
import com.mertkasar.kui.adapters.QuestionRecyclerViewAdapter;
import com.mertkasar.kui.core.Database;
import com.mertkasar.kui.models.Course;
import com.mertkasar.kui.models.User;

import java.util.ArrayList;

public class CourseDetailActivity extends AppCompatActivity {
    public static final String TAG = CourseDetailActivity.class.getSimpleName();

    public static final String EXTRA_COURSE_KEY = "course_key";

    private String mCourseKey;

    private ViewSwitcher mViewSwitcher;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private TextView mDescriptionTextView;
    private TextView mSubtitleTextView;
    private TextView mOwnerTitleTextView;
    private TextView mQuestionCountTextView;
    private TextView mSubscriberCountTextView;

    private Database mDB;

    private ArrayList<DataSnapshot> mDataSet;
    private QuestionRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        }

        mViewSwitcher = (ViewSwitcher) findViewById(R.id.view_switcher_main);
        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        mCourseKey = getIntent().getStringExtra(EXTRA_COURSE_KEY);
        if (mCourseKey == null) {
            throw new IllegalArgumentException("Must pass " + EXTRA_COURSE_KEY);
        }

        mDescriptionTextView = (TextView) findViewById(R.id.description);
        mSubtitleTextView = (TextView) findViewById(R.id.toolbar_subtitle);
        mOwnerTitleTextView = (TextView) findViewById(R.id.text_course_detail_owner);
        mQuestionCountTextView = (TextView) findViewById(R.id.text_course_detail_question_count);
        mSubscriberCountTextView = (TextView) findViewById(R.id.text_course_detail_subscriber_count);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);

        mDB = Database.getInstance();

        mDataSet = new ArrayList<>();
        mAdapter = new QuestionRecyclerViewAdapter(mDataSet, this);
        recyclerView.setAdapter(mAdapter);

        getCourse();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void getCourse() {
        mDB.getCourseByKey(mCourseKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Course course = dataSnapshot.getValue(Course.class);
                    bindLayout(course);
                } else {
                    Log.d(TAG, "Course not found: " + dataSnapshot.toString());
                    Toast.makeText(CourseDetailActivity.this, R.string.error_course_not_found, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void bindLayout(Course course) {
        mCollapsingToolbarLayout.setTitleEnabled(true);
        mCollapsingToolbarLayout.setTitle(course.title);

        mDescriptionTextView.setText(course.description);
        mSubtitleTextView.setText("Enroll code: " + course.getSubsKey());
        mQuestionCountTextView.setText(course.question_count.toString());
        mSubscriberCountTextView.setText(course.subscriber_count.toString());

        mDB.getUserByKey(course.owner).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mOwnerTitleTextView.setText(user.name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDB.getQuestionsByCourseKey(mCourseKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot courseSnap : dataSnapshot.getChildren()) {
                        mDB.getQuestionByKey(courseSnap.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    mDataSet.add(dataSnapshot);
                                    mAdapter.notifyDataSetChanged();
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

        mViewSwitcher.showNext();
    }
}
