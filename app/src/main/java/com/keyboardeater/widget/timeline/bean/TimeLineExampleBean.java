package com.keyboardeater.widget.timeline.bean;

import org.jetbrains.annotations.NotNull;

public class TimeLineExampleBean implements TimeLine {

    private String title;

    private String time;

    public TimeLineExampleBean(String time, String title) {
        this.title = title;
        this.time = time;
    }

    @NotNull
    @Override
    public String getTimeText() {
        return time;
    }

    public String getTitle() {
        return title;
    }
}
