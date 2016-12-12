package com.mertkasar.kui.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mertkasar.kui.R;
import com.mertkasar.kui.core.Database;
import com.mertkasar.kui.models.Course;

public class NewCourseActivity extends AppCompatActivity {
    public static final String TAG = NewCourseActivity.class.getSimpleName();

    private String userKey;

    private TextView title;
    private TextView description;
    private Switch publicSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_course);

        userKey = getIntent().getStringExtra("EXTRA_USER_KEY");
        if (userKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_USER_KEY");
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }

        title = (TextView) findViewById(R.id.title_edit_text);
        description = (TextView) findViewById(R.id.description_edit_text);
        publicSwitch = (Switch) findViewById(R.id.is_public_switch);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_course, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_course_send:
                createNewCourse();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNewCourse() {
        Course newCourse = new Course(userKey, title.getText().toString(), description.getText().toString(), publicSwitch.isChecked());

        Database.getInstance().createCourse(newCourse).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                setResult(RESULT_OK);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, e.toString());

                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}
