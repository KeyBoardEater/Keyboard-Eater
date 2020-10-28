package com.keyboardeater.widget.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.keyboardeater.widget.R

class SampleRecyclerViewAdapter(context: Context) : RecyclerView.Adapter<SampleRecyclerViewAdapter.ViewHolder>(), View.OnFocusChangeListener {

    private val tabList = emptyList<String>().toMutableList()

    private val titleUnderlinePadding: Int

    private val titleUnderlineHeight: Int

    private val titlePadding: Int

    private val titleTextSize: Int

    private val titleHeight: Int

    private var lastView: View? = null

    init {
        val metric = DisplayMetrics()
        (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(metric)
        val screenWidth = metric.widthPixels

        titleHeight = FLOOR_TITLE_HEIGHT * screenWidth / DEFAULT_SCREEN_WIDTH

        titleTextSize = FLOOR_TITLE_TEXT_SIZE * screenWidth / DEFAULT_SCREEN_WIDTH

        titlePadding = GAP / 2 * screenWidth / DEFAULT_SCREEN_WIDTH

        titleUnderlineHeight = FLOOR_TITLE_UNDERLINE_HEIGHT * screenWidth / DEFAULT_SCREEN_WIDTH

        titleUnderlinePadding = FLOOR_TITLE_UNDERLINE_PADDING * screenWidth / DEFAULT_SCREEN_WIDTH
    }

    private fun selectTitleView(titleView: View) {
        Log.d(TAG, "before setSelectedTab $titleView")
        if (titleView != lastView) {
            lastView?.isSelected = false
            lastView = titleView
            lastView?.isSelected = true
            Log.d(TAG, "after setSelectedTab ${lastView?.isSelected} $lastView")
        }
    }

    fun setTabData(tabData: List<String>) {
        this.tabList.clear()
        this.tabList.addAll(tabData)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = TextView(parent.context)
        itemView.isFocusable = true
        itemView.gravity = Gravity.CENTER_VERTICAL
        itemView.isFocusableInTouchMode = false
        itemView.setPadding(titlePadding, (titleHeight - titleTextSize) / 2, titlePadding, (titleHeight - titleTextSize) / 2)
        itemView.textSize = titleTextSize.toFloat()
        itemView.maxEms = 5
        itemView.setSingleLine()
        itemView.includeFontPadding = false
        itemView.ellipsize = TextUtils.TruncateAt.END
        itemView.onFocusChangeListener = this
        itemView.setTextColor(itemView.resources.getColorStateList(R.color.tab_floor_title_color))

        val background = StateListDrawable()
        val defaultBackground = ColorDrawable()
        defaultBackground.color = Color.TRANSPARENT

        val shapeDrawable = ColorDrawable()
        shapeDrawable.color = Color.WHITE

        val shapeDrawables = arrayOf<Drawable>(shapeDrawable)
        val layerDrawable = LayerDrawable(shapeDrawables)

        layerDrawable.setLayerInset(0, titlePadding + titleUnderlinePadding, (titleHeight + titleTextSize) / 2 + titleUnderlinePadding,
                titlePadding + titleUnderlinePadding, (titleHeight - titleTextSize) / 2 - titleUnderlinePadding - titleUnderlineHeight)

        background.addState(intArrayOf(android.R.attr.state_focused), layerDrawable)
        background.addState(intArrayOf(), defaultBackground)

        itemView.background = background

        Log.d(TAG, "onCreateViewHolder $itemView")

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tabList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        Log.d(TAG, "onBindViewHolder $holder $position")

        holder.setTitle(tabList[position])
    }

    override fun onFocusChange(v: View?, hasFocus: Boolean) {
        if (v is TextView) {
            val titleView = v as TextView
            if (hasFocus) {
                selectTitleView(v);
                titleView.paint.isFakeBoldText = true
                titleView.setSingleLine(true)
                titleView.ellipsize = TextUtils.TruncateAt.MARQUEE
                titleView.marqueeRepeatLimit = -1
            } else {
                titleView.paint.isFakeBoldText = false
                titleView.setSingleLine(false)
                titleView.maxLines = 1
                titleView.ellipsize = TextUtils.TruncateAt.END
                titleView.marqueeRepeatLimit = 0
            }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun setTitle(title: String) {
            (itemView as Button).text = title
        }
    }

    companion object {

        const val TAG = "SampleRecyclerViewAdapter"

        const val FLOOR_TITLE_HEIGHT = 116

        const val DEFAULT_SCREEN_WIDTH = 1920

        const val FLOOR_TITLE_UNDERLINE_HEIGHT = 1

        const val FLOOR_TITLE_UNDERLINE_PADDING = 6

        const val FLOOR_TITLE_TEXT_SIZE = 40

        const val GAP = 30

        const val TAB_SPACE_HORIZONTAL_PADDING = 50

    }
}
