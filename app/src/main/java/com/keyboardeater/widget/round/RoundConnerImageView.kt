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
class RoundConnerImageView : AppCompatImageView {
    private val roundRadius: Float
    private val radius: FloatArray
    private val mPaint: Paint
    private var supportRoundConner: Boolean

    constructor(context: Context, attrs: AttributeSet? = null) : super(context, attrs) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.RoundConner, 0, 0)

        if (typedArray.hasValue(R.styleable.RoundConner_left_top_conner)
                || typedArray.hasValue(R.styleable.RoundConner_left_bottom_conner)
                || typedArray.hasValue(R.styleable.RoundConner_right_top_conner)
                || typedArray.hasValue(R.styleable.RoundConner_right_bottom_conner)) {
            roundRadius = 0f
        } else {
            roundRadius = typedArray.getDimension(R.styleable.RoundConner_round_conner,
                    context.resources.getDimension(DEFAULT_ROUND_CONNER_RADIUS))
        }

        val leftTopConner = typedArray.getDimension(R.styleable.RoundConner_left_top_conner, 0f)
        val rightTopConner = typedArray.getDimension(R.styleable.RoundConner_right_top_conner, 0f)
        val leftBottomConner = typedArray.getDimension(R.styleable.RoundConner_left_bottom_conner, 0f)
        val rightBottomConner = typedArray.getDimension(R.styleable.RoundConner_right_bottom_conner, 0f)

        radius = floatArrayOf(leftTopConner, leftTopConner, rightTopConner, rightTopConner, rightBottomConner, rightBottomConner, leftBottomConner, leftBottomConner)

        typedArray.recycle()
        log("RoundConnerView: $roundRadius [$leftTopConner,$rightTopConner,$rightBottomConner,$leftBottomConner]")

        mPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        scaleType = ScaleType.FIT_XY
        supportRoundConner = true

    }

    private fun log(msg: String) {
        Log.d(TAG, msg)
    }

    /**
     * 一键开关控制是否绘制圆角
     */
    fun setSupportRoundConner(supportRoundConner: Boolean) {
        this.supportRoundConner = supportRoundConner
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (!supportRoundConner) {
            super.onDraw(canvas)
        } else {
            var mDrawable: Drawable? = drawable
            val mPaddingTop = paddingTop
            val mPaddingLeft = paddingLeft

            if (mDrawable == null) {
                mDrawable = background
                if (mDrawable == null) {
                    // 没有可以绘制的内容
                    return
                }
            }

            val mDrawableWidth = mDrawable.intrinsicWidth
            val mDrawableHeight = mDrawable.intrinsicHeight

            if (mDrawableWidth == 0 || mDrawableHeight == 0) {
                // 绘制内容不合法
                return
            }

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

                mPaint.shader = drawable2Bitmap(mDrawable, canvas.width, canvas.height)?.let { BitmapShader(it, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP) }

                if (roundRadius >= 0) {
                    // 绘制四个相同圆角
                    canvas.drawRoundRect(rectF,
                            roundRadius, roundRadius, mPaint)
                } else {
                    // 圆角并不完全相同，注意Path.Direction.CW和radius中参数是有关系的，是从左上角开始顺时针数圆弧的两个半径，两个半径的含义表示是可以绘制椭圆弧的
                    val path = Path()
                    path.addRoundRect(rectF, radius, Path.Direction.CW)
                    canvas.drawPath(path, mPaint)
                }
                canvas.restoreToCount(saveCount)
            }

        }
    }

    private fun drawable2Bitmap(drawable: Drawable?, width: Int, height: Int): Bitmap? {
        var width = width
        var height = height
        if (drawable == null) {
            return null
        }
        if (width <= 0) {
            width = drawable.intrinsicWidth
        }
        if (height <= 0) {
            height = drawable.intrinsicHeight
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
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