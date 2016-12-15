package com.mertkasar.kui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mertkasar.kui.R;
import com.mertkasar.kui.adapters.CoursesPagerAdapter;
import com.mertkasar.kui.fragments.CourseFragment;

public class CoursesActivity extends AppCompatActivity implements
        CourseFragment.OnCourseTouchedListener {
    public static final String TAG = CoursesActivity.class.getSimpleName();

    private CoursesPagerAdapter mCoursesPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mCoursesPagerAdapter = new CoursesPagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mCoursesPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onCourseTouchedListener(String key) {
        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.putExtra(CourseDetailActivity.EXTRA_COURSE_KEY, key);
        startActivity(intent);
    }
}
