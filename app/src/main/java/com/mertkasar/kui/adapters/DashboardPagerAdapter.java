package com.mertkasar.kui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mertkasar.kui.R;
import com.mertkasar.kui.activities.DashboardActivity;
import com.mertkasar.kui.core.App;
import com.mertkasar.kui.fragments.CourseFragment;
import com.mertkasar.kui.fragments.PlaceholderFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class DashboardPagerAdapter extends FragmentPagerAdapter {
    private App app;

    private DashboardActivity mDashboardActivity;


    public DashboardPagerAdapter(FragmentManager fm, DashboardActivity dashboardActivity) {
        super(fm);

        app = App.getInstance();
        mDashboardActivity = dashboardActivity;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return PlaceholderFragment.newInstance(position + 1);
            case 1:
                return CourseFragment.newInstance(CourseFragment.MODE_SUBSCRIBED, app.uid);
            case 2:
                return CourseFragment.newInstance(CourseFragment.MODE_CREATED, app.uid);
        }

        return null;
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
}
