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

        getItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_course_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.course_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), layoutManager.getOrientation());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(mAdapter);
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
                    for (DataSnapshot courseSnap : dataSnapshot.getChildren()) {
                        mDB.getCourseByKey(courseSnap.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    mCourseList.add(dataSnapshot);
                                    mAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
