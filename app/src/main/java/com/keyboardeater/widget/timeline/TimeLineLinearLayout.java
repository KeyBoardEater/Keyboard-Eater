package com.keyboardeater.widget.timeline;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.keyboardeater.widget.timeline.bean.TimeLineExampleBean;
import com.keyboardeater.widget.timeline.view.TimeLineViewGroup;

import java.util.ArrayList;
import java.util.List;

public class TimeLineLinearLayout extends TimeLineViewGroup<TimeLineExampleBean> {

    private final static float DEFAULT_ITEM_SCALE = 222f / 396;

    private static final String TAG = "TimeLineLinearLayout";

    private List<TimeLineExampleBean> timeLineBeen = new ArrayList<>();

    public TimeLineLinearLayout(Context context) {
        super(context);
    }

    public TimeLineLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TimeLineLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View view;

        float parentWidth = getMeasuredWidth();

        Log.d(TAG, "onLayout parentWidth = " + parentWidth);

        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {
            view = getChildAt(childIndex);

            int left = (int) (childIndex * parentWidth / column + gap / 2);
            int top = timeLineAreaHeight;
            int right = (int) ((childIndex + 1) * parentWidth / column - gap / 2);
            int bottom = getMeasuredHeight();

            Log.d(TAG, "onLayout [" + left + " , " + top + " , " + right + " , " + bottom + "]");

            view.layout(left, top, right, bottom);
        }
    }

    @Override
    protected String getTagStr() {
        return TAG;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int parentMeasureWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);

        float itemWidth = (parentMeasureWidth - column * gap) / column;

        float itemHeight = itemWidth * DEFAULT_ITEM_SCALE;

        Log.d(TAG, "parentMeasureWidth = " + parentMeasureWidth + " itemWidth = " + itemWidth + " itemHeight = " + itemHeight + " timeLineAreaHeight = " + timeLineAreaHeight);

        int parentMeasureHeight = MeasureSpec.makeMeasureSpec((int) (itemHeight + timeLineAreaHeight), MeasureSpec.EXACTLY);

        setMeasuredDimension(parentMeasureWidth, parentMeasureHeight);

        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void measureChildren(int itemWidth, int itemHeight) {
        View view;

        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {
            view = getChildAt(childIndex);

            view.measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY));
        }
    }

    @Override
    public void addTimeLine(List<TimeLineExampleBean> timeLines) {
        this.timeLineBeen.clear();
        this.timeLineBeen.addAll(timeLines);
    }
}
