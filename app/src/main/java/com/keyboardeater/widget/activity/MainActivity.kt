package com.keyboardeater.widget.activity

import android.app.Activity
import android.os.Bundle
import com.keyboardeater.widget.R
import com.keyboardeater.widget.adapter.MyTimeLineAdapter
import com.keyboardeater.widget.timeline.view.TimeLineAdapterView


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val timeLineView = findViewById<TimeLineAdapterView>(R.id.my_time_line)
        val timeLineAdapter = MyTimeLineAdapter()
        timeLineView.setAdapter(timeLineAdapter)

    }
}
