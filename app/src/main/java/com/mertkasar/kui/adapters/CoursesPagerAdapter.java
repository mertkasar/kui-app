package com.mertkasar.kui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.mertkasar.kui.R;
import com.mertkasar.kui.activities.CoursesActivity;
import com.mertkasar.kui.core.App;
import com.mertkasar.kui.fragments.CourseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class CoursesPagerAdapter extends FragmentPagerAdapter {
    private App app;

    private CoursesActivity mCoursesActivity;

    private List<Fragment> mFragments;

    public CoursesPagerAdapter(FragmentManager fm, CoursesActivity coursesActivity) {
        super(fm);

        app = App.getInstance();
        mCoursesActivity = coursesActivity;

        mFragments = new ArrayList<Fragment>(3);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return CourseFragment.newInstance(CourseFragment.MODE_SUBSCRIBED, app.uid);
            case 1:
                return CourseFragment.newInstance(CourseFragment.MODE_CREATED, app.uid);
            case 2:
                return CourseFragment.newInstance(CourseFragment.MODE_BROWSE, app.uid);
        }

        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFragment = (Fragment) super.instantiateItem(container, position);

        mFragments.add(position, createdFragment);

        return createdFragment;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return mCoursesActivity.getString(R.string.pager_courses_subscribed);
            case 1:
                return mCoursesActivity.getString(R.string.pager_courses_created);
            case 2:
                return mCoursesActivity.getString(R.string.pager_courses_browse);
        }

        return null;
    }

    public Fragment getFragment(int position) {
        return mFragments.get(position);
    }
}
