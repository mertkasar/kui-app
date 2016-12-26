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
import com.mertkasar.kui.activities.AnswersActivity;
import com.mertkasar.kui.models.Question;
import com.mertkasar.kui.views.CircleView;

import java.util.List;

public class QuestionRecyclerViewAdapter extends RecyclerView.Adapter<QuestionRecyclerViewAdapter.ViewHolder> {

    private List<DataSnapshot> mDataSet;
    private OnClickQuestionListener mListener;

    public QuestionRecyclerViewAdapter(List<DataSnapshot> dataSet, OnClickQuestionListener listener) {
        mDataSet = dataSet;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_question_list, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        DataSnapshot current = mDataSet.get(position);

        holder.mKey = current.getKey();
        holder.mItem = current.getValue(Question.class);

        int ratio = (int) (holder.mItem.correct_count / (double) holder.mItem.answer_count * 100);
        holder.mPercentage.setText("%" + ratio);
        holder.mCircle.setRatio(ratio);

        holder.mIdView.setText(holder.mItem.title);
        holder.mContentView.setText(holder.mItem.description);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onClickQuestionListener(holder.mKey);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final CircleView mCircle;
        public final TextView mPercentage;
        public final TextView mIdView;
        public final TextView mContentView;
        public String mKey;
        public Question mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCircle = (CircleView) view.findViewById(R.id.primary_action);
            mPercentage = (TextView) view.findViewById(R.id.percentage);
            mIdView = (TextView) view.findViewById(R.id.question_title);
            mContentView = (TextView) view.findViewById(R.id.question_description);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public interface OnClickQuestionListener {
        void onClickQuestionListener(final String key);
    }
}
