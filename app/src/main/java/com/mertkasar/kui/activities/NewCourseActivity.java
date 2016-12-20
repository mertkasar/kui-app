package com.mertkasar.kui.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mertkasar.kui.R;
import com.mertkasar.kui.core.Database;
import com.mertkasar.kui.models.Course;

import java.util.HashMap;

public class NewCourseActivity extends AppCompatActivity {
    public static final String TAG = NewCourseActivity.class.getSimpleName();

    private String userKey;

    private TextView title;
    private TextView description;
    private Switch publicSwitch;

    private ProgressDialog mCreatingDialog;

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
            actionBar.setHomeAsUpIndicator(R.drawable.ic_close_light);
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
        mCreatingDialog = ProgressDialog.show(NewCourseActivity.this, "", getString(R.string.dialog_message_creating), true);

        Database db = Database.getInstance();

        final String courseKey = db.getCourses().push().getKey();

        Course newCourse = new Course(userKey, title.getText().toString(), description.getText().toString(), publicSwitch.isChecked());

        HashMap<String, Object> updateBatch = new HashMap<>();

        updateBatch.put("courses/" + courseKey, newCourse);
        updateBatch.put("user_courses/" + newCourse.owner + "/" + courseKey, true);

        db.getDB().updateChildren(updateBatch).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onFinish(courseKey);
            }
        });
    }

    private void onFinish(String createdCourseKey) {
        Intent intent = new Intent(this, CourseDetailActivity.class);
        intent.putExtra(CourseDetailActivity.EXTRA_COURSE_KEY, createdCourseKey);
        startActivity(intent);

        Toast.makeText(NewCourseActivity.this, R.string.toast_create_course_success, Toast.LENGTH_SHORT).show();

        mCreatingDialog.dismiss();

        finish();
    }
}
