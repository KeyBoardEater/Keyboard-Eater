package com.keyboardeater.widget.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.github.nukc.LoadMoreWrapper.LoadMoreWrapper;

import java.util.ArrayList;
import java.util.List;

public abstract class LoadMoreAdapter<T, VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    protected final List<T> myDataList = new ArrayList<>();

    protected final LoadMoreWrapper mLoadMoreWrapper = LoadMoreWrapper.with(this);

    protected OnLoadMoreListener loadMoreListener;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mLoadMoreWrapper.setListener(

                new com.github.nukc.LoadMoreWrapper.LoadMoreAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore(com.github.nukc.LoadMoreWrapper.LoadMoreAdapter.Enabled enabled) {
                        if (null != loadMoreListener) {
                            loadMoreListener.loadMore(myDataList.size());
                        }
                    }
                }).setShowNoMoreEnabled(true).into(recyclerView);
    }

    public void setFooterView(View footerView){
        mLoadMoreWrapper.setFooterView(footerView);
    }

    public void setLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.loadMoreListener = loadMoreListener;
    }

    public void addData(List<T> newData){
        myDataList.addAll(newData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return myDataList.size();
    }

    public interface OnLoadMoreListener {
        void loadMore(int totalLength);
    }
}
