package com.mertkasar.kui.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mertkasar.kui.R;
import com.mertkasar.kui.adapters.QuestionRecyclerViewAdapter;
import com.mertkasar.kui.core.App;
import com.mertkasar.kui.core.Database;

import java.util.ArrayList;

public class NavDashboardFragment extends Fragment {
    public static final String TAG = NavDashboardFragment.class.getSimpleName();

    private App mApp;
    private Database mDB;

    private String mUserKey;

    private ArrayList<DataSnapshot> mQuestionList;
    private QuestionRecyclerViewAdapter mAdapter;

    public NavDashboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApp = App.getInstance();
        mDB = Database.getInstance();

        mUserKey = mApp.uid;

        mQuestionList = new ArrayList<>();
        mAdapter = new QuestionRecyclerViewAdapter(mQuestionList, getActivity());

        getItems();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nav_dashboard, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Dashboard");

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recent_questions_list);
        recyclerView.setAdapter(mAdapter);
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
