package com.mertkasar.kui.fragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mertkasar.kui.R;
import com.mertkasar.kui.activities.CourseDetailActivity;
import com.mertkasar.kui.activities.DemoActivity;
import com.mertkasar.kui.activities.NewCourseActivity;
import com.mertkasar.kui.adapters.CoursesPagerAdapter;
import com.mertkasar.kui.core.App;
import com.mertkasar.kui.core.Database;

public class NavCoursesFragment extends Fragment {
    public static final String TAG = NavCoursesFragment.class.getSimpleName();

    private static final String ARG_PREFERRED_TAB = "preferred-tab";

    private CoursesPagerAdapter mCoursesPagerAdapter;

    private FloatingActionButton mSharedFab;
    private ViewPager mViewPager;

    private AlertDialog mSubscribeDialog;

    private App mApp;
    private Database mDB;

    @SuppressWarnings("unused")
    public static NavCoursesFragment newInstance(int preferredTab) {
        NavCoursesFragment fragment = new NavCoursesFragment();
        Bundle args = new Bundle();

        args.putInt(ARG_PREFERRED_TAB, preferredTab);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mApp = App.getInstance();
        mDB = Database.getInstance();

        final EditText editText = new EditText(getActivity());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_title_subscribe)
                .setMessage(R.string.dialog_message_subscribe)
                .setView(editText)
                .setPositiveButton(R.string.button_subscribe,
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog, int whichButton) {
                                final String subsKey = editText.getText().toString();

                                editText.getText().clear();
                                dialog.dismiss();

                                final ProgressDialog loadingDialog = ProgressDialog.show(getActivity(), "", getString(R.string.dialog_loading), true);

                                mDB.getCourseBySubsKey(subsKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.exists()) {
                                            loadingDialog.dismiss();
                                            Toast.makeText(getActivity(), R.string.toast_wrong_subscription_key, Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        if (dataSnapshot.getChildrenCount() != 1)
                                            throw new IllegalArgumentException("Multiple subs keys: " + subsKey);

                                        DataSnapshot courseSnapshot = dataSnapshot.getChildren().iterator().next();
                                        final String courseKey = courseSnapshot.getKey();

                                        mDB.getSubscribedCourse(mApp.uid, courseKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                            private void redirectCourseDetail(String key) {
                                                Intent intent = new Intent(getActivity(), CourseDetailActivity.class);
                                                intent.putExtra(CourseDetailActivity.EXTRA_COURSE_KEY, key);
                                                startActivity(intent);

                                                Log.d(TAG, "redirectCourseDetail: " + key);
                                            }

                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    redirectCourseDetail(courseKey);

                                                    loadingDialog.dismiss();

                                                    Toast.makeText(getActivity(), R.string.toast_already_subscribed, Toast.LENGTH_SHORT).show();

                                                    return;
                                                }

                                                mDB.subscribeUser(mApp.uid, courseKey).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        // TODO: Course not found after this line
                                                        redirectCourseDetail(courseKey);

                                                        loadingDialog.dismiss();

                                                        Toast.makeText(getActivity(), R.string.toast_subscribe, Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        })
                .setNegativeButton(R.string.button_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                editText.getText().clear();
                                dialog.dismiss();
                            }
                        });

        mSubscribeDialog = builder.create();
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

        mCoursesPagerAdapter = new CoursesPagerAdapter(getChildFragmentManager(), getActivity());
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

        TabLayout tabs = (TabLayout) view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(mViewPager);

        Bundle args = getArguments();
        if (args != null) {
            int preferredTab = args.getInt(ARG_PREFERRED_TAB);
            mViewPager.setOffscreenPageLimit(2);
            mViewPager.setCurrentItem(preferredTab);
        }

        handleFAB();
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
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mSubscribeDialog.show();
                    }
                });
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
