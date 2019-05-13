package com.keyboardeater.widget.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SimpleLoadMoreAdapter extends LoadMoreAdapter<String, SimpleLoadMoreAdapter.SimpleViewHolder> {
    private float itemHeight = 60;

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Button textView = new Button(parent.getContext());
        textView.setBackgroundColor(Color.parseColor("#66333333"));
        textView.setHeight((int) itemHeight);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, itemHeight / 2);
        return new SimpleViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        String title = myDataList.get(position);
        holder.setTitle(title);
    }

    public void setFloorHeight(float itemHeight) {
        Log.d("SimpleLoadMoreAdapter", "setFloorHeight: itemHeight = " + itemHeight);
        this.itemHeight = itemHeight;
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        public SimpleViewHolder(View itemView) {
            super(itemView);
        }

        void setTitle(String title) {
            ((Button) itemView).setText(title);
        }
    }
}
