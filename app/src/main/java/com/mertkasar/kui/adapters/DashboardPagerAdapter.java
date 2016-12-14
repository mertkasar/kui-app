package com.mertkasar.kui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.mertkasar.kui.R;
import com.mertkasar.kui.activities.DashboardActivity;
import com.mertkasar.kui.core.App;
import com.mertkasar.kui.fragments.CourseFragment;
import com.mertkasar.kui.fragments.RecentFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class DashboardPagerAdapter extends FragmentPagerAdapter {
    private App app;

    private DashboardActivity mDashboardActivity;

    private List<Fragment> mFragments;

    public DashboardPagerAdapter(FragmentManager fm, DashboardActivity dashboardActivity) {
        super(fm);

        app = App.getInstance();
        mDashboardActivity = dashboardActivity;

        mFragments = new ArrayList<Fragment>(3);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return RecentFragment.newInstance(app.uid);
            case 1:
                return CourseFragment.newInstance(CourseFragment.MODE_SUBSCRIBED, app.uid);
            case 2:
                return CourseFragment.newInstance(CourseFragment.MODE_CREATED, app.uid);
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
                return mDashboardActivity.getString(R.string.pager_dashboard_recent);
            case 1:
                return mDashboardActivity.getString(R.string.pager_dashboard_subscribed);
            case 2:
                return mDashboardActivity.getString(R.string.pager_dashboard_created);
        }

        return null;
    }

    public Fragment getFragment(int position) {
        return mFragments.get(position);
    }
}
