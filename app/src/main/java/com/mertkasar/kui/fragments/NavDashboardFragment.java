package com.mertkasar.kui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ViewFlipper;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mertkasar.kui.R;
import com.mertkasar.kui.activities.QuizActivity;
import com.mertkasar.kui.adapters.QuestionRecyclerViewAdapter;
import com.mertkasar.kui.core.App;
import com.mertkasar.kui.core.Database;

import java.util.ArrayList;

public class NavDashboardFragment extends Fragment implements QuestionRecyclerViewAdapter.OnClickQuestionListener {
    public static final String TAG = NavDashboardFragment.class.getSimpleName();

    private App mApp;
    private Database mDB;

    private String mUserKey;
    private long mSize;

    private ArrayList<DataSnapshot> mQuestionList;
    private QuestionRecyclerViewAdapter mAdapter;

    private FloatingActionButton mFAB;
    private ViewFlipper mViewFlipper;

    public NavDashboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApp = App.getInstance();
        mDB = Database.getInstance();

        mUserKey = mApp.getUID();

        mQuestionList = new ArrayList<>();
        mAdapter = new QuestionRecyclerViewAdapter(mQuestionList, this);

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

        mViewFlipper = (ViewFlipper) view.findViewById(R.id.view_flipper);

        mFAB = (FloatingActionButton) view.findViewById(R.id.fab);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), QuizActivity.class);
                intent.putExtra(QuizActivity.EXTRA_QUIZ_MODE, QuizActivity.QUIZ_MODE_RECENT);
                startActivity(intent);
            }
        });
        mFAB.hide();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), layoutManager.getOrientation());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);
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
                    mSize = dataSnapshot.getChildrenCount();

                    for (DataSnapshot questionSnap : dataSnapshot.getChildren()) {
                        mDB.getQuestionByKey(questionSnap.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    mQuestionList.add(dataSnapshot);

                                    if (mQuestionList.size() == mSize) {
                                        mAdapter.notifyDataSetChanged();
                                        mFAB.show();

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
                    if (mSize == 0) {
                        mViewFlipper.setDisplayedChild(2);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClickQuestionListener(String key) {
        Intent intent = new Intent(getActivity(), QuizActivity.class);

        intent.putExtra(QuizActivity.EXTRA_QUIZ_MODE, QuizActivity.QUIZ_MODE_SINGLE);
        intent.putExtra(QuizActivity.EXTRA_QUESTION_KEY, key);

        startActivity(intent);
    }
}
