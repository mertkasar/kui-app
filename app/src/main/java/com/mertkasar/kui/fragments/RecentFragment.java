package com.mertkasar.kui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mertkasar.kui.R;
import com.mertkasar.kui.adapters.QuestionRecyclerViewAdapter;
import com.mertkasar.kui.core.Database;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnQuestionClickedListener}
 * interface.
 */
public class RecentFragment extends Fragment {
    public static final String TAG = RecentFragment.class.getSimpleName();

    private static final String ARG_USER_KEY = "user_key";

    private OnQuestionClickedListener mListener;

    private String mUserKey;

    private ArrayList<DataSnapshot> mQuestionList;
    private QuestionRecyclerViewAdapter mAdapter;

    private Database mDB;

    public RecentFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static RecentFragment newInstance(final String userKey) {
        RecentFragment fragment = new RecentFragment();

        Bundle args = new Bundle();
        args.putString(ARG_USER_KEY, userKey);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserKey = getArguments().getString(ARG_USER_KEY);

        mQuestionList = new ArrayList<>();
        mAdapter = new QuestionRecyclerViewAdapter(mQuestionList, mListener);

        mDB = Database.getInstance();

        getItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_list, container, false);

        RecyclerView recyclerView = (RecyclerView) view;
        refreshItems();
        recyclerView.setAdapter(mAdapter);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof RecentFragment.OnQuestionClickedListener) {
            mListener = (RecentFragment.OnQuestionClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnQuestionClickedListener");
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
    public interface OnQuestionClickedListener {
        // TODO: Update argument type and name
        void onQuestionClickedListener(String key);
    }

    public void refreshItems() {
        mQuestionList.clear();
        mAdapter.notifyDataSetChanged();

        getItems();
    }

    private void getItems() {
        mDB.getUserRecentByKey(mUserKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot questionSnap : dataSnapshot.getChildren()) {
                        mDB.getQuestionByKey(questionSnap.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    mQuestionList.add(dataSnapshot);
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
