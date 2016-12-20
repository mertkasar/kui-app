package com.mertkasar.kui.adapters;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.mertkasar.kui.R;
import com.mertkasar.kui.core.App;
import com.mertkasar.kui.fragments.CourseListFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class CoursesPagerAdapter extends FragmentPagerAdapter {
    private App app;

    private Context mContext;

    private List<CourseListFragment> mFragments;

    public CoursesPagerAdapter(FragmentManager fragmentManager, Context context) {
        super(fragmentManager);

        app = App.getInstance();

        mContext = context;

        mFragments = new ArrayList<>(3);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return CourseListFragment.newInstance(CourseListFragment.MODE_SUBSCRIBED, app.uid);
            case 1:
                return CourseListFragment.newInstance(CourseListFragment.MODE_CREATED, app.uid);
            case 2:
                return CourseListFragment.newInstance(CourseListFragment.MODE_BROWSE, app.uid);
        }

        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        CourseListFragment createdFragment = (CourseListFragment) super.instantiateItem(container, position);

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
                return mContext.getString(R.string.pager_courses_subscribed);
            case 1:
                return mContext.getString(R.string.pager_courses_created);
            case 2:
                return mContext.getString(R.string.pager_courses_browse);
        }

        return null;
    }

    public CourseListFragment getFragment(int position) {
        return mFragments.get(position);
    }
}
