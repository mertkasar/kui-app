package com.mertkasar.kui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.mertkasar.kui.R;
import com.mertkasar.kui.activities.CourseDetailActivity;
import com.mertkasar.kui.core.Database;
import com.mertkasar.kui.models.Answer;
import com.mertkasar.kui.models.Course;
import com.mertkasar.kui.models.User;

import java.util.List;

public class AnswerRecyclerViewAdapter extends RecyclerView.Adapter<AnswerRecyclerViewAdapter.ViewHolder> {

    private List<DataSnapshot> mDataSet;

    public AnswerRecyclerViewAdapter(List<DataSnapshot> dataSet) {
        mDataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_answer_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        DataSnapshot current = mDataSet.get(position);

        holder.mItem = current.getValue(Answer.class);
        holder.mAnswerView.setText(holder.mItem.choice);

        if (holder.mItem.is_correct) {
            holder.mView.setBackgroundResource(R.color.correct_ghost);
        } else {
            holder.mView.setBackgroundResource(R.color.wrong_ghost);
        }

        Database.getInstance().getUserByKey(holder.mItem.owner).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    holder.mOwnerNameView.setText(user.name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mOwnerNameView;
        public final TextView mAnswerView;
        public Answer mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mOwnerNameView = (TextView) view.findViewById(R.id.answer_owner);
            mAnswerView = (TextView) view.findViewById(R.id.answer);
        }
    }
}
