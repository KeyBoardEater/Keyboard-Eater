package com.keyboardeater.widget.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import com.key.shimmer.Shimmer
import com.key.shimmer.ShimmerTextView
import com.keyboardeater.widget.R


class MainActivity : Activity() {

    private var tv: ShimmerTextView? = null

    private var shimmer: Shimmer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.shimmer_activity_layout)


        tv = findViewById(R.id.shimmer_tv)

//        val timeLineView = findViewById<TimeLineAdapterView>(R.id.my_time_line)
//        val timeLineAdapter = MyTimeLineAdapter()
//        timeLineView.setAdapter(timeLineAdapter)

    }


    fun toggleAnimation(target: View) {
        if (shimmer != null && shimmer!!.isAnimating()) {
            shimmer!!.cancel()
        } else {
            shimmer = Shimmer()
            shimmer!!.start(tv)
        }
    }
}
