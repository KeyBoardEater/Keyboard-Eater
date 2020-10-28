package com.keyboardeater.widget.timeline.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.keyboardeater.widget.R;
import com.keyboardeater.widget.timeline.bean.TimeLine;

import java.util.List;

public abstract class TimeLineViewGroup<T extends TimeLine> extends ViewGroup {

    private final static int DEFAULT_COLUMN = 6;

    private final static int DEFAULT_DATE_LINE_HEIGHT = 60;

    private final static int DEFAULT_TEXT_SIZE = 30;

    private final static int DEFAULT_GAP = 30;

    private final static int DEFAULT_ROUND_CIRCLE_IN_RADIUS = 6;

    private final static int DEFAULT_TIME_LINE_WIDTH = 3;

    protected final int timeLineAreaHeight;

    private final Paint.FontMetricsInt messageMetricsInt;

    protected int column = DEFAULT_COLUMN;

    private final int textBaseLine;

    protected final int gap;

    private final int timeLineBaseLine;

    private final int roundCircleInRadius;

    private final int timeLineWidth;

    private TextPaint textPaint;

    private Paint timeLinePaint;

    private Paint circlePaint;

    public TimeLineViewGroup(Context context) {
        this(context, null);
    }

    public TimeLineViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeLineViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        DisplayMetrics displayMetrics = new DisplayMetrics();

        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

        int screenWidth = displayMetrics.widthPixels;

        timeLineAreaHeight = DEFAULT_DATE_LINE_HEIGHT * screenWidth / 1920;

        gap = DEFAULT_GAP * screenWidth / 1920;

        timeLineBaseLine = DEFAULT_DATE_LINE_HEIGHT * screenWidth * 5 / 1920 / 6;

        textBaseLine = DEFAULT_DATE_LINE_HEIGHT * screenWidth * 2 / 1920 / 3;

        roundCircleInRadius = DEFAULT_ROUND_CIRCLE_IN_RADIUS * screenWidth / 1920;

        timeLineWidth = DEFAULT_TIME_LINE_WIDTH * screenWidth / 1920;

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(getResources().getColor(R.color.time_text_color));
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(DEFAULT_TEXT_SIZE * screenWidth / 1920);
        messageMetricsInt = textPaint.getFontMetricsInt();

        timeLinePaint = new Paint();
        timeLinePaint.setColor(getResources().getColor(R.color.time_line_color));
        timeLinePaint.setAntiAlias(true);
        timeLinePaint.setStyle(Paint.Style.FILL);
        timeLinePaint.setStrokeWidth(timeLineWidth);

        circlePaint = new Paint();
        circlePaint.setColor(getResources().getColor(R.color.time_line_color));
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(timeLineWidth);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {

        boolean drawChild = super.drawChild(canvas, child, drawingTime);

        int left = child.getLeft();

        int right = child.getRight();

        int circleLeft = left + (right - left - roundCircleInRadius - 2 * timeLineWidth) / 2;

        int circleRight = left + (right - left + roundCircleInRadius + 2 * timeLineWidth) / 2;

        int circleCentreX = left + (right - left) / 2;

        log("drawChild left = " + left + " right = " + right + " ");

        drawLine(timeLineBaseLine, left, circleLeft - timeLineWidth, timeLineWidth, timeLinePaint, canvas);

        drawCircle(circleCentreX, timeLineBaseLine, roundCircleInRadius, timeLineWidth, circlePaint, canvas);

        drawLine(timeLineBaseLine, circleRight + timeLineWidth, right, timeLineWidth, timeLinePaint, canvas);

        if (child.getTag() instanceof TimeLine) {
            TimeLine timeLine = (TimeLine) child.getTag();
            String dateText = timeLine.getTimeText();
            if (!TextUtils.isEmpty(dateText)) {
                drawDateText(textBaseLine - textPaint.getTextSize(), left, right, dateText, textPaint, canvas);
            }
        }

        if (0 != indexOfChild(child)) {
            drawLine(timeLineBaseLine, left - gap, left, timeLineWidth, timeLinePaint, canvas);
        }

        return drawChild;
    }

    protected void drawDateText(float textBaseLine, int left, int right, String dateText, TextPaint textPaint, Canvas canvas) {

        float baseY = textBaseLine - messageMetricsInt.ascent;

        float textWidth = getTextWidth(textPaint, dateText);

        canvas.drawText(dateText, (left + right - textWidth) / 2,
                baseY, textPaint);

    }

    public static int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    private void log(String msg) {
        Log.d(getTagStr(), msg);
    }

    protected abstract String getTagStr();

    private void drawCircle(int circleCentreX, int circleCentreY, int roundCircleInRadius, int timeLineWidth, Paint backgroundPaint, Canvas canvas) {
        canvas.drawCircle(circleCentreX, circleCentreY, roundCircleInRadius + timeLineWidth / 2, backgroundPaint);
    }

    private void drawLine(int timeLineBaseLine, int left, int right, int timeLineWidth, Paint backgroundPaint, Canvas canvas) {
        canvas.drawLine(left, timeLineBaseLine, right, timeLineBaseLine, backgroundPaint);
    }

    public abstract void addTimeLine(List<T> timeLines);
}
