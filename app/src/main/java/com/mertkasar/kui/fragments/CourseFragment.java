package com.mertkasar.kui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnCourseTouchedListener}
 * interface.
 */
public class CourseFragment extends Fragment {
    public static final String TAG = CourseFragment.class.getSimpleName();


    public static final int MODE_SUBSCRIBED = 1;
    public static final int MODE_CREATED = 2;
    public static final int MODE_BROWSE = 3;

    private static final String ARG_DISPLAY_MODE = "display_mode";
    private static final String ARG_USER_KEY = "user_key";

    private OnCourseTouchedListener mListener;

    private int mDisplayMode;
    private String mUserKey;

    private ArrayList<DataSnapshot> mCourseList;
    private CourseRecyclerViewAdapter mAdapter;

    private Database mDB;
    private DatabaseReference mDBRef;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CourseFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CourseFragment newInstance(final int displayMode, final String userKey) {
        CourseFragment fragment = new CourseFragment();
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
        mAdapter = new CourseRecyclerViewAdapter(mCourseList, mListener);

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
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        RecyclerView recyclerView = (RecyclerView) view;
        recyclerView.setAdapter(mAdapter);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCourseTouchedListener) {
            mListener = (OnCourseTouchedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCourseTouchedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnCourseTouchedListener {
        // TODO: Update argument type and name
        void onCourseTouchedListener(String key);
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
