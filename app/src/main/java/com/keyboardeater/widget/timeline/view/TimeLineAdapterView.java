package com.keyboardeater.widget.timeline.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TimeLineAdapterView extends ViewGroup {

    private static final String TAG = "TimeLineAdapterView";

    public static final int SCROLL_DURATION = 300;

    public static final int STATE_SCROLLING = RecyclerView.SCROLL_STATE_SETTLING;

    public static final int STATE_SCROLL_IDLE = RecyclerView.SCROLL_STATE_IDLE;

    private int scrollOffset;

    private final FloorScroller mFloorScroller;

    private OnScrollStateChangeListener onScrollStateChangeListener;

    private int scrollState;

    private Adapter mAdapter = null;

    private boolean shouldNotifyItemScrollIdle;

    private int itemWidth = 0;

    private AtomicBoolean isLayout = new AtomicBoolean();

    private List<View> mRecycler = new ArrayList<>();

    private int minOffset;

    private int maxOffset;

    private int firstChildPositionInAdapter;

    public TimeLineAdapterView(Context context) {
        this(context, null);
    }

    public TimeLineAdapterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeLineAdapterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scrollOffset = 0;
        mFloorScroller = new FloorScroller(context);
        scrollState = STATE_SCROLL_IDLE;
        shouldNotifyItemScrollIdle = false;
        firstChildPositionInAdapter = 0;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        log("onLayout");

        if (null != mAdapter && isLayout.compareAndSet(false, true)) {
            fillChild();
            dispatchLayout();
            checkIfNeedRecycleChild();
            isLayout.set(false);
        }

    }

    private void checkIfNeedRecycleChild() {
        if (null != mAdapter) {

            final float gap = mAdapter.getGap();

            final int right = getRight();

            final int left = getRight();

            log("checkIfNeedRecycleChild before child count = " + getChildCount() + " parent left = " + left + " parent right = " + right);

            for (int childIndex = getChildCount() - 1; childIndex >= 0; childIndex--) {
                View lastChildView = getChildAt(childIndex);

                log("checkIfNeedRecycleChild child index = " + childIndex + " child left = " + (lastChildView.getLeft() - gap / 2));
                if (lastChildView.getLeft() - gap / 2 > getRight()) {
                    removeView(lastChildView);
                    mRecycler.add(lastChildView);
                } else {
                    break;
                }
            }

            for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {
                View frontChildView = getChildAt(childIndex);

                log("checkIfNeedRecycleChild child index = " + childIndex + " right = " + (frontChildView.getRight() + gap / 2));
                if (frontChildView.getRight() + gap / 2 < getLeft()) {
                    removeView(frontChildView);
                    mRecycler.add(frontChildView);
                } else {
                    break;
                }
            }

            log("checkIfNeedRecycleChild after child count = " + getChildCount());
        }


    }

    private void fillChild() {
        if (null != mAdapter) {

            log("fillChild before child count = " + getChildCount());

            float gap = mAdapter.getGap();

            float lastChildRight = 0;

            int lastChildPositionInAdapter = firstChildPositionInAdapter + getChildCount() - 1;

            if (getChildCount() > 0) {

                lastChildRight = getChildAt(getChildCount() - 1).getRight() + gap / 2;
            }

            while (lastChildRight <= getRight() && lastChildPositionInAdapter - 1 < mAdapter.getItemCount()) {
                View childView = getChildFromRecycler();

                addView(childView);

                mAdapter.bindChildView(childView, ++lastChildPositionInAdapter);

                lastChildRight += itemWidth + gap;
            }

            float firstChildLeft;

            if (getChildCount() > 0) {

                firstChildLeft = getChildAt(0).getLeft() - gap / 2;

                while (firstChildLeft >= 0) {
                    View childView = getChildFromRecycler();

                    addView(childView, 0);

                    mAdapter.bindChildView(childView, --firstChildPositionInAdapter);

                    firstChildLeft -= itemWidth + gap;
                }
            }

            log("fillChild after child count = " + getChildCount());
        }

    }

    private View getChildFromRecycler() {
        View childView = null;
        for (View recyclerView : mRecycler) {
            if (null != recyclerView && recyclerView.getParent() == null) {
                childView = recyclerView;
                break;
            }
        }
        if (childView == null) {
            childView = mAdapter.getChildView(this);
        }

        return childView;
    }

    public void setAdapter(Adapter adapter) {
        this.mAdapter = adapter;
        requestLayout();
    }

    protected String getTagStr() {
        return TAG;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int parentMeasureWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        if (null != mAdapter) {
            int timeLineAreaHeight = mAdapter.getTimeLineAreaHeight();
            int gap = mAdapter.getGap();
            int column = mAdapter.getColumn();
            float itemScale = mAdapter.getItemScale();

            itemWidth = (parentMeasureWidth - column * gap) / column;

            minOffset = 0;

            maxOffset = parentMeasureWidth / column * (mAdapter.getItemCount() - mAdapter.getColumn());

            int itemHeight = (int) (itemWidth * itemScale);

            Log.d(TAG, "parentMeasureWidth = " + parentMeasureWidth + " itemWidth = " + itemWidth + " itemHeight = " + itemHeight + " timeLineAreaHeight = " + timeLineAreaHeight);

            int parentMeasureHeight = MeasureSpec.makeMeasureSpec(itemHeight + timeLineAreaHeight, MeasureSpec.EXACTLY);

            setMeasuredDimension(parentMeasureWidth, parentMeasureHeight);

            measureChildren(itemWidth, itemHeight);

        }
    }

    @Override
    protected void measureChildren(int itemWidth, int itemHeight) {
        View view;

        log("measureChildren child count = " + getChildCount() + " itemWidth = " + itemWidth + " itemHeight = " + itemHeight);

        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {

            view = getChildAt(childIndex);

            view.measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {

        if (null != mAdapter) {
            int position = firstChildPositionInAdapter + indexOfChild(child);
            mAdapter.drawTimeText(child, position, canvas);
            mAdapter.drawTimeLine(child, position, canvas);
        }

        return super.drawChild(canvas, child, drawingTime);
    }

    public void setOnScrollStateChangeListener(OnScrollStateChangeListener onScrollStateChangeListener) {
        this.onScrollStateChangeListener = onScrollStateChangeListener;
    }

    public int getScrollState() {
        return mFloorScroller.isFinished() ? STATE_SCROLL_IDLE : STATE_SCROLLING;
    }

    private void log(String msg) {
        Log.d(getTagStr(), msg);
    }

    protected void decreaseOffset(View childView) {

        if (null != mAdapter) {

            float gap = mAdapter.getGap();

            Rect childRect = new Rect((int) (childView.getLeft() - gap / 2), childView.getTop(),
                    (int) (childView.getRight() + gap / 2), childView.getBottom());
            if (scrollOffset - childRect.width() >= 0) {
                scrollOffset -= childRect.width();
            }
            log("decreaseOffset = " + scrollOffset + " rect = " + childRect.toShortString());
        }
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        if (null != child) {
            scrollToChild(child);
        }
        super.requestChildFocus(child, focused);
    }

    private void dispatchLayout() {

        int top = mAdapter.getTimeLineAreaHeight();

        int bottom = getMeasuredHeight();

        int left;

        int right;

        View view;

        final int currentOffset = scrollOffset;

        int parentWidth = getMeasuredWidth();

        int gap = mAdapter.getGap();

        int column = mAdapter.getColumn();


        for (int childIndex = 0; childIndex < getChildCount(); childIndex++) {

            view = getChildAt(childIndex);

            left = (firstChildPositionInAdapter + childIndex) * parentWidth / column + gap / 2 - currentOffset;

            right = (firstChildPositionInAdapter + childIndex + 1) * parentWidth / column - gap / 2 - currentOffset;

            log("dispatchLayout [ " + left + " , " + top + " , " + right + " , " + bottom + "]");

            view.layout(left, top, right, bottom);
        }
    }

    protected void scrollToChild(View child) {

        if (null != mAdapter && 0 != mAdapter.getItemCount()) {

            final int currentOffset = scrollOffset;

            float dx = child.getLeft() - (getMeasuredWidth() - child.getMeasuredWidth()) / 2;

            Log.d(TAG, "scrollToChild child left = " + child.getLeft() + " middle left = " + ((getMeasuredWidth() - child.getMeasuredWidth()) / 2));

            float targetOffset = currentOffset + dx;

            Log.d(TAG, "scrollToChild currentOffset = " + currentOffset + " targetOffset = " + targetOffset + " maxOffset = " + maxOffset + " dx = " + dx);

            targetOffset = Math.max(minOffset, targetOffset);

            targetOffset = Math.min(maxOffset, targetOffset);

            dx = targetOffset - currentOffset;

            if (0 != dx) {
                Log.d(TAG, "scrollToChild: dx = " + dx);
                mFloorScroller.forceFinished(true);
                mFloorScroller.abortAnimation();
                setScrollState(STATE_SCROLLING);
                mFloorScroller.startScroll(currentOffset, 0, (int) dx, 0, SCROLL_DURATION);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    postInvalidateOnAnimation();
                } else {
                    postInvalidate();
                }
            }
        }
    }

    @Override
    public void computeScroll() {
        if (!mFloorScroller.isFinished()) {
            mFloorScroller.computeScrollOffset();
            scrollOffset = mFloorScroller.getCurrX();
            requestLayout();
        } else if (this.scrollState != STATE_SCROLL_IDLE) {
            mFloorScroller.forceFinished(true);
            scrollOffset = mFloorScroller.getFinalX();
            requestLayout();
        }
    }

    public void setScrollState(int scrollState) {
        Log.d(TAG, "setScrollState: scrollState = " + scrollState + " is state changed = " + (this.scrollState != scrollState));

        if (scrollState == STATE_SCROLLING) {
            shouldNotifyItemScrollIdle = true;
            return;
        }

        this.scrollState = scrollState;
        if (null != onScrollStateChangeListener && shouldNotifyItemScrollIdle) {
            this.onScrollStateChangeListener.onScrollStateChange(this.scrollState);
            shouldNotifyItemScrollIdle = false;
        }
    }

    public interface OnScrollStateChangeListener {
        void onScrollStateChange(int newState);
    }

    private class FloorScroller extends Scroller {

        public FloorScroller(Context context) {
            this(context, new LinearInterpolator());
        }

        public FloorScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        @Override
        public boolean computeScrollOffset() {
            boolean computeScrollOffset = super.computeScrollOffset();
            if (getCurrX() == getFinalX()) {
                forceFinished(true);
            }
            return computeScrollOffset;
        }
    }

    public interface Adapter {
        int getColumn();

        int getGap();

        int getTimeLineAreaHeight();

        float getItemScale();

        void drawTimeText(View childView, int position, Canvas canvas);

        void drawTimeLine(View childView, int position, Canvas canvas);

        View getChildView(ViewGroup parentView);

        void bindChildView(View childView, int position);

        int getItemCount();
    }

}
