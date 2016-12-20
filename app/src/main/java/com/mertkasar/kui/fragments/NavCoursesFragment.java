package com.mertkasar.kui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mertkasar.kui.R;
import com.mertkasar.kui.activities.NewCourseActivity;
import com.mertkasar.kui.adapters.CoursesPagerAdapter;
import com.mertkasar.kui.core.App;

public class NavCoursesFragment extends Fragment {
    public static final String TAG = NavCoursesFragment.class.getSimpleName();

    private CoursesPagerAdapter mCoursesPagerAdapter;

    private FloatingActionButton mSharedFab;
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

        mSharedFab = (FloatingActionButton) view.findViewById(R.id.shared_fab);
        mSharedFab.hide();

        mViewPager = (ViewPager) view.findViewById(R.id.container);
        mViewPager.setAdapter(mCoursesPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            boolean isDragged = false;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING:
                        mSharedFab.hide();
                        isDragged = true;

                        break;
                    case ViewPager.SCROLL_STATE_IDLE:
                        if (isDragged)
                            break;

                        handleFAB();

                        break;
                    case ViewPager.SCROLL_STATE_SETTLING:
                        if (isDragged) {
                            handleFAB();
                            isDragged = false;
                            break;
                        }

                        mSharedFab.hide();

                        break;
                }
            }
        });

        handleFAB();

        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(mViewPager);
    }

    private void handleFAB() {
        int position = mViewPager.getCurrentItem();

        if (useFAB(mSharedFab, position))
            mSharedFab.show();
    }

    public boolean useFAB(FloatingActionButton fab, int position) {
        switch (position) {
            case 0:
                fab.setImageResource(R.drawable.ic_subscribe);
                fab.setOnClickListener(null);
                return true;

            case 1:
                fab.setImageResource(R.drawable.ic_add);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), NewCourseActivity.class);
                        intent.putExtra("EXTRA_USER_KEY", App.getInstance().uid);
                        startActivity(intent);
                    }
                });
                return true;

            case 2:
                return false;

            default:
                throw new IllegalArgumentException("Undefined position");
        }
    }
}
