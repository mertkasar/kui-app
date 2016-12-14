package com.mertkasar.kui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.mertkasar.kui.R;
import com.mertkasar.kui.adapters.DashboardPagerAdapter;
import com.mertkasar.kui.fragments.CourseFragment;
import com.mertkasar.kui.fragments.RecentFragment;

public class DashboardActivity extends AppCompatActivity implements
        CourseFragment.OnCourseTouchedListener,
        RecentFragment.OnQuestionClickedListener {
    public static final String TAG = DashboardActivity.class.getSimpleName();

    public static final int RC_QUIZ = 1;

    private DashboardPagerAdapter mDashboardPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mDashboardPagerAdapter = new DashboardPagerAdapter(getSupportFragmentManager(), this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mDashboardPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_QUIZ:
                //TODO: Instead of doing this, consider make it realtime
                RecentFragment fragment = (RecentFragment) mDashboardPagerAdapter.getFragment(0);
                fragment.refreshItems();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCourseTouchedListener(String key) {
        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.putExtra(CourseDetailActivity.EXTRA_COURSE_KEY, key);
        startActivity(intent);
    }

    @Override
    public void onQuestionClickedListener(String key) {
        Intent intent = new Intent(this, QuizActivity.class);

        intent.putExtra(QuizActivity.EXTRA_QUIZ_MODE, QuizActivity.QUIZ_MODE_SINGLE);
        intent.putExtra(QuizActivity.EXTRA_QUESTION_KEY, key);

        startActivityForResult(intent, RC_QUIZ);
    }
}
