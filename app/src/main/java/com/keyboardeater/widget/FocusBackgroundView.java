package com.keyboardeater.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class FocusBackgroundView extends View {

    private static final String TAG = "FocusBackgroundView";

    protected StateListDrawable mDrawableFocusBg;

    protected final Rect mTmpRect;

    protected boolean radiusWork = true;

    protected float connerRadius = 0;

    public FocusBackgroundView(Context context) {
        this(context, null);
    }

    public FocusBackgroundView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusBackgroundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTmpRect = new Rect();
        setRadius(context.getResources().getDimension(R.dimen.cell_view_conner));
    }


    @Override
    protected void onDraw(Canvas canvas) {

        Drawable drawable = getBackground();

        if (drawable != null) {
            super.getDrawingRect(this.mTmpRect);
            Log.d(TAG, "left:" + mTmpRect.left + ",.mTmpRect.top" + mTmpRect.top + ",right:" + mTmpRect.right + ",bottom:" + mTmpRect.bottom);
            int paddingValue = (int) getResources().getDimension(R.dimen.cell_view_selector_stroke_width);
            drawable.setBounds(mTmpRect.left - paddingValue, mTmpRect.top - paddingValue,
                    mTmpRect.right + paddingValue, mTmpRect.bottom + paddingValue);
            drawable.draw(canvas);
        }
    }

    public void setRadius(float connerRadius) {

        this.connerRadius = connerRadius;

        GradientDrawable transparentShape = new GradientDrawable();

        GradientDrawable focusShape = new GradientDrawable();

        int strokeWidth = getResources().getDimensionPixelSize(R.dimen.cell_view_selector_stroke_width);

        focusShape.setStroke(strokeWidth, Color.WHITE);
        if (radiusWork) {
            focusShape.setCornerRadius(connerRadius + strokeWidth / 2);
        } else {
            focusShape.setCornerRadius(0);
        }

        mDrawableFocusBg = new StateListDrawable();
        mDrawableFocusBg.addState(new int[]{android.R.attr.state_focused}, focusShape);
        mDrawableFocusBg.addState(new int[]{}, transparentShape);

        setBackgroundDrawable(mDrawableFocusBg);

        invalidate();
    }

    public void setRadiusWork(boolean radiusWork) {
        this.radiusWork = radiusWork;

        GradientDrawable transparentShape = new GradientDrawable();

        GradientDrawable focusShape = new GradientDrawable();

        int strokeWidth = getResources().getDimensionPixelSize(R.dimen.cell_view_selector_stroke_width);

        focusShape.setStroke(strokeWidth, Color.WHITE);
        if (radiusWork) {
            focusShape.setCornerRadius(connerRadius + strokeWidth / 2);
        } else {
            focusShape.setCornerRadius(0);
        }

        mDrawableFocusBg = new StateListDrawable();
        mDrawableFocusBg.addState(new int[]{android.R.attr.state_focused}, focusShape);
        mDrawableFocusBg.addState(new int[]{}, transparentShape);

        setBackgroundDrawable(mDrawableFocusBg);

        invalidate();
    }

    public void onParentFocusChanged(boolean isParentFocused) {

    }
}
