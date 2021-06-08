package com.keyboardeater.widget.adapter

import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.keyboardeater.widget.timeline.bean.TimeLineExampleBean
import com.keyboardeater.widget.timeline.view.TimeLineAdapterView

class MyTimeLineAdapter:TimeLineAdapterView.Adapter {

    private val timeLineBeans = emptyList<TimeLineExampleBean>().toMutableList()

    override fun getItemCount() = timeLineBeans.size

    override fun getTimeLineAreaHeight(): Int {
        return 20
    }

    override fun drawTimeText(childView: View, position: Int, canvas: Canvas) {
    }

    override fun getChildView(parentView: ViewGroup): View {
        return TextView(parentView.context)
    }

    override fun getColumn(): Int {
        return 6
    }

    override fun getItemScale(): Float {
        return 0.75f
    }

    override fun drawTimeLine(childView: View, position: Int, canvas: Canvas) {
    }

    override fun getGap(): Int {
        return 10
    }

    override fun bindChildView(childView: View, position: Int) {
        if (childView is TextView){
            childView.setText(timeLineBeans[position].title)
        }
    }
}