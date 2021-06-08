package com.keyboardeater.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class ExpandWhileFocusedView extends LinearLayout {

    private final static String TAG = "ExpandWhileFocusedView";

    protected final static long SCALE_DURATION = 300;

    private final ImageView iconView;

    private final TextView titleView;

    private int expandHeight = 0;

    private int expandWidth = 0;

    private int unExpandWidth = 0;

    private float widthScaleParam = 1.0f;

    protected ValueAnimator focusValueAnimator;

    protected ValueAnimator unfocusValueAnimator;

    public ExpandWhileFocusedView(Context context) {
        this(context, null);
    }

    public ExpandWhileFocusedView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandWhileFocusedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(HORIZONTAL);

        setBackgroundResource(R.drawable.expand_view_background_selector);

        iconView = new ImageView(context);
        iconView.setId(R.id.icon_id);
        iconView.setImageResource(R.mipmap.statusbar_icon_search);
        iconView.setScaleType(ImageView.ScaleType.FIT_XY);

        titleView = new TextView(context);
        titleView.setTextColor(Color.WHITE);
        titleView.setText("搜索");
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimensionPixelSize(R.dimen.expand_text_size));

        LayoutParams iconLayoutParams = new LayoutParams(getDimensionPixelSize(R.dimen.expand_view_icon_size), getDimensionPixelSize(R.dimen.expand_view_icon_size));
        iconLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        iconLayoutParams.setMargins(getDimensionPixelSize(R.dimen.expand_icon_horizontal_margin), getDimensionPixelSize(R.dimen.expand_icon_horizontal_margin),
                getDimensionPixelSize(R.dimen.expand_icon_horizontal_margin), getDimensionPixelSize(R.dimen.expand_icon_horizontal_margin));
        addView(iconView, iconLayoutParams);

        LayoutParams titleLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        titleLayoutParams.gravity = Gravity.CENTER_VERTICAL;
        titleLayoutParams.setMargins(getDimensionPixelSize(R.dimen.expand_text_left_margin), 0,
                getDimensionPixelSize(R.dimen.expand_text_right_margin), 0);
        addView(titleView, titleLayoutParams);

        focusValueAnimator = ValueAnimator.ofFloat(0, 1);
        focusValueAnimator.setDuration(SCALE_DURATION);
        focusValueAnimator.setInterpolator(new OvershootInterpolator());
        focusValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                widthScaleParam = (float) valueAnimator.getAnimatedValue();
                requestLayout();
            }
        });

        unfocusValueAnimator = ValueAnimator.ofFloat(1, 0);
        unfocusValueAnimator.setDuration(SCALE_DURATION);
        unfocusValueAnimator.setInterpolator(new LinearInterpolator());
        unfocusValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                widthScaleParam = (float) valueAnimator.getAnimatedValue();
                requestLayout();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (couldDoScaleAnimation()) {

            int height = expandHeight;

            int width = (int) (unExpandWidth + widthScaleParam * (expandWidth - unExpandWidth));

            Log.d(TAG, "onMeasure width = " + width);

            super.onMeasure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private float getDimension(int id) {
        return getResources().getDimension(id);
    }

    private int getDimensionPixelSize(int id) {
        return getResources().getDimensionPixelSize(id);
    }

    public void setExpandParams(int expandHeight, int expandWidth, int unExpandWidth) {
        if (expandWidth < unExpandWidth) {
            Log.w(TAG, "expand width must be large than unExpand width");
            return;
        } else if (expandHeight <= 0 || expandWidth <= 0 || unExpandWidth <= 0) {
            Log.w(TAG, "expand params must be large than 0");
            return;
        }

        this.expandHeight = expandHeight;
        this.expandWidth = expandWidth;
        this.unExpandWidth = unExpandWidth;

        getLayoutParams().height = expandHeight;
        if (isFocused()) {
            getLayoutParams().width = expandWidth;
        } else {
            getLayoutParams().width = unExpandWidth;
        }

        int iconSize = expandHeight / 2;
        iconView.getLayoutParams().width = iconSize;
        iconView.getLayoutParams().height = iconSize;
        ((LayoutParams) iconView.getLayoutParams()).gravity = Gravity.CENTER_VERTICAL;
        ((LayoutParams) iconView.getLayoutParams()).setMargins(iconSize / 2, 0, iconSize / 2, 0);

        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, iconSize);
        titleView.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
        titleView.getLayoutParams().width = unExpandWidth - getLayoutParams().height - getDimensionPixelSize(R.dimen.expand_text_left_margin) - getDimensionPixelSize(R.dimen.expand_text_right_margin);
        ((LayoutParams) titleView.getLayoutParams()).setMargins(getDimensionPixelSize(R.dimen.expand_text_left_margin), 0, getDimensionPixelSize(R.dimen.expand_text_right_margin), 0);

        StateListDrawable stateListDrawable = new StateListDrawable();
        GradientDrawable unFocusedDrawable = new GradientDrawable();
        unFocusedDrawable.setCornerRadius(expandHeight / 2);
        unFocusedDrawable.setColor(Color.parseColor("#66ffffff"));

        GradientDrawable focusedDrawable = new GradientDrawable();
        focusedDrawable.setCornerRadius(expandHeight / 2);
        focusedDrawable.setColor(Color.parseColor("#0096ee"));
        focusedDrawable.setStroke(getDimensionPixelSize(R.dimen.expand_view_stroke), Color.WHITE);

        stateListDrawable.addState(new int[]{}, unFocusedDrawable);
        stateListDrawable.addState(new int[]{android.R.attr.state_focused}, focusedDrawable);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        doScaleAnimation(gainFocus);
    }

    private void doScaleAnimation(boolean gainFocus) {
        if (!couldDoScaleAnimation()) {
            return;
        }
        Log.d(TAG, "doScaleAnimation gainFocus = " + gainFocus);
        if (gainFocus) {
            if (unfocusValueAnimator.isRunning()) {
                unfocusValueAnimator.cancel();
            }
            if (!focusValueAnimator.isRunning()) {
                focusValueAnimator.start();
            }
        } else {
            if (focusValueAnimator.isRunning()) {
                focusValueAnimator.cancel();
            }
            if (!unfocusValueAnimator.isRunning()) {
                unfocusValueAnimator.start();
            }
        }
    }

    public void setIconUrl(String iconUrl) {

    }

    public void setIconSrc(int srcId) {

    }

    public void setText(String titleText) {
        titleView.setText(titleText);
    }

    private boolean couldDoScaleAnimation() {
        return expandWidth > 0 && expandHeight > 0 && unExpandWidth < expandWidth;
    }
}
