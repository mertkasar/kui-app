package com.mertkasar.kui;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mertkasar.kui.core.Database;
import com.mertkasar.kui.models.Course;

public class CourseDetailActivity extends AppCompatActivity {
    public static final String TAG = CourseDetailActivity.class.getSimpleName();

    private String mCourseKey;

    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private TextView mDescriptionTextView;
    private TextView mSubtitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        mCourseKey = getIntent().getStringExtra("EXTRA_COURSE_KEY");
        if (mCourseKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_COURSE_KEY");
        }

        mDescriptionTextView = (TextView) findViewById(R.id.description);
        mSubtitleTextView = (TextView) findViewById(R.id.toolbar_subtitle);

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
        Database.getInstance().getCourseByKey(mCourseKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Course course = dataSnapshot.getValue(Course.class);
                bindLayout(course);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void bindLayout(Course course){
        mCollapsingToolbarLayout.setTitle(course.title);
        mDescriptionTextView.setText(course.description);
        mSubtitleTextView.setText("Enroll code: " + course.getSubsKey());
    }
}
