package com.mertkasar.kui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mertkasar.kui.R;
import com.mertkasar.kui.adapters.QuestionRecyclerViewAdapter;
import com.mertkasar.kui.core.App;
import com.mertkasar.kui.core.Database;
import com.mertkasar.kui.models.Course;
import com.mertkasar.kui.models.User;

import java.util.ArrayList;

public class CourseDetailActivity extends AppCompatActivity implements QuestionRecyclerViewAdapter.OnClickQuestionListener {
    public static final String TAG = CourseDetailActivity.class.getSimpleName();

    public static final String EXTRA_COURSE_KEY = "course-key";
    private static final int RC_NEW_QUESTION = 1;

    private static final int DISPLAY_MODE_OWNER = 1;
    private static final int DISPLAY_MODE_SUBSCRIBER = 2;
    private static final int DISPLAY_MODE_VISITOR = 3;

    private String mCourseKey;
    private String mEnrollCode;
    private int mDisplayMode;

    private ViewSwitcher mViewSwitcher;
    private ViewFlipper mQuestionsViewFlipper;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;

    private FloatingActionButton mFAB;
    private TextView mDescriptionTextView;
    private TextView mSubtitleTextView;
    private TextView mOwnerTitleTextView;
    private TextView mQuestionCountTextView;
    private TextView mSubscriberCountTextView;

    private App mApp;
    private Database mDB;

    private ArrayList<DataSnapshot> mDataSet;
    private QuestionRecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        }

        mViewSwitcher = (ViewSwitcher) findViewById(R.id.view_switcher_main);
        mQuestionsViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper_questions);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);

        mCourseKey = getIntent().getStringExtra(EXTRA_COURSE_KEY);
        if (mCourseKey == null) {
            throw new IllegalArgumentException("Must pass " + EXTRA_COURSE_KEY);
        }

        mFAB = (FloatingActionButton) findViewById(R.id.fab);
        mFAB.hide();

        mDescriptionTextView = (TextView) findViewById(R.id.description);
        mSubtitleTextView = (TextView) findViewById(R.id.toolbar_subtitle);
        mOwnerTitleTextView = (TextView) findViewById(R.id.text_course_detail_owner);
        mQuestionCountTextView = (TextView) findViewById(R.id.text_course_detail_question_count);
        mSubscriberCountTextView = (TextView) findViewById(R.id.text_course_detail_subscriber_count);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);

        mApp = App.getInstance();
        mDB = Database.getInstance();

        mDataSet = new ArrayList<>();
        mAdapter = new QuestionRecyclerViewAdapter(mDataSet, this);
        recyclerView.setAdapter(mAdapter);

        getCourse();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent sendIntent = new Intent();

                sendIntent.setAction(Intent.ACTION_SEND);
                String shareMessage = String.format(getString(R.string.content_share), mEnrollCode);
                sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                sendIntent.setType("text/plain");

                startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.chooser_share)));

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_NEW_QUESTION:
                if (resultCode == RESULT_OK) {
                    refreshQuestions();
                    int currentQuestionCount = Integer.parseInt(mQuestionCountTextView.getText().toString());
                    mQuestionCountTextView.setText(String.valueOf(currentQuestionCount + 1));
                }
                break;
            default:
                break;
        }
    }

    private void refreshQuestions() {
        mQuestionsViewFlipper.setDisplayedChild(0);

        mDataSet.clear();
        mAdapter.notifyDataSetChanged();

        mDB.getQuestionsByCourseKey(mCourseKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final long size = dataSnapshot.getChildrenCount();

                    for (DataSnapshot courseSnap : dataSnapshot.getChildren()) {
                        mDB.getQuestionByKey(courseSnap.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    mDataSet.add(dataSnapshot);

                                    if (mDataSet.size() == size) {
                                        mAdapter.notifyDataSetChanged();
                                        mQuestionsViewFlipper.showNext();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                } else {
                    mQuestionsViewFlipper.setDisplayedChild(2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void getDisplayMode(String key, Course course) {
        final String uid = mApp.getUID();

        if (uid.equals(course.owner)) {
            onDisplayModeOwner();
        } else {
            mDB.getSubscribedCourse(uid, key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists())
                        onDisplayModeSubscriber();
                    else
                        onDisplayModeVisitor();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void onDisplayModeSubscriber() {
        mDisplayMode = DISPLAY_MODE_SUBSCRIBER;

        mFAB.setImageResource(R.drawable.ic_play);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseDetailActivity.this, QuizActivity.class);

                intent.putExtra(QuizActivity.EXTRA_QUIZ_MODE, QuizActivity.QUIZ_MODE_COURSE);
                intent.putExtra(QuizActivity.EXTRA_COURSE_KEY, mCourseKey);

                startActivity(intent);
            }
        });

        mFAB.show();
    }

    private void onDisplayModeOwner() {
        mDisplayMode = DISPLAY_MODE_OWNER;

        mFAB.setImageResource(R.drawable.ic_add);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseDetailActivity.this, NewQuestionActivity.class);
                intent.putExtra(NewQuestionActivity.EXTRA_COURSE_KEY, mCourseKey);
                startActivityForResult(intent, RC_NEW_QUESTION);
            }
        });

        mFAB.show();
    }

    private void onDisplayModeVisitor() {
        mDisplayMode = DISPLAY_MODE_VISITOR;

        mFAB.setImageResource(R.drawable.ic_subscribe);
        mFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFAB.hide();

                final ProgressDialog subscribeDialog = ProgressDialog.show(CourseDetailActivity.this, "", getString(R.string.dialog_message_sunscribing), true);

                mDB.subscribeUser(mApp.getUID(), mCourseKey).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onDisplayModeSubscriber();

                        int currentQuestionCount = Integer.parseInt(mSubscriberCountTextView.getText().toString());
                        mSubscriberCountTextView.setText(String.valueOf(currentQuestionCount + 1));

                        subscribeDialog.dismiss();
                    }
                });
            }
        });

        mFAB.show();
    }

    private void getCourse() {
        mDB.getCourseByKey(mCourseKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Course course = dataSnapshot.getValue(Course.class);
                    bindLayout(course);
                } else {
                    Log.d(TAG, "Course not found: " + dataSnapshot.toString());
                    Toast.makeText(CourseDetailActivity.this, R.string.error_course_not_found, Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void bindLayout(Course course) {
        getDisplayMode(mCourseKey, course);

        mEnrollCode = course.getSubsKey();

        mCollapsingToolbarLayout.setTitleEnabled(true);
        mCollapsingToolbarLayout.setTitle(course.title);

        mDescriptionTextView.setText(course.description);
        mSubtitleTextView.setText("Enroll code: " + mEnrollCode);
        mQuestionCountTextView.setText(course.question_count.toString());
        mSubscriberCountTextView.setText(course.subscriber_count.toString());

        mDB.getUserByKey(course.owner).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                mOwnerTitleTextView.setText(user.name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        refreshQuestions();

        mViewSwitcher.showNext();
    }

    @Override
    public void onClickQuestionListener(String key) {
        if (mDisplayMode == DISPLAY_MODE_OWNER) {
            Intent intent = new Intent(CourseDetailActivity.this, AnswersActivity.class);
            intent.putExtra(AnswersActivity.EXTRA_QUESTION_KEY, key);
            startActivity(intent);
        }
    }
}
