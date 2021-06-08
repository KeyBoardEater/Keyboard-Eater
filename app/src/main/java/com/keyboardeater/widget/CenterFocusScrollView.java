package com.keyboardeater.widget;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.FocusFinder;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Scroller;

import androidx.annotation.IntDef;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 不带回收功能的焦点居中自定义View
 */
public class CenterFocusScrollView extends ViewGroup {

    private final static String TAG = "CenterFocusScrollView";

    public static final int SCROLL_DURATION = 300;

    @IntDef({HORIZONTAL, VERTICAL})
    public @interface OrientationMode {
    }

    public static final int STATE_SCROLLING = RecyclerView.SCROLL_STATE_SETTLING;

    public static final int STATE_SCROLL_IDLE = RecyclerView.SCROLL_STATE_IDLE;

    public static final int HORIZONTAL = 0;

    public static final int VERTICAL = 1;

    private final AtomicBoolean mOnLayoutFlag = new AtomicBoolean();

    private final AtomicBoolean mOnMeasureFlag = new AtomicBoolean();

    private final AtomicInteger offset = new AtomicInteger();

    private final AtomicBoolean isLayout = new AtomicBoolean(false);

    private int mTotalLength;

    private int dividerSize;

    private int mOrientation;

    private Adapter adapter;

    private FocusInterceptor focusInterceptor;

    private final FloorScroller mFloorScroller;

    private OnScrollStateChangeListener onScrollStateChangeListener;

    private final DataObserver dataObserver = new DataObserver() {
        @Override
        public void onDataChanged() {
            notifyViews();
        }
    };

    private boolean shouldNotifyItemScrollIdle = false;

    private int scrollState = STATE_SCROLL_IDLE;

    private final Recycler mRecycler;

    public CenterFocusScrollView(Context context) {
        this(context, null);
    }

    public CenterFocusScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CenterFocusScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mFloorScroller = new FloorScroller(context);
        mRecycler = new Recycler();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (mOnMeasureFlag.compareAndSet(false, true)) {

            int parentWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);

            int parentHeight = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);

            setMeasuredDimension(parentWidth, parentHeight);

            tryCheckChildIsFullOnMeasure(parentWidth, parentHeight);

//            measureChildren(parentWidth, parentHeight);

            mOnMeasureFlag.set(false);
        }
    }

//    @Override
//    protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
//        final int size = getChildCount();
//        mTotalLength = 0;
//        for (int i = 0; i < size; ++i) {
//            final View child = getChildAt(i);
//            if (child.getVisibility() != GONE) {
//                measureChild(child, widthMeasureSpec, heightMeasureSpec);
//                LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
//                layoutParams.offset = mTotalLength;
//                mTotalLength += child.getMeasuredWidth();
//                if (i != size - 1) {
//                    mTotalLength += dividerSize;
//                }
//            }
//        }
//    }

    private void tryCheckChildIsFullOnMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (null == adapter) {
            return;
        }

        if (isLayout.compareAndSet(false, true)) {

            final int size = getChildCount();

            final int leftEdge = this.offset.get();

            final int rightEdge = leftEdge + getWidth();

            mTotalLength = 0;

            Log.d(TAG, "tryCheckChildIsFullOnMeasure rightEdge = " + rightEdge + " leftEdge = " + leftEdge);

            for (int i = 0; i < adapter.getItemCount(); i++) {
                View child = getChildAt(i);
                if (null == child) {
                    child = tryGetRecycleViewFromCache();
                    if (null == child) {
                        child = adapter.onCreateView(i, this);
                    }

                    int widthSpec = mOrientation == VERTICAL ? LayoutParams.MATCH_PARENT : LayoutParams.WRAP_CONTENT;
                    int heightSpec = mOrientation == VERTICAL ? LayoutParams.WRAP_CONTENT : LayoutParams.MATCH_PARENT;

                    LayoutParams layoutParams = new LayoutParams(widthSpec, heightSpec);
                    addViewInLayout(child, -1, layoutParams, true);

                    adapter.bindView(i, child);

                    measureChild(child, widthMeasureSpec, heightMeasureSpec);

                    Log.d(TAG, "tryCheckChildIsFullOnMeasure child measure size = " + (mOrientation == VERTICAL ? child.getMeasuredHeight() : child.getMeasuredWidth()));

                    layoutParams.offset = mTotalLength;
                    layoutParams.index = i;

                    final int childLeftEdge = mTotalLength;

                    mTotalLength += mOrientation == VERTICAL ? child.getMeasuredHeight() : child.getMeasuredWidth();

                    Log.d(TAG, "tryCheckChildIsFullOnMeasure rightEdge = " + rightEdge + " leftEdge = " + leftEdge + " childLeftEdge = " + childLeftEdge + " mTotalLength = " + mTotalLength);
                    if (childLeftEdge > rightEdge || mTotalLength < leftEdge) {
                        removeViewInLayout(child);
                        tryRecycleViewToCache(child);
                    }

                    if (i != size - 1) {
                        mTotalLength += dividerSize;
                    }
                } else {

                    final int childLeftEdge = mTotalLength;

                    measureChild(child, widthMeasureSpec, heightMeasureSpec);

                    adapter.bindView(i, child);

                    mTotalLength += child.getMeasuredWidth();

                    LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
                    layoutParams.offset = mTotalLength;
                    mTotalLength += child.getMeasuredWidth();
                    if (i != size - 1) {
                        mTotalLength += dividerSize;
                    }

                    Log.d(TAG, "tryCheckChildIsFullOnMeasure rightEdge = " + rightEdge + " leftEdge = " + leftEdge + " childLeftEdge = " + childLeftEdge + " mTotalLength = " + mTotalLength);
                    if (childLeftEdge > rightEdge || mTotalLength < leftEdge) {
                        removeViewInLayout(child);
                        tryRecycleViewToCache(child);
                    }
                }

                Log.d(TAG, "tryCheckChildIsFullOnMeasure size = " + getChildCount());
            }

            isLayout.set(false);
        }
    }

    private View tryGetRecycleViewFromCache() {
        View recycleChild = mRecycler.getFromRecycle();
        Log.d(TAG, "tryGetRecycleViewFromCache " + recycleChild);
        return recycleChild;
    }

    private void tryRecycleViewToCache(View childView) {
        Log.d(TAG, "tryRecycleViewToCache " + childView);
        mRecycler.putIntoRecycle(childView);
    }

    private void tryCheckChildIsFullOnLayout() {
        if (null == adapter) {
            return;
        }

        if (isLayout.compareAndSet(false, true)) {


            isLayout.set(false);
        }
    }

    private void tryRecycleChildIfPossible() {
        if (null == adapter) {
            return;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

        if (mOnLayoutFlag.compareAndSet(false, true)) {

            tryCheckChildIsFullOnLayout();

            final int offset = this.offset.get();

            final int parentLeft = getPaddingLeft();

            final int parentTop = getPaddingBottom();

            final int parentWidth = getMeasuredWidth();

            final int parentHeight = getMeasuredHeight();

            for (int index = 0; index < getChildCount(); index++) {
                final View child = getChildAt(index);
                if (child.getVisibility() != GONE) {
                    final LayoutParams lp = (LayoutParams) child.getLayoutParams();

                    final int width = child.getMeasuredWidth();
                    final int height = child.getMeasuredHeight();

                    final int childLeft;
                    final int childTop;

                    if (mOrientation == HORIZONTAL) {
                        childLeft = lp.offset - offset + parentLeft;
                        childTop = parentTop;
                    } else {
                        childLeft = parentLeft;
                        childTop = lp.offset - offset + parentTop;
                    }

                    child.layout(childLeft, childTop, childLeft + width, childTop + height);
                }
            }

            tryRecycleChildIfPossible();

            mOnLayoutFlag.set(false);
        }
    }

    public void setOnScrollStateChangeListener(OnScrollStateChangeListener onScrollStateChangeListener) {
        this.onScrollStateChangeListener = onScrollStateChangeListener;
    }

    private void notifyViews() {
//        if (null != adapter) {
//
//            int itemCount = adapter.getItemCount();
//
//            for (int index = 0; index < itemCount; index++) {
//                View view = getChildAt(index);
//
//                if (null == view) {
//                    view = adapter.onCreateView(index, this);
//
//                    if (mOrientation == VERTICAL) {
//                        addView(view, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//                    } else {
//                        addView(view, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
//                    }
//                }
//
//                adapter.bindView(index, view);
//            }
//
//            requestLayout();
//            invalidate();
//        }

    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    public void setAdapter(Adapter adapter) {
        if (this.adapter != adapter) {
            if (null != this.adapter) {
                this.adapter.setDataObserver(null);
            }
            this.adapter = adapter;
            removeAllViews();
            if (null != this.adapter) {
                this.adapter.setDataObserver(dataObserver);
            }
        }
    }

    public void setFocusInterceptor(FocusInterceptor focusInterceptor) {
        this.focusInterceptor = focusInterceptor;
    }

    public void setOrientation(int mOrientation) {
        if (this.mOrientation != mOrientation) {
            this.mOrientation = mOrientation;
            requestLayout();
        }
    }

    public void setDividerSize(int dividerSize) {
        this.dividerSize = dividerSize;
        requestLayout();
    }

    public int getDividerSize() {
        return dividerSize;
    }

    public int getOrientation() {
        return mOrientation;
    }

    public static abstract class Adapter {
        private DataObserver dataObserver;

        public abstract View onCreateView(int position, ViewGroup parent);

        public abstract void bindView(int position, View childView);

        public abstract int getItemCount();

        private void setDataObserver(DataObserver dataObserver) {
            this.dataObserver = dataObserver;
            notifyDateSetChanged();
        }

        public final void notifyDateSetChanged() {
            if (null != dataObserver) {
                dataObserver.onDataChanged();
            }
        }
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        Log.d(TAG, "requestChildFocus child = " + child + "\nfocused = " + focused);
        super.requestChildFocus(child, focused);

        if (needScroll()) {
            requestChildFocusOnScreen(child);
        }
    }

    private boolean needScroll() {
        return (mTotalLength >= getHeight() && mOrientation == VERTICAL) || ((mTotalLength >= getWidth() && mOrientation == HORIZONTAL));

    }

    private void requestChildFocusOnScreen(View child) {
        if (null != child && child.getLayoutParams() instanceof LayoutParams) {

            final int currentOffset = offset.get();

            LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();

            final int childViewOffset = layoutParams.offset;

            int toOffset = focusViewToCenter(child, childViewOffset);

            Log.d(TAG, "requestChildFocusOnScreen: currentOffset = " + currentOffset + " childViewOffset = " + childViewOffset + " toOffset = " + toOffset);

            if (mOrientation == VERTICAL) {
                if (childViewOffset - toOffset < 0) {
                    toOffset = 0;
                } else if (mTotalLength - childViewOffset - (getHeight() + child.getHeight()) / 2 < 0) {
                    toOffset = mTotalLength - getHeight();
                }
            } else {

                Log.d(TAG, "requestChildFocusOnScreen: fixing " + mTotalLength + " - " + (getWidth() / 2) + " - " + child.getWidth() / 2 + " = " + (mTotalLength - getWidth() / 2 - child.getWidth() / 2));
                if (childViewOffset - toOffset < 0) {
                    toOffset = 0;
                } else if (mTotalLength - childViewOffset - (getWidth() + child.getWidth()) / 2 < 0) {
                    toOffset = mTotalLength - getWidth();
                }


            }

            Log.d(TAG, "requestChildFocusOnScreen: after fixed = " + toOffset);

            int dValue = toOffset - currentOffset;

            if (0 != dValue) {
                Log.d(TAG, "requestChildFocusOnScreen: dValue = " + dValue);
                mFloorScroller.forceFinished(true);
                mFloorScroller.abortAnimation();
                setScrollState(STATE_SCROLLING);
                if (mOrientation == VERTICAL) {
                    mFloorScroller.startScroll(0, currentOffset, 0, dValue, SCROLL_DURATION);
                } else {
                    mFloorScroller.startScroll(currentOffset, 0, dValue, 0, SCROLL_DURATION);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    postInvalidateOnAnimation();
                } else {
                    postInvalidate();
                }
            }
        }
    }

    private int focusViewToCenter(View child, int childViewOffset) {
        int viewToCenterOffset;


        if (mOrientation == VERTICAL) {
            int parentHeight = getHeight();

            int childHeight = child.getHeight();

            viewToCenterOffset = childViewOffset - (parentHeight - childHeight) / 2;

        } else {
            int parentWidth = getWidth();

            int childWidth = child.getWidth();

            Log.d(TAG, "focusViewToCenter parentWidth = " + parentWidth + " childWidth = " + childWidth);

            viewToCenterOffset = childViewOffset - (parentWidth - childWidth) / 2;
        }

        return Math.max(viewToCenterOffset, 0);
    }

    @Override
    public void computeScroll() {
        if (!mFloorScroller.isFinished()) {
            mFloorScroller.computeScrollOffset();
            if (mOrientation == VERTICAL) {
                offset.set(mFloorScroller.getCurrY());
            } else {
                offset.set(mFloorScroller.getCurrX());
            }
            requestLayout();
        } else if (this.scrollState != STATE_SCROLL_IDLE) {
            mFloorScroller.forceFinished(true);
            if (mOrientation == VERTICAL) {
                offset.set(mFloorScroller.getFinalY());
            } else {
                offset.set(mFloorScroller.getFinalX());
            }
            requestLayout();
        }
        Log.d(TAG, "computeScroll offset = " + offset.get());
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

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        Log.d(TAG, "requestFocus direction = " + direction + " previouslyFocusedRect = " + ((null != previouslyFocusedRect) ? previouslyFocusedRect.toShortString() : "null"));
        boolean requestFocus = false;
        if (null != focusInterceptor) {
            requestFocus = focusInterceptor.onInterceptFocus(direction, previouslyFocusedRect);
        }
        if (requestFocus) {
            return true;
        } else {
            return super.requestFocus(direction, previouslyFocusedRect);
        }
    }

    @Override
    public View focusSearch(View focused, int direction) {

        if (mOrientation == HORIZONTAL) {
            if (direction == FOCUS_LEFT) {
                View nextFocused = FocusFinder.getInstance().findNextFocus(this, focused, direction);
                if (null == nextFocused) {
//                    ShakeFocusUtil.shakeFocus(focused, KeyEvent.KEYCODE_DPAD_LEFT);

                    return focused;
                } else {
                    return nextFocused;
                }
            } else if (direction == FOCUS_RIGHT) {
                View nextFocused = FocusFinder.getInstance().findNextFocus(this, focused, direction);
                if (null == nextFocused) {
//                    ShakeFocusUtil.shakeFocus(focused, KeyEvent.KEYCODE_DPAD_RIGHT);

                    return focused;
                } else {
                    return nextFocused;
                }
            }
        }
        return super.focusSearch(focused, direction);
    }

    public interface FocusInterceptor {
        boolean onInterceptFocus(int direction, Rect previouslyFocusedRect);
    }

    public interface DataObserver {
        void onDataChanged();
    }

    private class LayoutParams extends ViewGroup.LayoutParams {

        private int offset = 0;

        private int index = -1;

        public LayoutParams(int width, int height) {
            super(width, height);
        }

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

    public interface OnScrollStateChangeListener {
        void onScrollStateChange(int newState);
    }

    private class Recycler {

        private final List<View> mRecyclerPool = new ArrayList<>();

        private void putIntoRecycle(View recycleChild) {
            if (null != recycleChild && !mRecyclerPool.contains(recycleChild)) {
                mRecyclerPool.add(recycleChild);
            }
        }

        synchronized View getFromRecycle() {
            View childView = null;
            for (int index = 0; index < mRecyclerPool.size(); index++) {
                if (mRecyclerPool.get(index) == null) {
                    childView = mRecyclerPool.remove(index);
                    break;
                }
            }
            return childView;
        }

    }
}
