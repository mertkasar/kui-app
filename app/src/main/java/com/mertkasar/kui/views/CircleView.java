package com.mertkasar.kui.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CircleView extends View {
    public static final String TAG = CircleView.class.getSimpleName();

    private int mInnerWidth;
    private int mOuterWidth;

    private Paint mOuterColor;
    private Paint mInnerColor;

    private RectF mOuterRect;
    private RectF mInnerRect;

    private float mRatio = 30;

    public CircleView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mInnerWidth = 3;
        mOuterWidth = 12;

        mOuterColor = new Paint();
        mOuterColor.setAntiAlias(true);
        mOuterColor.setStyle(Paint.Style.STROKE);
        mOuterColor.setStrokeWidth(mOuterWidth);
        mOuterColor.setColor(Color.parseColor("#4CAF50"));

        mInnerColor = new Paint();
        mInnerColor.setAntiAlias(true);
        mInnerColor.setStyle(Paint.Style.STROKE);
        mInnerColor.setStrokeWidth(mInnerWidth);
        mInnerColor.setColor(Color.parseColor("#F44336"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float outerSweepAngle = 360.f * mRatio / 100.f;

        canvas.drawArc(mInnerRect, 270, 360, false, mInnerColor);
        canvas.drawArc(mOuterRect, 270, outerSweepAngle, false, mOuterColor);
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        int shift = mOuterWidth / 2;

        mInnerRect = new RectF(shift, shift, w - shift, h - shift);
        mOuterRect = new RectF(shift, shift, w - shift, h - shift);
    }
}
