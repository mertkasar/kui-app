package com.mertkasar.kui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.mertkasar.kui.R;
import com.mertkasar.kui.adapters.CourseRecyclerViewAdapter;
import com.mertkasar.kui.core.Database;

import java.util.ArrayList;

public class CourseListFragment extends Fragment {
    public static final String TAG = CourseListFragment.class.getSimpleName();


    public static final int MODE_SUBSCRIBED = 1;
    public static final int MODE_CREATED = 2;
    public static final int MODE_BROWSE = 3;

    private static final String ARG_DISPLAY_MODE = "display_mode";
    private static final String ARG_USER_KEY = "user_key";

    private int mDisplayMode;
    private String mUserKey;

    private ArrayList<DataSnapshot> mCourseList;
    private CourseRecyclerViewAdapter mAdapter;

    private Database mDB;
    private DatabaseReference mDBRef;

    private ViewFlipper mViewFlipper;
    private TextView mEmptyText;

    public CourseListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CourseListFragment newInstance(int displayMode, String userKey) {
        CourseListFragment fragment = new CourseListFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_DISPLAY_MODE, displayMode);
        args.putString(ARG_USER_KEY, userKey);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDisplayMode = getArguments().getInt(ARG_DISPLAY_MODE);
        mUserKey = getArguments().getString(ARG_USER_KEY);

        mCourseList = new ArrayList<>();
        mAdapter = new CourseRecyclerViewAdapter(mCourseList, getActivity());

        mDB = Database.getInstance();
        switch (mDisplayMode) {
            case MODE_SUBSCRIBED:
                mDBRef = mDB.getUserSubsByKey(mUserKey);
                break;
            case MODE_CREATED:
                mDBRef = mDB.getUserCoursesByKey(mUserKey);
                break;
            case MODE_BROWSE:
                mDBRef = mDB.getCourses();
                break;
            default:
                throw new IllegalArgumentException("Undefined display mode!");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViewFlipper = (ViewFlipper) view.findViewById(R.id.view_flipper);
        mEmptyText = (TextView) view.findViewById(R.id.empty_text);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.course_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), layoutManager.getOrientation());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(mAdapter);

        refreshItems();
    }

    public void refreshItems() {
        mCourseList.clear();
        mAdapter.notifyDataSetChanged();

        getItems();
    }

    private void getItems() {
        mDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final long size = dataSnapshot.getChildrenCount();

                    for (DataSnapshot courseSnap : dataSnapshot.getChildren()) {
                        mDB.getCourseByKey(courseSnap.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    mCourseList.add(dataSnapshot);

                                    if (mCourseList.size() == size) {
                                        mAdapter.notifyDataSetChanged();
                                        mViewFlipper.showNext();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    switch (mDisplayMode) {
                        case MODE_SUBSCRIBED:
                            mEmptyText.setText(R.string.empty_courses_subscribed);
                            break;
                        case MODE_CREATED:
                            mEmptyText.setText(R.string.empty_courses_created);
                            break;
                        case MODE_BROWSE:
                            mEmptyText.setText(R.string.empty_courses_browse);
                            break;
                        default:
                            throw new IllegalArgumentException("Undefined display mode!");
                    }

                    mViewFlipper.setDisplayedChild(2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
