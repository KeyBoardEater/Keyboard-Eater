package com.keyboardeater.widget;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FocusRotateView extends FocusBackgroundView implements ValueAnimator.AnimatorUpdateListener {

    private static final String TAG = "FocusRotateView";

    private final int strokeWidth;

    private static final @ColorInt
    int[] colors = new int[]{Color.parseColor("#66333333"), Color.parseColor("#66ffffff"),
            Color.parseColor("#cc666666")
    };

    private SweepFrameDrawable mFocusDrawable;

    private final ValueAnimator focusAnimator;

    private boolean isParentFocused;

    public FocusRotateView(Context context) {
        this(context, null);
    }

    public FocusRotateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusRotateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        strokeWidth = getResources().getDimensionPixelSize(R.dimen.cell_view_selector_stroke_width);
        focusAnimator = ValueAnimator.ofFloat(0f, 1f);
        focusAnimator.setRepeatCount(ValueAnimator.INFINITE);
        focusAnimator.setRepeatMode(ValueAnimator.RESTART);
        focusAnimator.setInterpolator(new LinearInterpolator());
        focusAnimator.setDuration(10000);
        focusAnimator.addUpdateListener(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.save();

        Drawable drawable = getBackground();

        Log.d(TAG, "onDraw drawable = " + drawable);

        if (drawable != null) {
            super.getDrawingRect(this.mTmpRect);
            drawable.setBounds(mTmpRect);
            drawable.draw(canvas);
        }

        canvas.restore();
    }

    @Override
    public void setRadius(float connerRadius) {
        this.connerRadius = connerRadius;

        mFocusDrawable = new SweepFrameDrawable(colors);

        mFocusDrawable.setStrokeWidth(strokeWidth);

        if (radiusWork) {
            mFocusDrawable.setCornerRadius(connerRadius + strokeWidth / 2);
        } else {
            mFocusDrawable.setCornerRadius(0);
        }

        mDrawableFocusBg = new StateListDrawable();
        mDrawableFocusBg.addState(new int[]{android.R.attr.state_focused}, mFocusDrawable);
        mDrawableFocusBg.addState(new int[]{android.R.attr.state_selected}, mFocusDrawable);
        mDrawableFocusBg.addState(new int[]{}, null);

        setBackgroundDrawable(mDrawableFocusBg);

        invalidate();
    }

    @Override
    public void setRadiusWork(boolean radiusWork) {
        this.radiusWork = radiusWork;

        mFocusDrawable = new SweepFrameDrawable(colors);

        mFocusDrawable.setStrokeWidth(strokeWidth);

        int strokeWidth = getResources().getDimensionPixelSize(R.dimen.cell_view_selector_stroke_width);

        if (radiusWork) {
            mFocusDrawable.setCornerRadius(connerRadius + strokeWidth / 2);
        } else {
            mFocusDrawable.setCornerRadius(0);
        }

        mDrawableFocusBg = new StateListDrawable();
        mDrawableFocusBg.addState(new int[]{android.R.attr.state_focused}, mFocusDrawable);
        mDrawableFocusBg.addState(new int[]{android.R.attr.state_selected}, mFocusDrawable);
        mDrawableFocusBg.addState(new int[]{}, null);

        setBackgroundDrawable(mDrawableFocusBg);

        invalidate();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        if (null != mFocusDrawable && isParentFocused) {
            float percentage = (Float) animation.getAnimatedValue();
            mFocusDrawable.setStartPosition(percentage);
            invalidate();
        }
    }

    @Override
    public void onParentFocusChanged(boolean isParentFocused) {
        Log.d(TAG, "onFocusChanged gainFocus = " + isParentFocused);
        this.isParentFocused = isParentFocused;

        setSelected(isParentFocused);

        if (isParentFocused) {
            mFocusDrawable.setStartPosition(0);

            if (!focusAnimator.isRunning()) {
                focusAnimator.start();
            }
        } else {
            if (!focusAnimator.isRunning()) {
                focusAnimator.cancel();
            }
        }
        super.onParentFocusChanged(isParentFocused);
    }


    private class SweepFrameDrawable extends Drawable {

        private ColorFilter mColorFilter;   // optional, set by the caller

        private int mAlpha = 0xFF;  // modified by the caller

        private float mConnerRadius;

        private float mStrokeWidth;

        private final Paint mStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

        private final @ColorInt
        int[] mGradientColors;

        private @ColorInt
        int[] mTempColors;

        private final ArgbEvaluator argbEvaluator = new ArgbEvaluator();

        private float[] mTempPositions;

        private final RectF mRect = new RectF();

        private float mStartPosition;

        private boolean debug = false;

        public SweepFrameDrawable(int... colors) {
            mGradientColors = colors;

            mStrokePaint.setStyle(Paint.Style.STROKE);

            textPaint.setColor(Color.BLACK);
            textPaint.setTextSize(30);
            textPaint.setTextAlign(Paint.Align.CENTER);
        }

        private void ensureSweepRing() {

            Rect bounds = getBounds();

            final float inset = mStrokeWidth * 0.5f;

            mRect.set(bounds.left - inset, bounds.top - inset,
                    bounds.right + inset, bounds.bottom + inset);


            final RectF r = new RectF(bounds);

            float x0 = r.left + r.width() * 0.5f;
            float y0 = r.top + r.height() * 0.5f;

            float start = getStartPosition();

            final int length = mGradientColors.length;

            float space = 1.0f / length;

            int fromColorIndex = (int) Math.floor((1 - start) / space) % length;

            int toColorIndex = (fromColorIndex + 1) % length;

            int startColor = calculateStartColor(start, mGradientColors);

            Log.d(TAG, "ensureSweepRing length = " + length + " start = " + start + " space = " + space + " toColorIndex = " + toColorIndex);

            if (mTempColors == null || mTempColors.length != length + 2) {
                mTempColors = new int[length + 2];
            }

            String mColorIndexStr = "[0.0f,";

            mTempColors[0] = startColor;

            for (int index = 1; index <= length; index++) {
                int currentIndex = (toColorIndex + index - 1) % length;
                mTempColors[index] = mGradientColors[currentIndex];
                mColorIndexStr += currentIndex + ",";
            }

            mColorIndexStr += "1.0f]";

            mTempColors[length + 1] = startColor;

            if (mTempPositions == null || mTempPositions.length != length + 2) {
                mTempPositions = new float[length + 2];
            }

            mTempPositions[0] = 0.0f;

            String mPositionStr = "[0.0f,";

            for (int index = 1; index <= length; index++) {
                mTempPositions[index] = (float) ((start - Math.floor(start / space) * space + (index - 1) * space) % 1.0f);
                mPositionStr += mTempPositions[index] + ",";
            }
            mTempPositions[length + 1] = 1.0f;

            mPositionStr += "1.0f]";

            Log.d(TAG, "ensureSweepRing mColorIndexStr = " + mColorIndexStr + " mTempPositions = " + mPositionStr);

            mStrokePaint.setShader(new SweepGradient(x0, y0, mTempColors, mTempPositions));
        }

        private int calculateStartColor(float start, int[] mGradientColors) {
            int length = mGradientColors.length;

            float space = 1.0f / length;

            int fromColorIndex = (int) Math.floor((1 - start) / space) % length;

            int toColorIndex = (fromColorIndex + 1) % length;

            int toColor = mGradientColors[toColorIndex];

            int fromColor = mGradientColors[fromColorIndex];

            float percentage = (1 - start - space * fromColorIndex) / space;

            int startColor = (int) argbEvaluator.evaluate(percentage, fromColor, toColor);

            Log.d(TAG, "calculateStartColor " + start + " percentage = " + percentage + " from [" + fromColorIndex + " - " + fromColor + "] to [" + toColorIndex + " - " + toColor + "] = " + startColor);

            return startColor;
        }

        public void setStartPosition(float startPosition) {
            mStartPosition = startPosition;
        }

        private float getStartPosition() {
            return mStartPosition;
        }

        @Override
        public void draw(@NonNull Canvas canvas) {
            ensureSweepRing();

            if (debug) {
                canvas.drawText(String.valueOf(Math.floor(getStartPosition() * 10) / 10), mRect.width() / 2, mRect.height() / 2, textPaint);
            }

            canvas.drawRoundRect(mRect, connerRadius + strokeWidth / 2, connerRadius + strokeWidth / 2, mStrokePaint);
        }

        @Override
        public void setAlpha(int alpha) {
            if (alpha != mAlpha) {
                mAlpha = alpha;
                invalidateSelf();
            }
        }

        @Override
        public void setColorFilter(@Nullable ColorFilter colorFilter) {
            if (colorFilter != mColorFilter) {
                mColorFilter = colorFilter;
                invalidateSelf();
            }
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        public void setCornerRadius(float connerRadius) {
            if (connerRadius != mConnerRadius) {
                mConnerRadius = connerRadius;
                invalidateSelf();
            }
        }

        public void setStrokeWidth(float mStrokeWidth) {
            if (mStrokeWidth != this.mStrokeWidth) {
                this.mStrokeWidth = mStrokeWidth;
                mStrokePaint.setStrokeWidth(mStrokeWidth);
                invalidateSelf();
            }
        }
    }
}
