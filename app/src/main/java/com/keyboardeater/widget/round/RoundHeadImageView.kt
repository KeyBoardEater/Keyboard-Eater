package com.keyboardeater.widget.round

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import androidx.appcompat.widget.AppCompatImageView
import com.keyboardeater.widget.R

/**
 * Created by LittleOne on 18/9/4.
 */
class RoundHeadImageView : AppCompatImageView {
    private val mPaint: Paint

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        mPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        scaleType = ScaleType.MATRIX
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun log(msg: String) {
        Log.d(TAG, msg)
    }

    override fun onDraw(canvas: Canvas) {
        val mDrawable: Drawable

        if (drawable == null) {
            mDrawable = background
            if (mDrawable == null) {
                // 没有可以绘制的内容
                return
            }
        }else{
            mDrawable = drawable
        }

        val mPaddingTop = paddingTop
        val mPaddingLeft = paddingLeft

        val canvasSize = Math.min(canvas.width, canvas.height)

        val mDrawableWidth = mDrawable.intrinsicWidth
        val mDrawableHeight = mDrawable.intrinsicHeight

        if (mDrawableWidth == 0 || mDrawableHeight == 0) {
            // 绘制内容不合法
            return
        }

        mDrawable.setBounds(0, 0, canvasSize, canvasSize * mDrawableHeight / mDrawableWidth)

        val mDrawMatrix = imageMatrix

        if (mDrawMatrix == null && mPaddingTop == 0 && mPaddingLeft == 0) {
            mDrawable.draw(canvas)
        } else {
            val saveCount = canvas.saveCount
            canvas.save()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (cropToPadding) {
                    val scrollX = scrollX
                    val scrollY = scrollY
                    canvas.clipRect(scrollX + mPaddingLeft, scrollY + mPaddingTop,
                            scrollX + right - left - paddingRight,
                            scrollY + bottom - top - paddingBottom)
                }
            }

            canvas.translate(mPaddingLeft.toFloat(), mPaddingTop.toFloat())

            val rectF = RectF(paddingLeft.toFloat(), paddingTop.toFloat(), (width - paddingRight).toFloat(), (height - paddingBottom).toFloat())


            mPaint.shader = drawable2Bitmap(mDrawable, canvasSize)?.let { BitmapShader(it, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP) }

            if (canvasSize >= 0) {
                // 绘制四个相同圆角
                canvas.drawRoundRect(rectF,
                        (canvasSize / 2).toFloat(), (canvasSize / 2).toFloat(), mPaint)
            }
            canvas.restoreToCount(saveCount)
        }

    }

    private fun drawable2Bitmap(drawable: Drawable, canvasSize: Int): Bitmap? {
        if (drawable == null) {
            return null
        }
        val size = if (canvasSize > 0) canvasSize else drawable.intrinsicWidth

        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        //根据传递的scaletype获取matrix对象，设置给bitmap
        val matrix = imageMatrix
        if (matrix != null) {
            canvas.concat(matrix)
        }
        drawable.draw(canvas)
        return bitmap
    }

    companion object {
        private const val TAG = "RoundConner"
        private const val DEFAULT_ROUND_CONNER_RADIUS = R.dimen.round_conner
    }
}