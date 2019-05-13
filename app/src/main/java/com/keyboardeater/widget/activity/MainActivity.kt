package com.keyboardeater.widget.activity

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView
import com.keyboardeater.widget.CenterFocusScrollView
import com.keyboardeater.widget.R
import com.keyboardeater.widget.adapter.LoadMoreAdapter
import com.keyboardeater.widget.adapter.SimpleLoadMoreAdapter
import com.keyboardeater.widget.adapter.TabAdapter


class MainActivity : Activity() {

    private val simpleAdapter: SimpleLoadMoreAdapter = SimpleLoadMoreAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val metric = DisplayMetrics()
        (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(metric)
        val screenWidth = metric.widthPixels

        val centerFocusFloorView = findViewById<CenterFocusScrollView>(R.id.center_focus_tab_view)

        centerFocusFloorView.dividerSize = TabAdapter.TAB_SPACE_HORIZONTAL_PADDING * screenWidth / TabAdapter.DEFAULT_SCREEN_WIDTH

        val tabAdapter = TabAdapter(this)

        val tabList = ArrayList<String>()

        tabList.add("赵又廷")

        tabList.add("电影")

        tabList.add("真相")

        tabList.add("阮经天")

        tabList.add("狄仁杰")

        tabList.add("四大天王")

        tabList.add("徐克神棍")

        tabList.add("战狼无敌")

        tabList.add("lgd大巴黎！")

        tabList.add("恭喜秘密")

        tabList.add("光头搞事")

        tabList.add("fy还不醒？")

        tabAdapter.setTabData(tabList)

        centerFocusFloorView.setAdapter(tabAdapter)

//        val recycle: RecyclerView = findViewById(R.id.recycle_view)
//        recycle.layoutManager = LinearLayoutManager(this)
//        val dm = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(dm)
//
//        val itemHeight = dm.heightPixels * 2 / PAGE_SIZE
//        simpleAdapter.setFloorHeight(itemHeight.toFloat())
//        simpleAdapter.setLoadMoreListener(this)
//
//        val footerView = TextView(this)
//        footerView.setTextSize((itemHeight/2).toFloat())
//        footerView.setHeight(itemHeight)
//        footerView.setText("加载更多")
//        footerView.gravity = Gravity.CENTER
//
//        simpleAdapter.setFooterView(footerView)
//        recycle.adapter = simpleAdapter
//
//        getFirstPage()
    }

//    private fun getFirstPage() {
//        loadMore(0)
//    }
//
//    override fun loadMore(totalLength: Int) {
//        val pageIndex = totalLength / PAGE_SIZE
//        val newData = getPageByIndex(pageIndex)
//        Log.d(TAG, "loadMore " + newData)
//        simpleAdapter.addData(newData)
//    }
//
//    private fun getPageByIndex(pageIndex: Int): List<String> {
//        val list = emptyList<String>().toMutableList()
//        for (index in 0..(PAGE_SIZE - 1)) {
//            list.add("Floor " + (pageIndex * PAGE_SIZE + index))
//        }
//        return list.toList()
//    }

    companion object {
        val PAGE_SIZE = 20
        val TAG = "MainActivity"
    }
}
