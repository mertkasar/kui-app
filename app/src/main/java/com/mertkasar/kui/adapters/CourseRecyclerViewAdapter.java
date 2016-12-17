package com.mertkasar.kui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.mertkasar.kui.R;
import com.mertkasar.kui.activities.CourseDetailActivity;
import com.mertkasar.kui.models.Course;

import java.util.List;

public class CourseRecyclerViewAdapter extends RecyclerView.Adapter<CourseRecyclerViewAdapter.ViewHolder> {

    private List<DataSnapshot> mDataSet;
    private Activity mActivity;

    public CourseRecyclerViewAdapter(List<DataSnapshot> dataSet, Activity activity) {
        mDataSet = dataSet;
        mActivity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        DataSnapshot current = mDataSet.get(position);

        holder.mKey = current.getKey();
        holder.mItem = current.getValue(Course.class);
        holder.mIdView.setText(holder.mItem.title);
        holder.mContentView.setText(holder.mItem.description);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, CourseDetailActivity.class);
                intent.putExtra(CourseDetailActivity.EXTRA_COURSE_KEY, holder.mKey);
                mActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public String mKey;
        public Course mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mIdView = (TextView) view.findViewById(R.id.course_title);
            mContentView = (TextView) view.findViewById(R.id.course_description);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
