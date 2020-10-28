package com.keyboardeater.widget.activity

import android.app.Activity
import android.os.Bundle
import android.widget.ImageView
import com.keyboardeater.widget.R
import com.keyboardeater.widget.timeline.view.ExpandWhileFocusedView


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val expandView = findViewById<ExpandWhileFocusedView>(R.id.expand_view)
        expandView.setExpandParams(resources.getDimensionPixelSize(R.dimen.expand_view_height),
                resources.getDimensionPixelSize(R.dimen.expand_view_width),
                resources.getDimensionPixelSize(R.dimen.expand_view_height)
        )
    }
}
