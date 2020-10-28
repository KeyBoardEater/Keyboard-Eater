package com.keyboardeater.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout

class ClipViewGroup(context: Context?) : LinearLayout(context) {

    private var marginToWindowTop = 0

    private var marginToWindowBottom = 0

    init {
        clipChildren = false
        clipToPadding = false
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        super.onLayout(changed, l, t, r, b)

        val drawRect = Rect()

        getGlobalVisibleRect(drawRect)

        val displayMetrics = DisplayMetrics()

        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)

        marginToWindowTop = drawRect.top
        marginToWindowBottom = displayMetrics.heightPixels - drawRect.bottom

        Log.d(TAG, "onLayout ${drawRect.toShortString()}")
    }

    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {

        val clipRect = Rect(canvas.clipBounds)
        clipRect.top -= marginToWindowTop
        clipRect.bottom += marginToWindowBottom

        canvas.clipRect(clipRect)

        Log.d(TAG, "drawChild $clipRect")

        return super.drawChild(canvas, child, drawingTime)
    }

    companion object {
        private const val TAG = "ClipViewGroup"
    }
}