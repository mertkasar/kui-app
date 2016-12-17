package com.mertkasar.kui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mertkasar.kui.R;
import com.mertkasar.kui.adapters.CoursesPagerAdapter;

public class NavCoursesFragment extends Fragment {
    public static final String TAG = NavCoursesFragment.class.getSimpleName();

    private CoursesPagerAdapter mCoursesPagerAdapter;
    private ViewPager mViewPager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCoursesPagerAdapter = new CoursesPagerAdapter(getChildFragmentManager(), getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nav_courses, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Courses");

        mViewPager = (ViewPager) view.findViewById(R.id.container);
        mViewPager.setAdapter(mCoursesPagerAdapter);

        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(mViewPager);
    }
}
