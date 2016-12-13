package com.mertkasar.kui.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.mertkasar.kui.R;
import com.mertkasar.kui.fragments.PlaceholderFragment;

/**
 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class DashboardPagerAdapter extends FragmentPagerAdapter {
    private Context mContext;

    public DashboardPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return PlaceholderFragment.newInstance(position + 1);
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
                return mContext.getString(R.string.pager_dashboard_recent);
            case 1:
                return mContext.getString(R.string.pager_dashboard_subscribed);
            case 2:
                return mContext.getString(R.string.pager_dashboard_created);
        }
        return null;
    }
}
